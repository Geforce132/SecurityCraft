package net.geforcemods.securitycraft.screen;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.CameraView;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CameraMonitorScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private InventoryPlayer playerInventory;
	private CameraMonitorItem cameraMonitor;
	private NBTTagCompound nbtTag;
	private GuiButton[] cameraButtons = new GuiButton[10];
	private GuiButton[] unbindButtons = new GuiButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCameraBlockEntity[] cameraTEs = new SecurityCameraBlockEntity[10];
	private int[] cameraViewDim = new int[10];
	private CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int page = 1;

	public CameraMonitorScreen(InventoryPlayer inventory, CameraMonitorItem item, NBTTagCompound itemNBTTag) {
		super(new GenericMenu(null));
		playerInventory = inventory;
		cameraMonitor = item;
		nbtTag = itemNBTTag;
	}

	public CameraMonitorScreen(InventoryPlayer inventory, CameraMonitorItem item, NBTTagCompound itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@Override
	public void initGui() {
		super.initGui();

		GuiButton prevPageButton = new GuiButton(-1, width / 2 - 25, height / 2 + 57, 20, 20, "<");
		GuiButton nextPageButton = new GuiButton(0, width / 2 + 5, height / 2 + 57, 20, 20, ">");
		List<CameraView> views = CameraMonitorItem.getCameraPositions(nbtTag);
		World world = Minecraft.getMinecraft().world;

		buttonList.add(prevPageButton);
		buttonList.add(nextPageButton);

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int x = guiLeft + 18 + (i % 5) * 30;
			int y = guiTop + 30 + (i / 5) * 55;
			int camID = (buttonId + ((page - 1) * 10));
			GuiButton cameraButton = new GuiButton(buttonId, x, y, 20, 20, "#" + camID);
			GuiButton unbindButton = new GuiButton(buttonId + 10, x + 19, y - 8, 8, 8, "x");
			CameraView view = views.get(camID - 1);

			cameraButtons[i] = cameraButton;
			unbindButtons[i] = unbindButton;
			buttonList.add(cameraButton);
			buttonList.add(unbindButton);

			if (view != null) {
				if (view.getDimension() != Minecraft.getMinecraft().player.dimension)
					cameraViewDim[i] = view.getDimension();

				TileEntity te = world.getTileEntity(view.getPos());

				cameraTEs[i] = te instanceof SecurityCameraBlockEntity ? (SecurityCameraBlockEntity) te : null;
				hoverCheckers[i] = new HoverChecker(cameraButton);

				if (cameraTEs[i] != null) {
					IBlockState state = world.getBlockState(view.getPos());

					if (cameraTEs[i].isDisabled() || cameraTEs[i].isShutDown())
						cameraButton.enabled = false;

					if (state.getWeakPower(world, view.getPos(), state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraTEs[i].isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}
			}
			else {
				cameraButton.enabled = false;
				unbindButton.enabled = false;
				cameraTEs[i] = null;
			}
		}

		if (page == 1)
			prevPageButton.enabled = false;

		if (page == 3 || CameraMonitorItem.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.enabled = false;

		for (int i = CameraMonitorItem.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++) {
			cameraButtons[(i - 1) - ((page - 1) * 10)].enabled = false;
		}
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (int i = 0; i < hoverCheckers.length; i++) {
			if (cameraTEs[i] != null && hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)) {
				if (cameraTEs[i].isDisabled() || cameraTEs[i].isShutDown())
					drawHoveringText(Utils.localize("gui.securitycraft:scManual.disabled").getFormattedText(), mouseX, mouseY);
				else if (cameraTEs[i].hasCustomName())
					drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:monitor.cameraName").getFormattedText().replace("#", cameraTEs[i].getName()), 150), mouseX, mouseY, mc.fontRenderer);
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == -1)
			mc.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1));
		else if (button.id == 0)
			mc.displayGuiScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1));
		else if (button.id < 11) {
			int camID = button.id + ((page - 1) * 10);
			BlockPos cameraPos = CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1).getPos();
			TileEntity te = mc.world.getTileEntity(cameraPos);

			if (te instanceof SecurityCameraBlockEntity && ((SecurityCameraBlockEntity) te).isDisabled()) {
				SecurityCameraBlockEntity be = (SecurityCameraBlockEntity) te;

				if (be.isDisabled() || be.isShutDown()) {
					button.enabled = false;
					return;
				}
			}

			SecurityCraft.network.sendToServer(new MountCamera(cameraPos));
			Minecraft.getMinecraft().player.closeScreen();
		}
		else {
			int camID = (button.id - 10) + ((page - 1) * 10);
			int i = (camID - 1) % 10;

			SecurityCraft.network.sendToServer(new RemoveCameraTag(camID));
			nbtTag.removeTag(CameraMonitorItem.getTagNameFromPosition(nbtTag, CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1)));
			button.enabled = false;
			cameraButtons[i].enabled = false;
			redstoneModuleStates[i] = null;
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(Utils.localize("gui.securitycraft:monitor.selectCameras").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(Utils.localize("gui.securitycraft:monitor.selectCameras").getFormattedText()) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		for (int i = 0; i < 10; i++) {
			GuiButton button = cameraButtons[i];
			CameraRedstoneModuleState redstoneModuleState = redstoneModuleStates[i];

			if (redstoneModuleState != null)
				redstoneModuleState.render(this, button.x + 4, button.y + 25);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
}