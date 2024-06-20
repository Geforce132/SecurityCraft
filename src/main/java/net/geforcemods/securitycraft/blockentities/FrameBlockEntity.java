package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class FrameBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity { //TODO: Changelog entry
	private Option.DisabledOption disabled = new Option.DisabledOption(false);
	private List<NamedPositions.Entry> cameraPositions = new ArrayList<>();
	private GlobalPos currentCamera;
	private boolean activated;

	public FrameBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.OWNABLE_BLOCK_ENTITY.get(), pos, state);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (currentCamera != null && disabled.get())
			CameraController.removeFrameLink(this, currentCamera);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();

		if (currentCamera != null && activated)
			switchCameras(null, null);
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);

		ListTag cameras = tag.getList("cameras", Tag.TAG_COMPOUND);

		for (int i = 0; i < cameras.size(); i++) {
			CompoundTag cameraTag = cameras.getCompound(i);
			cameraPositions.add(cameraTag.isEmpty() ? null : NamedPositions.Entry.CODEC.parse(NbtOps.INSTANCE, cameraTag).getOrThrow());
		}

		if (tag.contains("currentCamera"))
			currentCamera = GlobalPos.CODEC.parse(NbtOps.INSTANCE, tag.get("currentCamera")).getOrThrow();
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		ListTag camerasTag = new ListTag();

		for (NamedPositions.Entry camera : cameraPositions) {
			camerasTag.add(camera == null ? new CompoundTag() : NamedPositions.Entry.CODEC.encodeStart(NbtOps.INSTANCE, camera).getOrThrow());
		}

		tag.put("cameras", camerasTag);

		if (currentCamera != null)
			tag.put("currentCamera", GlobalPos.CODEC.encodeStart(NbtOps.INSTANCE, currentCamera).getOrThrow());
	}

	public void setCameraPositions(ItemStack cameraMonitor) {
		if (cameraMonitor.has(SCContent.BOUND_CAMERAS.get()))
			cameraPositions = new ArrayList<>(cameraMonitor.get(SCContent.BOUND_CAMERAS.get()).positions());
	}

	public List<NamedPositions.Entry> getCameraPositions() {
		return cameraPositions;
	}

	public void removeCameraOnClient(GlobalPos cameraPos) {
		removeCamera(cameraPos);

		if (cameraPos == currentCamera) {
			CameraController.removeFrameLink(this, currentCamera);
			currentCamera = null;
		}

		PacketDistributor.sendToServer(new SyncFrame(getBlockPos(), Optional.of(cameraPos), Optional.ofNullable(currentCamera)));
	}

	public void removeCamera(GlobalPos cameraPos) {
		for (int i = 0; i < cameraPositions.size(); i++) {
			NamedPositions.Entry entry = cameraPositions.get(i);

			if (entry.globalPos().equals(cameraPos)) {
				cameraPositions.set(i, null);
				break;
			}
		}

		if (cameraPositions.stream().allMatch(Objects::isNull))
			cameraPositions = new ArrayList<>();
	}

	public void setCurrentCameraAndUpdate(GlobalPos camera) {
		switchCameras(camera, null);
		PacketDistributor.sendToServer(new SyncFrame(getBlockPos(), Optional.empty(), Optional.ofNullable(currentCamera)));
	}

	public void switchCameras(GlobalPos newCameraPos, Player player) {
		GlobalPos previousCameraPos = getCurrentCamera();
		boolean shouldBeActive = newCameraPos != null;

		if (!isDisabled()) {
			setCurrentCamera(newCameraPos);
			this.activated = shouldBeActive;

			if (!level.isClientSide) {
				if (previousCameraPos != null && level.getBlockEntity(previousCameraPos.pos()) instanceof SecurityCameraBlockEntity previousCamera)
					previousCamera.unlinkFrame(worldPosition);

				if (shouldBeActive && player instanceof ServerPlayer serverPlayer) {
					if (level.dimension() == newCameraPos.dimension() && level.getBlockEntity(newCameraPos.pos()) instanceof SecurityCameraBlockEntity newCamera) {
						if (newCamera.isOwnedBy(player) || newCamera.isAllowed(player))
							newCamera.linkFrameForPlayer(serverPlayer, worldPosition, Mth.clamp(serverPlayer.requestedViewDistance(), 2, serverPlayer.server.getPlayerList().getViewDistance()));
						else
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", newCamera.getOwner().getName()), ChatFormatting.RED);
					}
					else
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", newCameraPos.pos()), ChatFormatting.RED);
				}
			}
			else {
				CameraController.removeFrameLink(this, previousCameraPos);

				if (shouldBeActive)
					CameraController.addFrameLink(this, newCameraPos);
			}
		}
	}

	public boolean isActivated() {
		return activated;
	}

	public void setCurrentCamera(GlobalPos camera) {
		currentCamera = camera;
		setChanged();
	}

	public GlobalPos getCurrentCamera() {
		return currentCamera;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				disabled
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST
		};
	}

	@Override
	public boolean shouldRender() {
		return true;
	}
}
