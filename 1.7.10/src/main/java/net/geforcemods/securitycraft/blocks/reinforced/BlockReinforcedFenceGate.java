package net.geforcemods.securitycraft.blocks.reinforced;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockRetinalScanner;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockReinforcedFenceGate extends BlockFenceGate implements ITileEntityProvider {

	public BlockReinforcedFenceGate(){
		super();
		ObfuscationReflectionHelper.setPrivateValue(Block.class, this, Material.iron, 34);
	}

	/**
	 * Called upon block activation (right click on the block.)
	 */
	public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
	{
		return false;
	}

	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5Block, int par6)
	{
		super.breakBlock(par1World, par2, par3, par4, par5Block, par6);
		par1World.removeTileEntity(par2, par3, par4);
	}

	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6)
	{
		super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
		TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);
		return tileentity != null ? tileentity.receiveClientEvent(par5, par6) : false;
	}

	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
	{
		if(world.getBlockMetadata(x, y, z) > 3)
			return;
		
		//so dropped items don't get destroyed
		if(entity instanceof EntityItem)
			return;
		//owner check
		else if(entity instanceof EntityPlayer)
		{
			if(((TileEntityOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner((EntityPlayer)entity))
				return;
		}
		else if(entity instanceof EntityCreeper)
		{
			EntityCreeper creeper = (EntityCreeper)entity;
			EntityLightningBolt lightning = new EntityLightningBolt(world, x, y, z);

			creeper.onStruckByLightning(lightning);
			creeper.extinguish();
			return;
		}

		entity.attackEntityFrom(CustomDamageSources.electricity, 6.0F); //3 hearts per attack
	}
	
	public void onNeighborBlockChange(World par1World, int x, int y, int z, Block neighborBlock) {
        if (!par1World.isRemote)
        {
            int l = par1World.getBlockMetadata(x, y, z);
            boolean flag = isSCBlock(neighborBlock) && par1World.isBlockIndirectlyGettingPowered(x, y, z);

            if (flag || neighborBlock.canProvidePower())
            {
                if (flag && !isFenceGateOpen(l))
                {
                	par1World.setBlockMetadataWithNotify(x, y, z, l | 4, 2);
                	par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
                }
                else if (!flag && isFenceGateOpen(l))
                {
                	par1World.setBlockMetadataWithNotify(x, y, z, l & -5, 2);
                	par1World.playAuxSFXAtEntity((EntityPlayer)null, 1003, x, y, z, 0);
                }
            }
        }
    }
	
	private boolean isSCBlock(Block block) {
    	return (block instanceof BlockLaserBlock || block instanceof BlockRetinalScanner ||
    			block instanceof BlockKeypad || block instanceof BlockKeycardReader || block instanceof BlockInventoryScanner);
    }

	/**
	 * Gets the block's texture. Args: side, meta
	 */
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int p_149691_1_, int p_149691_2_)
	{
		return mod_SecurityCraft.reinforcedDoor.getBlockTextureFromSide(p_149691_1_);
	}

	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityOwnable();
	}

}
