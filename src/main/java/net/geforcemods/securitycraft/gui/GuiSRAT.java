package net.geforcemods.securitycraft.gui;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiSRAT extends GuiContainer {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/srat.png");
	private static final ResourceLocation SENTRY_ICONS = new ResourceLocation(SecurityCraft.MODID, "textures/items/sentry_256.png");
	private ItemStack srat;
	private GuiButton[][] buttons = new GuiButton[6][4]; // 6 buttons, 4 modes (aggressive, camouflage, idle, unbind)
	private GuiButton[][] buttonsGlobal = new GuiButton[1][3];
	private static final int AGGRESSIVE = 0, CAMOUFLAGE = 1, IDLE = 2, UNBIND = 3;

	public GuiSRAT(InventoryPlayer inventory, ItemStack item) {
		super(new ContainerGeneric(inventory, null));

		srat = item;
		xSize = 256;
		ySize = 235;
	}

	@Override
	public void initGui() {
		super.initGui();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;

		for (int i = 0; i < 6; i++) {
			y += 30;
			coords = getSentryCoordinates(i);

			BlockPos sentryPos = new BlockPos(coords[0], coords[1], coords[2]);
			List<EntitySentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(sentryPos));
			
			if (!sentries.isEmpty()) {
				boolean aggressiveMode = sentries.get(0).getMode() == EnumSentryMode.AGGRESSIVE ? true : false;
				boolean camouflageMode = sentries.get(0).getMode() == EnumSentryMode.CAMOUFLAGE ? true : false;
				boolean idleMode = sentries.get(0).getMode() == EnumSentryMode.IDLE ? true : false;
				boolean bound = !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0);

				for (int j = 0; j < 4; j++) {
					int btnX = guiLeft + j * padding + 154;
					int btnY = guiTop + y - 48;

					switch (j) {
						case AGGRESSIVE:
							buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, -2, -1, 18, 18);
							buttons[i][j].enabled = !aggressiveMode && bound;
							break;
						case CAMOUFLAGE:
							buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 40, -1, 18, 18);
							buttons[i][j].enabled = !camouflageMode && bound;
							break;
						case IDLE:
							buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, SENTRY_ICONS, 19, -1, 18, 17);
							buttons[i][j].enabled = !idleMode && bound;
							break;
						case UNBIND:
							buttons[i][j] = new GuiButton(id++, btnX, btnY, 20, 20, "X");
							buttons[i][j].enabled = bound;
							break;
					}
					buttonList.add(buttons[i][j]);
				}
			}
			else {
				removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], mc.player);
				int btnY = guiTop + y - 48;
				buttons[i][0] = new GuiPictureButton(id++, guiLeft + 0 * padding + 154, btnY, 20, 20, SENTRY_ICONS,	-2, -1, 18, 18);
				buttons[i][1] = new GuiPictureButton(id++, guiLeft + 1 * padding + 154, btnY, 20, 20, SENTRY_ICONS,	40, -1, 18, 18);
				buttons[i][2] = new GuiPictureButton(id++, guiLeft + 2 * padding + 154, btnY, 20, 20, SENTRY_ICONS, 19, -1, 18, 17);
				buttons[i][3] = new GuiButton(id++, guiLeft + 3 * padding + 154, btnY, 20, 20, "X");

				for (int j = 0; j < 4; j++) {
					buttons[i][j].enabled = false;
					buttonList.add(buttons[i][j]);
				}
			}
		}
		//Add buttons for global operation (all sentries), large id
		buttonsGlobal[0][0] = new GuiPictureButton(1000, guiLeft + 154, guiTop + 200, 20, 20, SENTRY_ICONS, -2, -1, 18, 18);
		buttonsGlobal[0][1] = new GuiPictureButton(1001, guiLeft + 25 + 154, guiTop + 200, 20, 20, SENTRY_ICONS, 40, -1, 18, 18);
		buttonsGlobal[0][2] = new GuiPictureButton(1002, guiLeft + 50 + 154, guiTop + 200, 20, 20, SENTRY_ICONS, 19, -1, 18, 17);
		for (int j = 0; j < 3; j++) {
			buttonsGlobal[0][j].enabled = true;
			buttonList.add(buttonsGlobal[0][j]);
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of
	 * the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(ClientUtils.localize("item.securitycraft:remoteAccessSentry.name"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("item.securitycraft:remoteAccessSentry.name")) + 15, -25 + 13, 0xFF0000);

		for (int i = 0; i < 6; i++) {
			int[] coords = getSentryCoordinates(i);
			String line;

			if (coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:srat.notBound");
			else
				line = ClientUtils.localize("gui.securitycraft:srat.sentryLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));

			fontRenderer.drawString(line, xSize / 2 - fontRenderer.getStringWidth(line) + 25, i * 30 + 13, 4210752);
			fontRenderer.drawString("Modify all bound sentries", xSize / 2 - fontRenderer.getStringWidth("Modify all bound sentries") + 25, 206, 4210752);
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
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	/**
	 * Change the sentry mode, and update GUI buttons state
	 */
	protected void performSingleAction(int sentry, int mode){
		int[] coords = getSentryCoordinates(sentry);
		List<EntitySentry> sentries = Minecraft.getMinecraft().player.world.getEntitiesWithinAABB(EntitySentry.class, new AxisAlignedBB(new BlockPos(coords[0], coords[1], coords[2])));
		if (!sentries.isEmpty()) {
			switch (mode) {
				case AGGRESSIVE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, AGGRESSIVE);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode));
					buttons[sentry][AGGRESSIVE].enabled = false;
					buttons[sentry][CAMOUFLAGE].enabled = true;
					buttons[sentry][IDLE].enabled = true;
					break;
				case CAMOUFLAGE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, CAMOUFLAGE);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode));
					buttons[sentry][AGGRESSIVE].enabled = true;
					buttons[sentry][CAMOUFLAGE].enabled = false;
					buttons[sentry][IDLE].enabled = true;
					break;
				case IDLE:
					sentries.get(0).toggleMode(Minecraft.getMinecraft().player, IDLE);
					SecurityCraft.network.sendToServer(new PacketSetSentryMode(sentries.get(0).getPosition(), mode));
					buttons[sentry][AGGRESSIVE].enabled = true;
					buttons[sentry][CAMOUFLAGE].enabled = true;
					buttons[sentry][IDLE].enabled = false;
					break;
				case UNBIND:
					removeTagFromToolAndUpdate(srat, coords[0], coords[1], coords[2], Minecraft.getMinecraft().player);
					for (int i = 0; i < 4; i++) {
						buttons[sentry][i].enabled = false;
					}
			}
		}
	}

	/**
	 * Forces sentry to a defined mode, overriding its associated button state/mode
	 */
	protected void forceMode(GuiButton button, int mode) {
		int sentry = button.id / 4;
		performSingleAction(sentry, mode);
	}
	
	/**
	 * Action to perform when a button is clicked
	 */
	@Override
	protected void actionPerformed(GuiButton button) {
		int sentry = button.id / 4;
		int mode = button.id % 4;

		if (sentry > 100) { //clicked on global buttons
			for (int i = 0; i < buttonList.size() / 4; i++) {
				forceMode(buttonList.get(i * 4), mode);
			}
			return;
		}

		performSingleAction(sentry, mode);
	}

	/**
	 * @param sentry
	 *            0 based
	 */
	private int[] getSentryCoordinates(int sentry) {
		sentry++; // sentries are stored starting by sentry1 up to sentry6

		if (srat.getItem() != null && srat.getItem() == SCContent.remoteAccessSentry && srat.getTagCompound() != null && srat.getTagCompound().getIntArray("sentry" + sentry) != null && srat.getTagCompound().getIntArray("sentry" + sentry).length > 0)
			return srat.getTagCompound().getIntArray("sentry" + sentry);
		else
			return new int[] { 0, 0, 0 };
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z, EntityPlayer player) {
		if (stack.getTagCompound() == null)
			return;

		for (int i = 1; i <= 6; i++) {
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
