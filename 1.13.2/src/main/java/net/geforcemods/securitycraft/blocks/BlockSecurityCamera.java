package net.geforcemods.securitycraft.blocks;

import java.util.Iterator;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockSecurityCamera extends BlockContainer{

	public static final DirectionProperty FACING = DirectionProperty.create("facing", facing -> {
		return facing != EnumFacing.DOWN;
	});
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	public BlockSecurityCamera(Material material) {
		super(Block.Properties.create(material).hardnessAndResistance(-1.0F, 6000000.0F));
		stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(POWERED, false);
	}

	@Override
	public VoxelShape getCollisionShape(IBlockState blockState, IBlockReader access, BlockPos pos){
		return VoxelShapes.empty();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}

	@Override
	public void onReplaced(IBlockState state, World world, BlockPos pos, IBlockState newState, boolean isMoving)
	{
		super.onReplaced(state, world, pos, newState, isMoving);

		world.notifyNeighborsOfStateChange(pos.north(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.south(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.east(), world.getBlockState(pos).getBlock());
		world.notifyNeighborsOfStateChange(pos.west(), world.getBlockState(pos).getBlock());
	}

	@Override
	public VoxelShape getShape(IBlockState state, IBlockReader source, BlockPos pos)
	{
		//		EnumFacing dir = BlockUtils.getBlockPropertyAsEnum(source, pos, FACING);
		//		float px = 1.0F/16.0F; //one sixteenth of a block
		//
		//		if(dir == EnumFacing.SOUTH)
		//			return new AxisAlignedBB(0.275F, 0.250F, 0.000F, 0.700F, 0.800F, 0.850F);
		//		else if(dir == EnumFacing.NORTH)
		//			return new AxisAlignedBB(0.275F, 0.250F, 0.150F, 0.700F, 0.800F, 1.000F);
		//		else if(dir == EnumFacing.WEST)
		//			return new AxisAlignedBB(0.125F, 0.250F, 0.275F, 1.000F, 0.800F, 0.725F);
		//		else if(dir == EnumFacing.DOWN)
		//			return new AxisAlignedBB(px * 5, 1.0F - px * 2, px * 5, px * 11, 1.0F, px * 11);
		//		else
		//			return new AxisAlignedBB(0.000F, 0.250F, 0.275F, 0.850F, 0.800F, 0.725F);
		return VoxelShapes.fullCube();
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext ctx)
	{
		return getStateForPlacement(ctx.getWorld(), ctx.getPos(), ctx.getFace(), ctx.getHitX(), ctx.getHitY(), ctx.getHitZ(), ctx.getPlayer());
	}

	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, EntityPlayer placer)
	{
		IBlockState state = getDefaultState().with(POWERED, Boolean.valueOf(false));

		if(world.isSideSolid(pos.offset(facing.getOpposite()), facing))
			return state.with(FACING, facing).with(POWERED, false);
		else{
			Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();
			EnumFacing iFacing;

			do{
				if(!iterator.hasNext())
					return state;

				iFacing = (EnumFacing)iterator.next();
			}while (!world.isSideSolid(pos.offset(iFacing.getOpposite()), iFacing));

			return state.with(FACING, facing).with(POWERED, false);
		}
	}

	public void mountCamera(World world, int x, int y, int z, int id, EntityPlayer player){
		if(!world.isRemote && player.getRidingEntity() == null)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("tile.securitycraft:securityCamera.name"), ClientUtils.localize("messages.securitycraft:securityCamera.mounted"), TextFormatting.GREEN);

		if(player.getRidingEntity() != null && player.getRidingEntity() instanceof EntitySecurityCamera){
			EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, x, y, z, id, (EntitySecurityCamera) player.getRidingEntity());
			WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
			player.startRiding(dummyEntity);
			return;
		}

		EntitySecurityCamera dummyEntity = new EntitySecurityCamera(world, x, y, z, id, player);
		WorldUtils.addScheduledTask(world, () -> world.spawnEntity(dummyEntity));
		player.startRiding(dummyEntity);

		for(Object e : world.loadedEntityList)
			if(e instanceof EntityLiving)
				if(((EntityLiving)e).getAttackTarget() == player)
					((EntityLiving)e).setAttackTarget(null);
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side){
		return world.isSideSolid(pos.offset(side.getOpposite()), side);
	}

	@Override
	public boolean canProvidePower(IBlockState state){
		return true;
	}

	@Override
	public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.get(POWERED) && ((CustomizableSCTE) blockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side){
		if(blockState.get(POWERED) && ((CustomizableSCTE) blockAccess.getTileEntity(pos)).hasModule(EnumCustomModules.REDSTONE))
			return 15;
		else
			return 0;
	}

	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
		if (!isValidPosition(world.getBlockState(pos), world, pos) && !canPlaceBlockOnSide(world, pos, state.get(FACING))) {
			dropBlockAsItemWithChance(state, world, pos, 1.0F, 0);
			world.removeBlock(pos);
		}
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder)
	{
		builder.add(FACING);
		builder.add(POWERED);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader world){
		return new TileEntitySecurityCamera().nameable();
	}

}
