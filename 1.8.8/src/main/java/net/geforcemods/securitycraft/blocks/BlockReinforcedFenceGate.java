package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BlockReinforcedFenceGate extends BlockFenceGate implements ITileEntityProvider, IIntersectable {

	public BlockReinforcedFenceGate(){
		super(EnumType.BIRCH); //doesn't matter what gets put in here
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Material.iron, 35);
	}

	/**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, BlockPos pos, IBlockState state, EntityPlayer p_149727_5_, EnumFacing facing, float p_149727_7_, float p_149727_8_, float p_149727_9_){
        return false;
    }
    
    public void breakBlock(World par1World, BlockPos pos, IBlockState state){
        super.breakBlock(par1World, pos, state);
        par1World.removeTileEntity(pos);
    }
    
    public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(BlockUtils.getBlockPropertyAsBoolean(world, pos, OPEN)){
			return;
		}
    	
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


    public boolean onBlockEventReceived(World par1World, BlockPos pos, IBlockState state, int par5, int par6){
        super.onBlockEventReceived(par1World, pos, state, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(pos);
        return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
    }
    
    public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable().intersectsEntities();
    }

}
