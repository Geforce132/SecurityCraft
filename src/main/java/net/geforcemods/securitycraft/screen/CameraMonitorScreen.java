package net.geforcemods.securitycraft.screen;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

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
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private StringHoverChecker[] tpHoverCheckers = new StringHoverChecker[10];
	private SecurityCameraBlockEntity[] cameraTEs = new SecurityCameraBlockEntity[10];
	private String[] cameraNames = new String[10];
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
		List<Pair<CameraView, String>> views = CameraMonitorItem.getCameraPositions(nbtTag);
		World world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		buttonList.add(prevPageButton);
		buttonList.add(nextPageButton);

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = (buttonId + ((page - 1) * 10));
			int x = guiLeft + 18 + (i % 5) * 30;
			int y = guiTop + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			Pair<CameraView, String> pair = views.get(camID - 1);
			CameraView view = pair.getLeft();
			GuiButton cameraButton = new GuiButton(buttonId, x, y, 20, 20, "#" + camID);
			GuiButton unbindButton = new GuiButton(buttonId + 10, x + 19, aboveCameraButton, 8, 8, "x");

			cameraButtons[i] = cameraButton;
			buttonList.add(cameraButton);
			buttonList.add(unbindButton);

			if (view != null) {
				BlockPos pos = view.getPos();

				if (view.getDimension() != Minecraft.getMinecraft().player.dimension)
					cameraViewDim[i] = view.getDimension();

				TileEntity te = world.getTileEntity(pos);

				cameraTEs[i] = te instanceof SecurityCameraBlockEntity ? (SecurityCameraBlockEntity) te : null;
				cameraNames[i] = pair.getRight();
				hoverCheckers[i] = new HoverChecker(cameraButton);

				if (cameraNames[i] == null && cameraTEs[i] != null && cameraTEs[i].hasCustomName())
					cameraNames[i] = cameraTEs[i].getName();

				if (cameraTEs[i] != null) {
					IBlockState state = world.getBlockState(pos);

					if (cameraTEs[i].isDisabled() || cameraTEs[i].isShutDown())
						cameraButton.enabled = false;

					if (state.getWeakPower(world, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraTEs[i].isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}

				//op check is done on the server through the command
				if (player.isCreative()) {
					GuiButton tpButton = addButton(new ClickButton(buttonId + 20, x, aboveCameraButton, 8, 8, "", b -> {
						if (player.dimension == view.getDimension())
							player.sendChatMessage(String.format("/tp @p %s %s %s", pos.getX(), pos.getY(), pos.getZ()));
						else
							player.sendChatMessage(String.format("/forge setdim @p %s %s %s %s", view.getDimension(), pos.getX(), pos.getY(), pos.getZ()));

						mc.displayGuiScreen(null);
					}));

					tpHoverCheckers[i] = new StringHoverChecker(tpButton, Utils.localize("securitycraft.teleport").getFormattedText());
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
			if (hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)) {
				if (cameraTEs[i] != null && (cameraTEs[i].isDisabled() || cameraTEs[i].isShutDown()))
					drawHoveringText(Utils.localize("gui.securitycraft:scManual.disabled").getFormattedText(), mouseX, mouseY);
				else if (cameraNames[i] != null)
					drawHoveringText(mc.fontRenderer.listFormattedStringToWidth(Utils.localize("gui.securitycraft:monitor.cameraName").getFormattedText().replace("#", cameraNames[i]), 150), mouseX, mouseY, mc.fontRenderer);
			}
		}

		for (int i = 0; i < tpHoverCheckers.length; i++) {
			if (tpHoverCheckers[i] != null && tpHoverCheckers[i].checkHover(mouseX, mouseY))
				drawHoveringText(tpHoverCheckers[i].getName(), mouseX, mouseY);
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
			BlockPos cameraPos = CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1).getLeft().getPos();
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
		else if (button instanceof ClickButton)
			((ClickButton) button).onClick();
		else {
			int camID = (button.id - 10) + ((page - 1) * 10);
			int i = (camID - 1) % 10;

			SecurityCraft.network.sendToServer(new RemoveCameraTag(camID));
			nbtTag.removeTag(CameraMonitorItem.getTagNameFromPosition(nbtTag, CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1).getLeft()));
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