package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedHopperBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HopperBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;

public class ReinforcedHopperBlock extends HopperBlock implements IReinforcedBlock, IOverlayDisplay, IDisguisable {
	private final float destroyTimeForOwner;

	public ReinforcedHopperBlock(BlockBehaviour.Properties properties) {
		super(OwnableBlock.withReinforcedDestroyTime(properties));
		destroyTimeForOwner = OwnableBlock.getStoredDestroyTime();
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);
		Block disguisedBlock = disguisedState.getBlock();

		if (disguisedBlock != state.getBlock())
			return disguisedBlock.getDestroyProgress(disguisedState, player, level, pos);
		else
			return BlockUtils.getDestroyProgress(super::getDestroyProgress, destroyTimeForOwner, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, BlockGetter level, BlockPos pos, Player player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof Player player)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, player));

		super.setPlacedBy(level, pos, state, placer, stack);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		//only allow the owner or players on the allowlist to access a reinforced hopper
		if (!level.isClientSide && level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be && (be.isOwnedBy(player) || be.isAllowed(player)))
			player.openMenu(be);

		return InteractionResult.SUCCESS;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be) {
			if (isMoving)
				be.clearContent(); //Clear the items from the block before it is moved by a piston to prevent duplication

			level.updateNeighbourForOutputSignal(pos, this);
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
		if (level.getBlockEntity(pos) instanceof ReinforcedHopperBlockEntity be)
			HopperBlockEntity.entityInside(level, pos, state, entity, be);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedHopperBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return level.isClientSide ? null : createTickerHelper(type, SCContent.REINFORCED_HOPPER_BLOCK_ENTITY.get(), ReinforcedHopperBlockEntity::pushItemsTick);
	}

	@Override
	public Block getVanillaBlock() {
		return Blocks.HOPPER;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShape(level, pos, ctx);
		else
			return super.getShape(state, level, pos, ctx);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightEmission(level, pos);
		else
			return super.getLightEmission(state, level, pos);
	}

	@Override
	public SoundType getSoundType(BlockState state, LevelReader level, BlockPos pos, Entity entity) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getSoundType(level, pos, entity);
		else
			return super.getSoundType(state, level, pos, entity);
	}

	@Override
	public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getShadeBrightness(level, pos);
		else
			return super.getShadeBrightness(state, level, pos);
	}

	@Override
	public int getLightBlock(BlockState state, BlockGetter level, BlockPos pos) {
		BlockState disguisedState = IDisguisable.getDisguisedBlockState(level.getBlockEntity(pos)).orElse(state);

		if (disguisedState.getBlock() != this)
			return disguisedState.getLightBlock(level, pos);
		else
			return super.getLightBlock(state, level, pos);
	}

	@Override
	public ItemStack getDisplayStack(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos);
	}

	@Override
	public boolean shouldShowSCInfo(Level level, BlockState state, BlockPos pos) {
		return getDisguisedStack(level, pos).getItem() == asItem();
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		if (IDisguisable.shouldPickBlockDisguise(level, pos, player))
			return getDisguisedStack(level, pos);

		return super.getCloneItemStack(state, target, level, pos, player);
	}

	public static class ExtractionBlock implements IExtractionBlock {
		@Override
		public boolean canExtract(IOwnable ownable, Level level, BlockPos pos, BlockState state) {
			ReinforcedHopperBlockEntity hopperBe = (ReinforcedHopperBlockEntity) level.getBlockEntity(pos);

			if (!hopperBe.getOwner().isValidated())
				return false;
			else if (!ownable.getOwner().owns(hopperBe)) {
				if (ownable instanceof IModuleInventory inv)
					return inv.isAllowed(hopperBe.getOwner().getName()); //hoppers can extract out of e.g. chests if the hopper's owner is on the chest's allowlist module

				return false;
			}
			else
				return true;
		}

		@Override
		public Block getBlock() {
			return SCContent.REINFORCED_HOPPER.get();
		}
	}
}
