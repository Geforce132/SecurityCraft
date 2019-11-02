package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateNBTTag;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SuppressWarnings("static-access")
public class ItemRemoteAccess extends Item{

	private final int remoteAccessVarity;
	
	public int listIndex = 0;
	
	public TileEntityMineLoc[] tEList = new TileEntityMineLoc[6];
	
	private Block[] allowedBlocks = {mod_SecurityCraft.Mine, mod_SecurityCraft.MineCut};
	
	public static ItemRemoteAccess activeRemote;
	public static EntityPlayer playerObj;
	public static World worldObj;

	public ItemRemoteAccess(int par1) {
		super();
		this.remoteAccessVarity = par1;
	}
	
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){
    	this.activeRemote = (ItemRemoteAccess) par1ItemStack.getItem();
  	    this.playerObj = par3EntityPlayer;
  	    this.worldObj = par2World;
  	    
    	if(par2World.isRemote){
    		return par1ItemStack;
    	}else{
    		if(this.remoteAccessVarity == 1){
    			par3EntityPlayer.openGui(mod_SecurityCraft.instance, 5, par2World, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);
    		}
    		
    		return par1ItemStack;
    	}
    	
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
    	this.activeRemote = (ItemRemoteAccess) par1ItemStack.getItem();
  	  	this.playerObj = par2EntityPlayer;
  	  	this.worldObj = par3World;
  
  	  	if(par3World.isRemote){
  	  		return true;
  	  	}else{
  	  		if(isValidMine(par3World, par4, par5, par6)){
  	  			if(!isMineAdded(par1ItemStack, par3World, par4, par5, par6)){
		  	  		int availSlot = this.getNextAvaliableSlot(par1ItemStack);
		  	  		
		  	  		if(availSlot == 0){
		  	  			HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "There are no more empty slots to bind this mine to!", EnumChatFormatting.RED);
		  	  			return false;
		  	  		}
		  	  		
		  	  		if(par1ItemStack.stackTagCompound == null){
		  	  			par1ItemStack.stackTagCompound = new NBTTagCompound();
		  	  		}
		  	  		
		  	  		par1ItemStack.stackTagCompound.setIntArray(("mine" + availSlot), new int[]{par4, par5, par6});
		  	  		mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, par2EntityPlayer.getCommandSenderName() + " bound a mine at X:" + par4 + " Y:" + par5 + " Z:" + par6 + " to a remote access tool.", null);
  	  			}else{
  	  				this.removeTagFromItemAndUpdate(par1ItemStack, par4, par5, par6, par2EntityPlayer);
  	  				HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, par2EntityPlayer.getCommandSenderName() + " unbound a mine at X:" + par4 + " Y:" + par5 + " Z:" + par6 + " from a remote access tool.", null);
  	  			}
  	  		}
  	  		
  	  		return true;
  	  	}
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	if(par1ItemStack.stackTagCompound == null){
    		return;
    	}
    	
    	for(int i = 1; i <= 6; i++){
    		if(par1ItemStack.stackTagCompound.getIntArray("mine" + i).length > 0){
    			int[] coords = par1ItemStack.stackTagCompound.getIntArray("mine" + i);
    			
    			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0){
    				par3List.add("---");
    				continue;
    			}else{
    				par3List.add("Mine " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
    			}
    		}else{
				par3List.add("---");
    		}
    	}
    }

  
    private void removeTagFromItemAndUpdate(ItemStack par1ItemStack, int par2, int par3, int par4, EntityPlayer par5EntityPlayer) {
    	if(par1ItemStack.stackTagCompound == null){
			return;
		}
		
		for(int i = 1; i <= 6; i++){
			if(par1ItemStack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.stackTagCompound.getIntArray("mine" + i);
				
				if(coords[0] == par2 && coords[1] == par3 && coords[2] == par4){
					par1ItemStack.stackTagCompound.setIntArray("mine" + i, new int[]{0, 0, 0});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par5EntityPlayer);
					return;
				}
			}else{
				continue;
			}
		}
    	
    	
    	return;
	}

	private boolean isMineAdded(ItemStack par1ItemStack, World par2World, int par3, int par4, int par5) {
		if(par1ItemStack.stackTagCompound == null){
			return false;
		}
		
		for(int i = 1; i <= 6; i++){
			if(par1ItemStack.stackTagCompound.getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.stackTagCompound.getIntArray("mine" + i);
				
				if(coords[0] == par3 && coords[1] == par4 && coords[2] == par5){
					return true;
				}
			}else{
				continue;
			}
		}
    	
    	
    	return false;
	}

	private boolean isValidMine(World par1World, int par2, int par3, int par4){
    	for(int i = 1; i <= this.allowedBlocks.length; i++){
    		if(par1World.getBlock(par2, par3, par4) == this.allowedBlocks[i - 1]){  
    			return true;
    		}else{
    			continue;
    		}
    	}

    	return false;
    }
    
    private int getNextAvaliableSlot(ItemStack par1ItemStack){
    	for(int i = 1; i <= 6; i++){
    		if(par1ItemStack.stackTagCompound == null){
    			return 1;
    		}else if(par1ItemStack.stackTagCompound.getIntArray("mine" + i).length == 0 || (par1ItemStack.stackTagCompound.getIntArray("mine" + i)[0] == 0 && par1ItemStack.stackTagCompound.getIntArray("mine" + i)[1] == 0 && par1ItemStack.stackTagCompound.getIntArray("mine" + i)[2] == 0)){
    			return i;
    		}else{
    			continue;
    		}
    	}
    	
		return 0;
    }
}
