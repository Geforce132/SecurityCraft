package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockAlarm extends BlockOwnable {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	private static final VoxelShape SHAPE_EAST = Block.makeCuboidShape(0, 4, 4, 8, 12, 12);
	private static final VoxelShape SHAPE_WEST = Block.makeCuboidShape(8, 4, 4, 16, 12, 12);
	private static final VoxelShape SHAPE_NORTH = Block.makeCuboidShape(4, 4, 8, 12, 12, 16);
	private static final VoxelShape SHAPE_SOUTH = Block.makeCuboidShape(4, 4, 0, 12, 12, 8);
	private static final VoxelShape SHAPE_UP = Block.makeCuboidShape(4, 0, 4, 12, 8, 12);
	private static final VoxelShape SHAPE_DOWN = Block.makeCuboidShape(4, 8, 4, 12, 16, 12);

	public BlockAlarm() {
		super(Block.Properties.create(Material.IRON).hardnessAndResistance(-1.0F, 6000000.0F).tickRandomly());

		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.UP).with(LIT, false));
	}

	@Override
	public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos)
	{
		return state.get(LIT) ? 15 : 0;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
	{
		return EnumBlockRenderType.MODEL;
	}

	/**
	 * Check whether this Block can be placed on the given side
	 */
	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos){
		EnumFacing facing = state.get(FACING);

		return facing == EnumFacing.UP && world.getBlockState(pos.down()).isTopSolid() ? true : BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing);
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockReader world, IBlockState state, BlockPos pos, EnumFacing face)
	{
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!isValidPosition(state, world, pos)) {
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing) ? getDefaultState().with(FACING, facing) : null;
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(IBlockState state, World world, BlockPos pos, IBlockState oldState) {
		if(world.isRemote)
			return;
		else
			world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 1);
	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(IBlockState state, World world, BlockPos pos, Random random){
		if(!world.isRemote){
			playSoundAndUpdate(world, pos);

			world.getPendingBlockTicks().scheduleTick(pos, state.getBlock(), 5);
		}
	}

	@Override
	public void onNeighborChange(IBlockState state, IWorldReader w, BlockPos pos, BlockPos neighbor){
		if(w.isRemote() || !(w instanceof World))
			return;

		World world = (World)w;

		playSoundAndUpdate((world), pos);

		EnumFacing facing = world.getBlockState(pos).get(FACING);

		if (!BlockUtils.isSideSolid(world, pos.offset(facing.getOpposite()), facing))
		{
			dropBlockAsItemWithChance(world.getBlockState(pos), world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		EnumFacing facing = state.get(FACING);

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
		if(!(world.getTileEntity(pos) instanceof TileEntityAlarm)) return;

		if(world.getRedstonePowerFromNeighbors(pos) > 0){
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(pos)).isPowered();

			if(!isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(pos)).getOwner();
				BlockUtils.setBlockProperty(world, pos, LIT, true);
				((TileEntityAlarm) world.getTileEntity(pos)).getOwner().set(owner);
				((TileEntityAlarm) world.getTileEntity(pos)).setPowered(true);
			}

		}else{
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(pos)).isPowered();

			if(isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(pos)).getOwner();
				BlockUtils.setBlockProperty(world, pos, LIT, false);
				((TileEntityAlarm) world.getTileEntity(pos)).getOwner().set(owner);
				((TileEntityAlarm) world.getTileEntity(pos)).setPowered(false);
			}
		}
	}

	@Override
	public ItemStack getItem(IBlockReader world, BlockPos pos, IBlockState state)
	{
		return new ItemStack(SCContent.alarm.asItem());
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune)
	{
		return SCContent.alarm.asItem();
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder){
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader reader){
		return new TileEntityAlarm();
	}
}
