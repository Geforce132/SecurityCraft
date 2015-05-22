package org.freeforums.geforce.securitycraft.blocks;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.Utils.BlockUtils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCageTrap extends BlockOwnable implements IHelpInfo {
	
	public final boolean deactivated;
	private final int blockTextureIndex;
	
	@SideOnly(Side.CLIENT)
	private IIcon topIcon;

	public BlockCageTrap(Material par2Material, boolean deactivated, int blockTextureIndex) {
		super(par2Material);
		this.deactivated = deactivated;
		this.blockTextureIndex = blockTextureIndex;
	}
	
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
		if(!deactivated){
			return null;
		}else{
			return AxisAlignedBB.getBoundingBox((double)par2 + this.minX, (double)par3 + this.minY, (double)par4 + this.minZ, (double)par2 + this.maxX, (double)par3 + this.maxY, (double)par4 + this.maxZ);
		}
    }
	
	
	 public void onEntityCollidedWithBlock(World par1World, int par2, int par3, int par4, Entity par5Entity){
		 if(par1World.isRemote){
			 return;
		 }else{
			 if(par5Entity instanceof EntityPlayer && !deactivated){
				 par1World.setBlock(par2, par3, par4, mod_SecurityCraft.deactivatedCageTrap);

				 par1World.setBlock(par2, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);
				 par1World.setBlock(par2 + 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				 par1World.setBlock(par2 - 1, par3 + 4, par4, mod_SecurityCraft.unbreakableIronBars);	
				 par1World.setBlock(par2, par3 + 4, par4 + 1, mod_SecurityCraft.unbreakableIronBars);	
				 par1World.setBlock(par2, par3 + 4, par4 - 1, mod_SecurityCraft.unbreakableIronBars);	

				 BlockUtils.setBlockInBox(par1World, par2, par3, par4, mod_SecurityCraft.unbreakableIronBars);

				 par1World.playSoundAtEntity(par5Entity, "random.anvil_use", 3.0F, 1.0F);
				 MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation(((EntityPlayer) par5Entity).getCommandSenderName() + " was captured in a trap at" + Utils.getFormattedCoordinates(par2, par3, par4)));
			 }
		 }
	 }
	 
	 public IIcon getIcon(int par1, int par2){
		 if(this.blockTextureIndex == 9999){
			 return par1 == 1 ? this.topIcon : this.blockIcon;
		 }else{
			 return this.blockIcon;
		 }
	 }
	 
	 /**
	   * Returns the quantity of items to drop on block destruction.
	   */
	 public int quantityDropped(Random par1Random){
		 return this.deactivated ? 0 : 1;
	 }
	 
   /**
     * Returns the ID of the items to drop on destruction.
     */
    public Item getItemDropped(int par1, Random par2Random, int par3)
    {
        return this.deactivated ? BlockUtils.getItemFromBlock(mod_SecurityCraft.deactivatedCageTrap) : BlockUtils.getItemFromBlock(this);
    }
	  
    public void registerIcons(IIconRegister par1IconRegister){
    	if(this.blockTextureIndex == 9999){
    		this.blockIcon = par1IconRegister.registerIcon("mod/cageTrapTop");
    		this.topIcon = par1IconRegister.registerIcon("mod/cageTrapSides");
    	}else{
    		//this.blockIcon = par1IconRegister.registerIcon(Block.blocksList[blockTextureIndex].getTextureName());
    	}
    }

	public String[] getRecipe() {
		return new String[]{"The cage trap requires: 3 reinforced iron bars, 2 gold ingots, 1 redstone, 3 iron blocks", "WWW", "XYX", "ZZZ", "W = reinforced iron bars, X = gold ingot, Y = redstone, Z = iron block"};
	}
		
}
