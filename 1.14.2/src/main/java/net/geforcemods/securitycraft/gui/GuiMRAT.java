package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
import net.geforcemods.securitycraft.gui.components.GuiPictureButton;
import net.geforcemods.securitycraft.network.server.SetExplosiveState;
import net.geforcemods.securitycraft.network.server.UpdateNBTTagOnServer;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiMRAT extends GuiContainer{

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/mrat.png");
	private static final ResourceLocation INFO_BOOK_ICONS = new ResourceLocation("securitycraft:textures/gui/info_book_icons.png"); //for the explosion icon
	private ItemStack mrat;
	private GuiButton[][] guiButtons = new GuiButton[6][4]; //6 mines, 4 actions (defuse, prime, detonate, unbind)
	private static final int DEFUSE = 0, ACTIVATE = 1, DETONATE = 2, UNBIND = 3;

	public GuiMRAT(ItemStack item) {
		super(new ContainerGeneric());

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

		for(int i = 0; i < 6; i++)
		{
			y += 30;
			coords = getMineCoordinates(i);

			BlockPos minePos = new BlockPos(coords[0], coords[1], coords[2]);
			Block block = mc.world.getBlockState(minePos).getBlock();
			boolean active = block instanceof IExplosive && ((IExplosive) block).isActive(mc.world, minePos);
			boolean defusable = (block instanceof IExplosive && ((IExplosive) block).isDefusable());
			boolean bound = !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0);

			for(int j = 0; j < 4; j++)
			{
				int btnX = guiLeft + j * padding + 154;
				int btnY = guiTop + y - 48;

				switch(j)
				{
					case DEFUSE:
						guiButtons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, itemRender, new ItemStack(SCContent.wireCutters), this::actionPerformed);
						guiButtons[i][j].enabled = active && bound && defusable;
						break;
					case ACTIVATE:
						guiButtons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, itemRender, new ItemStack(Items.FLINT_AND_STEEL), this::actionPerformed);
						guiButtons[i][j].enabled = !active && bound && defusable;
						break;
					case DETONATE:
						guiButtons[i][j] = new GuiPictureButton(id++, btnX, btnY, 20, 20, INFO_BOOK_ICONS, 54, 1, 18, 18, this::actionPerformed);
						guiButtons[i][j].enabled = active && bound;
						break;
					case UNBIND:
						guiButtons[i][j] = new GuiButtonClick(id++, btnX, btnY, 20, 20, "X", this::actionPerformed);
						guiButtons[i][j].enabled = bound;
						break;
				}

				addButton(guiButtons[i][j]);

				if(!(block instanceof IExplosive))
				{
					removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], mc.player);
					guiButtons[i][j].enabled = false;
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
		fontRenderer.drawString(ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey()), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize(SCContent.remoteAccessMine.getTranslationKey())), -25 + 13, 0xFF0000);

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
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
	}

	protected void actionPerformed(GuiButton button){
		int mine = button.id / 4;
		int action = button.id % 4;

		int[] coords = getMineCoordinates(mine);

		switch(action)
		{
			case DEFUSE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).defuseMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "defuse"));
				guiButtons[mine][DEFUSE].enabled = false;
				guiButtons[mine][ACTIVATE].enabled = true;
				guiButtons[mine][DETONATE].enabled = false;
				break;
			case ACTIVATE:
				((IExplosive)Minecraft.getInstance().player.world.getBlockState(new BlockPos(coords[0], coords[1], coords[2])).getBlock()).activateMine(Minecraft.getInstance().player.world, new BlockPos(coords[0], coords[1], coords[2]));
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "activate"));
				guiButtons[mine][DEFUSE].enabled = true;
				guiButtons[mine][ACTIVATE].enabled = false;
				guiButtons[mine][DETONATE].enabled = true;
				break;
			case DETONATE:
				SecurityCraft.channel.sendToServer(new SetExplosiveState(coords[0], coords[1], coords[2], "detonate"));
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], Minecraft.getInstance().player);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].enabled = false;
				}

				break;
			case UNBIND:
				removeTagFromToolAndUpdate(mrat, coords[0], coords[1], coords[2], Minecraft.getInstance().player);

				for(int i = 0; i < 4; i++)
				{
					guiButtons[mine][i].enabled = false;
				}
		}
	}

	/**
	 * @param mine 0 based
	 */
	private int[] getMineCoordinates(int mine)
	{
		mine++; //mines are stored starting by mine1 up to mine6

		if(mrat.getItem() != null && mrat.getItem() == SCContent.remoteAccessMine && mrat.getTag() != null &&  mrat.getTag().getIntArray("mine" + mine) != null && mrat.getTag().getIntArray("mine" + mine).length > 0)
			return mrat.getTag().getIntArray("mine" + mine);
		else
			return new int[] {0,0,0};
	}

	private void removeTagFromToolAndUpdate(ItemStack stack, int x, int y, int z, PlayerEntity player)
	{
		if(stack.getTag() == null)
			return;

		for(int i = 1; i <= 6; i++)
		{
			if(stack.getTag().getIntArray("mine" + i).length > 0)
			{
				int[] coords = stack.getTag().getIntArray("mine" + i);

				if(coords[0] == x && coords[1] == y && coords[2] == z && !(coords[0] == 0 && coords[1] == 0 && coords[2] == 0))
				{
					stack.getTag().putIntArray("mine" + i, new int[]{0, 0, 0});
					SecurityCraft.channel.sendToServer(new UpdateNBTTagOnServer(stack));
					return;
				}
			}
		}
	}
}
