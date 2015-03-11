package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketCUpdateNBTTag;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityMineLoc;

@SuppressWarnings("static-access")
public class ItemRemoteAccess extends ItemWithInfo{

	private final int remoteAccessVarity;
	
	public int listIndex = 0;
	
	public TileEntityMineLoc[] tEList = new TileEntityMineLoc[6];
	
	private Block[] allowedBlocks = {mod_SecurityCraft.Mine};
	
	public static ItemRemoteAccess activeRemote;
	public static EntityPlayer playerObj;
	public static World worldObj;

	public ItemRemoteAccess(int par1) {
		super("The mine remote access tool will allow you to access mines remotely. Right-click on a mine to 'bind' it to the tool. Right-click in the air (with the tool equipped) to open the tool's GUI, which will allow you to activate, deactivate, or detonate any bound mines.", new String[]{"The mine remote access tool requires: 1 redstone torch, 1 diamond, 1 gold ingot, 1 stick", " R ", " DG", "S  ", "R = redstone torch, D = diamond, G = gold ingot, S = stick"});
		this.remoteAccessVarity = par1;
	}
	
	public boolean isFull3D(){
		return true;
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
    
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, BlockPos pos, EnumFacing facing, float par8, float par9, float par10){
    	this.activeRemote = (ItemRemoteAccess) par1ItemStack.getItem();
  	  	this.playerObj = par2EntityPlayer;
  	  	this.worldObj = par3World;
  
  	  	if(par3World.isRemote){
  	  		return true;
  	  	}else{
  	  		if(isValidMine(par3World, pos)){
  	  			if(!isMineAdded(par1ItemStack, par3World, pos)){
		  	  		int availSlot = this.getNextAvaliableSlot(par1ItemStack);
		  	  		
		  	  		if(availSlot == 0){
		  	  			HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, "There are no more empty slots to bind this mine to!", EnumChatFormatting.RED);
		  	  			return false;
		  	  		}
		  	  		
		  	  		if(par1ItemStack.getTagCompound() == null){
		  	  			par1ItemStack.setTagCompound(new NBTTagCompound());
		  	  		}
		  	  		
		  	  		par1ItemStack.getTagCompound().setIntArray(("mine" + availSlot), new int[]{pos.getX(), pos.getY(), pos.getZ()});
		  	  		mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par2EntityPlayer);
					HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, par2EntityPlayer.getName() + " bound a mine at X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + " to a remote access tool.", null);
  	  			}else{
  	  				this.removeTagFromItemAndUpdate(par1ItemStack, pos, par2EntityPlayer);
  	  				HelpfulMethods.sendMessageToPlayer(par2EntityPlayer, par2EntityPlayer.getName() + " unbound a mine at X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + " from a remote access tool.", null);
  	  			}
  	  		}
  	  		
  	  		return true;
  	  	}
    }
    
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
    	if(par1ItemStack.getTagCompound() == null){
    		return;
    	}
    	
    	for(int i = 1; i <= 6; i++){
    		if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
    			int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);
    			
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

  
    private void removeTagFromItemAndUpdate(ItemStack par1ItemStack, BlockPos pos, EntityPlayer par5EntityPlayer) {
    	if(par1ItemStack.getTagCompound() == null){
			return;
		}
		
		for(int i = 1; i <= 6; i++){
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);
				
				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					par1ItemStack.getTagCompound().setIntArray("mine" + i, new int[]{0, 0, 0});
					mod_SecurityCraft.network.sendTo(new PacketCUpdateNBTTag(par1ItemStack), (EntityPlayerMP) par5EntityPlayer);
					return;
				}
			}else{
				continue;
			}
		}
    	
    	
    	return;
	}

	private boolean isMineAdded(ItemStack par1ItemStack, World par2World, BlockPos pos) {
		if(par1ItemStack.getTagCompound() == null){
			return false;
		}
		
		for(int i = 1; i <= 6; i++){
			if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length > 0){
				int[] coords = par1ItemStack.getTagCompound().getIntArray("mine" + i);
				
				if(coords[0] == pos.getX() && coords[1] == pos.getY() && coords[2] == pos.getZ()){
					return true;
				}
			}else{
				continue;
			}
		}
    	
    	
    	return false;
	}

	private boolean isValidMine(World par1World, BlockPos pos){
    	for(int i = 1; i <= this.allowedBlocks.length; i++){
    		if(par1World.getBlockState(pos).getBlock() == this.allowedBlocks[i - 1]){  
    			return true;
    		}else{
    			continue;
    		}
    	}

    	return false;
    }
    
    private int getNextAvaliableSlot(ItemStack par1ItemStack){
    	for(int i = 1; i <= 6; i++){
    		if(par1ItemStack.getTagCompound() == null){
    			return 1;
    		}else if(par1ItemStack.getTagCompound().getIntArray("mine" + i).length == 0 || (par1ItemStack.getTagCompound().getIntArray("mine" + i)[0] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[1] == 0 && par1ItemStack.getTagCompound().getIntArray("mine" + i)[2] == 0)){
    			return i;
    		}else{
    			continue;
    		}
    	}
    	
		return 0;
    }
}
