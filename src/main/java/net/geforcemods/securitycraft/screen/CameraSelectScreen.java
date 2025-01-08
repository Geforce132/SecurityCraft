package net.geforcemods.securitycraft.screen;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.geforcemods.securitycraft.screen.components.TooltipExtendedButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class CameraSelectScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final List<Pair<GlobalPos, String>> cameras;
	private final Consumer<Integer> onUnbindCamera;
	private final Consumer<GlobalPos> onViewCamera;
	private final boolean hasStopButton;
	private final boolean initialStopButtonState;
	private final Button[] cameraButtons = new Button[10];
	private final CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int xSize = 176, ySize = 166, leftPos, topPos;
	private int page;

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, Consumer<Integer> onUnbindCamera, Consumer<GlobalPos> onViewCamera, boolean hasStopButton, boolean initialStopButtonState) {
		this(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, 1);
	}

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, Consumer<Integer> onUnbindCamera, Consumer<GlobalPos> onViewCamera, boolean hasStopButton, boolean initialStopButtonState, int page) {
		super(Utils.localize("gui.securitycraft:monitor.selectCameras"));
		this.cameras = cameras;
		this.onUnbindCamera = onUnbindCamera;
		this.onViewCamera = onViewCamera;
		this.hasStopButton = hasStopButton;
		this.initialStopButtonState = initialStopButtonState;
		this.page = page;
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		Button prevPageButton = addRenderableWidget(new Button(width / 2 - 25, height / 2 + 57, 20, 20, new TextComponent("<"), b -> minecraft.setScreen(new CameraSelectScreen(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, page - 1))));
		Button nextPageButton = addRenderableWidget(new Button(width / 2 + 5, height / 2 + 57, 20, 20, new TextComponent(">"), b -> minecraft.setScreen(new CameraSelectScreen(cameras, onUnbindCamera, onViewCamera, hasStopButton, initialStopButtonState, page + 1))));
		Level level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			int x = leftPos + 18 + (i % 5) * 30;
			int y = topPos + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			Pair<GlobalPos, String> pair = cameras.get(camID - 1);
			GlobalPos view = pair.getLeft();
			Button cameraButton = addRenderableWidget(new Button(x, y, 20, 20, TextComponent.EMPTY, button -> cameraButtonClicked(button, camID)));

			if (onUnbindCamera != null)
				addRenderableWidget(new ExtendedButton(x + 19, aboveCameraButton, 8, 8, new TextComponent("x"), button -> unbindButtonClicked(button, camID))).active = view != null;

			cameraButtons[i] = cameraButton;
			cameraButton.setMessage(cameraButton.getMessage().plainCopy().append(new TextComponent("" + camID)));

			if (view != null) {
				BlockPos pos = view.pos();
				SecurityCameraBlockEntity cameraBe = level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity camera ? camera : null;
				String cameraName = pair.getRight();

				if (cameraBe != null) {
					BlockState state = level.getBlockState(pos);

					if (cameraBe.isDisabled() || cameraBe.isShutDown()) {
						cameraButton.onTooltip = new Tooltip(this, font, Utils.localize("gui.securitycraft:scManual.disabled"));
						cameraButton.active = false;
					}
					else if (cameraName != null && cameraBe.hasCustomName())
						cameraName = cameraBe.getCustomName().getString();

					if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraBe.isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}

				if (cameraButton.active && cameraName != null)
					cameraButton.onTooltip = new Tooltip(this, font, Utils.localize("gui.securitycraft:monitor.cameraName", cameraName));

				//op check is done on the server through the command
				if (player.isCreative()) {
					Button tpButton = addRenderableWidget(new TooltipExtendedButton(x, aboveCameraButton, 8, 8, TextComponent.EMPTY, b -> {
						player.chat(String.format("/execute in %s run tp %s %s %s", view.dimension().location(), pos.getX(), pos.getY(), pos.getZ()));
						minecraft.setScreen(null);
					}));

					tpButton.onTooltip = new Tooltip(this, font, new TranslatableComponent("chat.coordinates.tooltip"));
				}
			}
			else
				cameraButton.active = false;
		}

		if (hasStopButton) {
			Button stopViewingButton = addRenderableWidget(new Button(width / 2 - 55, height / 2 + 57, 20, 20, new TextComponent("x"), b -> viewCamera(null)));

			stopViewingButton.active = initialStopButtonState;

			if (initialStopButtonState)
				stopViewingButton.onTooltip = new Tooltip(this, font, new TranslatableComponent("gui.securitycraft:monitor.stopViewing"));
		}

		prevPageButton.active = page != 1;
		nextPageButton.active = page != 3;
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

		font.draw(pose, title, leftPos + xSize / 2 - font.width(title) / 2, topPos + 6, 4210752);
	}

	private void cameraButtonClicked(Button button, int camID) {
		Pair<GlobalPos, String> camera = cameras.get(camID - 1);

		if (camera != null) {
			GlobalPos cameraPos = camera.getLeft();

			if (minecraft.level.getBlockEntity(cameraPos.pos()) instanceof SecurityCameraBlockEntity cameraEntity && (cameraEntity.isDisabled() || cameraEntity.isShutDown())) {
				button.active = false;
				return;
			}

			viewCamera(cameraPos);
		}
	}

	private void viewCamera(GlobalPos camera) {
		onViewCamera.accept(camera);
		Minecraft.getInstance().player.closeContainer();
	}

	private void unbindButtonClicked(Button button, int camID) {
		Pair<GlobalPos, String> camera = cameras.get(camID - 1);

		if (camera != null) {
			int i = (camID - 1) % 10;
			Button cameraButton = cameraButtons[i];

			onUnbindCamera.accept(camID);
			button.active = false;
			cameraButton.active = false;
			cameraButton.onTooltip = null;
			redstoneModuleStates[i] = null;
		}
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