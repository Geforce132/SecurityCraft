package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.items.CameraMonitorItem;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.server.MountCamera;
import net.geforcemods.securitycraft.network.server.RemoveCameraTag;
import net.geforcemods.securitycraft.screen.components.SmallButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CameraMonitorScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final Component selectCameras = Utils.localize("gui.securitycraft:monitor.selectCameras");
	private Inventory playerInventory;
	private CameraMonitorItem cameraMonitor;
	private CompoundTag nbtTag;
	private Button[] cameraButtons = new Button[10];
	private CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int xSize = 176, ySize = 166, leftPos, topPos;
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
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		Button prevPageButton = addRenderableWidget(new Button(width / 2 - 25, height / 2 + 57, 20, 20, Component.literal("<"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page - 1)), Button.DEFAULT_NARRATION));
		Button nextPageButton = addRenderableWidget(new Button(width / 2 + 5, height / 2 + 57, 20, 20, Component.literal(">"), b -> minecraft.setScreen(new CameraMonitorScreen(playerInventory, cameraMonitor, nbtTag, page + 1)), Button.DEFAULT_NARRATION));
		List<GlobalPos> views = CameraMonitorItem.getCameraPositions(nbtTag);
		Level level = Minecraft.getInstance().level;
		LocalPlayer player = Minecraft.getInstance().player;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			int x = leftPos + 18 + (i % 5) * 30;
			int y = topPos + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			GlobalPos view = views.get(camID - 1);
			Button cameraButton = addRenderableWidget(new Button(x, y, 20, 20, Component.empty(), button -> cameraButtonClicked(button, buttonId), Button.DEFAULT_NARRATION));
			Button unbindButton = addRenderableWidget(SmallButton.createWithX(x + 19, y - 8, button -> unbindButtonClicked(button, buttonId)));

			cameraButtons[i] = cameraButton;
			cameraButton.setMessage(cameraButton.getMessage().plainCopy().append(Component.literal("" + camID)));

			if (view != null) {
				BlockPos pos = view.pos();
				SecurityCameraBlockEntity cameraBe = level.getBlockEntity(pos) instanceof SecurityCameraBlockEntity camera ? camera : null;

				if (cameraBe != null) {
					BlockState state = level.getBlockState(pos);

					if (cameraBe.isDisabled() || cameraBe.isShutDown()) {
						cameraButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:scManual.disabled")));
						cameraButton.active = false;
					}
					else if (cameraBe.hasCustomName())
						cameraButton.setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:monitor.cameraName", cameraBe.getCustomName())));

					if (state.getSignal(level, pos, state.getValue(SecurityCameraBlock.FACING)) == 0) {
						if (!cameraBe.isModuleEnabled(ModuleType.REDSTONE))
							redstoneModuleStates[i] = CameraRedstoneModuleState.NOT_INSTALLED;
						else
							redstoneModuleStates[i] = CameraRedstoneModuleState.DEACTIVATED;
					}
					else
						redstoneModuleStates[i] = CameraRedstoneModuleState.ACTIVATED;
				}

				//op check is done on the server through the command
				if (player.isCreative()) {
					Button tpButton = addRenderableWidget(SmallButton.create(x, aboveCameraButton, Component.empty(), b -> {
						player.connection.sendUnsignedCommand(String.format("execute in %s run tp %s %s %s", view.dimension().location(), pos.getX(), pos.getY(), pos.getZ()));
						minecraft.setScreen(null);
					}));

					tpButton.setTooltip(Tooltip.create(Component.translatable("chat.coordinates.tooltip")));
				}
			}
			else {
				cameraButton.active = false;
				unbindButton.active = false;
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
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		renderBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		for (int i = 0; i < 10; i++) {
			Button button = cameraButtons[i];
			CameraRedstoneModuleState redstoneModuleState = redstoneModuleStates[i];

			if (redstoneModuleState != null)
				redstoneModuleState.render(guiGraphics, button.getX() + 4, button.getY() + 25);
		}

		guiGraphics.drawString(font, selectCameras, leftPos + xSize / 2 - font.width(selectCameras) / 2, topPos + 6, 4210752, false);
	}

	private void cameraButtonClicked(Button button, int buttonId) {
		int camID = buttonId + (page - 1) * 10;
		BlockPos cameraPos = CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1).pos();

		if (minecraft.level.getBlockEntity(cameraPos) instanceof SecurityCameraBlockEntity camera && (camera.isDisabled() || camera.isShutDown())) {
			button.active = false;
			return;
		}

		SecurityCraft.CHANNEL.sendToServer(new MountCamera(cameraPos));
		Minecraft.getInstance().player.closeContainer();
	}

	private void unbindButtonClicked(Button button, int buttonId) {
		int camID = buttonId + (page - 1) * 10;
		int i = (camID - 1) % 10;
		Button cameraButton = cameraButtons[i];

		SecurityCraft.CHANNEL.sendToServer(new RemoveCameraTag(camID));
		nbtTag.remove(CameraMonitorItem.getTagNameFromPosition(nbtTag, CameraMonitorItem.getCameraPositions(nbtTag).get(camID - 1)));
		button.active = false;
		cameraButton.active = false;
		cameraButton.setTooltip(null);
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
}