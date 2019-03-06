package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.ConfigHandler.ServerConfig;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.tileentity.TileEntityMotionLight;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.BlockStateContainer;

public class BlockMotionActivatedLight extends BlockOwnable {

	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final BooleanProperty LIT = BlockStateProperties.LIT;

	public BlockMotionActivatedLight(Material material) {
		super(SoundType.GLASS, Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		setDefaultState(stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(LIT, false));
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isNormalCube(IBlockState state){
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader world, BlockPos pos){
		//		EnumFacing dir = state.getValue(FACING);
		//		float px = 1.0F / 16.0F;
		//
		//		if(dir == EnumFacing.NORTH) {
		//			return new AxisAlignedBB(px * 6, px * 3, 0F, px * 10, px * 9, px * 3);
		//		}
		//		else if(dir == EnumFacing.SOUTH) {
		//			return new AxisAlignedBB(px * 6, px * 3, 1F, px * 10, px * 9, 1F - (px * 3));
		//		}
		//		else if(dir == EnumFacing.EAST) {
		//			return new AxisAlignedBB(1F, px * 3, px * 6, 1F - (px * 3), px * 9, px * 10);
		//		}
		//		else if(dir == EnumFacing.WEST) {
		//			return new AxisAlignedBB(0F, px * 3, px * 6, px * 3, px * 9, px * 10);
		//		}
		//
		//		return new AxisAlignedBB(px * 6, px * 3, 0F, px * 10, px * 9, px * 3);
		return VoxelShapes.fullCube();
	}

	@Override
	public int getLightValue(IBlockState state, IWorldReader world, BlockPos pos) {
		if(BlockUtils.getBlock(world, pos) != SCContent.motionActivatedLight)
			return 0; //Weird if statement I had to include because Waila kept
		//crashing if I looked at one of these lights then looked away quickly.

		return world.getBlockState(pos).get(LIT) ? 15 : 0;
	}

	public static void toggleLight(World world, BlockPos pos, double searchRadius, Owner owner, boolean isLit) {
		if(!world.isRemote)
		{
			if(isLit)
			{
				BlockUtils.setBlockProperty(world, pos, LIT, true);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
			else
			{
				BlockUtils.setBlockProperty(world, pos, LIT, false);

				if(((IOwnable) world.getTileEntity(pos)) != null)
					((IOwnable) world.getTileEntity(pos)).setOwner(owner.getUUID(), owner.getName());

				BlockUtils.updateAndNotify(world, pos, SCContent.motionActivatedLight, 1, false);
			}
		}
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		if(side == EnumFacing.UP || side == EnumFacing.DOWN) return false;

		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		return world.isSideSolid(pos.offset(facing.getOpposite()), facing, true) ? getDefaultState().with(FACING, facing.getOpposite()) : getDefaultState().with(FACING, EnumFacing.DOWN);
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!canPlaceBlockOnSide(world, pos, state.get(FACING).getOpposite())) {
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(LIT);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world) {
		return new TileEntityMotionLight().attacks(EntityPlayer.class, ServerConfig.CONFIG.motionActivatedLightSearchRadius.get(), 1);
	}

}
