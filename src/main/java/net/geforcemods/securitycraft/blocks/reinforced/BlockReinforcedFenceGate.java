package net.geforcemods.securitycraft.blocks.reinforced;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockReinforcedFenceGate extends BlockFenceGate implements ITileEntityProvider, IIntersectable {

	public BlockReinforcedFenceGate(){
		super();
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Material.iron, 35);
	}

	@Override
	public boolean canEntityDestroy(IBlockAccess world, BlockPos pos, Entity entity)
	{
		return !(entity instanceof EntityWither);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing facing, float hitX, float hitY, float hitZ){
		return false;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state){
		super.breakBlock(world, pos, state);
		world.removeTileEntity(pos);
	}

	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(BlockUtils.getBlockPropertyAsBoolean(world, pos, OPEN))
			return;

		if(entity instanceof EntityItem)
			return;
		else if(entity instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entity;

			if(((TileEntityOwnable)world.getTileEntity(pos)).getOwner().isOwner(player))
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, pos.getX(), pos.getY(), pos.getZ());

			creeper.onStruckByLightning(lightning);
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.electricity, 6.0F);
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		if(!world.isRemote) {
			boolean isPoweredSCBlock = isSCBlock(neighborBlock) && world.isBlockPowered(pos);

			if (isPoweredSCBlock || neighborBlock.canProvidePower())
				if (isPoweredSCBlock && !((Boolean)state.getValue(OPEN)).booleanValue() && !((Boolean)state.getValue(POWERED)).booleanValue()) {
					world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(true)).withProperty(POWERED, Boolean.valueOf(true)), 2);
					world.playAuxSFXAtEntity((EntityPlayer)null, 1003, pos, 0);
				}
				else if (!isPoweredSCBlock && ((Boolean)state.getValue(OPEN)).booleanValue() && ((Boolean)state.getValue(POWERED)).booleanValue()) {
					world.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
					world.playAuxSFXAtEntity((EntityPlayer)null, 1006, pos, 0);
				}
				else if (isPoweredSCBlock != ((Boolean)state.getValue(POWERED)).booleanValue())
					world.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(isPoweredSCBlock)), 2);
		}
	}

	private boolean isSCBlock(Block block) {
		return (block instanceof BlockLaserBlock || block instanceof BlockRetinalScanner ||
				block instanceof BlockKeypad || block instanceof BlockKeycardReader || block instanceof BlockInventoryScanner);
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam){
		super.onBlockEventReceived(world, pos, state, eventID, eventParam);
		TileEntity tileentity = world.getTileEntity(pos);
		return tileentity != null ? tileentity.receiveClientEvent(eventID, eventParam) : false;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}

}
