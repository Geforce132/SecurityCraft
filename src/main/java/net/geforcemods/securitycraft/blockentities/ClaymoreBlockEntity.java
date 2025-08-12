package net.geforcemods.securitycraft.blockentities;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Option.RespectInvisibilityOption;
import net.geforcemods.securitycraft.api.Option.TargetingModeOption;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.inventory.InsertOnlyInvWrapper;
import net.geforcemods.securitycraft.inventory.LensContainer;
import net.geforcemods.securitycraft.inventory.SingleLensMenu;
import net.geforcemods.securitycraft.inventory.SingleLensMenu.SingleLensContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.misc.TargetingMode;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ITickingBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.wrapper.InvWrapper;

public class ClaymoreBlockEntity extends CustomizableBlockEntity implements ITickingBlockEntity, MenuProvider, ContainerListener, SingleLensContainer {
	private IntOption range = new IntOption("range", 5, 1, 10, 1);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private TargetingModeOption targetingMode = new TargetingModeOption(TargetingMode.PLAYERS_AND_MOBS);
	private RespectInvisibilityOption respectInvisibility = new RespectInvisibilityOption();
	private LensContainer lens = new LensContainer(1);
	private int cooldown = -1;

	public ClaymoreBlockEntity(BlockPos pos, BlockState state) {
		super(SCContent.CLAYMORE_BLOCK_ENTITY.get(), pos, state);
		lens.addListener(this);
	}

	@Override
	public void tick(Level level, BlockPos pos, BlockState state) { //server only as per ClaymoreBlock
		if (state.getValue(ClaymoreBlock.DEACTIVATED))
			return;

		if (cooldown > 0) {
			cooldown--;
			return;
		}

		if (cooldown == 0) {
			((ClaymoreBlock) state.getBlock()).explode(level, pos);
			return;
		}

		TargetingMode mode = getTargetingMode();
		Direction dir = state.getValue(ClaymoreBlock.FACING);
		AABB area = switch (dir) {
			case NORTH -> new AABB(pos).contract(0, 0, range.get());
			case SOUTH -> new AABB(pos).contract(0, 0, -range.get());
			case EAST -> new AABB(pos).contract(-range.get(), 0, 0);
			case WEST -> new AABB(pos).contract(range.get(), 0, 0);
			default -> new AABB(pos);
		};

		level.getEntitiesOfClass(LivingEntity.class, area, e -> mode.canAttackEntity(e, this, respectInvisibility::isConsideredInvisible)).stream().findFirst().ifPresent(e -> {
			cooldown = 20;
			level.playSound(null, BlockPos.containing(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D), SoundEvents.LEVER_CLICK, SoundSource.BLOCKS, 0.3F, 0.6F);
		});
	}

	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.saveAdditional(tag, lookupProvider);
		tag.putInt("cooldown", cooldown);
		tag.put("lens", lens.createTag(lookupProvider));
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
		super.loadAdditional(tag, lookupProvider);
		cooldown = tag.getIntOr("cooldown", -1);
		tag.getList("lens").ifPresent(inv -> lens.fromTag(inv, lookupProvider));
	}

	@Override
	public void preRemoveSideEffects(BlockPos pos, BlockState state) {
		if (level != null)
			Containers.dropContents(level, pos, getLensContainer());

		super.preRemoveSideEffects(pos, state);
	}

	public static IItemHandler getCapability(ClaymoreBlockEntity be, Direction side) {
		return BlockUtils.isAllowedToExtractFromProtectedObject(side, be) ? new InvWrapper(be.lens) : new InsertOnlyInvWrapper(be.lens);
	}

	@Override
	public void writeClientSideData(AbstractContainerMenu menu, RegistryFriendlyByteBuf buffer) {
		MenuProvider.super.writeClientSideData(menu, buffer);
		buffer.writeBlockPos(worldPosition);
	}

	@Override
	public void containerChanged(Container container) {
		if (level == null)
			return;

		setChanged();
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
	public Option<?>[] customOptions() {
		return new Option[] {
				range, ignoreOwner, targetingMode, respectInvisibility
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
}
