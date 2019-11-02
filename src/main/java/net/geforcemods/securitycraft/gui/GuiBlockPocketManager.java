package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiButtonClick;
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

		addButton(toggleButton = new GuiButtonClick(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 10, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")), this::actionPerformed));
		addButton(sizeButton = new GuiButtonClick(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 40, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size), this::actionPerformed));

		if(!te.getOwner().isOwner(Minecraft.getInstance().player))
			sizeButton.enabled = toggleButton.enabled = false;
		else
			sizeButton.enabled = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String translation = ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey());

		fontRenderer.drawString(translation, xSize / 2 - fontRenderer.getStringWidth(translation) / 2, 6, 4210752);
	}

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

	protected void actionPerformed(GuiButton button)
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
					PlayerUtils.sendMessageToPlayer(Minecraft.getInstance().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey()), ClientUtils.localize(feedback.getKey(), feedback.getFormatArgs()), TextFormatting.DARK_AQUA);
			}

			Minecraft.getInstance().player.closeScreen();
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
