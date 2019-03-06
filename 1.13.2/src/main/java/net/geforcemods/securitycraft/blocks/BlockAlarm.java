package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;


public class BlockAlarm extends BlockOwnable {

	public static final BooleanProperty LIT = BlockStateProperties.LIT;
	public static final DirectionProperty FACING = BlockStateProperties.FACING;

	public BlockAlarm(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F).tickRandomly());

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
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		return side == EnumFacing.UP && world.isSideSolid(pos.down(), EnumFacing.UP) ? true : world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!canPlaceBlockOnSide(world, pos, state.get(FACING))) {
			dropBlockAsItem(world, pos, state, 0);
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
		return world.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().with(FACING, facing) : getDefaultState().with(FACING, EnumFacing.DOWN);
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

		if (!world.isSideSolid(pos.offset(facing.getOpposite()), facing, true))
		{
			dropBlockAsItemWithChance(world.getBlockState(pos), world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		//		float threePx = 0.1875F;
		//		float ySideMin = 0.5F - threePx; //bottom of the alarm when placed on a block side
		//		float ySideMax = 0.5F + threePx; //top of the alarm when placed on a block side
		//		float hSideMin = 0.5F - threePx; //the left start for s/w and right start for n/e
		//		float hSideMax = 0.5F + threePx; //the left start for n/e and right start for s/w
		//		float px = 1.0F / 16.0F; //one sixteenth of a block
		//		EnumFacing facing = state.getValue(FACING);
		//
		//		switch(BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()]){
		//			case 1: //east
		//				return new AxisAlignedBB(0.0F, ySideMin - px, hSideMin - px, 0.5F, ySideMax + px, hSideMax + px);
		//			case 2: //west
		//				return new AxisAlignedBB(0.5F, ySideMin - px, hSideMin - px, 1.0F, ySideMax + px, hSideMax + px);
		//			case 3: //north
		//				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.0F, hSideMax + px, ySideMax + px, 0.5F);
		//			case 4: //south
		//				return new AxisAlignedBB(hSideMin - px, ySideMin - px, 0.5F, hSideMax + px, ySideMax + px, 1.0F);
		//			case 5: //up
		//				return new AxisAlignedBB(0.5F - threePx - px, 0F, 0.5F - threePx - px, 0.5F + threePx + px, 0.5F, 0.5F + threePx + px);
		//			case 6: //down
		//				return new AxisAlignedBB(0.5F - threePx - px, 0.5F, 0.5F - threePx - px, 0.5F + threePx + px, 1.0F, 0.5F + threePx + px);
		//		}

		return Block.makeCuboidShape(0, 0, 0, 16, 16, 16);
	}

	private void playSoundAndUpdate(World world, BlockPos pos){
		if(!(world.getTileEntity(pos) instanceof TileEntityAlarm)) return;

		if(world.getRedstonePowerFromNeighbors(pos) > 0){
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(pos)).isPowered();

			if(!isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(pos)).getOwner();
				EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(world, pos, FACING);
				BlockUtils.setBlock(world, pos, SCContent.alarmLit);
				BlockUtils.setBlockProperty(world, pos, FACING, dir);
				((TileEntityAlarm) world.getTileEntity(pos)).getOwner().set(owner);
				((TileEntityAlarm) world.getTileEntity(pos)).setPowered(true);
			}

		}else{
			boolean isPowered = ((TileEntityAlarm) world.getTileEntity(pos)).isPowered();

			if(isPowered){
				Owner owner = ((TileEntityAlarm) world.getTileEntity(pos)).getOwner();
				EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(world, pos, FACING);
				BlockUtils.setBlock(world, pos, SCContent.alarm);
				BlockUtils.setBlockProperty(world, pos, FACING, dir);
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
