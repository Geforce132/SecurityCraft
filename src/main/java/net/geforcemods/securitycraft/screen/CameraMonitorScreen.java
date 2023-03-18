package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.SmallXButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundTag nbtTag;
	private Button[] cameraButtons = new Button[10];
	private Button[] unbindButtons = new Button[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCameraBlockEntity[] cameraBEs = new SecurityCameraBlockEntity[10];
	private ResourceLocation[] cameraViewDim = new ResourceLocation[10];
	private int xSize = 176, ySize = 166;
	private int page = 1;

	public CameraMonitorScreen(Inventory inventory, CameraMonitorItem item, CompoundTag itemNBTTag) {
		super(Component.translatable(SCContent.CAMERA_MONITOR.get().getDescriptionId()));
		playerInventory = inventory;
		cameraMonitor = item;
		nbtTag = itemNBTTag;
	}

	public CameraMonitorScreen(Inventory inventory, CameraMonitorItem item, CompoundTag itemNBTTag, int page) {
		this(inventory, item, itemNBTTag);
		this.page = page;
	}

	@Override
	public void init() {
		super.init();

		Button prevPageButton = addRenderableWidget(new Button(width / 2 - 68, height / 2 + 40, 20, 20, Component.literal("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1)), Button.DEFAULT_NARRATION));
		Button nextPageButton = addRenderableWidget(new Button(width / 2 + 52, height / 2 + 40, 20, 20, Component.literal(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1)), Button.DEFAULT_NARRATION));

		cameraButtons[0] = new Button(width / 2 - 38, height / 2 - 60 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 1), Button.DEFAULT_NARRATION);
		cameraButtons[1] = new Button(width / 2 - 8, height / 2 - 60 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 2), Button.DEFAULT_NARRATION);
		cameraButtons[2] = new Button(width / 2 + 22, height / 2 - 60 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 3), Button.DEFAULT_NARRATION);
		cameraButtons[3] = new Button(width / 2 - 38, height / 2 - 30 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 4), Button.DEFAULT_NARRATION);
		cameraButtons[4] = new Button(width / 2 - 8, height / 2 - 30 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 5), Button.DEFAULT_NARRATION);
		cameraButtons[5] = new Button(width / 2 + 22, height / 2 - 30 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 6), Button.DEFAULT_NARRATION);
		cameraButtons[6] = new Button(width / 2 - 38, height / 2 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 7), Button.DEFAULT_NARRATION);
		cameraButtons[7] = new Button(width / 2 - 8, height / 2 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 8), Button.DEFAULT_NARRATION);
		cameraButtons[8] = new Button(width / 2 + 22, height / 2 + 10, 20, 20, Component.empty(), button -> cameraButtonClicked(button, 9), Button.DEFAULT_NARRATION);
		cameraButtons[9] = new Button(width / 2 - 38, height / 2 + 40, 80, 20, Component.empty(), button -> cameraButtonClicked(button, 10), Button.DEFAULT_NARRATION);

		unbindButtons[0] = new SmallXButton(width / 2 - 19, height / 2 - 68 + 10, button -> unbindButtonClicked(button, 1));
		unbindButtons[1] = new SmallXButton(width / 2 + 11, height / 2 - 68 + 10, button -> unbindButtonClicked(button, 2));
		unbindButtons[2] = new SmallXButton(width / 2 + 41, height / 2 - 68 + 10, button -> unbindButtonClicked(button, 3));
		unbindButtons[3] = new SmallXButton(width / 2 - 19, height / 2 - 38 + 10, button -> unbindButtonClicked(button, 4));
		unbindButtons[4] = new SmallXButton(width / 2 + 11, height / 2 - 38 + 10, button -> unbindButtonClicked(button, 5));
		unbindButtons[5] = new SmallXButton(width / 2 + 41, height / 2 - 38 + 10, button -> unbindButtonClicked(button, 6));
		unbindButtons[6] = new SmallXButton(width / 2 - 19, height / 2 + 2, button -> unbindButtonClicked(button, 7));
		unbindButtons[7] = new SmallXButton(width / 2 + 11, height / 2 + 2, button -> unbindButtonClicked(button, 8));
		unbindButtons[8] = new SmallXButton(width / 2 + 41, height / 2 + 2, button -> unbindButtonClicked(button, 9));
		unbindButtons[9] = new SmallXButton(width / 2 + 41, height / 2 + 32, button -> unbindButtonClicked(button, 10));

		for (int i = 0; i < 10; i++) {
			Button button = cameraButtons[i];
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			ArrayList<GlobalPos> views = cameraMonitor.getCameraPositions(nbtTag);
			GlobalPos view = views.get(camID - 1);

			button.setMessage(button.getMessage().plainCopy().append(Component.literal("" + camID)));
			addRenderableWidget(button);

			if (view != null) {
				if (!view.dimension().equals(Minecraft.getInstance().player.level.dimension())) {
					hoverCheckers[buttonId - 1] = new HoverChecker(button);
					cameraViewDim[buttonId - 1] = view.dimension().location();
				}

				cameraBEs[buttonId - 1] = Minecraft.getInstance().level.getBlockEntity(view.pos()) instanceof SecurityCameraBlockEntity camera ? camera : null;
				hoverCheckers[buttonId - 1] = new HoverChecker(button);

				if (cameraBEs[buttonId - 1] != null && cameraBEs[buttonId - 1].isDisabled())
					button.active = false;
			}
			else {
				button.active = false;
				unbindButtons[buttonId - 1].active = false;
				cameraBEs[buttonId - 1] = null;
				continue;
			}
		}

		for (int i = 0; i < 10; i++) {
			addRenderableWidget(unbindButtons[i]);
		}

		if (page == 1)
			prevPageButton.active = false;

		if (page == 3 || cameraMonitor.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.active = false;

		for (int i = cameraMonitor.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++) {
			cameraButtons[(i - 1) - ((page - 1) * 10)].active = false;
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(pose);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, selectCameras, startX + xSize / 2 - font.width(selectCameras) / 2, startY + 6, 4210752);

		for (int i = 0; i < hoverCheckers.length; i++) {
			if (hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)) {
				if (cameraBEs[i] != null) {
					if (cameraBEs[i].isDisabled()) {
						renderTooltip(pose, Utils.localize("gui.securitycraft:scManual.disabled"), mouseX, mouseY);
						break;
					}
					else if (cameraBEs[i].hasCustomName()) {
						renderTooltip(pose, font.split(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBEs[i].getCustomName()), 150), mouseX, mouseY);
						break;
					}
				}
			}
		}
	}

	private void cameraButtonClicked(Button button, int buttonId) {
		int camID = buttonId + (page - 1) * 10;
		BlockPos cameraPos = cameraMonitor.getCameraPositions(nbtTag).get(camID - 1).pos();

		if (minecraft.level.getBlockEntity(cameraPos) instanceof SecurityCameraBlockEntity camera && camera.isDisabled()) {
			button.active = false;
			return;
		}

		SecurityCraft.channel.sendToServer(new MountCamera(cameraPos));
		Minecraft.getInstance().player.closeContainer();
	}

	private void unbindButtonClicked(Button button, int buttonId) {
		int camID = buttonId + (page - 1) * 10;

		SecurityCraft.channel.sendToServer(new RemoveCameraTag(camID));
		nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
		button.active = false;
		cameraButtons[(camID - 1) % 10].active = false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}
}