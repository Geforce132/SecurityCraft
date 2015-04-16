package org.freeforums.geforce.securitycraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

import org.freeforums.geforce.securitycraft.items.ItemCameraMonitor;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.network.packets.PacketMountCamera;

public class GuiCameraMonitorOverlay extends GuiScreen {
	
	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;
	
	private int page = 1;
	
	public GuiCameraMonitorOverlay(ItemCameraMonitor item, NBTTagCompound itemNBTTag){
		super();
		this.cameraMonitor = item;
		this.nbtTag = itemNBTTag;
	}
	
	public GuiCameraMonitorOverlay(ItemCameraMonitor item, NBTTagCompound itemNBTTag, int page){
		this(item, itemNBTTag);
		this.page = page;
	}
	
	public void initGui(){
		super.initGui();
		
		this.buttonList.add(new GuiButton(((page * 5) - 4), 5, this.height - 45, 20, 20, "<"));
        
		System.out.println("'<' button: " + ((page * 5) - 4) + " '>' button: " + ((page * 5) + 2) + " Camera buttons: " + ((page * 5) - 3) + " thru " + ((page * 5) + 1) + " Array size: " + cameraMonitor.getCameraPositions(nbtTag).size());
		int counter = 1;
		for(int i = ((page * 5) - 3); i <= ((page * 5) + 1); i++){
			if(i - 2 < cameraMonitor.getCameraPositions(nbtTag).size() && cameraMonitor.getCameraPositions(nbtTag).get(i - 2) != null){
				this.buttonList.add(new GuiButton(i, -25 + ((counter) * 70), this.height - 45, 60, 20, "Camera #" + (i - 1)));
			}
			
			counter += 1;
        }
		
//		int counter = 1;
//		for(int i = ((page * 5) - 3); i <= 5; i++){
//			if(i < cameraMonitor.getCameraPositions(nbtTag).size() && cameraMonitor.getCameraPositions(nbtTag).get(i) != null){
//				this.buttonList.add(new GuiButton(i, -25 + ((counter) * 70), this.height - 45, 60, 20, "Camera #" + (i - 1)));
//			}
//			
//			counter += 1;
//		}
//		
		
		
		this.buttonList.add(new GuiButton(((page * 5) + 2), this.width - 25, this.height - 45, 20, 20, ">"));
    }
	
    public void onGuiClosed(){
    	HelpfulMethods.setCameraZoom(0D);
    }
	
	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
    }
	
	protected void actionPerformed(GuiButton guibutton){
		System.out.println(guibutton.id);

		if(guibutton.id == ((page * 5) - 4) && page > 1){
			this.mc.displayGuiScreen(new GuiCameraMonitorOverlay(cameraMonitor, nbtTag, page -= 1));
		}else if(guibutton.id == ((page * 5) + 2) && cameraMonitor.getCameraPositions(nbtTag).size() > ((page * 5) - 1)){
			this.mc.displayGuiScreen(new GuiCameraMonitorOverlay(cameraMonitor, nbtTag, page += 1));
		}else if(guibutton.id > page && guibutton.id <= (page * 6) ){
			mod_SecurityCraft.network.sendToServer(new PacketMountCamera(cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 2)[0], cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 2)[1], cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 2)[2]));
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
		
//		if(guibutton.id == ((page * 5) - 4) && page > 1){
//			this.mc.displayGuiScreen(new GuiCameraMonitorOverlay(cameraMonitor, nbtTag, page -= 1));
//		}else if(guibutton.id == ((page * 5) + 2) && cameraMonitor.getCameraPositions(nbtTag).size() > ((page * 5) - 1)){
//			this.mc.displayGuiScreen(new GuiCameraMonitorOverlay(cameraMonitor, nbtTag, page += 1));
//		}else if(guibutton.id > page && guibutton.id <= (page * 6) ){
//			mod_SecurityCraft.network.sendToServer(new PacketMountCamera(cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 1)[0], cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 1)[1], cameraMonitor.getCameraPositions(nbtTag).get(guibutton.id - 1)[2]));
//			Minecraft.getMinecraft().thePlayer.closeScreen();
//		}
	}

	public boolean doesGuiPauseGame()
    {
        return false;
    }
	
}
