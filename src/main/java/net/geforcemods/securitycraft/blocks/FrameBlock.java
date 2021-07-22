package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FrameBlock extends OwnableBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape SHAPE_NORTH = Block.box(2, 2, 0, 14, 14, 1);
	private static final VoxelShape SHAPE_EAST = Block.box(15, 2, 2, 16, 14, 14);
	private static final VoxelShape SHAPE_SOUTH = Block.box(2, 2, 15, 14, 14, 16);
	private static final VoxelShape SHAPE_WEST = Block.box(0, 2, 2, 1, 14, 14);

	public FrameBlock(Block.Properties properties){
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		VoxelShape shape = null;

		switch(state.getValue(FACING))
		{
			case NORTH: shape = SHAPE_NORTH; break;
			case EAST: shape = SHAPE_EAST; break;
			case SOUTH: shape = SHAPE_SOUTH; break;
			case WEST: shape = SHAPE_WEST; break;
			default: shape = VoxelShapes.empty();
		}

		return VoxelShapes.joinUnoptimized(VoxelShapes.block(), shape, IBooleanFunction.ONLY_FIRST); //subtract
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		ItemStack stack = player.getItemInHand(hand);

		if (stack.getItem() == SCContent.CAMERA_MONITOR.get()) {
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.FRAME.get().getDescriptionId()), Utils.localize("messages.securitycraft:frame.rightclick"), TextFormatting.RED);
			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection().getOpposite());
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot)
	{
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
