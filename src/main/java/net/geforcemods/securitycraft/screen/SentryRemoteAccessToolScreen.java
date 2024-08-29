package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.components.NamedPositions;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.items.SentryRemoteAccessToolItem;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.screen.components.IToggleableButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

public class SentryRemoteAccessToolScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation CAMOUFLAGE_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/camouflage");
	private static final ResourceLocation AGGRESSIVE_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/aggressive");
	private static final ResourceLocation IDLE_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/idle");
	private static final ResourceLocation ATTACK_HOSTILE_AND_PLAYERS_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/attack_hostile_and_players");
	private static final ResourceLocation ATTACK_HOSTILE_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/attack_hostile");
	private static final ResourceLocation ATTACK_PLAYERS_SPRITE = new ResourceLocation(SecurityCraft.MODID, "sentry/attack_players");
	private final Component modifyAll = Utils.localize("gui.securitycraft:srat.modifyAll");
	private ItemStack srat;
	private Button[][] guiButtons = new Button[12][3]; // 12 sentries, 3 actions (mode, targets, unbind)
	private Button[] guiButtonsGlobal = new Button[3];
	private static final int MODE = 0, TARGETS = 1, UNBIND = 2;
	private int xSize = 440, ySize = 215, leftPos, topPos;
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
		leftPos = (width - xSize) / 2;
		topPos = (height - ySize) / 2;

		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		int paddingX = 22;
		int paddingY = 50;
		int id = 0;
		boolean foundSentry = false;

		hoverCheckers.clear();

		for (int i = 0; i < 12; i++) {
			int x = (i / 6) * xSize / 2; //first six sentries in the left column, second six sentries in the right column
			int y = ((i % 6) + 1) * 25 + paddingY;
			int btnY = startY + y - 48;

			for (int j = 0; j < 3; j++) {
				int btnX = startX + j * paddingX + 147 + x;
				int sentry = id / 3;
				int type = id % 3;
				final int index = id++;

				switch (j) {
					case MODE:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, 2, 16, 16, 3, b -> buttonClicked(b, sentry, type), CAMOUFLAGE_SPRITE, AGGRESSIVE_SPRITE, IDLE_SPRITE);
						guiButtons[i][j].active = false;
						break;
					case TARGETS:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, 2, 16, 16, 3, b -> buttonClicked(b, sentry, type), ATTACK_HOSTILE_AND_PLAYERS_SPRITE, ATTACK_HOSTILE_SPRITE, ATTACK_PLAYERS_SPRITE);
						guiButtons[i][j].active = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new Button(btnX, btnY, 20, 20, Component.literal("X"), b -> unbindButtonClicked(index), Button.DEFAULT_NARRATION);
						guiButtons[i][j].active = false;
						break;
					default:
						throw new IllegalArgumentException("Sentry actions can only range from 0-2 (inclusive)");
				}

				addRenderableWidget(guiButtons[i][j]);
			}

			NamedPositions.Entry entry = getSentryEntry(i);

			if (entry != null) {
				GlobalPos globalPos = entry.globalPos();
				BlockPos sentryPos = globalPos.pos();
				Level level = Minecraft.getInstance().player.level();
				Component sentryName = null;

				if (entry.name().isPresent())
					sentryName = Component.literal(entry.name().get());

				lines[i] = Utils.getFormattedCoordinates(sentryPos);
				guiButtons[i][UNBIND].active = true;

				if (globalPos.dimension().equals(minecraft.level.dimension()) && level.isLoaded(sentryPos)) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AABB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);
						SentryMode mode = sentry.getMode();

						if (sentryName == null && sentry.hasCustomName())
							sentryName = sentry.getCustomName();

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

				if (sentryName != null) {
					int nameWidth = font.width(sentryName);
					int nameX = startX + xSize / 4 - nameWidth + 33 + (i / 6) * xSize / 2;
					TextHoverChecker posTooltipText = new TextHoverChecker(btnY, btnY + 20, nameX, nameX + nameWidth + 2, lines[i]);

					lines[i] = sentryName;
					hoverCheckers.add(posTooltipText);
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
		guiButtonsGlobal[0] = new TogglePictureButton(startX + 260, startY + 188, 20, 20, 2, 16, 16, 3, this::globalModeButtonClicked, CAMOUFLAGE_SPRITE, AGGRESSIVE_SPRITE, IDLE_SPRITE);
		guiButtonsGlobal[1] = new TogglePictureButton(startX + 22 + 260, startY + 188, 20, 20, 2, 16, 16, 3, this::globalTargetsButtonClicked, ATTACK_HOSTILE_AND_PLAYERS_SPRITE, ATTACK_HOSTILE_SPRITE, ATTACK_PLAYERS_SPRITE);
		guiButtonsGlobal[2] = new Button(startX + 44 + 260, startY + 188, 20, 20, Component.literal("X"), this::globalUnbindButtonClicked, Button.DEFAULT_NARRATION);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[j].active = foundSentry;
			addRenderableWidget(guiButtonsGlobal[j]);
		}

		updateModeButtonTooltip(guiButtonsGlobal[MODE]);
		updateTargetsButtonTooltip(guiButtonsGlobal[TARGETS]);
		guiButtonsGlobal[UNBIND].setTooltip(Tooltip.create(Utils.localize("gui.securitycraft:srat.unbind")));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		super.render(guiGraphics, mouseX, mouseY, partialTicks);

		guiGraphics.drawString(font, title, leftPos + xSize / 2 - font.width(title) / 2, topPos + 6, 4210752, false);

		for (int i = 0; i < 12; i++) {
			guiGraphics.drawString(font, lines[i], leftPos + xSize / 4 - lengths[i] + 35 + (i / 6) * xSize / 2, topPos + (i % 6) * 25 + 33, 4210752, false);
		}

		guiGraphics.drawString(font, modifyAll, leftPos + xSize / 2 - font.width(modifyAll) + 25, topPos + 194, 4210752, false);

		for (TextHoverChecker chc : hoverCheckers) {
			if (chc != null && chc.checkHover(mouseX, mouseY)) {
				guiGraphics.renderTooltip(font, chc.getName(), mouseX, mouseY);
				break;
			}
		}
	}

	@Override
	public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		renderTransparentBackground(guiGraphics);
		guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, xSize, ySize, 512, 256);
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected SetSentryMode.Info performSingleAction(int sentry, int mode, int targets) {
		NamedPositions.Entry entry = getSentryEntry(sentry);

		if (entry != null && entry.globalPos() != null) {
			List<Sentry> sentries = Minecraft.getInstance().player.level().getEntitiesOfClass(Sentry.class, new AABB(entry.globalPos().pos()));

			if (!sentries.isEmpty()) {
				int resultingMode = Math.clamp(targets + mode * 3, 0, 6);

				guiButtons[sentry][TARGETS].active = SentryMode.values()[resultingMode] != SentryMode.IDLE;
				sentries.get(0).toggleMode(Minecraft.getInstance().player, resultingMode, false);
				updateModeButtonTooltip(guiButtons[sentry][MODE]);
				updateTargetsButtonTooltip(guiButtons[sentry][TARGETS]);
				return new SetSentryMode.Info(sentries.get(0).blockPosition(), resultingMode);
			}
		}

		return null;
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
		NamedPositions.Entry entry = getSentryEntry(sentry);

		if (entry != null && entry.globalPos() != null)
			removeTagFromToolAndUpdate(srat, entry.globalPos());

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

		sendUpdates(Arrays.asList(performSingleAction(sentry, mode, targets)));
	}

	protected void globalModeButtonClicked(Button button) {
		List<SetSentryMode.Info> sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < guiButtons.length; i++) {
			TogglePictureButton modeButton = (TogglePictureButton) guiButtons[i][MODE];

			if (getSentryEntry(i) != null) {
				int sentry = i;
				int mode = ((TogglePictureButton) button).getCurrentIndex();
				int targets = ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();

				modeButton.setCurrentIndex(mode);
				sentriesToUpdate.add(performSingleAction(sentry, mode, targets));
			}
		}

		sendUpdates(sentriesToUpdate);
		updateModeButtonTooltip(guiButtonsGlobal[MODE]);
	}

	protected void globalTargetsButtonClicked(Button button) {
		List<SetSentryMode.Info> sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < guiButtons.length; i++) {
			TogglePictureButton targetsButton = (TogglePictureButton) guiButtons[i][TARGETS];

			if (getSentryEntry(i) != null) {
				int sentry = i;
				int mode = ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();
				int targets = ((TogglePictureButton) button).getCurrentIndex();

				targetsButton.setCurrentIndex(targets);
				sentriesToUpdate.add(performSingleAction(sentry, mode, targets));
			}
		}

		sendUpdates(sentriesToUpdate);
		updateTargetsButtonTooltip(guiButtonsGlobal[TARGETS]);
	}

	private void sendUpdates(List<SetSentryMode.Info> sentriesToUpdate) {
		PacketDistributor.sendToServer(new SetSentryMode(sentriesToUpdate));
	}

	/**
	 * @param sentry 0 based
	 */
	private NamedPositions.Entry getSentryEntry(int sentry) {
		if (srat.getItem() == SCContent.SENTRY_REMOTE_ACCESS_TOOL.get()) {
			NamedPositions positions = srat.get(SCContent.BOUND_SENTRIES);

			if (positions != null && sentry >= 0 && sentry < SentryRemoteAccessToolItem.MAX_SENTRIES)
				return positions.positions().get(sentry);
		}

		return null;
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, GlobalPos pos) {
		stack.getOrDefault(SCContent.BOUND_SENTRIES, SentryRemoteAccessToolItem.DEFAULT_NAMED_POSITIONS).remove(SCContent.BOUND_SENTRIES, stack, pos);
		PacketDistributor.sendToServer(new RemoveSentryFromSRAT(pos));
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
