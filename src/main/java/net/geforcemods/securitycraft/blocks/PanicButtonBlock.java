package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.AttachFace;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class PanicButtonBlock extends AbstractButtonBlock implements IWaterLoggable {
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape FLOOR_NS_POWERED = Block.box(3, 0, 5, 13, 1, 11);
	private static final VoxelShape FLOOR_NS_UNPOWERED = Block.box(3, 0, 5, 13, 2, 11);
	private static final VoxelShape FLOOR_EW_POWERED = Block.box(5, 0, 3, 11, 1, 13);
	private static final VoxelShape FLOOR_EW_UNPOWERED = Block.box(5, 0, 3, 11, 2, 13);
	private static final VoxelShape WALL_N_POWERED = Block.box(3, 5, 15, 13, 11, 16);
	private static final VoxelShape WALL_N_UNPOWERED = Block.box(3, 5, 14, 13, 11, 16);
	private static final VoxelShape WALL_S_POWERED = Block.box(3, 5, 1, 13, 11, 0);
	private static final VoxelShape WALL_S_UNPOWERED = Block.box(3, 5, 2, 13, 11, 0);
	private static final VoxelShape WALL_E_POWERED = Block.box(1, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_E_UNPOWERED = Block.box(2, 5, 3, 0, 11, 13);
	private static final VoxelShape WALL_W_POWERED = Block.box(15, 5, 3, 16, 11, 13);
	private static final VoxelShape WALL_W_UNPOWERED = Block.box(14, 5, 3, 16, 11, 13);
	private static final VoxelShape CEILING_NS_POWERED = Block.box(3, 15, 5, 13, 16, 11);
	private static final VoxelShape CEILING_NS_UNPOWERED = Block.box(3, 14, 5, 13, 16, 11);
	private static final VoxelShape CEILING_EW_POWERED = Block.box(5, 15, 3, 11, 16, 13);
	private static final VoxelShape CEILING_EW_UNPOWERED = Block.box(5, 14, 3, 11, 16, 13);

	public PanicButtonBlock(boolean isWooden, AbstractBlock.Properties properties) {
		super(isWooden, properties);
		registerDefaultState(defaultBlockState().setValue(WATERLOGGED, false));
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return BlockUtils.getDestroyProgress(super::getDestroyProgress, state, player, level, pos);
	}

	@Override
	public boolean canHarvestBlock(BlockState state, IBlockReader level, BlockPos pos, PlayerEntity player) {
		return ConfigHandler.SERVER.alwaysDrop.get() || super.canHarvestBlock(state, level, pos, player);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		BlockState superState = super.getStateForPlacement(ctx);

		if (superState != null)
			return superState.setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);

		return null;
	}

	@Override
	public void setPlacedBy(World level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(level, pos, (PlayerEntity) placer));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		boolean newPowered = !state.getValue(POWERED);

		level.setBlockAndUpdate(pos, state.setValue(POWERED, newPowered));
		playSound(player, level, pos, newPowered);

		if (state.getValue(FACE) == AttachFace.WALL)
			notifyNeighbors(level, pos, state.getValue(FACING));
		else if (state.getValue(FACE) == AttachFace.CEILING)
			notifyNeighbors(level, pos, Direction.DOWN);
		else if (state.getValue(FACE) == AttachFace.FLOOR)
			notifyNeighbors(level, pos, Direction.UP);

		return ActionResultType.SUCCESS;
	}

	private void notifyNeighbors(World level, BlockPos pos, Direction facing) {
		level.updateNeighborsAt(pos, this);
		level.updateNeighborsAt(pos.relative(facing.getOpposite()), this);
	}

	@Override
	public boolean triggerEvent(BlockState state, World level, BlockPos pos, int id, int param) {
		TileEntity be = level.getBlockEntity(pos);

		return be != null && be.triggerEvent(id, param);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld level, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(FACE)) {
			case FLOOR:
				switch (state.getValue(FACING)) {
					case NORTH:
					case SOUTH:
						if (state.getValue(POWERED))
							return FLOOR_NS_POWERED;
						else
							return FLOOR_NS_UNPOWERED;
					case EAST:
					case WEST:
						if (state.getValue(POWERED))
							return FLOOR_EW_POWERED;
						else
							return FLOOR_EW_UNPOWERED;
					default:
						break;
				}
				break;
			case WALL:
				switch (state.getValue(FACING)) {
					case NORTH:
						if (state.getValue(POWERED))
							return WALL_N_POWERED;
						else
							return WALL_N_UNPOWERED;
					case SOUTH:
						if (state.getValue(POWERED))
							return WALL_S_POWERED;
						else
							return WALL_S_UNPOWERED;
					case EAST:
						if (state.getValue(POWERED))
							return WALL_E_POWERED;
						else
							return WALL_E_UNPOWERED;
					case WEST:
						if (state.getValue(POWERED))
							return WALL_W_POWERED;
						else
							return WALL_W_UNPOWERED;
					default:
						break;
				}
				break;
			case CEILING:
				switch (state.getValue(FACING)) {
					case NORTH:
					case SOUTH:
						if (state.getValue(POWERED))
							return CEILING_NS_POWERED;
						else
							return CEILING_NS_UNPOWERED;
					case EAST:
					case WEST:
						if (state.getValue(POWERED))
							return CEILING_EW_POWERED;
						else
							return CEILING_EW_UNPOWERED;
					default:
						break;
				}
		}

		return VoxelShapes.block();
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		return VoxelShapes.empty();
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new OwnableBlockEntity();
	}

	@Override
	protected SoundEvent getSound(boolean turningOn) {
		return turningOn ? SoundEvents.STONE_BUTTON_CLICK_ON : SoundEvents.STONE_BUTTON_CLICK_OFF;
	}

	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(WATERLOGGED);
	}
}
