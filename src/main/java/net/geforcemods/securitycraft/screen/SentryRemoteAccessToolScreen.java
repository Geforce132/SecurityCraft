package net.geforcemods.securitycraft.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.Sentry.SentryMode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.screen.components.TextHoverChecker;
import net.geforcemods.securitycraft.screen.components.TogglePictureButton;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class SentryRemoteAccessToolScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private final TranslatableComponent modifyAll = Utils.localize("gui.securitycraft:srat.modifyAll");
	private ItemStack srat;
	private IdButton[][] guiButtons = new IdButton[12][3]; // 12 sentries, 3 actions (mode, targets, unbind)
	private Component[] names = new Component[12];
	private IdButton[] guiButtonsGlobal = new IdButton[3];
	private static final int MODE = 0, TARGETS = 1, UNBIND = 2;
	private int xSize = 440, ySize = 215;
	private static final int SENTRY_TRACKING_RANGE = 256; // as defined when registering SentryEntity
	private int viewDistance;
	private List<TextHoverChecker> hoverCheckers = new ArrayList<>();
	private final TranslatableComponent notBound = Utils.localize("gui.securitycraft:srat.notBound");
	private final Component[] lines = new Component[12];
	private final int[] lengths = new int[12];

	public SentryRemoteAccessToolScreen(ItemStack item, int viewDistance) {
		super(new TranslatableComponent(item.getDescriptionId()));

		srat = item;
		this.viewDistance = viewDistance;
	}

	@Override
	public void init() {
		super.init();

		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		int paddingX = 22;
		int paddingY = 25;
		int[] coords = null;
		int id = 0;
		boolean foundSentry = false;
		int[] modeTextureX = {0, 16, 32};
		int[] targetTextureX = {48, 64, 80};
		int[] yStarts = {0, 0, 0};

		hoverCheckers.clear();

		for (int i = 0; i < 12; i++) {
			int x = (i / 6) * xSize / 2; //first six sentries in the left column, second six sentries in the right column
			int y = ((i % 6) + 1) * 30 + paddingY;
			coords = getSentryCoordinates(i);

			for (int j = 0; j < 3; j++) {
				int btnX = startX + j * paddingX + 147 + x;
				int btnY = startY + y - 48;

				switch (j) {
					case MODE:
						guiButtons[i][j] = new TogglePictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
					case TARGETS:
						guiButtons[i][j] = new TogglePictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::actionPerformed);
						guiButtons[i][j].active = false;
						break;
					case UNBIND:
						guiButtons[i][j] = new IdButton(id++, btnX, btnY, 20, 20, "X", this::clickUnbind);
						guiButtons[i][j].active = false;
						break;
				}

				addRenderableWidget(guiButtons[i][j]);
			}

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);

			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				guiButtons[i][UNBIND].active = true;

				if (Minecraft.getInstance().player.level.isLoaded(sentryPos) && isSentryVisibleToPlayer(sentryPos)) {
					List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AABB(sentryPos));

					if (!sentries.isEmpty()) {
						Sentry sentry = sentries.get(0);
						SentryMode mode = sentry.getMode();

						if(sentry.hasCustomName())
						{
							TranslatableComponent line = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]));
							int nameWidth = font.width(sentry.getCustomName());
							int nameX = startX + xSize / 4 - nameWidth + 33 + (i / 6) * xSize / 2;
							int nameY = startY + (i % 6) * 30 + 7;
							TextHoverChecker posTooltipText = new TextHoverChecker(nameY + 4, nameY + 18, nameX, nameX + nameWidth + 2, line);

							names[i] = sentry.getCustomName();
							hoverCheckers.add(posTooltipText);
						}

						guiButtons[i][MODE].active = true;
						guiButtons[i][TARGETS].active = mode != SentryMode.IDLE;
						guiButtons[i][UNBIND].active = true;
						((TogglePictureButton)guiButtons[i][0]).setCurrentIndex(mode.ordinal() / 3);
						((TogglePictureButton)guiButtons[i][1]).setCurrentIndex(mode.ordinal() % 3);
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2"), Utils.localize("gui.securitycraft:srat.mode1"), Utils.localize("gui.securitycraft:srat.mode3"))));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1"), Utils.localize("gui.securitycraft:srat.targets2"), Utils.localize("gui.securitycraft:srat.targets3"))));
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
						foundSentry = true;
					}
					else {
						removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2]);

						for (int j = 0; j < 3; j++) {
							guiButtons[i][j].active = false;
						}
					}
				}
				else {
					for (int j = 0; j < 2; j++) {
						hoverCheckers.add(new TextHoverChecker(guiButtons[i][j], Utils.localize("gui.securitycraft:srat.outOfRange")));
					}

					hoverCheckers.add(new TextHoverChecker(guiButtons[i][UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
				}
			}

			if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				lines[i] = notBound;
			else if(names[i] != null)
				lines[i] = names[i];
			else
				lines[i] = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]));

			lengths[i] = font.width(lines[i]);
		}

		//Add buttons for global operation (all sentries), large id
		guiButtonsGlobal[0] = new TogglePictureButton(1000, startX + 260, startY + 188, 20, 20, SENTRY_ICONS, modeTextureX, yStarts, 2, 3, this::actionPerformedGlobal);
		guiButtonsGlobal[1] = new TogglePictureButton(1001, startX + 22 + 260, startY + 188, 20, 20, SENTRY_ICONS, targetTextureX, yStarts, 2, 3, this::actionPerformedGlobal);
		guiButtonsGlobal[2] = new IdButton(1002, startX + 44 + 260, startY + 188, 20, 20, "X", this::clickGlobalUnbind);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[j].active = foundSentry;
			addRenderableWidget(guiButtonsGlobal[j]);
		}

		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[MODE], Arrays.asList(Utils.localize("gui.securitycraft:srat.mode2"), Utils.localize("gui.securitycraft:srat.mode1"), Utils.localize("gui.securitycraft:srat.mode3"))));
		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[TARGETS], Arrays.asList(Utils.localize("gui.securitycraft:srat.targets1"), Utils.localize("gui.securitycraft:srat.targets2"), Utils.localize("gui.securitycraft:srat.targets3"))));
		hoverCheckers.add(new TextHoverChecker(guiButtonsGlobal[UNBIND], Utils.localize("gui.securitycraft:srat.unbind")));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, TEXTURE);
		blit(pose, startX, startY, 0, 0, xSize, ySize, 512, 256);
		super.render(pose, mouseX, mouseY, partialTicks);
		font.draw(pose, Utils.localize(SCContent.REMOTE_ACCESS_SENTRY.get().getDescriptionId()), startX + 5, startY - 25 + 13, 0xFF0000);

		for (int i = 0; i < 12; i++) {
			font.draw(pose, lines[i], startX + xSize / 4 - lengths[i] + 35 + (i / 6) * xSize / 2, startY + (i % 6) * 30 + 13, 4210752);
		}

		font.draw(pose, modifyAll, startX + xSize / 2 - font.width(modifyAll) + 25, startY + 194, 4210752);

		for(TextHoverChecker chc : hoverCheckers)
		{
			if(chc != null && chc.checkHover(mouseX, mouseY))
				renderTooltip(pose, chc.getName(), mouseX, mouseY);
		}
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected void performSingleAction(int sentry, int mode, int targets){
		int[] coords = getSentryCoordinates(sentry);
		List<Sentry> sentries = Minecraft.getInstance().player.level.getEntitiesOfClass(Sentry.class, new AABB(new BlockPos(coords[0], coords[1], coords[2])));

		if(!sentries.isEmpty()) {
			int resultingMode = Math.max(0, Math.min(targets + mode * 3, 6)); //bind between 0 and 6

			guiButtons[sentry][TARGETS].active = SentryMode.values()[resultingMode] != SentryMode.IDLE;
			sentries.get(0).toggleMode(Minecraft.getInstance().player, resultingMode, false);
			SecurityCraft.channel.sendToServer(new SetSentryMode(sentries.get(0).blockPosition(), resultingMode));
		}
	}

	private void clickUnbind(IdButton button)
	{
		unbindSentry(button.id / 3);
	}

	private void clickGlobalUnbind(IdButton button)
	{
		for(int i = 0; i < 12; i++)
		{
			unbindSentry(i);
		}
	}

	private void unbindSentry(int sentry)
	{
		int[] coords = getSentryCoordinates(sentry);

		removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2]);

		for(int i = 0; i < 3; i++) {
			guiButtons[sentry][i].active = false;
		}

		for(int i = 0; i < guiButtons.length; i++)
		{
			if(guiButtons[i][UNBIND].active)
				return;
		}

		for (int i = 0; i < 3; i++) {
			guiButtonsGlobal[i].active = false;
		}
	}

	protected void actionPerformed(IdButton button) {
		int sentry = button.id / 3;
		int type = button.id % 3;
		int mode = ((TogglePictureButton)button).getCurrentIndex();
		int targets = mode;

		if(type == 0)
			targets = ((TogglePictureButton)guiButtons[sentry][TARGETS]).getCurrentIndex();
		else if(type == 1)
			mode = ((TogglePictureButton)guiButtons[sentry][MODE]).getCurrentIndex();

		performSingleAction(sentry, mode, targets);
	}

	protected void actionPerformedGlobal(IdButton button) {
		for (int i = 0; i < renderables.size() / 3; i++) {
			Widget widget = renderables.get(i * 3);

			if(widget instanceof IdButton idButton && getSentryCoordinates(i)[1] != 0)
			{
				int sentry = idButton.id / 3;
				int mode = button.id == guiButtonsGlobal[MODE].id ? ((TogglePictureButton)guiButtonsGlobal[MODE]).getCurrentIndex() : ((TogglePictureButton)guiButtons[sentry][MODE]).getCurrentIndex();
				int targets = button.id == guiButtonsGlobal[TARGETS].id ? ((TogglePictureButton)guiButtonsGlobal[TARGETS]).getCurrentIndex() : ((TogglePictureButton)guiButtons[sentry][TARGETS]).getCurrentIndex();

				((TogglePictureButton)guiButtons[sentry][MODE]).setCurrentIndex(mode);
				((TogglePictureButton)guiButtons[sentry][TARGETS]).setCurrentIndex(targets);
				performSingleAction(sentry, mode, targets);
			}
		}
	}

	/**
	 * @param sentry 0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry12

		if (srat.getItem() != null && srat.getItem() == SCContent.REMOTE_ACCESS_SENTRY.get() && srat.getTag() != null && srat.getTag().getIntArray("sentry" + sentry) != null && srat.getTag().getIntArray("sentry" + sentry).length > 0)
			return srat.getTag().getIntArray("sentry" + sentry);
		else
			return new int[] { 0, 0, 0 };
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z) {
		if (stack.getTag() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTag().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTag().getIntArray("sentry" + i);

				if (coords[0] == x && coords[1] == y && coords[2] == z) {
					stack.getTag().putIntArray("sentry" + i, new int[] { 0, 0, 0 });
					SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(stack));
					return;
				}
			}
		}
	}

	// Based on ChunkManager$EntityTrackerEntry#updateTrackingState
	private boolean isSentryVisibleToPlayer(BlockPos sentryPos){
		Player player = Minecraft.getInstance().player;
		double xDistance = player.getX() - sentryPos.getX();
		double zDistance = player.getZ() - sentryPos.getZ();
		int trackingRange = Math.min(SENTRY_TRACKING_RANGE, viewDistance) - 1;

		return xDistance >= -trackingRange && xDistance <= trackingRange && zDistance >= -trackingRange && zDistance <= trackingRange;
	}

	@Override
	public boolean isPauseScreen(){
		return false;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (minecraft.options.keyInventory.isActiveAndMatches(InputConstants.getKey(keyCode, scanCode))) {
			removed();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
