package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.gui.widget.ExtendedButton;

public class SentryRemoteAccessToolScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private final Component modifyAll = Utils.localize("gui.securitycraft:srat.modifyAll");
	private ItemStack srat;
	private Button[][] guiButtons = new Button[12][3]; // 12 sentries, 3 actions (mode, targets, unbind)
	private Button[] guiButtonsGlobal = new Button[3];
	private static final int MODE = 0, TARGETS = 1, UNBIND = 2;
	private int xSize = 440, ySize = 215;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final Component notBound = Utils.localize("gui.securitycraft:srat.notBound");
	private final Component[] lines = new Component[12];
	private final int[] lengths = new int[12];

	public SentryRemoteAccessToolScreen(ItemStack item) {
		super(item.getHoverName());

		srat = item;
	}

	@Override
	public void init() {
		super.init();

		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		int paddingX = 22;
		int paddingY = 50;
		int[] coords = null;
		int id = 0;
		boolean foundSentry = false;
		int[] modeTextureX = {
				0, 16, 32
		};
		int[] targetTextureX = {
				48, 64, 80
		};
		int[] yStarts = {
				0, 0, 0
		};

		hoverCheckers.clear();

		for (int i = 0; i < 12; i++) {
			int x = (i / 6) * xSize / 2; //first six sentries in the left column, second six sentries in the right column
			int y = ((i % 6) + 1) * 25 + paddingY;
			coords = getSentryCoordinates(i);

			for (int j = 0; j < 3; j++) {
				int btnX = startX + j * paddingX + 147 + x;
				int btnY = startY + y - 48;
				int sentry = id / 3;
				int type = id % 3;
				final int index = id++;

				switch (j) {
					case MODE:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, b -> buttonClicked(b, sentry, type));
						guiButtons[i][j].active = false;
						break;
					case TARGETS:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, b -> buttonClicked(b, sentry, type));
						guiButtons[i][j].active = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new ExtendedButton(btnX, btnY, 20, 20, Component.literal("X"), b -> unbindButtonClicked(index));
						guiButtons[i][j].active = false;
						break;
					default:
						throw new IllegalArgumentException("Sentry actions can only range from 0-2 (inclusive)");
				}

				addRenderableWidget(guiButtons[i][j]);
			}

			if (coords.length == 3) {
				BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
				Level level = Minecraft.getInstance().player.level;

				lines[i] = Utils.getFormattedCoordinates(sentryPos);
				guiButtons[i][UNBIND].active = true;

				if (level.isLoaded(sentryPos)) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);
						SentryMode mode = sentry.getMode();

						if (sentry.hasCustomName()) {
							Component line = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]));
							int nameWidth = font.width(sentry.getCustomName());
							int nameX = startX + xSize / 4 - nameWidth + 33 + (i / 6) * xSize / 2;
							int nameY = startY + (i % 6) * 30 + 7;
							TextHoverChecker posTooltipText = new TextHoverChecker(nameY + 4, nameY + 18, nameX, nameX + nameWidth + 2, line);

							lines[i] = sentry.getCustomName();
							hoverCheckers.add(posTooltipText);
						}

						guiButtons[i][MODE].active = true;
						guiButtons[i][TARGETS].active = mode != SentryMode.IDLE;
						guiButtons[i][UNBIND].active = true;
						((TogglePictureButton) guiButtons[i][0]).setCurrentIndex(mode.ordinal() / 3);
						((TogglePictureButton) guiButtons[i][1]).setCurrentIndex(mode.ordinal() % 3);
						updateModeButtonTooltip(guiButtons[i][MODE]);
						updateTargetsButtonTooltip(guiButtons[i][TARGETS]);
						guiButtons[i][UNBIND].setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:srat.unbind")));
						foundSentry = true;
					}
				}

				if (!foundSentry) {
					for (int j = 0; j < 2; j++) {
						guiButtons[i][j].setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:srat.outOfRange")));
					}

					guiButtons[i][UNBIND].setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:srat.unbind")));
				}
			}
			else
				lines[i] = notBound;

			lengths[i] = font.width(lines[i]);
		}

		//Add buttons for global operation (all sentries)
		guiButtonsGlobal[0] = new TogglePictureButton(startX + 260, startY + 188, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::globalModeButtonClicked);
		guiButtonsGlobal[1] = new TogglePictureButton(startX + 22 + 260, startY + 188, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::globalTargetsButtonClicked);
		guiButtonsGlobal[2] = new ExtendedButton(startX + 44 + 260, startY + 188, 20, 20, Component.literal("X"), this::globalUnbindButtonClicked);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[j].active = foundSentry;
			addRenderableWidget(guiButtonsGlobal[j]);
		}

		updateModeButtonTooltip(guiButtonsGlobal[MODE]);
		updateTargetsButtonTooltip(guiButtonsGlobal[TARGETS]);
		guiButtonsGlobal[UNBIND].setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:srat.unbind")));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, xSize, ySize, 512, 256);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, title, startX + xSize / 2 - font.width(title) / 2, startY + 6, 4210752);

		for (int i = 0; i < 12; i++) {
			font.draw(pose, lines[i], startX + xSize / 4 - lengths[i] + 35 + (i / 6) * xSize / 2, startY + (i % 6) * 25 + 33, 4210752);
		}

		font.draw(pose, modifyAll, startX + xSize / 2 - font.width(modifyAll) + 25, startY + 194, 4210752);

		for (TextHoverChecker chc : hoverCheckers) {
			if (chc != null && chc.checkHover(mouseX, mouseY)) {
				renderTooltip(pose, chc.getName(), mouseX, mouseY);
				break;
			}
		}
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected void performSingleAction(int sentry, int mode, int targets) {
		int[] coords = getSentryCoordinates(sentry);

		if (coords.length == 3) {
			List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AABB(new BlockPos(coords[0], coords[1], coords[2])));

			if (!sentries.isEmpty()) {
				int resultingMode = Math.max(0, Math.min(targets + mode * 3, 6)); //bind between 0 and 6

				guiButtons[sentry][TARGETS].active = SentryMode.values()[resultingMode] != SentryMode.IDLE;
				sentries.get(0).toggleMode(Minecraft.getInstance().player, resultingMode, false);
				SecurityCraft.CHANNEL.sendToServer(new SetSentryMode(sentries.get(0).blockPosition(), resultingMode));
				updateModeButtonTooltip(guiButtons[sentry][MODE]);
				updateTargetsButtonTooltip(guiButtons[sentry][TARGETS]);
			}
		}
	}

	private void unbindButtonClicked(int id) {
		unbindSentry(id / 3);
	}

	private void globalUnbindButtonClicked(Button button) {
		for (int i = 0; i < 12; i++) {
			unbindSentry(i);
		}
	}

	private void unbindSentry(int sentry) {
		int[] coords = getSentryCoordinates(sentry);

		if (coords.length == 3)
			removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2]);

		for (int i = 0; i < 3; i++) {
			guiButtons[sentry][i].active = false;
			guiButtons[sentry][i].setTooltip(null);
		}

		for (int i = 0; i < guiButtons.length; i++) {
			if (guiButtons[i][UNBIND].active)
				return;
		}

		for (int i = 0; i < 3; i++) {
			guiButtonsGlobal[i].active = false;
			guiButtonsGlobal[i].setTooltip(null);
		}
	}

	private void buttonClicked(Button button, int sentry, int type) {
		int mode = ((TogglePictureButton) button).getCurrentIndex();
		int targets = mode;

		if (type == 0)
			targets = ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();
		else if (type == 1)
			mode = ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();

		performSingleAction(sentry, mode, targets);
	}

	protected void globalModeButtonClicked(Button button) {
		for (int i = 0; i < guiButtons.length; i++) {
			TogglePictureButton modeButton = (TogglePictureButton) guiButtons[i][MODE];

			if (getSentryCoordinates(i).length == 3) {
				int sentry = i;
				int mode = ((TogglePictureButton) button).getCurrentIndex();
				int targets = ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();

				modeButton.setCurrentIndex(mode);
				performSingleAction(sentry, mode, targets);
			}
		}

		updateModeButtonTooltip(guiButtonsGlobal[MODE]);
	}

	protected void globalTargetsButtonClicked(Button button) {
		for (int i = 0; i < guiButtons.length; i++) {
			TogglePictureButton targetsButton = (TogglePictureButton) guiButtons[i][TARGETS];

			if (getSentryCoordinates(i).length == 3) {
				int sentry = i;
				int mode = ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();
				int targets = ((TogglePictureButton) button).getCurrentIndex();

				targetsButton.setCurrentIndex(targets);
				performSingleAction(sentry, mode, targets);
			}
		}

		updateTargetsButtonTooltip(guiButtonsGlobal[TARGETS]);
	}

	/**
	 * @param sentry 0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry12

		if (srat.getItem() == SCContent.SENTRY_REMOTE_ACCESS_TOOL.get() && srat.hasTag()) {
			int[] coords = srat.getTag().getIntArray("sentry" + sentry);

			if (coords.length == 3)
				return coords;
		}

		return new int[0];
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			int[] coords = stack.getTag().getIntArray("sentry" + i);

			if (coords.length == 3 && coords[0] == x && coords[1] == y && coords[2] == z) {
				stack.getTag().remove("sentry" + i);
				SecurityCraft.CHANNEL.sendToServer(new RemoveSentryFromSRAT(i));
				return;
			}
		}
	}

	private void updateModeButtonTooltip(Button button) {
		button.setTooltip(Tooltip.create(switch (((IToggleableButton) button).getCurrentIndex()) {
			case 0 -> Utils.localize("gui.securitycraft:srat.mode2");
			case 1 -> Utils.localize("gui.securitycraft:srat.mode1");
			case 2 -> Utils.localize("gui.securitycraft:srat.mode3");
			default -> Utils.localize("gui.securitycraft:srat.mode2");
		}));
	}

	private void updateTargetsButtonTooltip(Button button) {
		button.setTooltip(Tooltip.create(switch (((IToggleableButton) button).getCurrentIndex()) {
			case 0 -> Utils.localize("gui.securitycraft:srat.targets1");
			case 1 -> Utils.localize("gui.securitycraft:srat.targets2");
			case 2 -> Utils.localize("gui.securitycraft:srat.targets3");
			default -> Utils.localize("gui.securitycraft:srat.targets1");
		}));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
