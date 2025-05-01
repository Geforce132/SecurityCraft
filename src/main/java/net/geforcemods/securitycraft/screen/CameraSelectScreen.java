package net.geforcemods.securitycraft.screen;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.misc.CameraRedstoneModuleState;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.screen.components.Tooltip;
import net.geforcemods.securitycraft.screen.components.TooltipExtendedButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class CameraSelectScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	private final List<Pair<GlobalPos, String>> cameras;
	protected final boolean readOnly;
	private final Button[] cameraButtons = new Button[10];
	private final CameraRedstoneModuleState[] redstoneModuleStates = new CameraRedstoneModuleState[10];
	private int xSize = 176, ySize = 166, leftPos, topPos;
	private int page;

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, boolean readOnly) {
		this(cameras, readOnly, 1);
	}

	public CameraSelectScreen(List<Pair<GlobalPos, String>> cameras, boolean readOnly, int page) {
		super(Utils.localize("gui.securitycraft:monitor.selectCameras"));
		this.cameras = cameras;
		this.readOnly = readOnly;
		this.page = page;
	}

	@Override
	public void init() {
		super.init();
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		Button prevPageButton = addButton(new Button(width / 2 - 25, height / 2 + 57, 20, 20, new StringTextComponent("<"), b -> {
			page--;
			init(minecraft, width, height);
		}));
		Button nextPageButton = addButton(new Button(width / 2 + 5, height / 2 + 57, 20, 20, new StringTextComponent(">"), b -> {
			page++;
			init(minecraft, width, height);
		}));
		World level = Minecraft.getInstance().level;
		ClientPlayerEntity player = Minecraft.getInstance().player;

		for (int i = 0; i < 10; i++) {
			int buttonId = i + 1;
			int camID = buttonId + (page - 1) * 10;
			int x = leftPos + 18 + (i % 5) * 30;
			int y = topPos + 30 + (i / 5) * 55;
			int aboveCameraButton = y - 8;
			Pair<GlobalPos, String> pair = cameras.get(camID - 1);
			GlobalPos view = pair.getLeft();
			Button cameraButton = addButton(new Button(x, y, 20, 20, StringTextComponent.EMPTY, button -> cameraButtonClicked(button, camID)));

			if (!readOnly)
				addButton(new ExtendedButton(x + 19, aboveCameraButton, 8, 8, new StringTextComponent("x"), button -> unbindButtonClicked(button, camID))).active = view != null;

			cameraButtons[i] = cameraButton;
			cameraButton.setMessage(cameraButton.getMessage().plainCopy().append(new StringTextComponent("" + camID)));
			redstoneModuleStates[i] = null;

			if (view != null) {
				BlockPos pos = view.pos();
				TileEntity te = level.getBlockEntity(pos);
				SecurityCameraBlockEntity cameraBe = te instanceof SecurityCameraBlockEntity ? (SecurityCameraBlockEntity) te : null;
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
					Button tpButton = addButton(new TooltipExtendedButton(x, aboveCameraButton, 8, 8, StringTextComponent.EMPTY, b -> {
						player.chat(String.format("/execute in %s run tp %s %s %s", view.dimension().location(), pos.getX(), pos.getY(), pos.getZ()));
						minecraft.setScreen(null);
					}));

					tpButton.onTooltip = new Tooltip(this, font, new TranslationTextComponent("chat.coordinates.tooltip"));
				}
			}
			else
				cameraButton.active = false;
		}

		prevPageButton.active = page != 1;
		nextPageButton.active = page != 3;
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
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
			TileEntity te = minecraft.level.getBlockEntity(cameraPos.pos());

			if (te instanceof SecurityCameraBlockEntity) {
				SecurityCameraBlockEntity cameraEntity = (SecurityCameraBlockEntity) te;

				if (cameraEntity.isDisabled() || cameraEntity.isShutDown()) {
					button.active = false;
					return;
				}
			}

			viewCamera(cameraPos);
		}
	}

	protected void viewCamera(GlobalPos cameraPos) {
		Minecraft.getInstance().player.closeContainer();
	}

	private void unbindButtonClicked(Button button, int camID) {
		Pair<GlobalPos, String> camera = cameras.get(camID - 1);

		if (camera != null) {
			int i = (camID - 1) % 10;
			Button cameraButton = cameraButtons[i];

			unbindCamera(camID);
			button.active = false;
			cameraButton.active = false;
			cameraButton.onTooltip = null;
			redstoneModuleStates[i] = null;
		}
	}

	protected void unbindCamera(int camID) {}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
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