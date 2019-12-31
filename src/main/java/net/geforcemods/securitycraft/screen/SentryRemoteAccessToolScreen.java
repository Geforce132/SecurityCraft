package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.entity.SentryEntity.EnumSentryMode;
import net.geforcemods.securitycraft.network.server.SetSentryMode;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.screen.components.ClickButton;
import net.geforcemods.securitycraft.screen.components.PictureButton;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SentryRemoteAccessToolScreen extends Screen {

	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private ItemStack srat;
	private ClickButton[][] guiButtons = new ClickButton[12][4]; // 12 buttons, 4 modes (aggressive, camouflage, idle, unbind)
	private ITextComponent[] names = new ITextComponent[12];
	private ClickButton[][] guiButtonsGlobal = new ClickButton[1][3];
	private static final int AGGRESSIVE = 0, CAMOUFLAGE = 1, IDLE = 2, UNBIND = 3;
	private int xSize = 440, ySize = 215;

	public SentryRemoteAccessToolScreen(ItemStack item) {
		super(new TranslationTextComponent(item.getTranslationKey()));

		srat = item;
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

		for (int i = 0; i < 12; i++) {
			int x = (i / 6) * xSize / 2; //first six sentries in the left column, second six sentries in the right column
			int y = ((i % 6) + 1) * 30 + paddingY;
			coords = getSentryCoordinates(i);

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
			List<SentryEntity> sentries = Minecraft.getInstance().player.world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(sentryPos));

			if (!sentries.isEmpty()) {
				SentryEntity sentry = sentries.get(0);
				boolean aggressiveMode = sentry.getMode() == EnumSentryMode.AGGRESSIVE ? true : false;
				boolean camouflageMode = sentry.getMode() == EnumSentryMode.CAMOUFLAGE ? true : false;
				boolean idleMode = sentry.getMode() == EnumSentryMode.IDLE ? true : false;
				boolean bound = !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0);

				if(sentry.hasCustomName())
					names[i] = sentry.getCustomName();

				for (int j = 0; j < 4; j++) {
					int btnX = startX + j * paddingX + 127 + x;
					int btnY = startY + y - 48;

					switch (j) {
						case AGGRESSIVE:
							guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, -2, -1, 18, 18, this::actionPerformed);
							guiButtons[i][j].active = !aggressiveMode && bound;
							break;
						case CAMOUFLAGE:
							guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 40, -1, 18, 18, this::actionPerformed);
							guiButtons[i][j].active = !camouflageMode && bound;
							break;
						case IDLE:
							guiButtons[i][j] = new PictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 19, -1, 18, 17, this::actionPerformed);
							guiButtons[i][j].active = !idleMode && bound;
							break;
						case UNBIND:
							guiButtons[i][j] = new ClickButton(id++, btnX, btnY, 20, 20, "X", this::actionPerformed);
							guiButtons[i][j].active = bound;
							break;
					}

					addButton(guiButtons[i][j]);
				}
			}
			else {
				int btnY = startY + y - 48;

				removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], minecraft.player);
				guiButtons[i][0] = new PictureButton(id++, startX + 0 * paddingX + 127 + x, btnY, 20, 20, SENTRY_ICONS,	-2, -1, 18, 18, this::actionPerformed);
				guiButtons[i][1] = new PictureButton(id++, startX + 1 * paddingX + 127 + x, btnY, 20, 20, SENTRY_ICONS,	40, -1, 18, 18, this::actionPerformed);
				guiButtons[i][2] = new PictureButton(id++, startX + 2 * paddingX + 127 + x, btnY, 20, 20, SENTRY_ICONS, 19, -1, 18, 17, this::actionPerformed);
				guiButtons[i][3] = new ClickButton(id++, startX + 3 * paddingX + 127 + x, btnY, 20, 20, "X", this::actionPerformed);

				for (int j = 0; j < 4; j++) {
					guiButtons[i][j].active = false;
					addButton(guiButtons[i][j]);
				}
			}
		}

		//Add buttons for global operation (all sentries), large id
		guiButtonsGlobal[0][0] = new PictureButton(1000, startX + 260, startY + 188, 20, 20, SENTRY_ICONS, -2, -1, 18, 18, this::actionPerformed);
		guiButtonsGlobal[0][1] = new PictureButton(1001, startX + 22 + 260, startY + 188, 20, 20, SENTRY_ICONS, 40, -1, 18, 18, this::actionPerformed);
		guiButtonsGlobal[0][2] = new PictureButton(1002, startX + 44 + 260, startY + 188, 20, 20, SENTRY_ICONS, 19, -1, 18, 17, this::actionPerformed);

		for (int j = 0; j < 3; j++) {
			guiButtonsGlobal[0][j].active = true;
			addButton(guiButtonsGlobal[0][j]);
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		String modifyAll = ClientUtils.localize("gui.securitycraft:srat.modifyAll");

		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(startX, startY, 0, 0, xSize, ySize, 512, 256);
		super.render(mouseX, mouseY, partialTicks);
		font.drawString(ClientUtils.localize(SCContent.remoteAccessSentry.getTranslationKey()), startX + 5, startY - 25 + 13, 0xFF0000);

		for (int i = 0; i < 12; i++) {
			int[] coords = getSentryCoordinates(i);
			String line;

			if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:srat.notBound");
			else if(names[i] != null)
				line = names[i].getFormattedText();
			else
				line = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]));

			font.drawString(line, startX + xSize / 4 - font.getStringWidth(line) + 15 + (i / 6) * xSize / 2, startY + (i % 6) * 30 + 13, 4210752);
			font.drawString(modifyAll, startX + xSize / 2 - font.getStringWidth(modifyAll) + 25, startY + 194, 4210752);
		}
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected void performSingleAction(int sentry, int mode, boolean sendMessage){
		int[] coords = getSentryCoordinates(sentry);
		List<SentryEntity> sentries = Minecraft.getInstance().player.world.getEntitiesWithinAABB(SentryEntity.class, new AxisAlignedBB(new BlockPos(coords[0], coords[1], coords[2])));

		if(!sentries.isEmpty()) {
			switch(mode) {
				case AGGRESSIVE:
					sentries.get(0).toggleMode(Minecraft.getInstance().player, AGGRESSIVE, sendMessage);
					SecurityCraft.channel.sendToServer(new SetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					guiButtons[sentry][AGGRESSIVE].active = false;
					guiButtons[sentry][CAMOUFLAGE].active = true;
					guiButtons[sentry][IDLE].active = true;
					break;
				case CAMOUFLAGE:
					sentries.get(0).toggleMode(Minecraft.getInstance().player, CAMOUFLAGE, sendMessage);
					SecurityCraft.channel.sendToServer(new SetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					guiButtons[sentry][AGGRESSIVE].active = true;
					guiButtons[sentry][CAMOUFLAGE].active = false;
					guiButtons[sentry][IDLE].active = true;
					break;
				case IDLE:
					sentries.get(0).toggleMode(Minecraft.getInstance().player, IDLE, sendMessage);
					SecurityCraft.channel.sendToServer(new SetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					guiButtons[sentry][AGGRESSIVE].active = true;
					guiButtons[sentry][CAMOUFLAGE].active = true;
					guiButtons[sentry][IDLE].active = false;
					break;
				case UNBIND:
					removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], Minecraft.getInstance().player);

					for(int i = 0; i < 4; i++) {
						guiButtons[sentry][i].active = false;
					}
			}
		}
	}

	/**
	 * Forces sentry to a defined mode, overriding its associated button state/mode
	 */
	protected void forceMode(ClickButton button, int mode, boolean sendMessage) {
		int sentry = button.id / 4;
		performSingleAction(sentry, mode, sendMessage);
	}

	/**
	 * Action to perform when a button is clicked
	 */
	protected void actionPerformed(ClickButton button) {
		int sentry = button.id / 4;
		int mode = button.id % 4;
		boolean messageSent = false;

		if (sentry > 100) { //clicked on global buttons
			for (int i = 0; i < buttons.size() / 4; i++) {
				Widget widget = buttons.get(i * 4);

				if(widget instanceof ClickButton)
				{
					if(getSentryCoordinates(i)[1] != 0)
					{
						forceMode((ClickButton)buttons.get(i * 4), mode, !messageSent);
						messageSent = true;
					}
				}
			}
			return;
		}

		performSingleAction(sentry, mode, true);
	}

	/**
	 * @param sentry 0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry12

		if (srat.getItem() != null && srat.getItem() == SCContent.remoteAccessSentry && srat.getTag() != null && srat.getTag().getIntArray("sentry" + sentry) != null && srat.getTag().getIntArray("sentry" + sentry).length > 0)
			return srat.getTag().getIntArray("sentry" + sentry);
		else
			return new int[] { 0, 0, 0 };
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z, PlayerEntity player) {
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
}
