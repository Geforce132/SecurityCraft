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
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SecurityCameraBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity, IEMPAffectedBE, MenuProvider, ContainerListener, SingleLensContainer {
	private double cameraRotation = 0.0D;
	private double oCameraRotation = 0.0D;
	private boolean addToRotation = SecurityCraft.RANDOM.nextBoolean();
	private boolean down = false, initialized = false;
	private int playersViewing = 0;
	private boolean shutDown = false;
	private float initialXRotation, initialYRotation;
	private DoubleOption rotationSpeedOption = new DoubleOption("rotationSpeed", 0.018D, 0.01D, 0.025D, 0.001D);
	private DoubleOption movementSpeedOption = new DoubleOption("movementSpeed", 2.0D, 0.0D, 20.0D, 0.1D);
	private BooleanOption shouldRotateOption = new BooleanOption("shouldRotate", true);
	private DoubleOption customRotationOption = new DoubleOption("customRotation", getCameraRotation(), 1.55D, -1.55D, rotationSpeedOption.get());
	private DisabledOption disabled = new DisabledOption(false);
	private IntOption opacity = new IntOption("opacity", 100, 0, 255, 1);
	private LazyOptional<IItemHandler> insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);

	public SecurityCameraBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.SECURITY_CAMERA_BLOCK_ENTITY.get(), pos, state);
		lens.addListener(this);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) {
		if (!initialized) {
			Direction facing = state.getValue(SecurityCameraBlock.FACING);

			initialized = true;
			down = facing == Direction.DOWN;

			if (!isModuleEnabled(ModuleType.SMART))
				setDefaultViewingDirection(facing);
		}

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
	public void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		tag.putBoolean("shutDown", shutDown);
		tag.put("lens", lens.createTag());
		tag.putFloat("initial_x_rotation", initialXRotation);
		tag.putFloat("initial_y_rotation", initialYRotation);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		shutDown = tag.getBoolean("shutDown");
		lens.fromTag(tag.getList("lens", Tag.TAG_COMPOUND));
		initialXRotation = tag.getFloat("initial_x_rotation");
		initialYRotation = tag.getFloat("initial_y_rotation");
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == ForgeCapabilities.ITEM_HANDLER)
			return BlockUtils.isAllowedToExtractFromProtectedObject(side, this) ? getNormalHandler().cast() : getInsertOnlyHandler().cast();
		else
			return super.getCapability(cap, side);
	}

	@Override
	public void invalidateCaps() {
		if (insertOnlyHandler != null)
			insertOnlyHandler.invalidate();

		if (lensHandler != null)
			lensHandler.invalidate();

		super.invalidateCaps();
	}

	@Override
	public void reviveCaps() {
		insertOnlyHandler = null;
		lensHandler = null;
		super.reviveCaps();
	}

	private LazyOptional<IItemHandler> getInsertOnlyHandler() {
		if (insertOnlyHandler == null)
			insertOnlyHandler = LazyOptional.of(() -> new InsertOnlyInvWrapper(lens));

		return insertOnlyHandler;
	}

	private LazyOptional<IItemHandler> getNormalHandler() {
		if (lensHandler == null)
			lensHandler = LazyOptional.of(() -> new InvWrapper(lens));

		return lensHandler;
	}

	@Override
	public void containerChanged(Container container) {
		if (level == null)
			return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
		return new SingleLensMenu(id, level, worldPosition, inventory);
	}

	@Override
	public Component getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public Container getLensContainer() {
		return lens;
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
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.POWERED, false));
		else if (module == ModuleType.SMART)
			setDefaultViewingDirection(getBlockState().getValue(SecurityCameraBlock.FACING));
	}

	@Override
	public <T> void onOptionChanged(Option<T> option) {
		//make players stop viewing the camera when it's disabled
		if (option.getName().equals("disabled") && !level.isClientSide && ((BooleanOption) option).get()) {
			for (ServerPlayer player : ((ServerLevel) level).players()) {
				if (player.getCamera() instanceof SecurityCamera camera && camera.blockPosition().equals(worldPosition))
					camera.stopViewing(player);
			}
		}

		super.onOptionChanged(option);
	}

	@Override
	public void shutDown() {
		BlockState state = level.getBlockState(worldPosition);

		IEMPAffectedBE.super.shutDown();

		if (state.getBlock() == SCContent.SECURITY_CAMERA.get() && state.getValue(SecurityCameraBlock.POWERED))
			level.setBlockAndUpdate(worldPosition, state.setValue(SecurityCameraBlock.POWERED, false));
	}

	@Override
	public boolean isShutDown() {
		return shutDown;
	}

	@Override
	public void setShutDown(boolean shutDown) {
		this.shutDown = shutDown;
	}

	public void startViewing() {
		if (playersViewing++ == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, true));
	}

	public void stopViewing() {
		if (--playersViewing == 0)
			level.setBlockAndUpdate(worldPosition, getBlockState().setValue(SecurityCameraBlock.BEING_VIEWED, false));
	}

	public boolean isDisabled() {
		return disabled.get();
	}

	public double getCameraRotation() {
		return cameraRotation;
	}

	public double getOriginalCameraRotation() {
		return oCameraRotation;
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

	public void setDefaultViewingDirection(Direction facing) {
		setDefaultViewingDirection(down ? 75F : 30F, switch (facing) {
			case NORTH -> 180F;
			case WEST -> 90F;
			case SOUTH -> 0F;
			case EAST -> 270F;
			case DOWN, UP -> 0F;
		});
	}

	public void setDefaultViewingDirection(float initialXRotation, float initialYRotation) {
		this.initialXRotation = initialXRotation;
		this.initialYRotation = initialYRotation;
		setChanged();
	}

	public float getInitialXRotation() {
		return initialXRotation;
	}

	public float getInitialYRotation() {
		return initialYRotation;
	}
}
