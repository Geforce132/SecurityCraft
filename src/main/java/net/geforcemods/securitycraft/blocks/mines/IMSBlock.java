package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExtractionBlock;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class IMSBlock extends OwnableBlock implements IWaterLoggable {
	public static final IntegerProperty MINES = IntegerProperty.create("mines", 0, 4);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	private static final VoxelShape SHAPE = Block.box(4, 0, 5, 12, 7, 11);
	private static final VoxelShape SHAPE_1_MINE = VoxelShapes.or(SHAPE, Block.box(0, 0, 0, 5, 5, 5));
	private static final VoxelShape SHAPE_2_MINES = VoxelShapes.or(SHAPE_1_MINE, Block.box(0, 0, 11, 5, 5, 16));
	private static final VoxelShape SHAPE_3_MINES = VoxelShapes.or(SHAPE_2_MINES, Block.box(11, 0, 0, 16, 5, 5));
	private static final VoxelShape SHAPE_4_MINES = VoxelShapes.or(SHAPE_3_MINES, Block.box(11, 0, 11, 16, 5, 16));

	public IMSBlock(AbstractBlock.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(MINES, 4).setValue(WATERLOGGED, false));
	}

	@Override
	public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader level, BlockPos pos) {
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, level, pos);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader level, BlockPos pos, ISelectionContext ctx) {
		switch (state.getValue(MINES)) {
			case 4:
				return SHAPE_4_MINES;
			case 3:
				return SHAPE_3_MINES;
			case 2:
				return SHAPE_2_MINES;
			case 1:
				return SHAPE_1_MINE;
			default:
				return SHAPE;
		}
	}

	@Override
	public void neighborChanged(BlockState state, World level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (level.getBlockState(pos.below()).getMaterial() == Material.AIR)
			level.destroyBlock(pos, true);
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

			if (te instanceof IModuleInventory)
				((IModuleInventory) te).dropAllModules();
		}

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		TileEntity te = level.getBlockEntity(pos);

		if (te instanceof IMSBlockEntity) {
			IMSBlockEntity be = (IMSBlockEntity) te;

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.isOwnedBy(player)) {
				ItemStack held = player.getItemInHand(hand);
				int mines = state.getValue(MINES);

				if (held.getItem() == SCContent.BOUNCING_BETTY.get().asItem() && mines < 4) {
					if (!player.isCreative())
						held.shrink(1);

					level.setBlockAndUpdate(pos, state.setValue(MINES, mines + 1));
					be.setBombsRemaining(mines + 1);
					return ActionResultType.sidedSuccess(level.isClientSide);
				}
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World level, BlockPos pos, Random random) {
		if (state.getValue(MINES) == 0) {
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			level.addParticle(ParticleTypes.SMOKE, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.SMOKE, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.SMOKE, false, x, y, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			level.addParticle(ParticleTypes.FLAME, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx) {
		return defaultBlockState().setValue(MINES, 4).setValue(WATERLOGGED, ctx.getLevel().getFluidState(ctx.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(MINES, WATERLOGGED);
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
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new IMSBlockEntity();
	}

	public static class ExtractionBlock implements IExtractionBlock {
		@Override
		public boolean canExtract(IOwnable ownable, World level, BlockPos pos, BlockState state) {
			return ownable.getOwner().owns((IMSBlockEntity) level.getBlockEntity(pos));
		}

		@Override
		public Block getBlock() {
			return SCContent.IMS.get();
		}
	}
}
