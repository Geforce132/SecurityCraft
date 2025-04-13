package net.geforcemods.securitycraft.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;

import com.feed_the_beast.ftblib.lib.util.BlockUtils;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.FrameBlock;
import net.geforcemods.securitycraft.entity.camera.CameraController;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.SyncFrame;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

public class FrameBlockEntity extends CustomizableBlockEntity implements ITickable {
	private final DisabledOption disabled = new DisabledOption(false);
	private final IntOption chunkLoadingDistance = new IntOption(this::getPos, "chunkLoadingDistance", 16, 2, 32, 1);
	private List<Pair<GlobalPos, String>> cameraPositions = new ArrayList<>();
	private GlobalPos currentCameraPosition;
	private GlobalPos newCameraPosition;
	private boolean activatedByRedstone = false;
	private boolean clientInteracted;
	private boolean switchCamera;

	@Override
	public void update() {
		if (activatedByRedstone) {
			boolean wasPowered = getBlockState().getValue(FrameBlock.POWERED);

			if (world.isRemote && !wasPowered && clientInteracted)
				switchCameras(currentCameraPosition, null, 0, true);
			else if (!world.isRemote) {
				boolean hasNeighborSignal = world.isBlockPowered(pos);

				if (wasPowered && !hasNeighborSignal)
					switchCameras(currentCameraPosition, null, 0, true);

				if (wasPowered != hasNeighborSignal)
					world.setBlockState(pos, getBlockState().withProperty(FrameBlock.POWERED, hasNeighborSignal));
			}
		}

		if (switchCamera) {
			switchCamera = false;
			switchCameras(newCameraPosition, null, 0, true);
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();
		removeLinkWithCamera();
	}

	@Override
	public void invalidate() {
		super.invalidate();
		removeLinkWithCamera();
	}

	private void removeLinkWithCamera() {
		if (currentCameraPosition != null) {
			if (!world.isRemote)
				switchCameras(null, null, 0, false);
			else if (clientInteracted)
				SecurityCraft.network.sendToServer(new SyncFrame(pos, CameraController.getFrameFeedViewDistance(this), Optional.empty(), Optional.ofNullable(currentCameraPosition), true));
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);

		if (tag.hasKey("cameras")) {
			NBTTagList cameras = tag.getTagList("cameras", Constants.NBT.TAG_COMPOUND);

			cameraPositions.clear();

			for (int i = 0; i < cameras.tagCount(); i++) {
				NBTTagCompound cameraTag = cameras.getCompoundTagAt(i);
				Pair<GlobalPos, String> cameraEntry;

				if (cameraTag.isEmpty())
					cameraEntry = Pair.of(null, null);
				else {
					GlobalPos cameraPos = GlobalPos.load(cameraTag.getCompoundTag("global_pos"));
					String name = cameraTag.hasKey("name") ? cameraTag.getString("name") : null;

					cameraEntry = Pair.of(cameraPos, name);
				}

				cameraPositions.add(cameraEntry);
			}
		}

		GlobalPos newCameraPos;

		if (tag.hasKey("current_camera"))
			newCameraPos = GlobalPos.load(tag.getCompoundTag("current_camera"));
		else
			newCameraPos = null;

		if ((currentCameraPosition == null && newCameraPos != null) || (currentCameraPosition != null && !currentCameraPosition.equals(newCameraPos))) {
			switchCamera = true;
			newCameraPosition = newCameraPos;
		}

		activatedByRedstone = isModuleEnabled(ModuleType.REDSTONE);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagList camerasTag = new NBTTagList();

		for (Pair<GlobalPos, String> camera : cameraPositions) {
			NBTTagCompound cameraTag = new NBTTagCompound();

			if (camera != null) {
				GlobalPos pos = camera.getLeft();
				String name = camera.getRight();

				if (pos != null)
					cameraTag.setTag("global_pos", pos.save());

				if (name != null && !name.isEmpty())
					cameraTag.setString("name", camera.getRight());
			}

			camerasTag.appendTag(cameraTag);
		}

		tag.setTag("cameras", camerasTag);

		if (currentCameraPosition != null)
			tag.setTag("current_camera", currentCameraPosition.save());

		return tag;
	}

	public boolean applyCameraPositions(ItemStack cameraMonitor) {
		if (!cameraMonitor.hasTagCompound())
			cameraMonitor.setTagCompound(new NBTTagCompound());

		List<Pair<GlobalPos, String>> newCameraPositions = CameraMonitorItem.getCameraPositions(cameraMonitor.getTagCompound());

		if (!cameraPositions.equals(newCameraPositions)) {
			cameraPositions = new ArrayList<>(newCameraPositions);
			markDirty();
			BlockUtils.notifyBlockUpdate(world, pos, getBlockState());
			return true;
		}

		return false;
	}

	public List<Pair<GlobalPos, String>> getCameraPositions() {
		return cameraPositions;
	}

	public void removeCameraOnClient(int camID) {
		Pair<GlobalPos, String> cameraEntry = cameraPositions.get(camID - 1);

		if (cameraEntry != null) {
			GlobalPos cameraPos = cameraEntry.getLeft();

			if (cameraPos != null) {
				removeCamera(cameraPos);

				if (cameraPos == currentCameraPosition) {
					CameraController.removeFrameLink(currentCameraPosition, this);
					currentCameraPosition = null;
				}

				SecurityCraft.network.sendToServer(new SyncFrame(getPos(), CameraController.getFrameFeedViewDistance(this), Optional.of(cameraPos), Optional.ofNullable(currentCameraPosition), false));
			}
		}
	}

	public void removeCamera(GlobalPos cameraPos) {
		for (int i = 0; i < cameraPositions.size(); i++) {
			Pair<GlobalPos, String> entry = cameraPositions.get(i);
			GlobalPos cameraListPos = entry.getLeft();

			if (cameraListPos != null && cameraListPos.equals(cameraPos)) {
				cameraPositions.set(i, null);
				break;
			}
		}

		if (cameraPositions.stream().allMatch(pair -> pair == null || pair.getLeft() == null))
			cameraPositions = new ArrayList<>();

		markDirty();
		BlockUtils.notifyBlockUpdate(world, pos, getBlockState());
	}

	public void setCurrentCameraAndUpdate(GlobalPos camera) {
		int requestedRenderDistance = CameraController.getFrameFeedViewDistance(this);

		switchCameras(camera, null, requestedRenderDistance, false);
		SecurityCraft.network.sendToServer(new SyncFrame(pos, requestedRenderDistance, Optional.empty(), Optional.ofNullable(currentCameraPosition), false));
	}

	public void switchCameras(GlobalPos newCameraPos, EntityPlayer player, int requestedRenderDistance, boolean disableNewCamera) {
 		GlobalPos previousCameraPos = getCurrentCamera();

		setCurrentCamera(newCameraPos);

		if (!world.isRemote) {
			if (previousCameraPos != null) {
				TileEntity te = world.getTileEntity(previousCameraPos.pos());

				if (te instanceof SecurityCameraBlockEntity) {
					SecurityCameraBlockEntity previousCamera = (SecurityCameraBlockEntity) te;

					if (!previousCameraPos.equals(newCameraPos) || (player == null && disableNewCamera))
						previousCamera.unlinkFrameForAllPlayers(pos);
					else if (disableNewCamera)
						previousCamera.unlinkFrameForPlayer(player.getUniqueID(), pos);
				}
			}

			if (player instanceof EntityPlayerMP && !disableNewCamera && newCameraPos != null) {
				TileEntity te = world.getTileEntity(newCameraPos.pos());

				if (world.provider.getDimension() != newCameraPos.dimension() || !(te instanceof SecurityCameraBlockEntity))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", newCameraPos.pos()), TextFormatting.RED);
				else {
					SecurityCameraBlockEntity newCamera = (SecurityCameraBlockEntity) te;

					if ((!newCameraPos.equals(previousCameraPos) || !newCamera.isFrameLinked(player, pos))) {
						if (redstoneSignalDisabled())
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.frame), Utils.localize("messages.securitycraft:frame.noRedstoneSignal", newCameraPos.pos()), TextFormatting.RED);
						else
							newCamera.linkFrameForPlayer((EntityPlayerMP) player, pos, MathHelper.clamp(requestedRenderDistance, 2, Math.min(getChunkLoadingDistanceOption(), Math.min(ConfigHandler.frameFeedViewDistance, ((EntityPlayerMP) player).server.getPlayerList().getViewDistance()))));
					}
				}
			}

			markDirty();
			BlockUtils.notifyBlockUpdate(world, pos, getBlockState());
		}
		else {
			if (previousCameraPos != null)
				CameraController.removeFrameLink(previousCameraPos, this);

			if (newCameraPos != null && !disableNewCamera) {
				CameraController.addFrameLink(this, newCameraPos);
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
		markDirty();
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
		if (!world.isRemote && ((option.getName().equals(disabled.getName()) && disabled.get()) || option.getName().equals(chunkLoadingDistance.getName())))
			switchCameras(null, null, 0, false);
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleInserted(stack, module, toggled);

		if (module == ModuleType.REDSTONE) {
			activatedByRedstone = true;
			world.setBlockState(pos, getBlockState().withProperty(FrameBlock.POWERED, true)); //This allows the frame to disable properly when a redstone module is inserted while active
			markDirty();
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.ALLOWLIST)
			switchCameras(null, null, 0, false); //Disable the frame feed for all players if the allowlist module is removed, because frames on the server side don't know which players are currently viewing them
		else if (module == ModuleType.REDSTONE) {
			activatedByRedstone = false;
			world.setBlockState(pos, getBlockState().withProperty(FrameBlock.POWERED, false));
			markDirty();
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.ALLOWLIST, ModuleType.REDSTONE
		};
	}

	public IBlockState getBlockState() {
		return world.getBlockState(pos);
	}
}
