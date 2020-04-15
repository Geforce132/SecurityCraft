package net.geforcemods.securitycraft.gui;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.gui.components.StringHoverChecker;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class GuiMRAT extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private ItemStack mrat;
	private GuiButton[][] buttons = new GuiButton[6][4]; //6 buttons, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;
	private List<StringHoverChecker> hoverCheckers = new ArrayList<>();

	public GuiMRAT(InventoryPlayer inventory, ItemStack item) {
		super(new ContainerGeneric(inventory, null));

		mrat = item;
		xSize = 256;
		ySize = 184;
	}

	@Override
	public void initGui(){
		super.initGui();

		int padding = 25;
		int y = padding;
		int[] coords = null;
		int id = 0;
		hoverCheckers.clear();

		for(int i = 0; i < 6; i++)
		{
			y += 30;
			coords = getMineCoordinates(i);

			//initialize buttons
			for(int j = 0; j < 4; j++)
			{
				int btnX = guiLeft + j * padding + 154;
				int btnY = guiTop + y - 48;

				switch(j)
				{
					case DEFUSE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, itemRender, new ItemStack(SCContent.wireCutters));
						buttons[i][j].enabled = false;
						break;
					case ACTIVATE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, itemRender, new ItemStack(Items.FLINT_AND_STEEL));
						buttons[i][j].enabled = false;
						break;
					case DETONATE:
						buttons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 18, 18);
						buttons[i][j].enabled = false;
						break;
					case UNBIND:
						buttons[i][j] = new GuiButton(id++, btnX, btnY, 20, 20, "X");
						buttons[i][j].enabled = false;
						break;
				}

				buttonList.add(buttons[i][j]);
			}

			BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);
			if (!(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)) {
				buttons[i][UNBIND].enabled = true;
				if (Minecraft.getMinecraft().player.world.isBlockLoaded(minePos, false)) {
					Block block = mc.world.getBlockState(minePos).getBlock();
					if (block instanceof IExplosive) {
						boolean active = ((IExplosive) block).isActive(mc.world, minePos);
						boolean defusable = ((IExplosive) block).isDefusable();

						buttons[i][DEFUSE].enabled = active && defusable;
						buttons[i][ACTIVATE].enabled = !active && defusable;
						buttons[i][DETONATE].enabled = active;
						hoverCheckers.add(new StringHoverChecker(buttons[i][DEFUSE], 20, ClientUtils.localize("gui.securitycraft:mrat.defuse")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][ACTIVATE], 20, ClientUtils.localize("gui.securitycraft:mrat.activate")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][DETONATE], 20, ClientUtils.localize("gui.securitycraft:mrat.detonate")));
						hoverCheckers.add(new StringHoverChecker(buttons[i][UNBIND], 20, ClientUtils.localize("gui.securitycraft:mrat.unbind")));
					}
					else {
						removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);
						for (int j = 0; j < 4; j++) {
							buttons[i][j].enabled = false;
						}
					}
				}
				else {
					for (int j = 0; j < 3; j++) {
						hoverCheckers.add(new StringHoverChecker(buttons[i][j], 20, ClientUtils.localize("gui.securitycraft:mrat.outOfRange")));
					}
					hoverCheckers.add(new StringHoverChecker(buttons[i][UNBIND], 20, ClientUtils.localize("gui.securitycraft:mrat.unbind")));
				}
			}
		}
	}

	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		fontRenderer.drawString(ClientUtils.localize("item.securitycraft:remoteAccessMine.name"), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("item.securitycraft:remoteAccessMine.name")), -25 + 13, 0xFF0000);

		for(int i = 0; i < 6; i++)
		{
			int[] coords = getMineCoordinates(i);
			String line;

			if(coords[0] == 0 && coords[1] == 0 && coords[2] == 0)
				line = ClientUtils.localize("gui.securitycraft:mrat.notBound");
			else
				line = ClientUtils.localize("gui.securitycraft:mrat.mineLocations").replace("#location", Utils.getFormattedCoordinates(new BlockPos(coords[0], coords[1], coords[2])));

			fontRenderer.drawString(line, xSize / 2 - fontRenderer.getStringWidth(line) + 25, i * 30 + 13, 4210752);
		}
	}

	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);

		for(StringHoverChecker chc : hoverCheckers)
		{
			if(chc != null && chc.checkHover(mouseX, mouseY) && chc.getName() != null)
				drawHoveringText(chc.getLines(), mouseX, mouseY, fontRenderer);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button){
		int mine = button.id / 4;
		int action = button.id % 4;

		int[] coords = getMineCoordinates(mine);

		switch(action)
		{
			case DEFUSE:
				((IExplosive)Minecraft.getMinecraft().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getMinecraft().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "defuse"));
				buttons[mine][DEFUSE].enabled = false;
				buttons[mine][ACTIVATE].enabled = true;
				buttons[mine][DETONATE].enabled = false;
				break;
			case ACTIVATE:
				((IExplosive)Minecraft.getMinecraft().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getMinecraft().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "activate"));
				buttons[mine][DEFUSE].enabled = true;
				buttons[mine][ACTIVATE].enabled = false;
				buttons[mine][DETONATE].enabled = true;
				break;
			case DETONATE:
				SecurityCraft.network.sendToServer(new PacketSetExplosiveState(coords[0], coords[1], coords[2], "detonate"));
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					buttons[mine][i].enabled = false;
				}

				break;
			case UNBIND:
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2]);

				for(int i = 0; i < 4; i++)
				{
					buttons[mine][i].enabled = false;
				}
		}
	}

	/**
	 * @param mine 0 based
	 */
	private int[] getMineCoordinates(int mine)
	{
		mine++; //mines are stored starting by mine1 up to mine6

		if(mrat.getItem() != null && mrat.getItem() == SCContent.remoteAccessMine && mrat.getTagCompound() != null &&  mrat.getTagCompound().getIntArray("mine" + mine) != null && mrat.getTagCompound().getIntArray("mine" + mine).length > 0)
			return mrat.getTagCompound().getIntArray("mine" + mine);
		else
			return new int[] {0,0,0};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z)
	{
		if(stack.getTagCompound() == null)
			return;

		for(int i = 1; i <= 6; i++)
		{
			if(stack.getTagCompound().getIntArray("mine" + i).length > 0)
			{
				int[] coords = stack.getTagCompound().getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z)
				{
					stack.getTagCompound().setIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.network.sendToServer(new PacketSUpdateNBTTag(stack));
					return;
				}
			}
		}
	}
}
