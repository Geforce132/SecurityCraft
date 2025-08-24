package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.SentryMode;
import net.geforcemods.securitycraft.network.server.RemoveSentryFromSRAT;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;
import net.minecraftforge.fml.network.PacketDistributor;

public class SentryRemoteAccessToolScreen extends Screen {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private final TranslationTextComponent modifyAll = Utils.localize("gui.securitycraft:srat.modifyAll");
	private ItemStack srat;
	private Button[][] guiButtons = new Button[12][3]; // 12 sentries, 3 actions (mode, targets, unbind)
	private Button[] guiButtonsGlobal = new Button[3];
	private static final int MODE = 0, TARGETS = 1, UNBIND = 2;
	private int xSize = 440, ySize = 215;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final TranslationTextComponent notBound = Utils.localize("gui.securitycraft:srat.notBound");
	private final ITextComponent[] lines = new ITextComponent[12];
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
			int btnY = startY + y - 48;
			coords = getSentryCoordinates(i);

			for (int j = 0; j < 3; j++) {
				int btnX = startX + j * paddingX + 147 + x;
				int sentry = id / 3;
				int type = id % 3;
				final int index = id++;

				switch (j) {
					case MODE:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, b -> buttonClicked(b, sentry, type));
						break;
					case TARGETS:
						guiButtons[i][j] = new TogglePictureButton(btnX, btnY, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, b -> buttonClicked(b, sentry, type));
						break;
					case UNBIND:
						guiButtons[i][j] = new ExtendedButton(btnX, btnY, 20, 20, new StringTextComponent("X"), b -> unbindButtonClicked(index));
						break;
					default:
						throw new IllegalArgumentException("Sentry actions can only range from 0-2 (inclusive)");
				}

