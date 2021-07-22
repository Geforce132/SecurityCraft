package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.tileentity.ClaymoreTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class ClaymoreBlock extends ExplosiveBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");
	private static final VoxelShape NORTH_OFF = VoxelShapes.or(Block.box(4, 0, 5, 12, 4, 7), VoxelShapes.or(Block.box(4, 4, 5, 12, 5, 6), VoxelShapes.or(Block.box(5, 4, 4, 6, 5, 5), VoxelShapes.or(Block.box(10, 4, 4, 11, 5, 5), VoxelShapes.or(Block.box(4, 4, 3, 5, 5, 4), Block.box(11, 4, 3, 12, 5, 4))))));
	private static final VoxelShape NORTH_ON = VoxelShapes.or(NORTH_OFF, VoxelShapes.or(Block.box(3, 4, 2, 4, 5, 3), Block.box(12, 4, 2, 13, 5, 3)));
	private static final VoxelShape EAST_OFF = VoxelShapes.or(Block.box(9, 0, 4, 11, 4, 12), VoxelShapes.or(Block.box(10, 4, 4, 11, 5, 12), VoxelShapes.or(Block.box(11, 4, 5, 12, 5, 6), VoxelShapes.or(Block.box(11, 4, 10, 12, 5, 11), VoxelShapes.or(Block.box(12, 4, 4, 13, 5, 5), Block.box(12, 4, 11, 13, 5, 12))))));
	private static final VoxelShape EAST_ON = VoxelShapes.or(EAST_OFF, VoxelShapes.or(Block.box(13, 4, 3, 14, 5, 4), Block.box(13, 4, 12, 14, 5, 13)));
	private static final VoxelShape SOUTH_OFF = VoxelShapes.or(Block.box(4, 0, 9, 12, 4, 11), VoxelShapes.or(Block.box(4, 4, 10, 12, 5, 11), VoxelShapes.or(Block.box(5, 4, 11, 6, 5, 12), VoxelShapes.or(Block.box(10, 4, 11, 11, 5, 12), VoxelShapes.or(Block.box(4, 4, 12, 5, 5, 13), Block.box(11, 4, 12, 12, 5, 13))))));
	private static final VoxelShape SOUTH_ON = VoxelShapes.or(SOUTH_OFF, VoxelShapes.or(Block.box(3, 4, 13, 4, 5, 14), Block.box(12, 4, 13, 13, 5, 14)));
	private static final VoxelShape WEST_OFF = VoxelShapes.or(Block.box(7, 0, 4, 5, 4, 12), VoxelShapes.or(Block.box(6, 4, 4, 5, 5, 12), VoxelShapes.or(Block.box(5, 4, 5, 4, 5, 6), VoxelShapes.or(Block.box(5, 4, 10, 4, 5, 11), VoxelShapes.or(Block.box(4, 4, 4, 3, 5, 5), Block.box(4, 4, 11, 3, 5, 12))))));
	private static final VoxelShape WEST_ON = VoxelShapes.or(WEST_OFF, VoxelShapes.or(Block.box(3, 4, 3, 2, 5, 4), Block.box(3, 4, 12, 2, 5, 13)));

	public ClaymoreBlock(Block.Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(DEACTIVATED, false));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean flag) {
		if (world.getBlockState(pos.below()).getMaterial() != Material.AIR)
			return;
		else
			world.destroyBlock(pos, true);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos)
	{
		return BlockUtils.isSideSolid(world, pos.below(), Direction.UP);
	}

	@Override
	public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid){
		if (!player.isCreative() && !world.isClientSide && !world.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED))
		{
			world.destroyBlock(pos, false);

			if(!EntityUtils.doesPlayerOwn(player, world, pos))
				explode(world, pos);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void wasExploded(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isClientSide && world.getBlockState(pos).hasProperty(ClaymoreBlock.DEACTIVATED) && !world.getBlockState(pos).getValue(ClaymoreBlock.DEACTIVATED))
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			explode(world, pos);
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getLevel(), ctx.getClickedPos(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return defaultBlockState().setValue(FACING, placer.getDirection()).setValue(DEACTIVATED, false);
	}

	@Override
	public boolean activateMine(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, false));
			return true;
		}

		return false;
	}

	@Override
	public boolean defuseMine(World world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);

		if(!state.getValue(DEACTIVATED))
		{
			world.setBlockAndUpdate(pos, state.setValue(DEACTIVATED, true));
			return true;
		}

		return false;
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isClientSide){
			world.destroyBlock(pos, false);
			world.explode((Entity) null, pos.getX(), pos.getY(), pos.getZ(), ConfigHandler.SERVER.smallerMineExplosion.get() ? 1.5F : 3.5F, ConfigHandler.SERVER.shouldSpawnFire.get(), BlockUtils.getExplosionMode());
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
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
			default: return VoxelShapes.block();
		}
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).getValue(DEACTIVATED);
	}

	@Override
	public boolean explodesWhenInteractedWith() {
		return false;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new ClaymoreTileEntity();
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
