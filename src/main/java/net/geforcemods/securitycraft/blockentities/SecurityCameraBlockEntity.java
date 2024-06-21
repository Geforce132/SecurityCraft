package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends CustomizableBlockEntity implements IEMPAffectedBE, ITickable, IInventoryChangedListener, SingleLensContainer {
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = SecurityCraft.RANDOM.nextBoolean();
	private boolean down = false, initialized = false;
	private boolean shutDown = false;
	private float initialXRotation, initialYRotation;
	private DoubleOption rotationSpeedOption = new DoubleOption(this::getPos, "rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D);
	private DoubleOption movementSpeedOption = new DoubleOption(this::getPos, "movementSpeed", 2.0D, 0.0D, 20.0D, 0.1D);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption(this::getPos, "customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get());
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption(this::getPos, "opacity", 100, 0, 255, 1);
	private IItemHandler insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);
	private int playersViewing = 0;

	public SecurityCameraBlockEntity() {
		lens.addInventoryChangeListener(this);
	}

	@Override
	public void update() {
		if (!initialized) {
			if (!isModuleEnabled(ModuleType.SMART))
				setDefaultViewingDirection(world.getBlockState(pos).getValue(SecurityCameraBlock.FACING));

			initialized = true;
		}

		oCameraRotation = getCameraRotation();

		if (!shutDown && !disabled.get()) {
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
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("ShutDown", shutDown);
		tag.setInteger("PlayersViewing", playersViewing);
		tag.setTag("lens", lens.getStackInSlot(0).writeToNBT(new NBTTagCompound()));
		tag.setFloat("initial_x_rotation", initialXRotation);
		tag.setFloat("initial_y_rotation", initialYRotation);
		return super.writeToNBT(tag);
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		shutDown = tag.getBoolean("ShutDown");
		playersViewing = tag.getInteger("PlayersViewing");
		lens.setInventorySlotContents(0, new ItemStack(tag.getCompoundTag("lens")));
		initialXRotation = tag.getFloat("initial_x_rotation");
		initialYRotation = tag.getFloat("initial_y_rotation");
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return BlockUtils.isAllowedToExtractFromProtectedObject(facing, this) ? (T) getNormalHandler() : (T) getInsertOnlyHandler();
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
		else if (module == ModuleType.SMART)
			setDefaultViewingDirection(world.getBlockState(pos).getValue(SecurityCameraBlock.FACING));
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST, ModuleType.SMART
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				rotationSpeedOption, shouldRotateOption, customRotationOption, disabled, opacity, movementSpeedOption
		};
	}

	@Override
	public void onLoad() {
		super.onLoad();

		if (world != null) {
			IBlockState state = world.getBlockState(pos);

			if (state.getBlock() instanceof SecurityCameraBlock)
				down = state.getValue(SecurityCameraBlock.FACING) == EnumFacing.DOWN;
		}
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		if (option == disabled && ((BooleanOption) option).get())
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
		markDirty();
		sync();
	}

	public void stopViewing() {
		playersViewing--;
		markDirty();
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

	public double getMovementSpeed() {
		return movementSpeedOption.get();
	}

	public void setDefaultViewingDirection(EnumFacing facing) {
		float yRotation;

		switch (facing) {
			case NORTH:
				yRotation = 180F;
				break;
			case WEST:
				yRotation = 90F;
				break;
			case EAST:
				yRotation = 270F;
				break;
			default:
				yRotation = 0F;
		}

		setDefaultViewingDirection(down ? 75F : 30F, yRotation);
	}

	public void setDefaultViewingDirection(float initialXRotation, float initialYRotation) {
		this.initialXRotation = initialXRotation;
		this.initialYRotation = initialYRotation;
		markDirty();
	}

	public float getInitialXRotation() {
		return initialXRotation;
	}

	public float getInitialYRotation() {
		return initialYRotation;
	}
}
