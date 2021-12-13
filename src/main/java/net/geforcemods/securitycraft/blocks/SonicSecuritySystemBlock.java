package net.geforcemods.securitycraft.blocks;

import java.util.stream.Stream;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.network.client.OpenSSSScreen;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

public class SonicSecuritySystemBlock extends OwnableBlock implements SimpleWaterloggedBlock {

	private static final VoxelShape SHAPE = Stream.of(
			Block.box(5.5, 11, 5.5, 10.5, 16, 10.5),
			Block.box(7.5, 13, 7.5, 8.5, 14, 9.5),
			Block.box(7.5, 2, 7.5, 8.5, 13, 8.5),
			Block.box(7, 1, 7, 9, 2, 9),
			Block.box(6.5, 0, 6.5, 9.5, 1, 9.5)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).orElse(Shapes.block());

	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	public SonicSecuritySystemBlock(Properties properties)
	{
		super(properties);

		registerDefaultState(stateDefinition.any().setValue(POWERED, false).setValue(WATERLOGGED, false));
	}

	public static boolean isNormalCube(BlockState state, BlockGetter reader, BlockPos pos) {
		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
		if(state.getValue(WATERLOGGED))
			level.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));

		return facing == Direction.DOWN && !canSurvive(state, level, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, level, currentPos, facingPos);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
		return canSupportCenter(world, pos.below(), Direction.UP);
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if (player.getItemInHand(hand).getItem() != SCContent.PORTABLE_TUNE_PLAYER.get()) {
			SonicSecuritySystemBlockEntity be = (SonicSecuritySystemBlockEntity)level.getBlockEntity(pos);

			if (!level.isClientSide && (be.getOwner().isOwner(player) || ModuleUtils.isAllowed(be, player)))
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer)player), new OpenSSSScreen(pos)); //watch out for the creeper

			return InteractionResult.SUCCESS;
		}
		else
			return InteractionResult.PASS;
	}

	@Override
	public int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side){
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public int getDirectSignal(BlockState state, BlockGetter level, BlockPos pos, Direction side){
		return state.getValue(POWERED) ? 15 : 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return defaultBlockState().setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).getType() == Fluids.WATER);
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
	{
		return SHAPE;
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(POWERED, WATERLOGGED);
	}

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		return getItemStackFromBlock(level.getBlockEntity(pos).getUpdateTag());
	}

	private ItemStack getItemStackFromBlock(CompoundTag blockTag)
	{
		ItemStack stack = new ItemStack(SCContent.SONIC_SECURITY_SYSTEM_ITEM.get());

		if(!blockTag.contains("LinkedBlocks"))
			return stack;

		stack.setTag(new CompoundTag());
		stack.getTag().put("LinkedBlocks", blockTag.getList("LinkedBlocks", Tag.TAG_COMPOUND));

		return stack;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new SonicSecuritySystemBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, SCContent.beTypeSonicSecuritySystem, WorldUtils::blockEntityTicker);
	}
}
