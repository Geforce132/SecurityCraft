package net.geforcemods.securitycraft.screen;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.GlobalPos;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CameraSelectScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final String title = Utils.localize("gui.securitycraft:monitor.selectCameras").getFormattedText();
	private final List<Pair<GlobalPos, String>> cameras;
	private final Consumer<Integer> onUnbindCamera;
	private final Consumer<GlobalPos> onViewCamera;
	private final boolean hasStopButton;
	private final boolean initialStopButtonState;
	private final ClickButton[] cameraButtons = new ClickButton[10];
	private final CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int page;

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, Consumer<Integer> onUnbindCamera, Consumer<GlobalPos> onViewCamera, boolean hasStopButton, boolean initialStopButtonState) {
		this(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, 1);
	}

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, Consumer<Integer> onUnbindCamera, Consumer<GlobalPos> onViewCamera, boolean hasStopButton, boolean initialStopButtonState, int page) {
		super(new GenericMenu(null));
		this.cameras = cameras;
		this.onUnbindCamera = onUnbindCamera;
		this.onViewCamera = onViewCamera;
		this.hasStopButton = hasStopButton;
		this.initialStopButtonState = initialStopButtonState;
		this.page = page;
	}

	@Override
	public void initGui() {
		super.initGui();

		ClickButton prevPageButton = addButton(new ClickButton(-1, width / 2 - 25, height / 2 + 57, 20, 20, "<", b -> mc.displayGuiScreen(new CameraSelectScreen(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, page - 1))));
		ClickButton nextPageButton = addButton(new ClickButton(0, width / 2 + 5, height / 2 + 57, 20, 20, ">", b -> mc.displayGuiScreen(new CameraSelectScreen(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, page + 1))));
		World world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = (buttonId + (page - 1) * 10);
			int x = guiLeft + 18 + (i % 5) * 30;
			int y = guiTop + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			Pair<GlobalPos, String> pair = cameras.get(camID - 1);
			GlobalPos view = pair.getLeft();
			ClickButton cameraButton = new ClickButton(buttonId, x, y, 20, 20, "" + camID, button -> cameraButtonClicked(button, camID));

			cameraButtons[i] = cameraButton;
			addButton(cameraButton);

			if (onUnbindCamera != null)
				addButton(new ClickButton(buttonId + 10, x + 19, aboveCameraButton, 8, 8, "x", button -> unbindButtonClicked(button, camID))).enabled = view != null;

			if (view != null) {
				BlockPos pos = view.pos();
				TileEntity te = world.getTileEntity(pos);
				SecurityCameraBlockEntity cameraBe = te instanceof SecurityCameraBlockEntity ? (SecurityCameraBlockEntity) te : null;
				String cameraName = pair.getRight();

				if (cameraBe != null) {
					IBlockState state = world.getBlockState(pos);

					if (cameraBe.isDisabled() || cameraBe.isShutDown()) {
						cameraButton.tooltip = new Tooltip(this, fontRenderer, Utils.localize("gui.securitycraft:scManual.disabled"));
						cameraButton.enabled = false;
					}
					else if (cameraName != null && cameraBe.hasCustomName())
						cameraName = cameraBe.getDisplayName().getFormattedText();

					if (state.getWeakPower(world, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraBe.isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}

				if (cameraButton.enabled && cameraName != null)
					cameraButton.tooltip = new Tooltip(this, fontRenderer, Utils.localize("gui.securitycraft:monitor.cameraName", cameraName));

				//op check is done on the server through the command
				if (player.isCreative()) {
					ClickButton tpButton = addButton(new ClickButton(buttonId + 20, x, aboveCameraButton, 8, 8, "", b -> {
						if (player.dimension == view.dimension())
							player.sendChatMessage(String.format("/tp @p %s %s %s", pos.getX(), pos.getY(), pos.getZ()));
						else
							player.sendChatMessage(String.format("/forge setdim @p %s %s %s %s", view.dimension(), pos.getX(), pos.getY(), pos.getZ()));

						mc.displayGuiScreen(null);
					}));
					tpButton.tooltip = new Tooltip(this, fontRenderer, Utils.localize("securitycraft.teleport"));
				}
			}
			else
				cameraButton.enabled = false;
		}

		if (hasStopButton) {
			ClickButton stopViewingButton = addButton(new ClickButton(-2, width / 2 - 55, height / 2 + 57, 20, 20, "x", b -> viewCamera(null)));

			stopViewingButton.enabled = initialStopButtonState;

			if (initialStopButtonState)
				stopViewingButton.tooltip = new Tooltip(this, fontRenderer, Utils.localize("gui.securitycraft:monitor.stopViewing"));
		}

		prevPageButton.enabled = page != 1;
		nextPageButton.enabled = page != 3;
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(title, xSize / 2 - fontRenderer.getStringWidth(title) / 2, 6, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

		for (int i = 0; i < 10; i++) {
			ClickButton button = cameraButtons[i];
			CameraRedstoneModuleState redstoneModuleState = redstoneModuleStates[i];

			if (redstoneModuleState != null)
				redstoneModuleState.render(this, button.x + 4, button.y + 25);
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	private void cameraButtonClicked(ClickButton button, int camID) {
		Pair<GlobalPos, String> camera = cameras.get(camID - 1);

		if (camera != null) {
			GlobalPos cameraPos = camera.getLeft();
			TileEntity te = mc.world.getTileEntity(cameraPos.pos());

			if (te instanceof SecurityCameraBlockEntity) {
				SecurityCameraBlockEntity cameraEntity = (SecurityCameraBlockEntity) te;

				if (cameraEntity.isDisabled() || cameraEntity.isShutDown()) {
					button.enabled = false;
					return;
				}
			}

			viewCamera(cameraPos);
		}
	}

	private void viewCamera(GlobalPos camera) {
		onViewCamera.accept(camera);
		mc.player.closeScreen();
	}

	private void unbindButtonClicked(ClickButton button, int camID) {
		Pair<GlobalPos, String> camera = cameras.get(camID - 1);

		if (camera != null) {
			int i = (camID - 1) % 10;
			ClickButton cameraButton = cameraButtons[i];

			onUnbindCamera.accept(camID);
			button.enabled = false;
			cameraButton.enabled = false;
			cameraButton.tooltip = null;
			redstoneModuleStates[i] = null;
		}
	}
}