package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClaymoreBlock extends ExplosiveBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape NORTH_OFF = Shapes.or(Block.box(4, 0, 5, 12, 4, 7), Shapes.or(Block.box(4, 4, 5, 12, 5, 6), Shapes.or(Block.box(5, 4, 4, 6, 5, 5), Shapes.or(Block.box(10, 4, 4, 11, 5, 5), Shapes.or(Block.box(4, 4, 3, 5, 5, 4), Block.box(11, 4, 3, 12, 5, 4))))));
	private static final VoxelShape NORTH_ON = Shapes.or(NORTH_OFF, Shapes.or(Block.box(3, 4, 2, 4, 5, 3), Block.box(12, 4, 2, 13, 5, 3)));
	private static final VoxelShape EAST_OFF = Shapes.or(Block.box(9, 0, 4, 11, 4, 12), Shapes.or(Block.box(10, 4, 4, 11, 5, 12), Shapes.or(Block.box(11, 4, 5, 12, 5, 6), Shapes.or(Block.box(11, 4, 10, 12, 5, 11), Shapes.or(Block.box(12, 4, 4, 13, 5, 5), Block.box(12, 4, 11, 13, 5, 12))))));
	private static final VoxelShape EAST_ON = Shapes.or(EAST_OFF, Shapes.or(Block.box(13, 4, 3, 14, 5, 4), Block.box(13, 4, 12, 14, 5, 13)));
	private static final VoxelShape SOUTH_OFF = Shapes.or(Block.box(4, 0, 9, 12, 4, 11), Shapes.or(Block.box(4, 4, 10, 12, 5, 11), Shapes.or(Block.box(5, 4, 11, 6, 5, 12), Shapes.or(Block.box(10, 4, 11, 11, 5, 12), Shapes.or(Block.box(4, 4, 12, 5, 5, 13), Block.box(11, 4, 12, 12, 5, 13))))));
	private static final VoxelShape SOUTH_ON = Shapes.or(SOUTH_OFF, Shapes.or(Block.box(3, 4, 13, 4, 5, 14), Block.box(12, 4, 13, 13, 5, 14)));
	private static final VoxelShape WEST_OFF = Shapes.or(Block.box(7, 0, 4, 5, 4, 12), Shapes.or(Block.box(6, 4, 4, 5, 5, 12), Shapes.or(Block.box(5, 4, 5, 4, 5, 6), Shapes.or(Block.box(5, 4, 10, 4, 5, 11), Shapes.or(Block.box(4, 4, 4, 3, 5, 5), Block.box(4, 4, 11, 3, 5, 12))))));
	private static final VoxelShape WEST_ON = Shapes.or(WEST_OFF, Shapes.or(Block.box(3, 4, 3, 2, 5, 4), Block.box(3, 4, 12, 2, 5, 13)));

	public ClaymoreBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DEACTIVATED, false));
	}

	@Override
	public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.below()).getMaterial() != Material.AIR)
			return;
		else
			world.destroyBlock(pos, true);
	}

	@Override
	public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos)
	{
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public boolean removedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid){
		if (!player.isCreative() && !world.isClientSide && !world.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED))
		{
			world.destroyBlock(pos, false);

			if(!EntityUtils.doesPlayerOwn(player, world, pos))
				explode(world, pos);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void wasExploded(Level world, BlockPos pos, Explosion explosion)
	{
		if (!world.isClientSide && world.getBlockState(pos).hasProperty(ClaymoreBlock.DEACTIVATED) && !world.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED))
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(Level world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, Player placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection()).setValue(DEACTIVATED, false);
	}

	@Override
	public boolean activateMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(Level world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(!state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(Level world, BlockPos pos) {
		if(!world.isClientSide){
			world.destroyBlock(pos, false);
			world.explode((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.5F : 3.5F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter source, BlockPos pos, CollisionContext ctx)
	{
		switch(state.getValue(FACING))
		{
			case NORTH:
				if(state.getValue(DEACTIVATED))
					return NORTH_OFF;
				else
					return NORTH_ON;
			case EAST:
				if(state.getValue(DEACTIVATED))
					return EAST_OFF;
				else
					return EAST_ON;
			case SOUTH:
				if(state.getValue(DEACTIVATED))
					return SOUTH_OFF;
				else
					return SOUTH_ON;
			case WEST:
				if(state.getValue(DEACTIVATED))
					return WEST_OFF;
				else
					return WEST_ON;
			default: return Shapes.block();
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(Level world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ClaymoreTileEntity(pos, state);
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
