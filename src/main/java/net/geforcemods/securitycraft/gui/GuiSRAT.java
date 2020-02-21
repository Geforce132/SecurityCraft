package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSetSentryMode;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class GuiSRAT extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/gui/container/sentry_icons.png");
	private ItemStack srat;
	private GuiButton[][] buttons = new GuiButton[12][4]; // 12 buttons, 4 modes (aggressive, camouflage, idle, unbind)
	private String[] names = new String[12];
	private GuiButton[][] buttonsGlobal = new GuiButton[1][3];
	private static final int AGGRESSIVE = 0, CAMOUFLAGE = 1, IDLE = 2, UNBIND = 3;
	private List<StringHoverChecker> hoverCheckers = new ArrayList<StringHoverChecker>();

	public GuiSRAT(InventoryPlayer inventory, ItemStack item) {
		super(new ContainerGeneric(inventory, null));

		srat = item;
		xSize = 440;
		ySize = 215;
	}

	@Override
	public void initGui() {
		super.initGui();

		int paddingX = 22;
		int paddingY = 25;
		int[] coords = null;
		int id = 0;
		boolean foundSentry = false;
		hoverCheckers.clear();

		for (int i = 0; i < 12; i++) {
			int x = (i / 6) * xSize / 2; //first six sentries in the left column, second six sentries in the right column
			int y = ((i % 6) + 1) * 30 + paddingY;
			coords = getSentryCoordinates(i);

			//initialize buttons
			for (int j = 0; j < 4; j++) {
				int btnX = guiLeft + j * paddingX + 127 + x;
				int btnY = guiTop + y - 48;

				switch (j) {
					case AGGRESSIVE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, -2, -1, 18, 18);
						buttons[i][j].enabled = false;
						break;
					case CAMOUFLAGE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 40, -1, 18, 18);
						buttons[i][j].enabled = false;
						break;
					case IDLE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 19, -1, 18, 17);
						buttons[i][j].enabled = false;
						break;
					case UNBIND:
						buttons[i][j] = new GuiButton(id++, btnX, btnY, 20, 20, "X");
						buttons[i][j].enabled = false;
						break;
				}
				buttonList.add(buttons[i][j]);
			}

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				if (Minecraft.getMinecraft().player.world.isBlockLoaded(sentryPos, false)) {
					List<EntitySentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(sentryPos));

					if (!sentries.isEmpty()) {
						EntitySentry sentry = sentries.get(0);
						boolean aggressiveMode = sentry.getMode() == EnumSentryMode.AGGRESSIVE;
						boolean camouflageMode = sentry.getMode() == EnumSentryMode.CAMOUFLAGE;
						boolean idleMode = sentry.getMode() == EnumSentryMode.IDLE;

						if(sentry.hasCustomName())
							names[i] = sentry.getCustomNameTag();

						buttons[i][AGGRESSIVE].enabled = !aggressiveMode;
						buttons[i][CAMOUFLAGE].enabled = !camouflageMode;
						buttons[i][IDLE].enabled = !idleMode;
						buttons[i][UNBIND].enabled = true;
						hoverCheckers.add(new StringHoverChecker(buttons[i][AGGRESSIVE], 20, ClientUtils.localize("gui.securitycraft:srat.mode1")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][CAMOUFLAGE], 20, ClientUtils.localize("gui.securitycraft:srat.mode2")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][IDLE], 20, ClientUtils.localize("gui.securitycraft:srat.mode3")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][UNBIND], 20, ClientUtils.localize("gui.securitycraft:srat.unbind")));
						foundSentry = true;
					}
					else {
						removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], mc.player);
						for (int j = 0; j < 4; j++) {	
							buttons[i][j].enabled = false;
						}
					}
				}
				else {
					for (int j = 0; j < 4; j++) {	
						hoverCheckers.add(new StringHoverChecker(buttons[i][j], 20, ClientUtils.localize("gui.securitycraft:srat.outOfRange")));
					}
				}
			}
		}

		//Add buttons for global operation (all sentries), large id
		buttonsGlobal[0][0] = new GuiPictureButton(1000, guiLeft + 260, guiTop + 188, 20, 20, SENTRY_ICONS, -2, -1, 18, 18);
		buttonsGlobal[0][1] = new GuiPictureButton(1001, guiLeft + 22 + 260, guiTop + 188, 20, 20, SENTRY_ICONS, 40, -1, 18, 18);
		buttonsGlobal[0][2] = new GuiPictureButton(1002, guiLeft + 44 + 260, guiTop + 188, 20, 20, SENTRY_ICONS, 19, -1, 18, 17);
		for (int j = 0; j < 3; j++) {
			buttonsGlobal[0][j].enabled = foundSentry;
			buttonList.add(buttonsGlobal[0][j]);
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String modifyAll = ClientUtils.localize("gui.securitycraft:srat.modifyAll");
		fontRenderer.drawString(ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), 5, -25 + 13, 0xFF0000);

		for (int i = 0; i < 12; i++) {
			int[] coords = getSentryCoordinates(i);
			String line;

			if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:srat.notBound");
			else if(names[i] != null)
				line = names[i];
			else
				line = Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2]));

			fontRenderer.drawString(line, xSize / 4 - fontRenderer.getStringWidth(line) + 15 + (i / 6) * xSize / 2, (i % 6) * 30 + 13, 4210752);
			fontRenderer.drawString(modifyAll, xSize / 2 - fontRenderer.getStringWidth(modifyAll) + 25, 194, 4210752);
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the
	 * items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		drawModalRectWithCustomSizedTexture(startX, startY, 0, 0, xSize, ySize, 512, 256);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(StringHoverChecker chc : hoverCheckers)
		{
			if(chc != null && chc.checkHover(mouseX, mouseY) && chc.getName() != null)
				drawHoveringText(((StringHoverChecker)chc).getLines(), mouseX, mouseY, fontRenderer);
		}
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected void performSingleAction(int sentry, int mode, boolean sendMessage){
		int[] coords = getSentryCoordinates(sentry);
		List<EntitySentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(new BlockPos(coords[0], coords[1], coords[2])));
		if (!sentries.isEmpty()) {
			switch (mode) {
				case AGGRESSIVE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, AGGRESSIVE, sendMessage);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					buttons[sentry][AGGRESSIVE].enabled = false;
					buttons[sentry][CAMOUFLAGE].enabled = true;
					buttons[sentry][IDLE].enabled = true;
					break;
				case CAMOUFLAGE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, CAMOUFLAGE, sendMessage);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					buttons[sentry][AGGRESSIVE].enabled = true;
					buttons[sentry][CAMOUFLAGE].enabled = false;
					buttons[sentry][IDLE].enabled = true;
					break;
				case IDLE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, IDLE, sendMessage);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode, sendMessage));
					buttons[sentry][AGGRESSIVE].enabled = true;
					buttons[sentry][CAMOUFLAGE].enabled = true;
					buttons[sentry][IDLE].enabled = false;
					break;
				case UNBIND:
					removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], Minecraft.getMinecraft().player);
					for (int i = 0; i < 4; i++) {
						buttons[sentry][i].enabled = false;
					}

					for(int i = 0; i < buttons.length; i++)
					{
						if(buttons[i][UNBIND].enabled)
							return;
					}

					for (int i = 0; i < 3; i++) {
						buttonsGlobal[0][i].enabled = false;
					}
			}
		}
	}

	/**
	 * Forces sentry to a defined mode, overriding its associated button state/mode
	 */
	protected void forceMode(GuiButton button, int mode, boolean sendMessage) {
		int sentry = button.id / 4;
		performSingleAction(sentry, mode, sendMessage);
	}

	/**
	 * Action to perform when a button is clicked
	 */
	@Override
	protected void actionPerformed(GuiButton button) {
		int sentry = button.id / 4;
		int mode = button.id % 4;
		boolean messageSent = false;

		if (sentry > 100) { //clicked on global buttons
			for (int i = 0; i < buttonList.size() / 4; i++) {
				if(getSentryCoordinates(i)[1] != 0)
				{
					forceMode(buttonList.get(i * 4), mode, !messageSent);
					messageSent = true;
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

		if (srat.getItem() != null && srat.getItem() == SCContent.remoteAccessSentry && srat.getTagCompound() != null && srat.getTagCompound().getIntArray("sentry" + sentry) != null && srat.getTagCompound().getIntArray("sentry" + sentry).length > 0)
			return srat.getTagCompound().getIntArray("sentry" + sentry);
		else
			return new int[] { 0, 0, 0 };
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 12; i++) {
			if (stack.getTagCompound().getIntArray("sentry" + i).length > 0) {
				int[] coords = stack.getTagCompound().getIntArray("sentry" + i);

				if (coords[0] == x && coords[1] == y && coords[2] == z) {
					stack.getTagCompound().setIntArray("sentry" + i, new int[] { 0, 0, 0 });
					SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(stack));
					return;
				}
			}
		}
	}
}
