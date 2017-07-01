package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.HoverChecker;

public class GuiCameraMonitor extends GuiContainer {

	private static final ResourceLocation field_110410_t = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private InventoryPlayer playerInventory;
	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;

	private GuiButton prevPageButton;
	private GuiButton nextPageButton;
	private GuiButton[] cameraButtons = new GuiButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private TileEntitySCTE[] cameraTEs = new TileEntitySCTE[10];
	private int[] cameraViewDim = new int[10];

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

	@Override
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

		for(int i = 0; i < 10; i++) {
			GuiButton button = cameraButtons[i];
			int camID = (button.id + ((page - 1) * 10));
			ArrayList<CameraView> views = this.cameraMonitor.getCameraPositions(this.nbtTag);
			CameraView view;

			button.displayString += camID;
			this.buttonList.add(button);

			if((view = views.get(camID - 1)) != null) {
				if(view.dimension != Minecraft.getMinecraft().player.dimension) {
					hoverCheckers[button.id - 1] = new HoverChecker(button, 20);
					cameraViewDim[button.id - 1] = view.dimension;
				}

				if(BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation()) != mod_SecurityCraft.securityCamera) {
					button.enabled = false;
					cameraTEs[button.id - 1] = null;
					continue;
				}

				cameraTEs[button.id - 1] = (TileEntitySCTE) Minecraft.getMinecraft().world.getTileEntity(view.getLocation());
				hoverCheckers[button.id - 1] = new HoverChecker(button, 20);
			}
			else
			{
				button.enabled = false;
				cameraTEs[button.id - 1] = null;
				continue;
			}
		}

		if(page == 1) {
			prevPageButton.enabled = false;
		}

		if(page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10) + 1) {
			nextPageButton.enabled = false;
		}

		for(int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++) {
			cameraButtons[(i - 1) - ((page - 1) * 10)].enabled = false;
		}

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(int i = 0; i < hoverCheckers.length; i++){
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
				if(cameraTEs[i] == null) {
					this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.monitor.cameraInDifferentDim").replace("#", cameraViewDim[i] + ""), 150), mouseX, mouseY, this.mc.fontRenderer);
				}

				if(cameraTEs[i] != null && cameraTEs[i].hasCustomName()) {
					this.drawHoveringText(this.mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.monitor.cameraName").replace("#", cameraTEs[i].getCustomName()), 150), mouseX, mouseY, this.mc.fontRenderer);
				}
			}	
		}
	}

	@Override
	protected void actionPerformed(GuiButton guibutton) {	
		if(guibutton.id == -1) {
			this.mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page - 1));
		}
		else if(guibutton.id == 0) {
			this.mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page + 1));
		}
		else { 
			int camID = guibutton.id + ((page - 1) * 10);

			CameraView view = (this.cameraMonitor.getCameraPositions(this.nbtTag).get(camID - 1));

			if(BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation()) == mod_SecurityCraft.securityCamera) {
				((BlockSecurityCamera) BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation())).mountCamera(Minecraft.getMinecraft().world, view.x, view.y, view.z, camID, Minecraft.getMinecraft().player);
				mod_SecurityCraft.network.sendToServer(new PacketSMountCamera(view.x, view.y, view.z, camID));
				Minecraft.getMinecraft().player.closeScreen();
			}
			else {
				guibutton.enabled = false;
			}
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRenderer.drawString(ClientUtils.localize("gui.monitor.selectCameras"), this.xSize / 2 - this.fontRenderer.getStringWidth(ClientUtils.localize("gui.monitor.selectCameras")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(field_110410_t);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);  
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}