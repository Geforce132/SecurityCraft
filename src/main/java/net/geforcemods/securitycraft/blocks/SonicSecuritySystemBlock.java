package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

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

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(world.isRemote)
			ClientHandler.displaySonicSecuritySystemGui((SonicSecuritySystemTileEntity) world.getTileEntity(pos));

		return ActionResultType.SUCCESS;
	}

	@Override
	public int getWeakPower(BlockState state, IBlockReader world, BlockPos pos, Direction side){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof SonicSecuritySystemTileEntity)
			return ((SonicSecuritySystemTileEntity)te).shouldEmitPower ? 15 : 0;

		return 0;
	}

	@Override
	public int getStrongPower(BlockState state, IBlockReader world, BlockPos pos, Direction side){
		TileEntity te = world.getTileEntity(pos);

		if(te instanceof SonicSecuritySystemTileEntity)
			return ((SonicSecuritySystemTileEntity)te).shouldEmitPower ? 15 : 0;

		return 0;
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
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
	{
		return getItemStackFromBlock(world.getTileEntity(pos).getUpdateTag());
	}

	private ItemStack getItemStackFromBlock(CompoundNBT blockTag)
	{
		ItemStack stack = new ItemStack(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if(!blockTag.contains("LinkedBlocks"))
			return stack;

		stack.setTag(new CompoundNBT());
		stack.getTag().put("LinkedBlocks", blockTag.getList("LinkedBlocks", Constants.NBT.TAG_COMPOUND));

		return stack;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new SonicSecuritySystemTileEntity();
	}

}
