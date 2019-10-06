package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GuiBlockPocketManager extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");
	public TileEntityBlockPocketManager te;
	private int size = 5;
	private GuiButton toggleButton;
	private GuiButton sizeButton;

	public GuiBlockPocketManager(TileEntityBlockPocketManager te)
	{
		super(new ContainerGeneric());

		this.te = te;
		size = te.size;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add(toggleButton = new GuiButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 10, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate"))));
		buttonList.add(sizeButton = new GuiButton(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 40, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size)));

		if(!te.getOwner().isOwner(Minecraft.getMinecraft().player))
			sizeButton.enabled = toggleButton.enabled = false;
		else
			sizeButton.enabled = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String translation = ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name");

		fontRenderer.drawString(translation, xSize / 2 - fontRenderer.getStringWidth(translation) / 2, 6, 4210752);
	}

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
	protected void actionPerformed(GuiButton button) throws IOException
	{
		if(button.id == toggleButton.id)
		{
			if(te.enabled)
				te.disableMultiblock();
			else
			{
				TextComponentTranslation feedback;

				te.size = size;
				feedback = te.enableMultiblock();

				if(feedback != null)
					PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), ClientUtils.localize(feedback.getKey(), feedback.getFormatArgs()), TextFormatting.DARK_AQUA);
			}

			Minecraft.getMinecraft().player.closeScreen();
		}
		else if(button.id == sizeButton.id)
		{
			size += 4;

			if(size > 25)
				size = 5;

			button.displayString = ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size);
		}
	}
}
