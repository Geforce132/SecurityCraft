package org.freeforums.geforce.securitycraft.tileentity;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;

import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class TileEntitySecurityCamera extends TileEntitySCTE{
	
	private boolean detectingPlayer = false;
	private String owner = "x";
	
	private String id1 = "", id2 = "", id3 = "", id4 = "", id5 = "", id6 = "", id7 = "", id8 = "", id9 = "", id10 = "";
	
	public void update(){
		
		if(this.worldObj.isRemote){
			return;
		}else{
		
			double d0 = 1;
			AxisAlignedBB axisalignedbb = AxisAlignedBB.fromBounds((double) pos.getX(), (double) pos.getY(), (double) pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1)).expand(d0, d0, d0);
			//System.out.println((int)axisalignedbb.minX + " | " + (int)axisalignedbb.minY + " | " + (int)axisalignedbb.minZ + " | " + (int)axisalignedbb.maxX + " | " + (int)axisalignedbb.maxY + " | " + (int)axisalignedbb.maxZ);
	        //axisalignedbb.maxY = (double)this.worldObj.getHeight();
	        List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, axisalignedbb);
	        Iterator iterator = list.iterator();
	        EntityPlayer entityplayer;
	        
	        if(id1 != ""){
		        if(iterator.hasNext()){
					if(!detectingPlayer && !isPlayerAllowed(((EntityPlayer)iterator.next()).getName())){
							detectingPlayer = true;
					}
					
					HelpfulMethods.updateAndNotify(worldObj, pos, mod_SecurityCraft.securityCamera, 1, true);
				}else{
					if(detectingPlayer){
						detectingPlayer = false;
					}
					
					HelpfulMethods.updateAndNotify(worldObj, pos, mod_SecurityCraft.securityCamera, 1, true);
				}		
	        }
		}
	}

	

	private boolean isPlayerAllowed(String username) {
		for(int i = 0; i < 10; i ++){
			if(username == this.getVariablesInArrayForm()[i]){
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}
	
	public String[] getVariablesInArrayForm(){
    	String[] array = {id1, id2, id3, id4, id5, id6, id7, id8, id9, id10};
    	return array;
    }
	
	public void setId(String par1, int par2){
    	
    	if(par2 == 0){
    		id1 = par1;
    		return;
    	}else if(par2 == 1){
    		id2 = par1;
    		return;
    	}else if(par2 == 2){
    		id3 = par1;
    		return;
    	}else if(par2 == 3){
    		id4 = par1;
    		return;
    	}else if(par2 == 4){
    		id5 = par1;
    		return;
    	}else if(par2 == 5){
    		id6 = par1;
    		return;
    	}else if(par2 == 6){
    		id7 = par1;
    		return;
    	}else if(par2 == 7){
    		id8 = par1;
    		return;
    	}else if(par2 == 8){
    		id9 = par1;
    		return;
    	}else if(par2 == 9){
    		id10 = par1;
    		return;
    	}
    	
    }
	
	public String getId(int par1){
    	
    	switch(par1){
    		case 1:
    			return id1;
    		case 2:
    			return id2;
    		case 3:
    			return id3;
    		case 4:
    			return id4;
    		case 5:
    			return id5;
    		case 6:
    			return id6;
    		case 7:
    			return id7;
    		case 8:
    			return id8;
    		case 9:
    			return id9;
    		case 10:
    			return id10;
    		default:
    			return "";
    	}
    	
    }



	private void checkPlayers(EntityPlayer par1EntityPlayer) {
		HelpfulMethods.updateAndNotify(worldObj, pos, mod_SecurityCraft.securityCamera, 1 , true);

	}
	
	/**
     * Reads a tile entity from NBT.
     */
    public void readFromNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.readFromNBT(par1NBTTagCompound);
        
        if (par1NBTTagCompound.hasKey("hasPlayers")){
        	this.detectingPlayer = par1NBTTagCompound.getBoolean("hasPlayers");
        }
        
        if (par1NBTTagCompound.hasKey("owner")){
        	this.owner = par1NBTTagCompound.getString("owner");
        }
        
        if (par1NBTTagCompound.hasKey("id1"))
        {
            this.id1 = par1NBTTagCompound.getString("id1");
        }
        if (par1NBTTagCompound.hasKey("id2"))
        {
            this.id2 = par1NBTTagCompound.getString("id2");
        }
        if (par1NBTTagCompound.hasKey("id3"))
        {
            this.id3 = par1NBTTagCompound.getString("id3");
        }
        if (par1NBTTagCompound.hasKey("id4"))
        {
            this.id4 = par1NBTTagCompound.getString("id4");
        }
        if (par1NBTTagCompound.hasKey("id5"))
        {
            this.id5 = par1NBTTagCompound.getString("id5");
        }
        if (par1NBTTagCompound.hasKey("id6"))
        {
            this.id6 = par1NBTTagCompound.getString("id6");
        }
        if (par1NBTTagCompound.hasKey("id7"))
        {
            this.id7 = par1NBTTagCompound.getString("id7");
        }
        if (par1NBTTagCompound.hasKey("id8"))
        {
            this.id8 = par1NBTTagCompound.getString("id8");
        }
        if (par1NBTTagCompound.hasKey("id9"))
        {
            this.id9 = par1NBTTagCompound.getString("id9");
        }
        if (par1NBTTagCompound.hasKey("id10"))
        {
            this.id10 = par1NBTTagCompound.getString("id10");
        }
        
        

    }

    /**
     * Writes a tile entity to NBT.
     */
    public void writeToNBT(NBTTagCompound par1NBTTagCompound)
    {
        super.writeToNBT(par1NBTTagCompound);
        
        par1NBTTagCompound.setString("id1", this.id1);
        par1NBTTagCompound.setString("id2", this.id2);
        par1NBTTagCompound.setString("id3", this.id3);
        par1NBTTagCompound.setString("id4", this.id4);
        par1NBTTagCompound.setString("id5", this.id5);
        par1NBTTagCompound.setString("id6", this.id6);
        par1NBTTagCompound.setString("id7", this.id7);
        par1NBTTagCompound.setString("id8", this.id8);
        par1NBTTagCompound.setString("id9", this.id9);
        par1NBTTagCompound.setString("id10", this.id10);
        
        
        

    }
    
    public boolean hasPlayer(){
    	if(this.detectingPlayer){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    public void setHasPlayer(boolean par1){
    	this.detectingPlayer = par1;
    }
    
    public String getOwner(){
    	return this.owner;
    }
    
    public void setOwner(String par1){
    	this.owner = par1;
    }
    
   

}
