package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.camera.FrameFeedHandler;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueInput.TypedInputList;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.ValueOutput.TypedOutputList;
import net.neoforged.neoforge.network.PacketDistributor;

public class FrameBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity {
	private final DisabledOption disabled = new DisabledOption(false);
	private final IntOption chunkLoadingDistance = new IntOption("chunkLoadingDistance", 16, 2, 32, 1);
	private List<NamedPositions.Entry> cameraPositions = new ArrayList<>();
	private GlobalPos currentCameraPosition;
	private GlobalPos newCameraPosition;
	private boolean activatedByRedstone = false;
	private boolean clientInteracted;
	private boolean switchCamera;

	public FrameBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.FRAME_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (activatedByRedstone) {
			boolean wasPowered = state.getValue(FrameBlock.POWERED);

			if (level.isClientSide && !wasPowered && clientInteracted)
				switchCameras(currentCameraPosition, null, 0, true);
			else if (!level.isClientSide) {
				boolean hasNeighborSignal = level.hasNeighborSignal(pos);

				if (wasPowered && !hasNeighborSignal)
					switchCameras(currentCameraPosition, null, 0, true);

				if (wasPowered != hasNeighborSignal)
					level.setBlockAndUpdate(pos, state.setValue(FrameBlock.POWERED, hasNeighborSignal));
			}
		}

