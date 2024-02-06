package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.IEMPAffectedBE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends CustomizableBlockEntity implements IEMPAffectedBE, ITickable, IInventoryChangedListener, SingleLensContainer {
	private static final double CAMERA_SPEED = 0.0180D;
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = true;
	private boolean down = false;
	private boolean shutDown = false;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getPos, "rotationSpeed", CAMERA_SPEED, 0.01D, 0.025D, 0.001D, true);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getPos, "customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get(), true);
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption(this::getPos, "opacity", 100, 0, 255, 1, true);
	private IItemHandler insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);
	private int playersViewing = 0;

	public SecurityCameraBlockEntity() {
		lens.addInventoryChangeListener(this);
	}

	@Override
	public void update() {
		oCameraRotation = getCameraRotation();

		if (!shutDown) {
			if (!shouldRotateOption.get()) {
				cameraRotation = customRotationOption.get();
				return;
			}

			if (addToRotation && getCameraRotation() <= 1.55F)
				cameraRotation = getCameraRotation() + rotationSpeedOption.get();
			else
				addToRotation = false;

			if (!addToRotation && getCameraRotation() >= -1.55F)
				cameraRotation = getCameraRotation() - rotationSpeedOption.get();
			else
				addToRotation = true;
		}
	}

	@Override
	public void shutDown() {
		IBlockState state = world.getBlockState(pos);

		IEMPAffectedBE.super.shutDown();

		if (state.getBlock() == SCContent.securityCamera && state.getValue(SecurityCameraBlock.POWERED)) {
			world.setBlockState(pos, state.withProperty(SecurityCameraBlock.POWERED, false));
			makeEveryoneStopViewingTheCamera();
		}
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	@Override
	public TileEntity getTileEntity() {
		return this;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("ShutDown", shutDown);
		tag.setInteger("PlayersViewing", playersViewing);
		tag.setTag("lens", lens.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		shutDown = tag.getBoolean("ShutDown");
		playersViewing = tag.getInteger("PlayersViewing");
		lens.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("lens")));
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedBlock(facing, this) ? (T) getNormalHandler() : (T) getInsertOnlyHandler();
		else
			return super.getCapability(capability, facing);
	}

	private IItemHandler getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = new InsertOnlyInvWrapper(lens);

		return insertOnlyHandler;
	}

	private IItemHandler getNormalHandler() {
		if (lensHandler == null)
			lensHandler = new InvWrapper(lens);

		return lensHandler;
	}

	@Override
	public void onInventoryChanged(IInventory container) {
		if (world == null)
			return;

		IBlockState state = world.getBlockState(pos);

		world.notifyBlockUpdate(pos, state, state, 2);
	}

	@Override
	public LensContainer getLensContainer() {
		return lens;
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE) {
			IBlockState newState = world.getBlockState(pos).withProperty(SecurityCameraBlock.POWERED, false);

			world.setBlockState(pos, newState);
			world.notifyNeighborsOfStateChange(pos, blockType, false);
			world.notifyNeighborsOfStateChange(pos.offset(newState.getValue(SecurityCameraBlock.FACING).getOpposite()), blockType, false);
		}
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				rotationSpeedOption, shouldRotateOption, customRotationOption, disabled, opacity
		};
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world != null && world.getBlockState(pos).getBlock() instanceof SecurityCameraBlock)
			down = world.getBlockState(pos).getValue(SecurityCameraBlock.FACING) == EnumFacing.DOWN;
	}

	@Override
	public void onOptionChanged(Option<?> option) {
		if (option.getName().equals("disabled") && ((BooleanOption) option).get())
			makeEveryoneStopViewingTheCamera();
	}

	private void makeEveryoneStopViewingTheCamera() {
		if (!world.isRemote) {
			for (EntityPlayer p : ((WorldServer) world).playerEntities) {
				EntityPlayerMP player = (EntityPlayerMP) p;

				if (player.getSpectatingEntity() instanceof SecurityCamera) {
					SecurityCamera camera = (SecurityCamera) player.getSpectatingEntity();

					if (camera.getPosition().equals(pos))
						camera.stopViewing(player);
				}
			}
		}
	}

	public void startViewing() {
		playersViewing++;
		sync();
	}

	public void stopViewing() {
		playersViewing--;
		sync();
	}

	public boolean isSomeoneViewing() {
		return playersViewing > 0;
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public double getOriginalCameraRotation() {
		return oCameraRotation;
	}

	public double getCameraRotation() {
		return cameraRotation;
	}

	public boolean isDown() {
		return down;
	}

	public int getOpacity() {
		return opacity.get();
	}
}
