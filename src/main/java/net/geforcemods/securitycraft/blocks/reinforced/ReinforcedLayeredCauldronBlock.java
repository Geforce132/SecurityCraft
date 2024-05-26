package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedLayeredCauldronBlock extends LayeredCauldronBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;

	public ReinforcedLayeredCauldronBlock(BlockBehaviour.Properties properties, Predicate<Precipitation> fillPredicate, Map<Item, CauldronInteraction> interactions, Block vanillaBlock) {
		super(properties, fillPredicate, interactions);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		if (collisionContext instanceof EntityCollisionContext ctx && ctx.getEntity() != null) {
			Entity entity = ctx.getEntity();

			if (entity instanceof Player player) {
				if (level.getBlockEntity(pos) instanceof ReinforcedCauldronBlockEntity be && be.isAllowedToInteract(player))
					return SHAPE;
				else
					return Shapes.block();
			}
		}

		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (level.getBlockEntity(pos) instanceof ReinforcedCauldronBlockEntity be && be.isAllowedToInteract(player))
			return super.use(state, level, pos, player, hand, hit);

		return InteractionResult.PASS;
	}

	public static void lowerFillLevel(BlockState state, Level level, BlockPos pos) {
		int fillLevel = state.getValue(LEVEL) - 1;
		BlockState newState = fillLevel == 0 ? SCContent.REINFORCED_CAULDRON.get().defaultBlockState() : state.setValue(LEVEL, fillLevel);

		ReinforcedCauldronBlock.updateBlockState(level, pos, newState);
		level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newState));
	}

	@Override
	protected void handleEntityOnFireInside(BlockState state, Level level, BlockPos pos) {
		lowerFillLevel(state, level, pos);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedCauldronBlockEntity(pos, state);
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
		return new ItemStack(SCContent.REINFORCED_CAULDRON.get());
	}
}
