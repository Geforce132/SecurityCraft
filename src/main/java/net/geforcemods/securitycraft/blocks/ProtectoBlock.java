package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.tileentity.ProtectoTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ProtectoBlock extends OwnableBlock {

	public static final BooleanProperty ACTIVATED = BlockStateProperties.ENABLED;
	public static final VoxelShape SHAPE = VoxelShapes.or(Block.box(0, 0, 5, 16, 16, 11), Block.box(5, 0, 0, 11, 16, 16));

	public ProtectoBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(ACTIVATED, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		return SHAPE;
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos){
		return world.getBlockState(pos.below()).isFaceSturdy(world, pos.below(), Direction.UP);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return defaultBlockState().setValue(ACTIVATED, false);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(ACTIVATED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ProtectoTileEntity().attacks(LivingEntity.class, 10, 200);
	}

}
