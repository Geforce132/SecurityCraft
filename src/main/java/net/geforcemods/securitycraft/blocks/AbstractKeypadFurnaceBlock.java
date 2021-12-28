package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordConvertible;
import net.geforcemods.securitycraft.tileentity.AbstractKeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
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
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractKeypadFurnaceBlock extends DisguisableBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	private static final VoxelShape NORTH_OPEN = Stream.of(Block.makeCuboidShape(11, 1, 1, 12, 2, 2), Block.makeCuboidShape(0, 0, 2, 16, 16, 16), Block.makeCuboidShape(4, 1, 0, 12, 2, 1), Block.makeCuboidShape(4, 1, 1, 5, 2, 2)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape NORTH_CLOSED = Stream.of(Block.makeCuboidShape(4, 14, 1, 5, 15, 2), Block.makeCuboidShape(11, 14, 1, 12, 15, 2), Block.makeCuboidShape(0, 0, 2, 16, 16, 16), Block.makeCuboidShape(4, 14, 0, 12, 15, 1)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape EAST_OPEN = Stream.of(Block.makeCuboidShape(14, 1, 11, 15, 2, 12), Block.makeCuboidShape(0, 0, 0, 14, 16, 16), Block.makeCuboidShape(15, 1, 4, 16, 2, 12), Block.makeCuboidShape(14, 1, 4, 15, 2, 5)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape EAST_CLOSED = Stream.of(Block.makeCuboidShape(14, 14, 4, 15, 15, 5), Block.makeCuboidShape(14, 14, 11, 15, 15, 12), Block.makeCuboidShape(0, 0, 0, 14, 16, 16), Block.makeCuboidShape(15, 14, 4, 16, 15, 12)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SOUTH_OPEN = Stream.of(Block.makeCuboidShape(4, 1, 14, 5, 2, 15), Block.makeCuboidShape(0, 0, 0, 16, 16, 14), Block.makeCuboidShape(4, 1, 15, 12, 2, 16), Block.makeCuboidShape(11, 1, 14, 12, 2, 15)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape SOUTH_CLOSED = Stream.of(Block.makeCuboidShape(11, 14, 14, 12, 15, 15), Block.makeCuboidShape(4, 14, 14, 5, 15, 15), Block.makeCuboidShape(0, 0, 0, 16, 16, 14), Block.makeCuboidShape(4, 14, 15, 12, 15, 16)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape WEST_OPEN = Stream.of(Block.makeCuboidShape(1, 1, 4, 2, 2, 5), Block.makeCuboidShape(2, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 1, 4, 1, 2, 12), Block.makeCuboidShape(1, 1, 11, 2, 2, 12)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape WEST_CLOSED = Stream.of(Block.makeCuboidShape(1, 14, 11, 2, 15, 12), Block.makeCuboidShape(1, 14, 4, 2, 15, 5), Block.makeCuboidShape(2, 0, 0, 16, 16, 16), Block.makeCuboidShape(0, 14, 4, 1, 15, 12)).reduce((v1, v2) -> VoxelShapes.combine(v1, v2, IBooleanFunction.OR)).get();
	private static final VoxelShape NORTH_COLLISION = Block.makeCuboidShape(0, 0, 2, 16, 16, 16);
	private static final VoxelShape EAST_COLLISION = Block.makeCuboidShape(0, 0, 0, 14, 16, 16);
	private static final VoxelShape SOUTH_COLLISION = Block.makeCuboidShape(0, 0, 0, 16, 16, 14);
	private static final VoxelShape WEST_COLLISION = Block.makeCuboidShape(2, 0, 0, 16, 16, 16);

	public AbstractKeypadFurnaceBlock(Block.Properties properties) {
		super(properties);
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, false).with(LIT, false));
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
		BlockState extendedState = getExtendedState(state, world, pos);

		if (extendedState.getBlock() != this)
			return extendedState.getShape(world, pos, ctx);

		switch (state.get(FACING)) {
			case NORTH:
				if (state.get(OPEN))
					return NORTH_OPEN;
				else
					return NORTH_CLOSED;
			case EAST:
				if (state.get(OPEN))
					return EAST_OPEN;
				else
					return EAST_CLOSED;
			case SOUTH:
				if (state.get(OPEN))
					return SOUTH_OPEN;
				else
					return SOUTH_CLOSED;
			case WEST:
				if (state.get(OPEN))
					return WEST_OPEN;
				else
					return WEST_CLOSED;
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		BlockState extendedState = getExtendedState(state, level, pos);

		if (extendedState.getBlock() != this)
			return extendedState.getShape(level, pos, ctx);

		switch (state.get(FACING)) {
			case NORTH:
				return NORTH_COLLISION;
			case EAST:
				return EAST_COLLISION;
			case SOUTH:
				return SOUTH_COLLISION;
			case WEST:
				return WEST_COLLISION;
			default:
				return VoxelShapes.fullCube();
		}
	}

	@Override
	public int getLightValue(BlockState state) {
		return state.get(LIT) ? 13 : 0;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!(newState.getBlock() instanceof AbstractKeypadFurnaceBlock)) {
			TileEntity tileentity = world.getTileEntity(pos);

			if (tileentity instanceof IInventory) {
				InventoryHelper.dropInventoryItems(world, pos, (IInventory) tileentity);
				world.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!world.isRemote) {
			AbstractKeypadFurnaceTileEntity te = (AbstractKeypadFurnaceTileEntity) world.getTileEntity(pos);

			if (ModuleUtils.isDenied(te, player)) {
				if (te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
			}
			else if (ModuleUtils.isAllowed(te, player)) {
				if (te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(state, world, pos, player);
			}
			else if (!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		if (!state.get(AbstractKeypadFurnaceBlock.OPEN))
			world.setBlockState(pos, state.with(AbstractKeypadFurnaceBlock.OPEN, true));

		if (player instanceof ServerPlayerEntity) {
			TileEntity te = world.getTileEntity(pos);

			if (te instanceof INamedContainerProvider) {
				world.playEvent((PlayerEntity) null, 1006, pos, 0);
				NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, pos);
			}
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer) {
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(OPEN, false).with(LIT, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, OPEN, LIT);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.toRotation(state.get(FACING)));
	}

	public class Convertible implements IPasswordConvertible {
		private final Block originalBlock;

		public Convertible(Block originalBlock) {
			this.originalBlock = originalBlock;
		}

		@Override
		public Block getOriginalBlock() {
			return originalBlock;
		}

		@Override
		public boolean convert(PlayerEntity player, World world, BlockPos pos) {
			BlockState state = world.getBlockState(pos);
			Direction facing = state.get(FACING);
			boolean lit = state.get(LIT);
			AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) world.getTileEntity(pos);
			CompoundNBT tag = furnace.write(new CompoundNBT());

			furnace.clear();
			world.setBlockState(pos, AbstractKeypadFurnaceBlock.this.getDefaultState().with(FACING, facing).with(OPEN, false).with(LIT, lit));
			((AbstractKeypadFurnaceTileEntity) world.getTileEntity(pos)).read(tag);
			((IOwnable) world.getTileEntity(pos)).setOwner(player.getUniqueID().toString(), player.getName().getFormattedText());
			return true;
		}
	}
}
