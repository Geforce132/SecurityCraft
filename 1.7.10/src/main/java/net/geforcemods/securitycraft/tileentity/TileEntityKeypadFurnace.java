package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;

public class TileEntityKeypadFurnace extends TileEntityFurnace implements IOwnable, ISidedInventory, IPasswordProtected {

	private String passcode;
	private Owner owner = new Owner();
	
    public void updateEntity(){
        boolean flag = this.furnaceBurnTime > 0;
        boolean flag1 = false;

        if (this.furnaceBurnTime > 0){
            this.furnaceBurnTime--;
        }

        if(!this.worldObj.isRemote){
            if(this.furnaceBurnTime != 0 || this.getStackInSlot(1) != null && this.getStackInSlot(0) != null){
                if(this.furnaceBurnTime == 0 && this.canSmelt()){
                    this.currentItemBurnTime = this.furnaceBurnTime = getItemBurnTime(this.getStackInSlot(1));

                    if(this.furnaceBurnTime > 0){
                        flag1 = true;

                        if(this.getStackInSlot(1) != null){
                            this.getStackInSlot(1).stackSize--;

                            if(this.getStackInSlot(1).stackSize == 0){
                                this.setInventorySlotContents(1, this.getStackInSlot(1).getItem().getContainerItem(this.getStackInSlot(1)));
                            }
                        }
                    }
                }

                if(this.isBurning() && this.canSmelt()){
                    this.furnaceCookTime++;

                    if(this.furnaceCookTime == 200){
                        this.furnaceCookTime = 0;
                        this.smeltItem();
                        flag1 = true;
                    }
                }else{
                    this.furnaceCookTime = 0;
                }
            }

            if(flag != this.furnaceBurnTime > 0){
                flag1 = true;
            }
        }

        if(flag1){
            this.markDirty();
        }
    }

	
	private boolean canSmelt(){
        if (this.getStackInSlot(0) == null){
            return false;
        }else{
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.getStackInSlot(0));
            if (itemstack == null) return false;
            if (this.getStackInSlot(2) == null) return true;
            if (!this.getStackInSlot(2).isItemEqual(itemstack)) return false;
            int result = this.getStackInSlot(2).stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.getStackInSlot(2).getMaxStackSize(); 
        }
    }
	
	public void writeToNBT(NBTTagCompound par1NBTTagCompound){
		super.writeToNBT(par1NBTTagCompound);    
		
		if(this.passcode != null && !this.passcode.isEmpty()){
        	par1NBTTagCompound.setString("passcode", this.passcode);
        }
		
		if(this.owner != null){
        	par1NBTTagCompound.setString("owner", this.owner.getName());
        	par1NBTTagCompound.setString("ownerUUID", this.owner.getUUID());
        }
	}
	
	public void readFromNBT(NBTTagCompound par1NBTTagCompound){
		super.readFromNBT(par1NBTTagCompound);   
		
		if (par1NBTTagCompound.hasKey("passcode"))
        {
        	if(par1NBTTagCompound.getInteger("passcode") != 0){
        		this.passcode = String.valueOf(par1NBTTagCompound.getInteger("passcode"));
        	}else{
        		this.passcode = par1NBTTagCompound.getString("passcode");
        	}
        }
		
		if (par1NBTTagCompound.hasKey("owner"))
        {
            this.owner.setOwnerName(par1NBTTagCompound.getString("owner"));
        }
        
        if (par1NBTTagCompound.hasKey("ownerUUID"))
        {
            this.owner.setOwnerUUID(par1NBTTagCompound.getString("ownerUUID"));
        }
	}
	
	public Packet getDescriptionPacket() {                
    	NBTTagCompound tag = new NBTTagCompound();                
    	this.writeToNBT(tag);                
    	return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);        
    }        
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {                
    	readFromNBT(packet.func_148857_g());        
    }
	
	public Owner getOwner(){
    	return owner;
    }
    
	public void activate(EntityPlayer player) {
		if(!worldObj.isRemote && worldObj.getBlock(xCoord, yCoord, zCoord) instanceof BlockKeypadFurnace){
			BlockKeypadFurnace.activate(worldObj, xCoord, yCoord, zCoord, player);
    	}
	}

	public String getPassword() {
		return passcode;
	}

	public void setPassword(String password) {
		passcode = password;
	}

}