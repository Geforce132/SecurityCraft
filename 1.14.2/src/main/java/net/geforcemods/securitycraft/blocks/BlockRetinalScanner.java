package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockRetinalScanner extends ContainerBlock {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public BlockRetinalScanner(Material material) {
		super(Block.Properties.create(material).sound(SoundType.METAL).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, false));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	/**
	 * Called when the block is placed in the world.
	 */
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack){
		if(entity instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)entity));

		//		BlockState north = world.getBlockState(pos.north());
		//		BlockState south = world.getBlockState(pos.south());
		//		BlockState west = world.getBlockState(pos.west());
		//		BlockState east = world.getBlockState(pos.east());
		Direction facing = state.get(FACING);

		if (facing == Direction.NORTH)// && north.isFullCube() && !south.isFullCube())
			facing = Direction.SOUTH;
		else if (facing == Direction.SOUTH)// && south.isFullCube() && !north.isFullCube())
			facing = Direction.NORTH;
		else if (facing == Direction.WEST)// && west.isFullCube() && !east.isFullCube())
			facing = Direction.EAST;
		else if (facing == Direction.EAST)// && east.isFullCube() && !west.isFullCube())
			facing = Direction.WEST;

		world.setBlockState(pos, state.with(FACING, facing), 2);

	}

	/**
	 * Ticks the block if it's been scheduled
	 */
	@Override
	public void tick(BlockState state, World world, BlockPos pos, Random random){
		if (!world.isRemote && state.get(POWERED).booleanValue())
			BlockUtils.setBlockProperty(world, pos, POWERED, false);
	}

	/**
	 * Can this block provide power. Only wire currently seems to have this change based on its state.
	 */
	@Override
	public boolean canProvidePower(BlockState state)
	{
		return true;
	}

	/**
	 * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
	 * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
	 * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
	 */
	@Override
	public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
	{
		if(blockState.get(POWERED))
			return 15;
		else
			return 0;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z, ctx.getPlayer());
	}

	public BlockState getStateForPlacement(World world, BlockPos pos, Direction facing, double hitX, double hitY, double hitZ, PlayerEntity placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing().getOpposite()).with(POWERED, false);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityRetinalScanner().activatedByView();
	}

}
