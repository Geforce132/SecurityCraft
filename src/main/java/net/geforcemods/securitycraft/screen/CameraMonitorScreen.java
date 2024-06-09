package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.HoverChecker;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final TranslatableComponent selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundTag nbtTag;
	private CameraButton[] cameraButtons = new CameraButton[10];
	private HoverChecker[] hoverCheckers = new HoverChecker[10];
	private TextHoverChecker[] tpHoverCheckers = new TextHoverChecker[10];
	private SecurityCameraBlockEntity[] cameraBEs = new SecurityCameraBlockEntity[10];
	private ResourceLocation[] cameraViewDim = new ResourceLocation[10];
	private CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int xSize = 176, ySize = 166, leftPos, topPos;
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
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		Button prevPageButton = addRenderableWidget(new ExtendedButton(width / 2 - 25, height / 2 + 57, 20, 20, new TextComponent("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1))));
		Button nextPageButton = addRenderableWidget(new ExtendedButton(width / 2 + 5, height / 2 + 57, 20, 20, new TextComponent(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1))));
		List<GlobalPos> views = CameraMonitorItem.getCameraPositions(nbtTag);
		Level level = Minecraft.getInstance().level;
		TextComponent xText = new TextComponent("x");
		LocalPlayer player = Minecraft.getInstance().player;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			int x = leftPos + 18 + (i % 5) * 30;
			int y = topPos + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			GlobalPos view = views.get(camID - 1);
			CameraButton cameraButton = addRenderableWidget(new CameraButton(buttonId, x, y, 20, 20, TextComponent.EMPTY, this::cameraButtonClicked));
			CameraButton unbindButton = addRenderableWidget(new CameraButton(buttonId, x + 19, aboveCameraButton, 8, 8, xText, this::unbindButtonClicked));

			cameraButtons[i] = cameraButton;
			cameraButton.setMessage(cameraButton.getMessage().plainCopy().append(new TextComponent("" + camID)));

			if (view != null) {
				BlockPos pos = view.pos();

				if (!view.dimension().equals(level.dimension())) {
					hoverCheckers[i] = new HoverChecker(cameraButton);
					cameraViewDim[i] = view.dimension().location();
				}

				cameraBEs[i] = level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity camera ? camera : null;
				hoverCheckers[i] = new HoverChecker(cameraButton);

				if (cameraBEs[i] != null) {
					BlockState state = level.getBlockState(pos);

					if (cameraBEs[i].isDisabled() || cameraBEs[i].isShutDown())
						cameraButton.active = false;

					if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraBEs[i].isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}

				//op check is done on the server through the command
				if (player.isCreative()) {
					Button tpButton = addRenderableWidget(new ExtendedButton(x, aboveCameraButton, 8, 8, TextComponent.EMPTY, b -> {
						player.chat(String.format("/execute in %s run tp %s %s %s", view.dimension().location(), pos.getX(), pos.getY(), pos.getZ()));
						minecraft.setScreen(null);
					}));

					tpHoverCheckers[i] = new TextHoverChecker(tpButton, new TranslatableComponent("chat.coordinates.tooltip"));
				}
			}
			else {
				cameraButton.active = false;
				unbindButton.active = false;
				cameraBEs[i] = null;
			}
		}

		if (page == 1)
			prevPageButton.active = false;

		if (page == 3 || CameraMonitorItem.getCameraPositions(nbtTag).size() < (page * 10) + 1)
			nextPageButton.active = false;

		for (int i = CameraMonitorItem.getCameraPositions(nbtTag).size() + 1; i <= (page * 10); i++) {
			cameraButtons[(i - 1) - ((page - 1) * 10)].active = false;
		}
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(pose, mouseX, mouseY, partialTicks);

		for (int i = 0; i < 10; i++) {
			Button button = cameraButtons[i];
			CameraRedstoneModuleState redstoneModuleState = redstoneModuleStates[i];

			if (redstoneModuleState != null)
				redstoneModuleState.render(this, pose, button.x + 4, button.y + 25);
		}

		font.draw(pose, selectCameras, leftPos + xSize / 2 - font.width(selectCameras) / 2, topPos + 6, 4210752);

		for (int i = 0; i < hoverCheckers.length; i++) {
			if (hoverCheckers[i] != null && cameraBEs[i] != null && hoverCheckers[i].checkHover(mouseX, mouseY)) {
				if (cameraBEs[i].isDisabled() || cameraBEs[i].isShutDown()) {
					renderTooltip(pose, Utils.localize("gui.securitycraft:scManual.disabled"), mouseX, mouseY);
					break;
				}
				else if (cameraBEs[i].hasCustomName()) {
					renderTooltip(pose, font.split(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBEs[i].getCustomName()), 150), mouseX, mouseY);
					break;
				}
			}
		}

		for (int i = 0; i < tpHoverCheckers.length; i++) {
			if (tpHoverCheckers[i] != null && tpHoverCheckers[i].checkHover(mouseX, mouseY))
				renderTooltip(pose, tpHoverCheckers[i].getName(), mouseX, mouseY);
		}
	}

	private void cameraButtonClicked(Button button) {
		int camID = ((CameraButton) button).camId + (page - 1) * 10;
		BlockPos cameraPos = CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1).pos();

		if (minecraft.level.getBlockEntity(cameraPos) instanceof SecurityCameraBlockEntity camera && (camera.isDisabled() || camera.isShutDown())) {
			button.active = false;
			return;
		}

		SecurityCraft.CHANNEL.sendToServer(new MountCamera(cameraPos));
		Minecraft.getInstance().player.closeContainer();
	}

	private void unbindButtonClicked(Button button) {
		int camID = ((CameraButton) button).camId + (page - 1) * 10;
		int i = (camID - 1) % 10;

		SecurityCraft.CHANNEL.sendToServer(new RemoveCameraTag(camID));
		nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1)));
		button.active = false;
		cameraButtons[i].active = false;
		redstoneModuleStates[i] = null;
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