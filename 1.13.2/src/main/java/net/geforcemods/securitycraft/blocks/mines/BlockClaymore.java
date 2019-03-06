package net.geforcemods.securitycraft.blocks.mines;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockClaymore extends BlockContainer implements IExplosive {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

	public BlockClaymore(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(!ServerConfig.CONFIG.ableToBreakMines.get() ? -1F : 1F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(DEACTIVATED, false));
	}

	/**
	 * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
	 */
	@Override
	public boolean isNormalCube(IBlockState state)
	{
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public boolean isFullCube(IBlockState state)
	{
		return false;
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState blockState, IBlockReader access, BlockPos pos)
	{
		return VoxelShapes.empty();
	}

	@Override
	public boolean isValidPosition(IBlockState state, IWorldReaderBase world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock().isSideSolid(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP);
	}

	@Override
	public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ){
		if(!world.isRemote)
			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == SCContent.wireCutters){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, true));
				return true;
			}else if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == Items.FLINT_AND_STEEL){
				world.setBlockState(pos, SCContent.claymore.getDefaultState().with(FACING, state.get(FACING)).with(DEACTIVATED, false));
				return true;
			}

		return false;
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest, IFluidState fluid){
		if (!player.isCreative() && !world.isRemote && !world.getBlockState(pos).get(BlockClaymore.DEACTIVATED))
		{
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}

		return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
	}

	@Override
	public void onExplosionDestroy(World world, BlockPos pos, Explosion explosion)
	{
		if (!world.isRemote && BlockUtils.hasBlockProperty(world, pos, BlockClaymore.DEACTIVATED) && !world.getBlockState(pos).get(BlockClaymore.DEACTIVATED))
		{
			if(pos.equals(new BlockPos(explosion.getPosition())))
				return;

			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, (double) pos.getX() + 0.5F, (double) pos.getY() + 0.5F, (double) pos.getZ() + 0.5F, 3.5F, true);
		}
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return getDefaultState().with(FACING, placer.getHorizontalFacing()).with(DEACTIVATED, false);
	}

	@Override
	public void activateMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, false);
	}

	@Override
	public void defuseMine(World world, BlockPos pos) {
		if(!world.isRemote)
			BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
	}

	@Override
	public void explode(World world, BlockPos pos) {
		if(!world.isRemote){
			BlockUtils.destroyBlock(world, pos, false);
			world.createExplosion((Entity) null, pos.getX(), pos.getY(), pos.getZ(), 3.5F, true);
		}
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		//		if (source.getBlockState(pos).getValue(FACING) == EnumFacing.NORTH)
		//			return new AxisAlignedBB(0.225F, 0.000F, 0.175F, 0.775F, 0.325F, 0.450F);
		//		else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.SOUTH)
		//			return new AxisAlignedBB(0.225F, 0.000F, 0.550F, 0.775F, 0.325F, 0.825F);
		//		else if (source.getBlockState(pos).getValue(FACING) == EnumFacing.EAST)
		//			return new AxisAlignedBB(0.550F, 0.0F, 0.225F, 0.825F, 0.335F, 0.775F);
		//		else
		//			return new AxisAlignedBB(0.175F, 0.0F, 0.225F, 0.450F, 0.335F, 0.775F);
		return VoxelShapes.fullCube();
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(DEACTIVATED);
	}

	@Override
	public boolean isActive(World world, BlockPos pos) {
		return !world.getBlockState(pos).get(DEACTIVATED);
	}

	@Override
	public boolean isDefusable() {
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityClaymore();
	}
}
