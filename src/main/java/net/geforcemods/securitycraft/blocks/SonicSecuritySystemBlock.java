package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class SonicSecuritySystemBlock extends OwnableBlock {

	private static final VoxelShape SHAPE = Stream.of(
			Block.makeCuboidShape(5.5, 11, 5.5, 10.5, 16, 10.5),
			Block.makeCuboidShape(7.5, 13, 7.5, 8.5, 14, 9.5),
			Block.makeCuboidShape(7.5, 2, 7.5, 8.5, 13, 8.5),
			Block.makeCuboidShape(7, 1, 7, 9, 2, 9),
			Block.makeCuboidShape(6.5, 0, 6.5, 9.5, 1, 9.5)
			).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.fullCube());

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public SonicSecuritySystemBlock(Properties properties)
	{
		super(properties);

		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH));
	}

	public static boolean isNormalCube(BlockState state, IBlockReader reader, BlockPos pos) {
		return false;
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext context)
	{
		return SHAPE;
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state)
	{
		// TODO have the dropped item retain the linked blocks
		return new ItemStack(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new SonicSecuritySystemTileEntity();
	}

}
