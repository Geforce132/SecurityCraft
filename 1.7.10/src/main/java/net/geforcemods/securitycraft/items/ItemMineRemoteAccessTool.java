package net.geforcemods.securitycraft.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.Utils;
import net.geforcemods.securitycraft.main.Utils.BlockUtils;
import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMineRemoteAccessTool extends Item {
	
	public int listIndex = 0;
			
	public ItemMineRemoteAccessTool() {
		super();
	}
	
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer){  	    
    	if(par2World.isRemote){
    		return par1ItemStack;
    	}else{
    		par3EntityPlayer.openGui(mod_SecurityCraft.instance, 5, par2World, (int)par3EntityPlayer.posX, (int)par3EntityPlayer.posY, (int)par3EntityPlayer.posZ);
    		return par1ItemStack;
    	}
    }
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
    	if(!par3World.isRemote){
  	  		if(par3World.getBlock(par4, par5, par6) instanceof IExplosive){
  	  			if(!isMineAdded(par1ItemStack, par3World, par4, par5, par6)){
		  	  		int availSlot = this.getNextAvaliableSlot(par1ItemStack);
		  	  		
		  	  		if(availSlot == 0){
		  	  			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.noSlots"), EnumChatFormatting.RED);
		  	  			return false;
		  	  		}
		  	  		
		  	  		if(par3World.getTileEntity(par4, par5, par6) instanceof IOwnable && !BlockUtils.isOwnerOfBlock((IOwnable) par3World.getTileEntity(par4, par5, par6), par2EntityPlayer)){
		  	  			PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.cantBind"), EnumChatFormatting.RED);
		  	  			return false;
		  	  		}
		  	  		
		  	  		if(par1ItemStack.stackTagCompound == null){
		  	  			par1ItemStack.stackTagCompound = new NBTTagCompound();
		  	  		}
		  	  		
		  	  		par1ItemStack.stackTagCompound.setIntArray(("mine" + availSlot), new int[]{par4, par5, par6});
		  	  		mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
		  	  		PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.bound").replace("#", Utils.getFormattedCoordinates(par4, par5, par6)), EnumChatFormatting.GREEN);
  	  			}else{
  	  				this.removeTagFromItemAndUpdate(par1ItemStack, par4, par5, par6, par2EntityPlayer);
  	  				PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("item.remoteAccessMine.name"), StatCollector.translateToLocal("messages.mrat.unbound").replace("#", Utils.getFormattedCoordinates(par4, par5, par6)), EnumChatFormatting.RED);
  	  			}
  	  		}else{
    			par2EntityPlayer.openGui(mod_SecurityCraft.instance, 5, par3World, (int) par2EntityPlayer.posX, (int) par2EntityPlayer.posY, (int) par2EntityPlayer.posZ);
  	  		}
  	  	}
    	
	  	return true;
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
    				par3List.add(StatCollector.translateToLocal("tooltip.mine") + " " + i + ": X:" + coords[0] + " Y:" + coords[1] + " Z:" + coords[2]);
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
