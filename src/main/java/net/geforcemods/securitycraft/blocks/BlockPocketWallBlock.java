package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.BlockPocketBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.util.IBlockPocket;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPocketWallBlock extends OwnableBlock implements IBlockPocket {
	public static final BooleanProperty SEE_THROUGH = BooleanProperty.create("see_through");
	public static final BooleanProperty SOLID = BooleanProperty.create("solid");

	public BlockPocketWallBlock(AbstractBlock.Properties properties) {
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(SEE_THROUGH, true).setValue(SOLID, false));
	}

	public static boolean causesSuffocation(BlockState state, IBlockReader level, BlockPos pos) {
		return state.getValue(SOLID);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		if (!state.getValue(SOLID) && ctx instanceof EntitySelectionContext) {
			Entity entity = ((EntitySelectionContext) ctx).getEntity();

			if (entity instanceof PlayerEntity) {
				TileEntity te1 = level.getBlockEntity(pos);

				if (te1 instanceof BlockPocketBlockEntity) {
					BlockPocketBlockEntity te = (BlockPocketBlockEntity) te1;

					if (te.isOwnedBy(entity) || te.getManager() == null || te.getManager().isAllowed(entity))
						return VoxelShapes.empty();
				}
			}
		}

		return VoxelShapes.block();
	}

	@Override
	public boolean canCreatureSpawn(BlockState state, IBlockReader level, BlockPos pos, PlacementType type, EntityType<?> entityType) {
		return false;
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity be = level.getBlockEntity(pos);

			if (be instanceof BlockPocketBlockEntity) {
				BlockPocketManagerBlockEntity manager = ((BlockPocketBlockEntity) be).getManager();

				if (manager != null)
					manager.disableMultiblock();
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
		return state.getValue(SEE_THROUGH) && adjacentBlockState.getBlock() == SCContent.BLOCK_POCKET_WALL.get();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(SEE_THROUGH, true);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(SEE_THROUGH, SOLID);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new BlockPocketBlockEntity();
	}
}
