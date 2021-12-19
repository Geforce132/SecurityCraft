package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Map;
import java.util.function.Predicate;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome.Precipitation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReinforcedLayeredCauldronBlock extends LayeredCauldronBlock implements IReinforcedBlock, EntityBlock {
	private final Block vanillaBlock;

	public ReinforcedLayeredCauldronBlock(Properties properties, Predicate<Precipitation> fillPredicate, Map<Item, CauldronInteraction> interactions, Block vanillaBlock) {
		super(properties, fillPredicate, interactions);
		this.vanillaBlock = vanillaBlock;
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext collisionContext) {
		if (collisionContext instanceof EntityCollisionContext ctx) {
			if (ctx.getEntity() != null) {
				Entity entity = ctx.getEntity();

				if (entity instanceof Player player) {
					if (level.getBlockEntity(pos) instanceof ReinforcedCauldronBlockEntity be && be.isAllowedToInteract(player))
						return SHAPE;
					else
						return Shapes.block();
				}
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
		BlockEntity be = level.getBlockEntity(pos);

		level.setBlockAndUpdate(pos, fillLevel == 0 ? SCContent.REINFORCED_CAULDRON.get().defaultBlockState() : state.setValue(LEVEL, fillLevel));
		level.setBlockEntity(be);
	}

	@Override
	protected void handleEntityOnFireInside(BlockState state, Level level, BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);

		lowerFillLevel(SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, state.getValue(LEVEL)), level, pos);
		level.setBlockEntity(be);
	}

	@Override
	public Block getVanillaBlock() {
		return vanillaBlock;
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState) {
		return defaultBlockState().setValue(LEVEL, vanillaState.getValue(LEVEL));
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ReinforcedCauldronBlockEntity(pos, state);
	}
}
