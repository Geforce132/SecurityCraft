package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundTag nbtTag;
	private CameraButton[] cameraButtons = new CameraButton[10];
	private CameraButton[] unbindButtons = new CameraButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private SecurityCameraBlockEntity[] cameraBEs = new SecurityCameraBlockEntity[10];
	private ResourceLocation[] cameraViewDim = new ResourceLocation[10];
	private int xSize = 176, ySize = 166;
	private int page = 1;

	public CameraMonitorScreen(Inventory inventory, CameraMonitorItem item, CompoundTag itemNBTTag) {
		super(new TranslatableComponent(SCContent.CAMERA_MONITOR.get().getDescriptionId()));
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

		Button prevPageButton = addRenderableWidget(new ExtendedButton(width / 2 - 68, height / 2 + 40, 20, 20, new TextComponent("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1))));
		Button nextPageButton = addRenderableWidget(new ExtendedButton(width / 2 + 52, height / 2 + 40, 20, 20, new TextComponent(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1))));
		TextComponent x = new TextComponent("x");

		cameraButtons[0] = new CameraButton(1, width / 2 - 38, height / 2 - 60 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[1] = new CameraButton(2, width / 2 - 8, height / 2 - 60 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[2] = new CameraButton(3, width / 2 + 22, height / 2 - 60 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[3] = new CameraButton(4, width / 2 - 38, height / 2 - 30 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[4] = new CameraButton(5, width / 2 - 8, height / 2 - 30 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[5] = new CameraButton(6, width / 2 + 22, height / 2 - 30 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[6] = new CameraButton(7, width / 2 - 38, height / 2 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[7] = new CameraButton(8, width / 2 - 8, height / 2 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[8] = new CameraButton(9, width / 2 + 22, height / 2 + 10, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked);
		cameraButtons[9] = new CameraButton(10, width / 2 - 38, height / 2 + 40, 80, 20, TextComponent.EMPTY, this::cameraButtonClicked);

		unbindButtons[0] = new CameraButton(1, width / 2 - 19, height / 2 - 68 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[1] = new CameraButton(2, width / 2 + 11, height / 2 - 68 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[2] = new CameraButton(3, width / 2 + 41, height / 2 - 68 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[3] = new CameraButton(4, width / 2 - 19, height / 2 - 38 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[4] = new CameraButton(5, width / 2 + 11, height / 2 - 38 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[5] = new CameraButton(6, width / 2 + 41, height / 2 - 38 + 10, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[6] = new CameraButton(7, width / 2 - 19, height / 2 + 2, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[7] = new CameraButton(8, width / 2 + 11, height / 2 + 2, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[8] = new CameraButton(9, width / 2 + 41, height / 2 + 2, 8, 8, x, this::unbindButtonClicked);
		unbindButtons[9] = new CameraButton(10, width / 2 + 41, height / 2 + 32, 8, 8, x, this::unbindButtonClicked);

		for (int i = 0; i < 10; i++) {
			CameraButton button = cameraButtons[i];
			int camID = button.camId + (page - 1) * 10;
			ArrayList<GlobalPos> views = cameraMonitor.getCameraPositions(nbtTag);
			GlobalPos view = views.get(camID - 1);

			button.setMessage(button.getMessage().plainCopy().append(new TextComponent("" + camID)));
			addRenderableWidget(button);

			if (view != null) {
				if (!view.dimension().equals(Minecraft.getInstance().player.level.dimension())) {
					hoverCheckers[button.camId - 1] = new HoverChecker(button);
					cameraViewDim[button.camId - 1] = view.dimension().location();
				}

				cameraBEs[button.camId - 1] = Minecraft.getInstance().level.getBlockEntity(view.pos()) instanceof SecurityCameraBlockEntity camera ? camera : null;
				hoverCheckers[button.camId - 1] = new HoverChecker(button);

				if (cameraBEs[button.camId - 1] != null && cameraBEs[button.camId - 1].isDisabled())
					button.active = false;
			}
			else {
				button.active = false;
				unbindButtons[button.camId - 1].active = false;
				cameraBEs[button.camId - 1] = null;
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

		for (int i = 0; i < hoverCheckers.length; i++) {
			if (hoverCheckers[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)) {
				if (cameraBEs[i] != null) {
					if (cameraBEs[i].isDisabled())
						renderTooltip(pose, Utils.localize("gui.securitycraft:scManual.disabled"), mouseX, mouseY);
					else if (cameraBEs[i].hasCustomName())
						renderTooltip(pose, font.split(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBEs[i].getCustomName()), 150), mouseX, mouseY);
				}
			}
		}
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

	private void unbindButtonClicked(Button button) {
		int camID = ((CameraButton) button).camId + (page - 1) * 10;

		SecurityCraft.channel.sendToServer(new RemoveCameraTag(PlayerUtils.getSelectedItemStack(playerInventory, SCContent.CAMERA_MONITOR.get()), camID));
		nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, cameraMonitor.getCameraPositions(nbtTag).get(camID - 1)));
		button.active = false;
		cameraButtons[(camID - 1) % 10].active = false;
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