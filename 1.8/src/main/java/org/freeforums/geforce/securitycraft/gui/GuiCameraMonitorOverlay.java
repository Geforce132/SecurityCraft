package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;

import org.freeforums.geforce.securitycraft.items.ItemCameraMonitor;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketMountCamera;

public class GuiCameraMonitorOverlay extends GuiScreen {
	
	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;
	
	public GuiCameraMonitorOverlay(ItemCameraMonitor item, NBTTagCompound itemNBTTag){
		super();
		this.cameraMonitor = item;
		this.nbtTag = itemNBTTag;
	}
	
	public void initGui(){
		super.initGui();
		
        for(int i = 0; i < cameraMonitor.getCameraPositions(nbtTag).size(); i++){
        	this.buttonList.add(new GuiButton(i, 5 + ((i) * 70), this.height - 45, 60, 20, "Camera #" + (i + 1)));
        }
    }
	
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
    }
	
	protected void actionPerformed(GuiButton guibutton){
		mod_SecurityCraft.network.sendToServer(new PacketMountCamera(cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id).getX(), cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id).getY(), cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id).getZ()));
	}

	public boolean doesGuiPauseGame()
    {
        return false;
    }
	
}