				guiButtons[i][j].active = false;
				addButton(guiButtons[i][j]);
			}

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
			World level = minecraft.level;

			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				String nameKey = "sentry" + (i + 1) + "_name";
				ITextComponent sentryName = null;

				if (srat.hasTag() && srat.getTag().contains(nameKey))
					sentryName = new StringTextComponent(srat.getTag().getString(nameKey));

				lines[i] = Utils.getFormattedCoordinates(sentryPos);
				guiButtons[i][UNBIND].active = true;

				if (level.isLoaded(sentryPos)) {
					List<Sentry> sentries = level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);

						if (sentry.isOwnedBy(minecraft.player)) {
							SentryMode mode = sentry.getMode();

							if (sentryName == null && sentry.hasCustomName())
								sentryName = sentry.getCustomName();

							guiButtons[i][MODE].active = true;
							guiButtons[i][TARGETS].active = mode != SentryMode.IDLE;
							guiButtons[i][UNBIND].active = true;
							((TogglePictureButton) guiButtons[i][0]).setCurrentIndex(mode.ordinal() / 3);
							((TogglePictureButton) guiButtons[i][1]).setCurrentIndex(mode.ordinal() % 3);
							hoverCheckers.add(new TextHoverChecker(guiButtons[i][MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2"), Utils.localize("gui.securitycraft:srat.mode1"), Utils.localize("gui.securitycraft:srat.mode3"))));
							hoverCheckers.add(new TextHoverChecker(guiButtons[i][TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1"), Utils.localize("gui.securitycraft:srat.targets2"), Utils.localize("gui.securitycraft:srat.targets3"))));
							hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
							foundSentry = true;
						}
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
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][j], Utils.localize("gui.securitycraft:srat.outOfRange")));
					}

					hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
				}
			}
			else
				lines[i] = notBound;

			lengths[i] = font.width(lines[i]);
		}

		//Add buttons for global operation (all sentries)
		guiButtonsGlobal[0] = new TogglePictureButton(startX + 260, startY + 188, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::globalModeButtonClicked);
		guiButtonsGlobal[1] = new TogglePictureButton(startX + 22 + 260, startY + 188, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::globalTargetsButtonClicked);
		guiButtonsGlobal[2] = new ExtendedButton(startX + 44 + 260, startY + 188, 20, 20, new StringTextComponent("X"), this::globalUnbindButtonClicked);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[j].active = foundSentry;
			addButton(guiButtonsGlobal[j]);
		}

		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2"), Utils.localize("gui.securitycraft:srat.mode1"), Utils.localize("gui.securitycraft:srat.mode3"))));
		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1"), Utils.localize("gui.securitycraft:srat.targets2"), Utils.localize("gui.securitycraft:srat.targets3"))));
		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
	}

	@Override
	public void render(MatrixStack pose, int mouseX, int mouseY, float partialTicks) {
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(pose);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
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

	protected SetSentryMode.Info performSingleAction(int sentry, int mode, int targets) {
		int[] coords = getSentryCoordinates(sentry);
		List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AxisAlignedBB(new BlockPos(coords[0], coords[1], coords[2])));

		if (!sentries.isEmpty()) {
			int resultingMode = Math.max(0, Math.min(targets + mode * 3, 6)); //bind between 0 and 6

			guiButtons[sentry][TARGETS].active = SentryMode.values()[resultingMode] != SentryMode.IDLE;
			sentries.get(0).toggleMode(Minecraft.getInstance().player, resultingMode, false);
			return new SetSentryMode.Info(sentries.get(0).blockPosition(), resultingMode);
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
		int[] coords = getSentryCoordinates(sentry);

		removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2]);

		for (int i = 0; i < 3; i++) {
			guiButtons[sentry][i].active = false;
		}

		for (int i = 0; i < guiButtons.length; i++) {
			if (guiButtons[i][UNBIND].active)
				return;
		}

		for (int i = 0; i < 3; i++) {
			guiButtonsGlobal[i].active = false;
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

			if (getSentryCoordinates(i)[1] != 0) {
				int sentry = i;
				int mode = ((TogglePictureButton) button).getCurrentIndex();
				int targets = ((TogglePictureButton) guiButtons[sentry][TARGETS]).getCurrentIndex();

				modeButton.setCurrentIndex(mode);
				sentriesToUpdate.add(performSingleAction(sentry, mode, targets));
			}
		}

		sendUpdates(sentriesToUpdate);
	}

	protected void globalTargetsButtonClicked(Button button) {
		List<SetSentryMode.Info> sentriesToUpdate = new ArrayList<>();

		for (int i = 0; i < guiButtons.length; i++) {
			TogglePictureButton targetsButton = (TogglePictureButton) guiButtons[i][TARGETS];

			if (getSentryCoordinates(i)[1] != 0) {
				int sentry = i;
				int mode = ((TogglePictureButton) guiButtons[sentry][MODE]).getCurrentIndex();
				int targets = ((TogglePictureButton) button).getCurrentIndex();

				targetsButton.setCurrentIndex(targets);
				sentriesToUpdate.add(performSingleAction(sentry, mode, targets));
			}
		}

		sendUpdates(sentriesToUpdate);
	}

	private void sendUpdates(List<SetSentryMode.Info> sentriesToUpdate) {
		SecurityCraft.channel.send(PacketDistributor.SERVER.noArg(), new SetSentryMode(sentriesToUpdate));
	}

	/**
	 * @param sentry 0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry12

		if (srat.getItem() != null && srat.getItem() == SCContent.SENTRY_REMOTE_ACCESS_TOOL.get() && srat.getTag() != null && srat.getTag().getIntArray("sentry" + sentry) != null && srat.getTag().getIntArray("sentry" + sentry).length > 0)
			return srat.getTag().getIntArray("sentry" + sentry);

		return new int[] {
				0, 0, 0
		};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTag().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if (coords[0] == x && coords[1] == y && coords[2] == z) {
					stack.getTag().putIntArray("sentry" + i, new int[] {
							0, 0, 0
					});
					SecurityCraft.channel.sendToServer(new RemoveSentryFromSRAT(i));
					return;
				}
			}
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputMappings.getKey(keyCode, scanCode))) {
			onClose();
			return true;
		}

		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
