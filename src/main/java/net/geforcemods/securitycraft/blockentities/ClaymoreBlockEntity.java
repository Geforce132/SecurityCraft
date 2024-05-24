package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class ClaymoreBlockEntity extends CustomizableBlockEntity implements ITickableTileEntity, INamedContainerProvider, IInventoryChangedListener, SingleLensContainer {
	private IntOption range = new IntOption(this::getBlockPos, "range", 5, 1, 10, 1);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);
	private LazyOptional<IItemHandler> insertOnlyHandler, lensHandler;
	private LensContainer lens = new LensContainer(1);
	private int cooldown = -1;

	public ClaymoreBlockEntity() {
		super(SCContent.CLAYMORE_BLOCK_ENTITY.get());
		lens.addListener(this);
	}

	@Override
	public void tick() {
		if (!getLevel().isClientSide) {
			if (getBlockState().getValue(ClaymoreBlock.DEACTIVATED))
				return;

			if (cooldown > 0) {
				cooldown--;
				return;
			}

			if (cooldown == 0) {
				((ClaymoreBlock) getBlockState().getBlock()).explode(level, worldPosition);
				return;
			}

			TargetingMode mode = getTargetingMode();
			Direction dir = getBlockState().getValue(ClaymoreBlock.FACING);
			AxisAlignedBB area = new AxisAlignedBB(worldPosition);

			if (dir == Direction.NORTH)
				area = area.contract(0, 0, range.get());
			else if (dir == Direction.SOUTH)
				area = area.contract(0, 0, -range.get());
			else if (dir == Direction.EAST)
				area = area.contract(-range.get(), 0, 0);
			else if (dir == Direction.WEST)
				area = area.contract(range.get(), 0, 0);

			level.getEntitiesOfClass(LivingEntity.class, area, e -> mode.canAttackEntity(e, this, true)).stream().findFirst().ifPresent(e -> {
				cooldown = 20;
				getLevel().playSound(null, new BlockPos(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundCategory.BLOCKS, 0.3F, 0.6F);
			});
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		tag.putInt("cooldown", cooldown);
		tag.put("lens", lens.createTag());
		return tag;
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		cooldown = tag.getInt("cooldown");
		lens.fromTag(tag.getList("lens", Constants.NBT.TAG_COMPOUND));
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
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
	public void containerChanged(IInventory container) {
		if (level == null)
			return;

		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player) {
		return new SingleLensMenu(id, level, worldPosition, inventory);
	}

	@Override
	public ITextComponent getDisplayName() {
		return super.getDisplayName();
	}

	@Override
	public Inventory getLensContainer() {
		return lens;
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				range, ignoreOwner, targetingMode
		};
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[0];
	}

	@Override
	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	public TargetingMode getTargetingMode() {
		return targetingMode.get();
	}

	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(worldPosition);
	}
}
