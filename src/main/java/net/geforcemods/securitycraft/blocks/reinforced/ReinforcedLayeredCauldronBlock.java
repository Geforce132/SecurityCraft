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
	public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext collisionContext) {
		if(collisionContext instanceof EntityCollisionContext ctx) {
			if(ctx.getEntity().isPresent()) {
				Entity entity = ctx.getEntity().get();

				if(entity instanceof Player player) {
					BlockEntity tile = world.getBlockEntity(pos);

					if(tile instanceof ReinforcedCauldronBlockEntity te && te.isAllowedToInteract(player))
						return SHAPE;
					else
						return Shapes.block();
				}
			}
		}

		return SHAPE;
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntity tile = world.getBlockEntity(pos);

		if(tile instanceof ReinforcedCauldronBlockEntity te && te.isAllowedToInteract(player))
			return super.use(state, world, pos, player, hand, hit);

		return InteractionResult.PASS;
	}

	public static void lowerFillLevel(BlockState state, Level world, BlockPos pos) {
		int level = state.getValue(LEVEL) - 1;
		BlockEntity te = world.getBlockEntity(pos);

		world.setBlockAndUpdate(pos, level == 0 ? SCContent.REINFORCED_CAULDRON.get().defaultBlockState() : state.setValue(LEVEL, level));
		world.setBlockEntity(te);
	}

	@Override
	protected void handleEntityOnFireInside(BlockState state, Level world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);

		lowerFillLevel(SCContent.REINFORCED_WATER_CAULDRON.get().defaultBlockState().setValue(LEVEL, state.getValue(LEVEL)), world, pos);
		world.setBlockEntity(te);
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
