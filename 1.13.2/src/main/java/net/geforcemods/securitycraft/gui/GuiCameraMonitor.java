package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.blocks.BlockSecurityCamera;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.items.ItemCameraMonitor;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.geforcemods.securitycraft.network.packets.PacketSRemoveCameraTag;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.HoverChecker;

public class GuiCameraMonitor extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	private InventoryPlayer playerInventory;
	private ItemCameraMonitor cameraMonitor;
	private NBTTagCompound nbtTag;

	private GuiButton prevPageButton;
	private GuiButton nextPageButton;
	private GuiButton[] cameraButtons = new GuiButton[10];
	private GuiButton[] unbindButtons = new GuiButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private TileEntitySCTE[] cameraTEs = new TileEntitySCTE[10];
	private int[] cameraViewDim = new int[10];

	private int page = 1;

	public GuiCameraMonitor(InventoryPlayer inventory, ItemCameraMonitor item, NBTTagCompound itemNBTTag) {
		super(new ContainerGeneric(inventory, null));
		playerInventory = inventory;
		cameraMonitor = item;
		nbtTag = itemNBTTag;
	}

	public GuiCameraMonitor(InventoryPlayer inventory, ItemCameraMonitor item, NBTTagCompound itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@Override
	public void initGui(){
		super.initGui();

		prevPageButton = new GuiButton(-1, width / 2 - 68, height / 2 + 40, 20, 20, "<");
		nextPageButton = new GuiButton(0, width / 2 + 52, height / 2 + 40, 20, 20, ">");
		buttonList.add(prevPageButton);
		buttonList.add(nextPageButton);

		cameraButtons[0] = new GuiButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[1] = new GuiButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[2] = new GuiButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, "#");
		cameraButtons[3] = new GuiButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[4] = new GuiButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[5] = new GuiButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, "#");
		cameraButtons[6] = new GuiButton(7, width / 2 - 38, height / 2 + 10, 20, 20, "#");
		cameraButtons[7] = new GuiButton(8, width / 2 - 8, height / 2 + 10, 20, 20, "#");
		cameraButtons[8] = new GuiButton(9, width / 2 + 22, height / 2 + 10, 20, 20, "#");
		cameraButtons[9] = new GuiButton(10, width / 2 - 38, height / 2 + 40, 80, 20, "#");

		unbindButtons[0] = new GuiButton(11, width / 2 - 19, height / 2 - 68 + 10, 8, 8, "x");
		unbindButtons[1] = new GuiButton(12, width / 2 + 11, height / 2 - 68 + 10, 8, 8, "x");
		unbindButtons[2] = new GuiButton(13, width / 2 + 41, height / 2 - 68 + 10, 8, 8, "x");
		unbindButtons[3] = new GuiButton(14, width / 2 - 19, height / 2 - 38 + 10, 8, 8, "x");
		unbindButtons[4] = new GuiButton(15, width / 2 + 11, height / 2 - 38 + 10, 8, 8, "x");
		unbindButtons[5] = new GuiButton(16, width / 2 + 41, height / 2 - 38 + 10, 8, 8, "x");
		unbindButtons[6] = new GuiButton(17, width / 2 - 19, height / 2 + 2, 8, 8, "x");
		unbindButtons[7] = new GuiButton(18, width / 2 + 11, height / 2 + 2, 8, 8, "x");
		unbindButtons[8] = new GuiButton(19, width / 2 + 41, height / 2 + 2, 8, 8, "x");
		unbindButtons[9] = new GuiButton(20, width / 2 + 41, height / 2 + 32, 8, 8, "x");

		for(int i = 0; i < 10; i++) {
			GuiButton button = cameraButtons[i];
			int camID = (button.id + ((page - 1) * 10));
			ArrayList<CameraView> views = cameraMonitor.getCameraPositions(nbtTag);
			CameraView view;

			button.displayString += camID;
			buttonList.add(button);

			if((view = views.get(camID - 1)) != null) {
				if(view.dimension != Minecraft.getMinecraft().player.dimension) {
					hoverCheckers[button.id - 1] = new HoverChecker(button, 20);
					cameraViewDim[button.id - 1] = view.dimension;
				}

				if(BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation()) != SCContent.securityCamera) {
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
				unbindButtons[button.id - 1].enabled = false;
				cameraTEs[button.id - 1] = null;
				continue;
			}
		}

		for(int i = 0; i < 10; i++)
			buttonList.add(unbindButtons[i]);

		if(page == 1)
			prevPageButton.enabled = false;

		if(page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.enabled = false;

		for(int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++)
			cameraButtons[(i - 1) - ((page - 1) * 10)].enabled = false;

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(int i = 0; i < hoverCheckers.length; i++)
			if(hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)){
				if(cameraTEs[i] == null)
					this.drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraInDifferentDim").replace("#", cameraViewDim[i] + ""), 150), mouseX, mouseY, mc.fontRenderer);

				if(cameraTEs[i] != null && cameraTEs[i].hasCustomName())
					this.drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(ClientUtils.localize("gui.securitycraft:monitor.cameraName").replace("#", cameraTEs[i].getCustomName()), 150), mouseX, mouseY, mc.fontRenderer);
			}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if(button.id == -1)
			mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page - 1));
		else if(button.id == 0)
			mc.displayGuiScreen(new GuiCameraMonitor(playerInventory, cameraMonitor, nbtTag, page + 1));
		else if (button.id < 11){
			int camID = button.id + ((page - 1) * 10);

			CameraView view = (cameraMonitor.getCameraPositions(nbtTag).get(camID - 1));

			if(BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation()) == SCContent.securityCamera) {
				((BlockSecurityCamera) BlockUtils.getBlock(Minecraft.getMinecraft().world, view.getLocation())).mountCamera(Minecraft.getMinecraft().world, view.x, view.y, view.z, camID, Minecraft.getMinecraft().player);
				SecurityCraft.network.sendToServer(new PacketSMountCamera(view.x, view.y, view.z, camID));
				Minecraft.getMinecraft().player.closeScreen();
			}
			else
				button.enabled = false;
		}
		else
		{
			int camID = (button.id - 10) + ((page - 1) * 10);

			SecurityCraft.network.sendToServer(new PacketSRemoveCameraTag(playerInventory.getCurrentItem(), camID));
			nbtTag.removeTag(ItemCameraMonitor.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
			button.enabled = false;
			cameraButtons[(camID - 1) % 10].enabled = false;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:monitor.selectCameras"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:monitor.selectCameras")) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

}