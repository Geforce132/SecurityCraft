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
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundTag nbtTag;
	private CameraButton[] cameraButtons = new CameraButton[10];
	private CameraButton[] unbindButtons = new CameraButton[10];
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

		Button prevPageButton = addRenderableWidget(new ExtendedButton(width / 2 - 68, height / 2 + 40, 20, 20, Component.literal("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1))));
		Button nextPageButton = addRenderableWidget(new ExtendedButton(width / 2 + 52, height / 2 + 40, 20, 20, Component.literal(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1))));
		Component x = Component.literal("x");

		cameraButtons[0] = new CameraButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[1] = new CameraButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[2] = new CameraButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[3] = new CameraButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[4] = new CameraButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[5] = new CameraButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[6] = new CameraButton(7, width / 2 - 38, height / 2 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[7] = new CameraButton(8, width / 2 - 8, height / 2 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[8] = new CameraButton(9, width / 2 + 22, height / 2 + 10, 20, 20, Component.empty(), this::cameraButtonClicked);
		cameraButtons[9] = new CameraButton(10, width / 2 - 38, height / 2 + 40, 80, 20, Component.empty(), this::cameraButtonClicked);

		unbindButtons[0] = new CameraButton(1, width / 2 - 19, height / 2 - 68 + 10, 8, 8, x, b -> unbindButtonClicked(b, 1));
		unbindButtons[1] = new CameraButton(2, width / 2 + 11, height / 2 - 68 + 10, 8, 8, x, b -> unbindButtonClicked(b, 2));
		unbindButtons[2] = new CameraButton(3, width / 2 + 41, height / 2 - 68 + 10, 8, 8, x, b -> unbindButtonClicked(b, 3));
		unbindButtons[3] = new CameraButton(4, width / 2 - 19, height / 2 - 38 + 10, 8, 8, x, b -> unbindButtonClicked(b, 4));
		unbindButtons[4] = new CameraButton(5, width / 2 + 11, height / 2 - 38 + 10, 8, 8, x, b -> unbindButtonClicked(b, 5));
		unbindButtons[5] = new CameraButton(6, width / 2 + 41, height / 2 - 38 + 10, 8, 8, x, b -> unbindButtonClicked(b, 6));
		unbindButtons[6] = new CameraButton(7, width / 2 - 19, height / 2 + 2, 8, 8, x, b -> unbindButtonClicked(b, 7));
		unbindButtons[7] = new CameraButton(8, width / 2 + 11, height / 2 + 2, 8, 8, x, b -> unbindButtonClicked(b, 8));
		unbindButtons[8] = new CameraButton(9, width / 2 + 41, height / 2 + 2, 8, 8, x, b -> unbindButtonClicked(b, 9));
		unbindButtons[9] = new CameraButton(10, width / 2 + 41, height / 2 + 32, 8, 8, x, b -> unbindButtonClicked(b, 10));

		for (int i = 0; i < 10; i++) {
			Button button = cameraButtons[i];
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			ArrayList<GlobalPos> views = cameraMonitor.getCameraPositions(nbtTag);
			GlobalPos view = views.get(camID - 1);

			button.setMessage(button.getMessage().plainCopy().append(Component.literal("" + camID)));
			addRenderableWidget(button);

			if (view != null) {
				SecurityCameraBlockEntity cameraBe = Minecraft.getInstance().level.getBlockEntity(view.pos()) instanceof SecurityCameraBlockEntity camera ? camera : null;

				if (cameraBe != null) {
					if (cameraBe.isDisabled()) {
						button.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:scManual.disabled")));
						button.active = false;
					}
					else if (cameraBe.hasCustomName())
						button.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBe.getCustomName())));
				}
			}
			else {
				button.active = false;
				unbindButtons[buttonId - 1].active = false;
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
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, selectCameras, startX + xSize / 2 - font.width(selectCameras) / 2, startY + 6, 4210752);
	}

	private void cameraButtonClicked(Button button) {
		int camID = ((CameraButton) button).camId + (page - 1) * 10;
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
		Button cameraButton = cameraButtons[(camID - 1) % 10];

		SecurityCraft.channel.sendToServer(new RemoveCameraTag(camID));
		nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
		button.active = false;
		cameraButton.active = false;
		cameraButton.setTooltip(null);
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

	private static class CameraButton extends ExtendedButton {
		private final int camId;

		public CameraButton(int camId, int xPos, int yPos, int width, int height, Component displayString, OnPress handler) {
			super(xPos, yPos, width, height, displayString, handler);

			this.camId = camId;
		}
	}
}