		if (switchCamera) {
			switchCamera = false;
			switchCameras(newCameraPosition, null, 0, true);
		}
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (currentCameraPosition != null) {
			if (!level.isClientSide)
				switchCameras(null, null, 0, false);
			else if (clientInteracted)
				PacketDistributor.sendToServer(new SyncFrame(getBlockPos(), FrameFeedHandler.getFrameFeedViewDistance(this), Optional.empty(), Optional.ofNullable(currentCameraPosition), true));
		}
	}

	@Override
	public void loadAdditional(ValueInput tag) {
		super.loadAdditional(tag);

		TypedInputList<NamedPositions.Entry> cameras = tag.listOrEmpty("cameras", NamedPositions.Entry.CODEC.orElse(NamedPositions.Entry.EMPTY));

		cameraPositions.clear();
		cameras.forEach(entry -> {
			if (entry.equals(NamedPositions.Entry.EMPTY))
				cameraPositions.add(null);
			else
				cameraPositions.add(entry);
		});

		GlobalPos newCameraPos = tag.read("current_camera", GlobalPos.CODEC).orElse(null);

		if ((currentCameraPosition == null && newCameraPos != null) || (currentCameraPosition != null && !currentCameraPosition.equals(newCameraPos))) {
			switchCamera = true;
			newCameraPosition = newCameraPos;
		}

		activatedByRedstone = isModuleEnabled(ModuleType.REDSTONE);
	}

	@Override
	public void saveAdditional(ValueOutput tag) {
		super.saveAdditional(tag);

		TypedOutputList<NamedPositions.Entry> cameras = tag.list("cameras", NamedPositions.Entry.CODEC.orElse(NamedPositions.Entry.EMPTY));

		cameraPositions.forEach(entry -> {
			if (entry == null)
				cameras.add(NamedPositions.Entry.EMPTY);
			else
				cameras.add(entry);
		});

		if (currentCameraPosition != null)
			tag.store("current_camera", GlobalPos.CODEC, currentCameraPosition);
	}

	public boolean applyCameraPositions(ItemStack cameraMonitor) {
		if (cameraMonitor.has(SCContent.BOUND_CAMERAS)) {
			List<NamedPositions.Entry> newCameraPositions = cameraMonitor.get(SCContent.BOUND_CAMERAS).positions();

			if (!cameraPositions.equals(newCameraPositions)) {
				cameraPositions = new ArrayList<>(newCameraPositions);
				setChanged();
				level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
				return true;
			}
		}

		return false;
	}

	public List<NamedPositions.Entry> getCameraPositions() {
		return cameraPositions;
	}

	public void removeCameraOnClient(GlobalPos cameraPos) {
		removeCamera(cameraPos);

		if (cameraPos == currentCameraPosition) {
			FrameFeedHandler.removeFrameLink(currentCameraPosition, this);
			currentCameraPosition = null;
		}

		PacketDistributor.sendToServer(new SyncFrame(getBlockPos(), FrameFeedHandler.getFrameFeedViewDistance(this), Optional.of(cameraPos), Optional.ofNullable(currentCameraPosition), false));
	}

	public void removeCamera(GlobalPos cameraPos) {
		for (int i = 0; i < cameraPositions.size(); i++) {
			NamedPositions.Entry entry = cameraPositions.get(i);

			if (entry != null && entry.globalPos().equals(cameraPos)) {
				cameraPositions.set(i, null);
				break;
			}
		}

		if (cameraPositions.stream().allMatch(Objects::isNull))
			cameraPositions = new ArrayList<>();

		setChanged();
		level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
	}

	public void setCurrentCameraAndUpdate(GlobalPos camera) {
		int requestedRenderDistance = FrameFeedHandler.getFrameFeedViewDistance(this);

		switchCameras(camera, null, requestedRenderDistance, false);
		PacketDistributor.sendToServer(new SyncFrame(getBlockPos(), requestedRenderDistance, Optional.empty(), Optional.ofNullable(currentCameraPosition), false));
	}

	public void switchCameras(GlobalPos newCameraPos, Player player, int requestedRenderDistance, boolean disableNewCamera) {
		GlobalPos previousCameraPos = getCurrentCamera();

		setCurrentCamera(newCameraPos);

		if (!level.isClientSide) {
			if (previousCameraPos != null && level.getBlockEntity(previousCameraPos.pos()) instanceof SecurityCameraBlockEntity previousCamera) {
				if (!previousCameraPos.equals(newCameraPos) || (player == null && disableNewCamera))
					previousCamera.unlinkFrameForAllPlayers(worldPosition);
				else if (disableNewCamera)
					previousCamera.unlinkFrameForPlayer(player.getUUID(), worldPosition);
			}

			if (player instanceof ServerPlayer serverPlayer && !disableNewCamera && newCameraPos != null) {
				if (level.dimension() != newCameraPos.dimension() || !(level.getBlockEntity(newCameraPos.pos()) instanceof SecurityCameraBlockEntity newCamera))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", newCameraPos.pos()), ChatFormatting.RED);
				else if (!newCameraPos.equals(previousCameraPos) || !newCamera.isFrameLinked(player, worldPosition)) {
					if (redstoneSignalDisabled())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:frame.noRedstoneSignal", newCameraPos.pos()), ChatFormatting.RED);
					else
						newCamera.linkFrameForPlayer(serverPlayer, worldPosition, Mth.clamp(requestedRenderDistance, 2, Math.min(getChunkLoadingDistanceOption(), Math.min(ConfigHandler.SERVER.frameFeedViewDistance.get(), serverPlayer.getServer().getPlayerList().getViewDistance()))));
				}
			}

			setChanged();
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
		}
		else {
			if (previousCameraPos != null)
				FrameFeedHandler.removeFrameLink(previousCameraPos, this);

			if (newCameraPos != null && !disableNewCamera) {
				FrameFeedHandler.addFrameLink(this, newCameraPos);
				clientInteracted = true;
			}
			else if (disableNewCamera)
				clientInteracted = false;
		}
	}

	public boolean hasClientInteracted() {
		return clientInteracted;
	}

	public void setCurrentCamera(GlobalPos camera) {
		currentCameraPosition = camera;
		setChanged();
	}

	public GlobalPos getCurrentCamera() {
		return currentCameraPosition;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public int getChunkLoadingDistanceOption() {
		return chunkLoadingDistance.get();
	}

	public boolean redstoneSignalDisabled() {
		return activatedByRedstone && !getBlockState().getValue(FrameBlock.POWERED);
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled, chunkLoadingDistance
		};
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (!level.isClientSide && ((option.getName().equals(disabled.getName()) && disabled.get()) || option.getName().equals(chunkLoadingDistance.getName())))
			switchCameras(null, null, 0, false);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.REDSTONE) {
			activatedByRedstone = true;
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(FrameBlock.POWERED, true)); //This allows the frame to disable properly when a redstone module is inserted while active
			setChanged();
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.ALLOWLIST)
			switchCameras(null, null, 0, false); //Disable the frame feed for all players if the allowlist module is removed, because frames on the server side don't know which players are currently viewing them
		else if (module == ModuleType.REDSTONE) {
			activatedByRedstone = false;
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(FrameBlock.POWERED, false));
			setChanged();
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.REDSTONE
		};
	}

	@Override
	public boolean shouldRender() {
		return true;
	}
}
