package net.geforcemods.securitycraft.gui;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiCameraMonitor extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private InventoryPlayer playerInventory;
	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;
	
	private GuiButton prevPageButton;
	private GuiButton nextPageButton;
	private GuiButton[] cameraButtons = new GuiButton[10];

	private int page = 1;

	public GuiCameraMonitor(InventoryPlayer inventory, ItemCameraMonitor item, NBTTagCompound itemNBTTag) {
        super(new ContainerGeneric(inventory, null));
        this.playerInventory = inventory;
		this.cameraMonitor = item;
		this.nbtTag = itemNBTTag;
	}

	public GuiCameraMonitor(InventoryPlayer inventory, ItemCameraMonitor item, NBTTagCompound itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@SuppressWarnings("unchecked")
	public void initGui(){
		super.initGui();
		
		prevPageButton = new GuiButton(-1, this.width / 2 - 68, this.height / 2 + 40, 20, 20, "<");
		nextPageButton = new GuiButton(0, this.width / 2 + 52, this.height / 2 + 40, 20, 20, ">");
		this.buttonList.add(prevPageButton);
		this.buttonList.add(nextPageButton);
		
		cameraButtons[0] = new GuiButton(1, this.width / 2 - 38, this.height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[1] = new GuiButton(2, this.width / 2 - 8, this.height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[2] = new GuiButton(3, this.width / 2 + 22, this.height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[3] = new GuiButton(4, this.width / 2 - 38, this.height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[4] = new GuiButton(5, this.width / 2 - 8, this.height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[5] = new GuiButton(6, this.width / 2 + 22, this.height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[6] = new GuiButton(7, this.width / 2 - 38, this.height / 2 + 10, 20, 20, "#");
		cameraButtons[7] = new GuiButton(8, this.width / 2 - 8, this.height / 2 + 10, 20, 20, "#");
		cameraButtons[8] = new GuiButton(9, this.width / 2 + 22, this.height / 2 + 10, 20, 20, "#");
		cameraButtons[9] = new GuiButton(10, this.width / 2 - 38, this.height / 2 + 40, 80, 20, "#");

		for(GuiButton button : cameraButtons) {
			button.displayString += (button.id + ((page - 1) * 10)); 
			this.buttonList.add(button);
			
			int camPos = (button.id + ((page - 1) * 10));
			if(camPos <= cameraMonitor.getCameraPositions(nbtTag).size()) {
				int[] cameraPos = ((int[]) this.cameraMonitor.getCameraPositions(this.nbtTag).get(camPos - 1));
				
				if(Minecraft.getMinecraft().theWorld.getBlock(cameraPos[0], cameraPos[1], cameraPos[2]) != mod_SecurityCraft.securityCamera) {
					button.enabled = false;
				}
			}
		}
		
		if(page == 1) {
			prevPageButton.enabled = false;
		}
		
		if(page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10)) {
			nextPageButton.enabled = false;
		}
		
		for(int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++) {
			cameraButtons[(i - 1) - ((page - 1) * 10)].enabled = false;
		}
			
	}

	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
	}

	protected void actionPerformed(GuiButton guibutton) {	
		if(guibutton.id == -1) {
	        this.mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page - 1));
		}
		else if(guibutton.id == 0) {
	        this.mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page + 1));
		}
		else { 
			int camID = guibutton.id + ((page - 1) * 10);
			
			int[] cameraPos = ((int[]) this.cameraMonitor.getCameraPositions(this.nbtTag).get(camID - 1));

			if(Minecraft.getMinecraft().theWorld.getBlock(cameraPos[0], cameraPos[1], cameraPos[2]) == mod_SecurityCraft.securityCamera) {
				((BlockSecurityCamera) Minecraft.getMinecraft().theWorld.getBlock(cameraPos[0], cameraPos[1], cameraPos[2])).mountCamera(Minecraft.getMinecraft().theWorld, cameraPos[0], cameraPos[1], cameraPos[2], camID, Minecraft.getMinecraft().thePlayer);
				mod_SecurityCraft.network.sendToServer(new PacketSMountCamera(cameraPos[0], cameraPos[1], cameraPos[2], camID));
				Minecraft.getMinecraft().thePlayer.closeScreen();
			}
			else {
				guibutton.enabled = false;
			}
		}
	}
	
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        this.fontRendererObj.drawString(StatCollector.translateToLocal("gui.monitor.selectCameras"), this.xSize / 2 - this.fontRendererObj.getStringWidth(StatCollector.translateToLocal("gui.monitor.selectCameras")) / 2, 6, 4210752);
    }

	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(field_110410_t);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);  
	}

	public boolean doesGuiPauseGame() {
		return false;
	}

}