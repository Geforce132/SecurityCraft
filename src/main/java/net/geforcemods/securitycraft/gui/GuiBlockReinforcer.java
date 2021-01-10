package net.geforcemods.securitycraft.gui;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerBlockReinforcer;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class GuiBlockReinforcer extends GuiContainer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation TEXTURE_LVL1 = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer_lvl1.png");
	private final boolean isLvl1;

	public GuiBlockReinforcer(Container container, boolean isLvl1)
	{
		super(container);

		this.isLvl1 = isLvl1;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderToolTip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		ContainerBlockReinforcer container = (ContainerBlockReinforcer)inventorySlots;
		NonNullList<ItemStack> inv = container.getInventory();
		String ubr = ClientUtils.localize("gui.securitycraft:blockReinforcer.title").getFormattedText();

		fontRenderer.drawString(ubr, (xSize - fontRenderer.getStringWidth(ubr)) / 2, 5, 4210752);
		fontRenderer.drawString(ClientUtils.localize("container.inventory").getFormattedText(), 8, ySize - 96 + 2, 4210752);

		if(!inv.get(0).isEmpty())
		{
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 25, 4210752);
			GuiUtils.drawItemStackToGui(container.reinforcingSlot.getOutput(), 116, 20, false);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 17 && mouseY < guiTop + 39)
				renderToolTip(container.reinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}

		if(!isLvl1 && !inv.get(1).isEmpty())
		{
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 50, 4210752);
			GuiUtils.drawItemStackToGui(container.unreinforcingSlot.getOutput(), 116, 46, false);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 43 && mouseY < guiTop + 64)
				renderToolTip(container.unreinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(isLvl1 ? TEXTURE_LVL1 : TEXTURE);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
