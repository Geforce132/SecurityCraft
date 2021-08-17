package net.geforcemods.securitycraft.blocks.mines;

import java.util.Random;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.IMSBlockEntity;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class IMSBlock extends OwnableBlock {

	public static final IntegerProperty MINES = IntegerProperty.create("mines", 0, 4);
	private static final VoxelShape SHAPE = Block.box(4, 0, 5, 12, 7, 11);
	private static final VoxelShape SHAPE_1_MINE = Shapes.or(SHAPE, Block.box(0, 0, 0, 5, 5, 5));
	private static final VoxelShape SHAPE_2_MINES = Shapes.or(SHAPE_1_MINE, Block.box(0, 0, 11, 5, 5, 16));
	private static final VoxelShape SHAPE_3_MINES = Shapes.or(SHAPE_2_MINES, Block.box(11, 0, 0, 16, 5, 5));
	private static final VoxelShape SHAPE_4_MINES = Shapes.or(SHAPE_3_MINES, Block.box(11, 0, 11, 16, 5, 16));

	public IMSBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(MINES, 4));
	}

	@Override
	public float getDestroyProgress(BlockState state, Player player, BlockGetter world, BlockPos pos)
	{
		return !ConfigHandler.SERVER.ableToBreakMines.get() ? -1F : super.getDestroyProgress(state, player, world, pos);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		return switch(state.getValue(MINES)) {
			case 4 -> SHAPE_4_MINES;
			case 3 -> SHAPE_3_MINES;
			case 2 -> SHAPE_2_MINES;
			case 1 -> SHAPE_1_MINE;
			default -> SHAPE;
		};
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.below()).isAir())
			world.destroyBlock(pos, true);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(!world.isClientSide)
		{
			BlockEntity te = world.getBlockEntity(pos);

			if(((IOwnable)te).getOwner().isOwner(player))
			{
				ItemStack held = player.getItemInHand(hand);
				int mines = state.getValue(MINES);

				if(held.getItem() == SCContent.BOUNCING_BETTY.get().asItem() && mines < 4)
				{
					if(!player.isCreative())
						held.shrink(1);

					world.setBlockAndUpdate(pos, state.setValue(MINES, mines + 1));
					((IMSBlockEntity)te).setBombsRemaining(mines + 1);
				}
				else if(player instanceof ServerPlayer)
				{

					if(te instanceof MenuProvider menuProvider)
						NetworkHooks.openGui((ServerPlayer)player, menuProvider, pos);
				}
			}
		}

		return InteractionResult.SUCCESS;
	}

	/**
	 * A randomly called display update to be able to add ParticleTypes or other items for display
	 */
	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, Level world, BlockPos pos, Random random){
		if(state.getValue(MINES) == 0){
			double x = pos.getX() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double y = pos.getY() + 0.4F + (random.nextFloat() - 0.5F) * 0.2D;
			double z = pos.getZ() + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.addParticle(ParticleTypes.SMOKE, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y + magicNumber1, z + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.SMOKE, false, x, y, z, 0.0D, 0.0D, 0.0D);

			world.addParticle(ParticleTypes.FLAME, false, x - magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, false, x + magicNumber2, y + magicNumber1, z, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
	{
		return defaultBlockState().setValue(MINES, 4);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(MINES);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new IMSBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeIms, WorldUtils::blockEntityTicker);
	}
}
