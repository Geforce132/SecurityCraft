package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.nbt.NBTTagCompound;

public class GuiCameraMonitor extends GuiScreen {

	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;
	private int page = 1;

	public GuiCameraMonitor(ItemCameraMonitor item, NBTTagCompound itemNBTTag){
		this.cameraMonitor = item;
		this.nbtTag = itemNBTTag;
	}

	public GuiCameraMonitor(ItemCameraMonitor item, NBTTagCompound itemNBTTag, int page){
		this(item, itemNBTTag);
		this.page = page;
	}

	public void initGui(){
		super.initGui();

		this.buttonList.add(new GuiButton(this.page * 5 - 4, 5, this.height - 45, 20, 20, "<"));

		mod_SecurityCraft.log("'<' button: " + (this.page * 5 - 4) + " '>' button: " + (this.page * 5 + 2) + " Camera buttons: " + (this.page * 5 - 3) + " thru " + (this.page * 5 + 1) + " Array size: " + this.cameraMonitor.getCameraPositions(this.nbtTag).size());
		
		int counter = 1;
		for(int i = ((page * 5) - 3); i <= ((page * 5) + 1); i++){
			if((i - 2) < this.cameraMonitor.getCameraPositions(this.nbtTag).size() && this.cameraMonitor.getCameraPositions(this.nbtTag).get(i - 2) != null){
				GuiButton button = new GuiButton(i, -25 + counter * 70, this.height - 45, 60, 20, "Camera #" + (i - 1));
				
				this.buttonList.add(button);
				
				if(Minecraft.getMinecraft().theWorld.getBlock(this.cameraMonitor.getCameraPositions(this.nbtTag).get(i - 2)[0], this.cameraMonitor.getCameraPositions(this.nbtTag).get(i - 2)[1], this.cameraMonitor.getCameraPositions(this.nbtTag).get(i - 2)[2]) != mod_SecurityCraft.securityCamera){
					button.enabled = false;
				}
			}

			counter++;
		}

		this.buttonList.add(new GuiButton(this.page * 5 + 2, this.width - 25, this.height - 45, 20, 20, ">"));
	}

	public void drawScreen(int par1, int par2, float par3){
		super.drawScreen(par1, par2, par3);
	}

	protected void actionPerformed(GuiButton guibutton) {
		if(guibutton.id == (this.page * 5 - 4) && this.page > 1){
			this.mc.displayGuiScreen(new GuiCameraMonitor(this.cameraMonitor, this.nbtTag, this.page--));
		}else if((guibutton.id == this.page * 5 + 2) && (this.cameraMonitor.getCameraPositions(this.nbtTag).size() > this.page * 5 - 1)) {
			this.mc.displayGuiScreen(new GuiCameraMonitor(this.cameraMonitor, this.nbtTag, this.page++));
		}else if((guibutton.id > this.page) && (guibutton.id <= this.page * 6)){ 
			int[] cameraPos = ((int[])this.cameraMonitor.getCameraPositions(this.nbtTag).get(guibutton.id - 2));
			((BlockSecurityCamera) Minecraft.getMinecraft().theWorld.getBlock(cameraPos[0], cameraPos[1], cameraPos[2])).mountCamera(Minecraft.getMinecraft().theWorld, cameraPos[0], cameraPos[1], cameraPos[2], guibutton.id + 1, Minecraft.getMinecraft().thePlayer);
			mod_SecurityCraft.network.sendToServer(new PacketSMountCamera(cameraPos[0], cameraPos[1], cameraPos[2], guibutton.id + 1));
			Minecraft.getMinecraft().thePlayer.closeScreen();
		}
	}

	public boolean doesGuiPauseGame(){
		return false;
	}
	
}