package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.StringHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SentryRemoteAccessToolScreen extends GuiContainer {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private ItemStack srat;
	private ClickButton[][] guiButtons = new ClickButton[12][3]; // 12 sentries, 3 actions (mode, targets, unbind)
	private String[] names = new String[12];
	private ClickButton[] guiButtonsGlobal = new ClickButton[3];
	private static final int MODE = 0, TARGETS = 1, UNBIND = 2;
	private List<StringHoverChecker> hoverCheckers = new ArrayList<>();

	public SentryRemoteAccessToolScreen(ItemStack item) {
		super(new GenericMenu(null));

		srat = item;
		xSize = 440;
		ySize = 215;
	}

	@Override
	public void initGui() {
		super.initGui();

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
			int btnY = guiTop + y - 48;
			coords = getSentryCoordinates(i);

			//initialize buttons
			for (int j = 0; j < 3; j++) {
				int btnX = guiLeft + j * paddingX + 147 + x;

				switch (j) {
					case MODE:
						guiButtons[i][j] = new TogglePictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::actionPerformedSingle);
						break;
					case TARGETS:
						guiButtons[i][j] = new TogglePictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::actionPerformedSingle);
						break;
					case UNBIND:
						guiButtons[i][j] = new ClickButton(id++, btnX, btnY, 20, 20, "X", this::clickUnbind);
						break;
					default:
						throw new IllegalArgumentException("Sentry actions can only range from 0-2 (inclusive)");
				}

				guiButtons[i][j].enabled = false;
				buttonList.add(guiButtons[i][j]);
			}

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
			World level = mc.world;

			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				String nameKey = "sentry" + (i + 1) + "_name";
				String sentryName = null;

				if (srat.hasTagCompound() && srat.getTagCompound().hasKey(nameKey))
					sentryName = srat.getTagCompound().getString(nameKey);

				names[i] = Utils.getFormattedCoordinates(sentryPos).getFormattedText();
				guiButtons[i][UNBIND].enabled = true;

				if (level.isBlockLoaded(sentryPos, false)) {
					List<Sentry> sentries = level.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);

						if (sentry.isOwnedBy(mc.player)) {
							SentryMode mode = sentry.getMode();

							if (sentryName == null && sentry.hasCustomName())
								sentryName = sentry.getCustomNameTag();

							guiButtons[i][MODE].enabled = true;
							guiButtons[i][TARGETS].enabled = mode != SentryMode.IDLE;
							guiButtons[i][UNBIND].enabled = true;
							((TogglePictureButton) guiButtons[i][0]).setCurrentIndex(mode.ordinal() / 3);
							((TogglePictureButton) guiButtons[i][1]).setCurrentIndex(mode.ordinal() % 3);
							hoverCheckers.add(new StringHoverChecker(guiButtons[i][MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2").getFormattedText(), Utils.localize("gui.securitycraft:srat.mode1").getFormattedText(), Utils.localize("gui.securitycraft:srat.mode3").getFormattedText())));
							hoverCheckers.add(new StringHoverChecker(guiButtons[i][TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1").getFormattedText(), Utils.localize("gui.securitycraft:srat.targets2").getFormattedText(), Utils.localize("gui.securitycraft:srat.targets3").getFormattedText())));
							hoverCheckers.add(new StringHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind").getFormattedText()));
							foundSentry = true;
						}
					}
				}

				if (sentryName != null) {
					int nameWidth = fontRenderer.getStringWidth(sentryName);
					int nameX = guiLeft + xSize / 4 - nameWidth + 33 + (i / 6) * xSize / 2;
					StringHoverChecker posTooltipText = new StringHoverChecker(btnY, btnY + 20, nameX, nameX + nameWidth + 2, names[i]);

					names[i] = sentryName;
					hoverCheckers.add(posTooltipText);
				}

				if (!foundSentry) {
					for (int j = 0; j < 2; j++) {
						hoverCheckers.add(new StringHoverChecker(guiButtons[i][j], Utils.localize("gui.securitycraft:srat.outOfRange").getFormattedText()));
					}

					hoverCheckers.add(new StringHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind").getFormattedText()));
				}
			}
		}

		//Add buttons for global operation (all sentries), large id
		guiButtonsGlobal[MODE] = new TogglePictureButton(1000, guiLeft + 260, guiTop + 188, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::actionPerformedGlobal);
		guiButtonsGlobal[TARGETS] = new TogglePictureButton(1001, guiLeft + 22 + 260, guiTop + 188, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::actionPerformedGlobal);
		guiButtonsGlobal[UNBIND] = new ClickButton(1002, guiLeft + 44 + 260, guiTop + 188, 20, 20, "X", this::clickGlobalUnbind);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[j].enabled = foundSentry;
			addButton(guiButtonsGlobal[j]);
		}

		hoverCheckers.add(new StringHoverChecker(guiButtonsGlobal[MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2").getFormattedText(), Utils.localize("gui.securitycraft:srat.mode1").getFormattedText(), Utils.localize("gui.securitycraft:srat.mode3").getFormattedText())));
		hoverCheckers.add(new StringHoverChecker(guiButtonsGlobal[TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1").getFormattedText(), Utils.localize("gui.securitycraft:srat.targets2").getFormattedText(), Utils.localize("gui.securitycraft:srat.targets3").getFormattedText())));
		hoverCheckers.add(new StringHoverChecker(guiButtonsGlobal[UNBIND], Utils.localize("gui.securitycraft:srat.unbind").getFormattedText()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String modifyAll = Utils.localize("gui.securitycraft:srat.modifyAll").getFormattedText();
		fontRenderer.drawString(srat.getDisplayName(), xSize / 2 - fontRenderer.getStringWidth(srat.getDisplayName()) / 2, 6, 4210752);

		for (int i = 0; i < 12; i++) {
			int[] coords = getSentryCoordinates(i);
			String line;

			if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = Utils.localize("gui.securitycraft:srat.notBound").getFormattedText();
			else if (names[i] != null)
				line = names[i];
			else
				line = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])).getFormattedText();

			fontRenderer.drawString(line, xSize / 4 - fontRenderer.getStringWidth(line) + 35 + (i / 6) * xSize / 2, (i % 6) * 25 + 33, 4210752);
		}

		fontRenderer.drawString(modifyAll, xSize / 2 - fontRenderer.getStringWidth(modifyAll) + 25, 194, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawModalRectWithCustomSizedTexture(startX, startY, 0, 0, xSize, ySize, 512, 256);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for (StringHoverChecker chc : hoverCheckers) {
			if (chc != null && chc.checkHover(mouseX, mouseY))
				drawHoveringText(chc.getName(), mouseX, mouseY);
		}
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected SetSentryMode.Info performSingleAction(int sentry, int mode, int targets) {
		int[] coords = getSentryCoordinates(sentry);
		List<Sentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(Sentry.class, new AxisAlignedBB(new BlockPos(coords[0], coords[1], coords[2])));

		if (!sentries.isEmpty()) {
			int resultingMode = Math.max(0, Math.min(targets + mode * 3, 6)); //bind between 0 and 6

			guiButtons[sentry][TARGETS].enabled = SentryMode.values()[resultingMode] != SentryMode.IDLE;
			sentries.get(0).toggleMode(Minecraft.getMinecraft().player, resultingMode, false);
			return new SetSentryMode.Info(sentries.get(0).getPosition(), resultingMode);
		}

		return null;
	}

	private void clickUnbind(ClickButton button) {
		unbindSentry(button.id / 3);
	}

	private void clickGlobalUnbind(ClickButton button) {
		for (int i = 0; i < 12; i++) {
			unbindSentry(i);
		}
	}

	private void unbindSentry(int sentry) {
		int[] coords = getSentryCoordinates(sentry);

		removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2]);

		for (int i = 0; i < 3; i++) {
			guiButtons[sentry][i].enabled = false;
		}

		for (int i = 0; i < guiButtons.length; i++) {
			if (guiButtons[i][UNBIND].enabled)
				return;
		}

		for (int i = 0; i < 3; i++) {
			guiButtonsGlobal[i].enabled = false;
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button instanceof ClickButton)
			((ClickButton) button).onClick();
	}

	protected void actionPerformedSingle(ClickButton button) {
		int sentry = button.id / 3;
		int type = button.id % 3;
		int mode = ((TogglePictureButton) button).getCurrentIndex();
		int targets = mode;

		if (type == 0)
			targets = ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();
		else if (type == 1)
			mode = ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();

		sendUpdates(Arrays.asList(performSingleAction(sentry, mode, targets)));
	}

	protected void actionPerformedGlobal(ClickButton button) {
		List<SetSentryMode.Info> sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < buttonList.size() / 3; i++) {
			GuiButton buttonFromList = buttonList.get(i * 3);

			if (buttonFromList instanceof ClickButton && getSentryCoordinates(i)[1] != 0) {
				int sentry = ((ClickButton) buttonList.get(i * 3)).id / 3;
				int mode = button.id == guiButtonsGlobal[MODE].id ? ((TogglePictureButton) guiButtonsGlobal[MODE]).getCurrentIndex() : ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();
				int targets = button.id == guiButtonsGlobal[TARGETS].id ? ((TogglePictureButton) guiButtonsGlobal[TARGETS]).getCurrentIndex() : ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();

				((TogglePictureButton) guiButtons[sentry][MODE]).setCurrentIndex(mode);
				((TogglePictureButton) guiButtons[sentry][TARGETS]).setCurrentIndex(targets);
				sentriesToUpdate.add(performSingleAction(sentry, mode, targets));
			}
		}

		sendUpdates(sentriesToUpdate);
	}

	private void sendUpdates(List<SetSentryMode.Info> sentriesToUpdate) {
		SecurityCraft.network.sendToServer(new SetSentryMode(sentriesToUpdate));
	}

	/**
	 * @param sentry 0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry12

		if (srat.getItem() != null && srat.getItem() == SCContent.sentryRemoteAccessTool && srat.getTagCompound() != null && srat.getTagCompound().getIntArray("sentry" + sentry) != null && srat.getTagCompound().getIntArray("sentry" + sentry).length > 0)
			return srat.getTagCompound().getIntArray("sentry" + sentry);

		return new int[] {
				0, 0, 0
		};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTagCompound().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if (coords[0] == x && coords[1] == y && coords[2] == z) {
					stack.getTagCompound().setIntArray("sentry" + i, new int[] {
							0, 0, 0
					});
					SecurityCraft.network.sendToServer(new RemoveSentryFromSRAT(i));
					return;
				}
			}
		}
	}
}
