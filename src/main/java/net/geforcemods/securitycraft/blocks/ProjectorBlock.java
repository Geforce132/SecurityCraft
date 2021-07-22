package net.geforcemods.securitycraft.blocks;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkHooks;

public class ProjectorBlock extends DisguisableBlock {

	private static final Style GRAY_STYLE = Style.EMPTY.withColor(TextFormatting.GRAY);
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	private static final VoxelShape NORTH = Stream.of(Block.box(3, 5, 0.9, 6, 8, 1.9), Block.box(0, 3, 1, 16, 10, 16), Block.box(2, 8, 0.5, 7, 9, 1), Block.box(2, 4, 0.5, 7, 5, 1), Block.box(6, 5, 0.5, 7, 8, 1), Block.box(2, 5, 0.5, 3, 8, 1), Block.box(0, 0, 1, 2, 3, 3), Block.box(14, 0, 1, 16, 3, 3), Block.box(14, 0, 14, 16, 3, 16), Block.box(0, 0, 14, 2, 3, 16)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape SOUTH = Stream.of(Block.box(0, 3, 0, 16, 10, 15), Block.box(10, 5, 14.1, 13, 8, 15.100000000000001), Block.box(9, 8, 15, 14, 9, 15.5), Block.box(9, 4, 15, 14, 5, 15.5), Block.box(9, 5, 15, 10, 8, 15.5), Block.box(13, 5, 15, 14, 8, 15.5), Block.box(14, 0, 13, 16, 3, 15), Block.box(0, 0, 13, 2, 3, 15), Block.box(0, 0, 0, 2, 3, 2), Block.box(14, 0, 0, 16, 3, 2)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape WEST = Stream.of(Block.box(0.5, 5, 13, 1, 8, 14), Block.box(0.5, 5, 9, 1, 8, 10), Block.box(0.5, 4, 9, 1, 5, 14), Block.box(0.5, 8, 9, 1, 9, 14), Block.box(0.75, 5, 10, 1.75, 8, 13), Block.box(1, 0, 14, 3, 3, 16), Block.box(14, 0, 14, 16, 3, 16), Block.box(14, 0, 0, 16, 3, 2), Block.box(1, 0, 0, 3, 3, 2), Block.box(1, 3, 0, 16, 10, 16)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());
	private static final VoxelShape EAST = Stream.of(Block.box(15, 5, 2, 15.5, 8, 3), Block.box(15, 5, 6, 15.5, 8, 7), Block.box(15, 4, 2, 15.5, 5, 7), Block.box(15, 8, 2, 15.5, 9, 7), Block.box(14.25, 5, 3, 15.25, 8, 6), Block.box(13, 0, 0, 15, 3, 2), Block.box(0, 0, 0, 2, 3, 2), Block.box(0, 0, 14, 2, 3, 16), Block.box(13, 0, 14, 15, 3, 16), Block.box(0, 3, 0, 15, 10, 16)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).orElse(VoxelShapes.block());

	public ProjectorBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx)
	{
		BlockState disguisedState = getDisguisedStateOrDefault(state, world, pos);

		if(disguisedState.getBlock() != this)
			return disguisedState.getShape(world, pos, ctx);
		else
		{
			switch(state.getValue(FACING))
			{
				case NORTH:
					return SOUTH;
				case EAST:
					return WEST;
				case SOUTH:
					return NORTH;
				case WEST:
					return EAST;
				default: return VoxelShapes.block();
			}
		}
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		TileEntity te = world.getBlockEntity(pos);

		if(!(te instanceof ProjectorTileEntity))
			return ActionResultType.FAIL;

		boolean isOwner = ((IOwnable)te).getOwner().isOwner(player);

		if(!world.isClientSide && isOwner)
			NetworkHooks.openGui((ServerPlayerEntity)player, (INamedContainerProvider) te, pos);

		return isOwner ? ActionResultType.SUCCESS : ActionResultType.FAIL;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
	{
		TileEntity te = world.getBlockEntity(pos);

		if(te instanceof ProjectorTileEntity)
		{
			// Drop the block being projected
			ItemEntity item = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), ((ProjectorTileEntity)te).getStackInSlot(36));
			WorldUtils.addScheduledTask(world, () -> world.addFreshEntity(item));
		}

		super.onRemove(state, world, pos, newState, isMoving);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		if(!world.isClientSide)
		{
			TileEntity tile = world.getBlockEntity(pos);

			if(tile instanceof ProjectorTileEntity)
			{
				ProjectorTileEntity te =  (ProjectorTileEntity)tile;

				if(te.isActivatedByRedstone())
				{
					te.setActive(world.hasNeighborSignal(pos));
					te.sync();
				}
			}
		}
	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand)
	{
		if(!world.hasNeighborSignal(pos))
		{
			TileEntity tile = world.getBlockEntity(pos);

			if(tile instanceof ProjectorTileEntity)
			{
				ProjectorTileEntity te =  (ProjectorTileEntity)tile;

				if(te.isActivatedByRedstone())
					te.setActive(false);
			}
		}
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
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ProjectorTileEntity();
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

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag flag)
	{
		tooltip.add(new TranslationTextComponent("tooltip.securitycraft:projector").setStyle(GRAY_STYLE));
	}
}
