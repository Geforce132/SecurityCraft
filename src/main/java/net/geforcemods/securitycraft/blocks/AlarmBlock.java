package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.AlarmTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class AlarmBlock extends OwnableBlock {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.makeCuboidShape(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.makeCuboidShape(4, 8, 4, 12, 16, 12);

	public AlarmBlock() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(-1.0F, 6000000.0F).tickRandomly());

		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.UP).with(LIT, false));
	}

	@Override
	public int getLightValue(BlockState state)
	{
		return state.get(LIT) ? 15 : 0;
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader world, BlockPos pos){
		Direction facing = state.get(FACING);

		return facing == Direction.UP && BlockUtils.isSideSolid(world, pos.down(), Direction.UP) ? true : BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean flag)
	{
		if (!isValidPosition(state, world, pos))
			world.destroyBlock(pos, true);
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing) ? getDefaultState().with(FACING, facing) : null;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean flag) {
		if(world.isRemote)
			return;
		else
			world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 1);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random){
		if(!world.isRemote){
			playSoundAndUpdate(world, pos);

			world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader w, BlockPos pos, BlockPos neighbor){
		if(w.isRemote() || !(w instanceof World))
			return;

		World world = (World)w;

		playSoundAndUpdate((world), pos);

		Direction facing = world.getBlockState(pos).get(FACING);

		if (!BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing))
			world.destroyBlock(pos, true);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader source, BlockPos pos, ISelectionContext ctx)
	{
		Direction facing = state.get(FACING);

		switch(facing){
			case EAST:
				return SHAPE_EAST;
			case WEST:
				return SHAPE_WEST;
			case NORTH:
				return SHAPE_NORTH;
			case SOUTH:
				return SHAPE_SOUTH;
			case UP:
				return SHAPE_UP;
			case DOWN:
				return SHAPE_DOWN;
		}

		return VoxelShapes.fullCube();
	}

	private void playSoundAndUpdate(World world, BlockPos pos){
		if(world.getBlockState(pos).getBlock() != SCContent.alarm || !(world.getTileEntity(pos) instanceof AlarmTileEntity)) return;

		if(world.getRedstonePowerFromNeighbors(pos) > 0){
			boolean isPowered = ((AlarmTileEntity) world.getTileEntity(pos)).isPowered();

			if(!isPowered){
				Owner owner = ((AlarmTileEntity) world.getTileEntity(pos)).getOwner();
				BlockUtils.setBlockProperty(world, pos, LIT, true);
				((AlarmTileEntity) world.getTileEntity(pos)).getOwner().set(owner);
				((AlarmTileEntity) world.getTileEntity(pos)).setPowered(true);
			}

		}else{
			boolean isPowered = ((AlarmTileEntity) world.getTileEntity(pos)).isPowered();

			if(isPowered){
				Owner owner = ((AlarmTileEntity) world.getTileEntity(pos)).getOwner();
				BlockUtils.setBlockProperty(world, pos, LIT, false);
				((AlarmTileEntity) world.getTileEntity(pos)).getOwner().set(owner);
				((AlarmTileEntity) world.getTileEntity(pos)).setPowered(false);
			}
		}
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, BlockState state)
	{
		return new ItemStack(SCContent.alarm.asItem());
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder){
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader){
		return new AlarmTileEntity();
	}
}
