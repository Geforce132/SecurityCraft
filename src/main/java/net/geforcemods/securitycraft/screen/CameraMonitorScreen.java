package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.components.GlobalPositions;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.SmallXButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private ItemStack cameraMonitor;
	private Button[] cameraButtons = new Button[10];
	private Button[] unbindButtons = new Button[10];
	private CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int xSize = 176, ySize = 166, leftPos, topPos;
	private int page = 1;

	public CameraMonitorScreen(Inventory inventory, ItemStack stack) {
		super(Component.translatable(SCContent.CAMERA_MONITOR.get().getDescriptionId()));
		playerInventory = inventory;
		cameraMonitor = stack;
	}

	public CameraMonitorScreen(Inventory inventory, ItemStack stack, int page) {
		this(inventory, stack);
		this.page = page;
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		Button prevPageButton = addRenderableWidget(new Button(width / 2 - 25, height / 2 + 57, 20, 20, Component.literal("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, page - 1)), Button.DEFAULT_NARRATION));
		Button nextPageButton = addRenderableWidget(new Button(width / 2 + 5, height / 2 + 57, 20, 20, Component.literal(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, page + 1)), Button.DEFAULT_NARRATION));
		GlobalPositions cameras = cameraMonitor.getOrDefault(SCContent.BOUND_CAMERAS, GlobalPositions.sized(CameraMonitorItem.MAX_CAMERAS));
		List<GlobalPos> views = cameras.positions();
		Level level = Minecraft.getInstance().level;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			int x = leftPos + 18 + (i % 5) * 30;
			int y = topPos + 30 + (i / 5) * 55;
			GlobalPos view = views.get(camID - 1);
			Button cameraButton = addRenderableWidget(new Button(x, y, 20, 20, Component.empty(), button -> cameraButtonClicked(button, view), Button.DEFAULT_NARRATION));
			Button unbindButton = addRenderableWidget(new SmallXButton(x + 19, y - 8, button -> unbindButtonClicked(button, view, camID)));

			cameraButtons[i] = cameraButton;
			unbindButtons[i] = unbindButton;
			cameraButton.setMessage(cameraButton.getMessage().plainCopy().append(Component.literal("" + camID)));

			if (view != null) {
				SecurityCameraBlockEntity cameraBe = level.getBlockEntity(view.pos()) instanceof SecurityCameraBlockEntity cameraEntity ? cameraEntity : null;

				if (cameraBe != null) {
					BlockState state = level.getBlockState(view.pos());

					if (cameraBe.isDisabled() || cameraBe.isShutDown()) {
						cameraButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:scManual.disabled")));
						cameraButton.active = false;
					}
					else if (cameraBe.hasCustomName())
						cameraButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBe.getCustomName())));

					if (state.getSignal(level, view.pos(), state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraBe.isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}
			}
			else {
				cameraButton.active = false;
				unbindButton.active = false;
			}
		}

		prevPageButton.active = page != 1;
		nextPageButton.active = page != 3;
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		for (int i = 0; i < 10; i++) {
			Button button = cameraButtons[i];
			CameraRedstoneModuleState redstoneModuleState = redstoneModuleStates[i];

			if (redstoneModuleState != null)
				redstoneModuleState.render(guiGraphics, button.getX() + 4, button.getY() + 25);
		}

		guiGraphics.drawString(font, selectCameras, leftPos + xSize / 2 - font.width(selectCameras) / 2, topPos + 6, 4210752, false);
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize);
	}

	private void cameraButtonClicked(Button button, GlobalPos camera) {
		if (camera != null) {
			BlockPos cameraPos = camera.pos();

			if (minecraft.level.getBlockEntity(cameraPos) instanceof SecurityCameraBlockEntity cameraEntity && (cameraEntity.isDisabled() || cameraEntity.isShutDown())) {
				button.active = false;
				return;
			}

			PacketDistributor.sendToServer(new MountCamera(cameraPos));
			Minecraft.getInstance().player.closeContainer();
		}
	}

	private void unbindButtonClicked(Button button, GlobalPos camera, int camID) {
		if (camera != null) {
			int i = (camID - 1) % 10;
			Button cameraButton = cameraButtons[i];

			PacketDistributor.sendToServer(new RemoveCameraTag(camera));
			cameraMonitor.get(SCContent.BOUND_CAMERAS).remove(SCContent.BOUND_CAMERAS, cameraMonitor, camera);
			button.active = false;
			cameraButton.active = false;
			cameraButton.setTooltip(null);
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