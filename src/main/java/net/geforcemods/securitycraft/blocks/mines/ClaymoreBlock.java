package net.geforcemods.securitycraft.blocks.mines;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ClaymoreBlock extends ExplosiveBlock implements IWaterLoggable {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape NORTH = Stream.of(Block.box(4, 0, 8, 12, 6, 9), Block.box(5, 0, 7, 11, 6, 8), Block.box(6, 6, 8, 10, 7, 9)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape EAST = Stream.of(Block.box(7, 0, 4, 8, 6, 12), Block.box(8, 0, 5, 9, 6, 11), Block.box(7, 6, 6, 8, 7, 10)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SOUTH = Stream.of(Block.box(4, 0, 7, 12, 6, 8), Block.box(5, 0, 8, 11, 6, 9), Block.box(6, 6, 7, 10, 7, 8)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape WEST = Stream.of(Block.box(8, 0, 4, 9, 6, 12), Block.box(7, 0, 5, 8, 6, 11), Block.box(8, 6, 6, 9, 7, 10)).reduce((v1, v2) -> VoxelShapes.join(v1, v2, IBooleanFunction.OR)).get();

	public ClaymoreBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DEACTIVATED, false).setValue(WATERLOGGED, false));
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity be = level.getBlockEntity(pos);

		if (be instanceof ClaymoreBlockEntity && ((ClaymoreBlockEntity) be).isOwnedBy(player)) {
			if (!level.isClientSide)
				NetworkHooks.openGui((ServerPlayerEntity) player, (ClaymoreBlockEntity) be, pos);

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (level.getBlockState(pos.below()).getMaterial() == Material.AIR)
			level.destroyBlock(pos, true);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader level, BlockPos pos) {
		return BlockUtils.isSideSolid(level, pos.below(), Direction.UP);
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
	public boolean removedByPlayer(BlockState state, World level, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
		if (!player.isCreative() && !level.isClientSide && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			ClaymoreBlockEntity claymore = (ClaymoreBlockEntity) level.getBlockEntity(pos);

			level.destroyBlock(pos, false);

			if (claymore.getTargetingMode().allowsPlayers() && (!claymore.isOwnedBy(player) || claymore.ignoresOwner()))
				explode(level, pos);
		}

		return super.removedByPlayer(state, level, pos, player, willHarvest, fluid);
	}

	@Override
	public void wasExploded(World level, BlockPos pos, Explosion explosion) {
		if (!level.isClientSide && level.getBlockState(pos).hasProperty(ClaymoreBlock.DEACTIVATED) && !level.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED)) {
			if (pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(level, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection()).setValue(DEACTIVATED, false).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public boolean activateMine(World level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);

		if (!state.getValue(DEACTIVATED)) {
			level.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World level, BlockPos pos) {
		if (!level.isClientSide) {
			level.destroyBlock(pos, false);
			level.explode((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.5F : 3.5F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public void playerWillDestroy(World level, BlockPos pos, BlockState state, PlayerEntity player) {
		//prevents dropping twice the amount of modules when breaking the block in creative mode
		if (player.isCreative()) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).getInventory().clear();
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public void onRemove(BlockState state, World level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			TileEntity te = level.getBlockEntity(pos);

			if (te instanceof ClaymoreBlockEntity) {
				((ClaymoreBlockEntity) te).dropAllModules();
				InventoryHelper.dropContents(level, pos, ((ClaymoreBlockEntity) te).getLensContainer());
			}
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(FACING)) {
			case NORTH:
				return NORTH;
			case EAST:
				return EAST;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			default:
				return VoxelShapes.block();
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(FACING, DEACTIVATED, WATERLOGGED);
	}

	@Override
	public boolean isActive(World level, BlockPos pos) {
		return !level.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new ClaymoreBlockEntity();
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
