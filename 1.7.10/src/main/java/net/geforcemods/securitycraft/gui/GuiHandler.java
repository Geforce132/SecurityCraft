package net.geforcemods.securitycraft.gui;

import cpw.mods.fml.common.network.IGuiHandler;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.containers.ContainerCustomizeBlock;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.containers.ContainerInventoryScanner;
import net.geforcemods.securitycraft.containers.ContainerKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityRAM;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler {
	
	public static final int SETUP_PASSWORD_ID = 17;
	public static final int INSERT_PASSWORD_ID = 18;

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile_entity = world.getTileEntity(x, y, z);
				
    	switch(ID){
    	case 4:
    		return new ContainerGeneric(player.inventory, tile_entity);
    	
    	case 5:
    		return new ContainerGeneric(player.inventory, tile_entity);
    	
    	case 6:
    		return new ContainerGeneric(player.inventory, tile_entity);
    	
    	case 7:
    		return new ContainerGeneric(player.inventory, tile_entity);
    	
    	case 8:
    		return new ContainerGeneric(player.inventory, tile_entity);

    	case 9:
    		return new ContainerInventoryScanner(player.inventory, (TileEntityInventoryScanner) tile_entity);
    	
    	case 11:
    		return new ContainerGeneric(player.inventory, tile_entity);
    		
    	case 16:
    		return new ContainerKeypadFurnace(player.inventory, (TileEntityKeypadFurnace) tile_entity);
    		
    	case 17:
    		return new ContainerGeneric(player.inventory, tile_entity);
    	
    	case 18:
    		return new ContainerGeneric(player.inventory, tile_entity);
    		
    	case 19:
    		return new ContainerGeneric(player.inventory, tile_entity);
    		
    		
    	case 100:
        	return new ContainerCustomizeBlock(player.inventory, (CustomizableSCTE) tile_entity);
        	
    	default:
        	return null;
    	}
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile_entity = world.getTileEntity(x, y, z);
		
		switch(ID){	
    	case 4:
    		return new GuiKeycardSetup(player.inventory, (TileEntityKeycardReader) tile_entity);
    	
    	case 5:
    		return new GuiRemoteAccessMine(player.inventory, (TileEntityRAM) tile_entity);
    		
    	case 6:
    		return new GuiRAMActivate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 7:
    		return new GuiRAMDeactivate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 8:
    		return new GuiRAMDetonate(player.inventory, (TileEntityRAM) tile_entity, player.getCurrentEquippedItem());
    	
    	case 9:
    		return new GuiInventoryScanner(player.inventory, (TileEntityInventoryScanner) tile_entity, player);
    	
    	case 11:
    		return new GuiLogger(player.inventory, (TileEntityLogger) tile_entity);
    		
    	case 16:
    		return new GuiKeypadFurnaceInventory(player.inventory, (TileEntityKeypadFurnace) tile_entity);
    	
    	case 17:
    		return new GuiSetPassword(player.inventory, tile_entity, world.getBlock(x, y, z));
    		
    	case 18:
    		return new GuiCheckPassword(player.inventory, tile_entity, world.getBlock(x, y, z));
    		
    	case 19:
    		return new GuiIMS(player.inventory, (TileEntityIMS) tile_entity);
    		
    		
    	case 100:
    		return new GuiCustomizeBlock(player.inventory, (CustomizableSCTE) tile_entity);
    		
    	default:
        	return null;
		}
	}

}
