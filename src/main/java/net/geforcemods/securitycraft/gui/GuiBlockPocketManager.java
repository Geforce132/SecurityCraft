package net.geforcemods.securitycraft.gui;

import java.io.IOException;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.gui.components.GuiSlider;
import net.geforcemods.securitycraft.gui.components.GuiSlider.ISlider;
import net.geforcemods.securitycraft.network.packets.PacketSSyncBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

public class GuiBlockPocketManager extends GuiContainer implements ISlider
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/block_pocket_manager.png");
	public TileEntityBlockPocketManager te;
	private int size = 5;
	private GuiButton toggleButton;
	private GuiButton sizeButton;
	private GuiButton assembleButton;
	private GuiButton outlineButton;
	private GuiSlider offsetSlider;
	private static final ItemStack BLOCK_POCKET_WALL = new ItemStack(SCContent.blockPocketWall);
	private static final ItemStack REINFORCED_CHISELED_CRYSTAL_QUARTZ = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 1);
	private static final ItemStack REINFORCED_CRYSTAL_QUARTZ_PILLAR = new ItemStack(SCContent.reinforcedCrystalQuartz, 1, 2);

	public GuiBlockPocketManager(TileEntityBlockPocketManager te)
	{
		super(new ContainerGeneric());

		this.te = te;
		size = te.size;
		ySize = 194;
	}

	@Override
	public void initGui()
	{
		super.initGui();

		buttonList.add(toggleButton = new GuiButton(0, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 - 40, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager." + (!te.enabled ? "activate" : "deactivate")).getFormattedText()));
		buttonList.add(sizeButton = new GuiButton(1, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 - 70, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText()));
		buttonList.add(assembleButton = new GuiButton(2, guiLeft + xSize / 2 - 45, guiTop + ySize / 2 + 23, 90, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.assemble").getFormattedText()));
		buttonList.add(outlineButton = new GuiButton(3, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 + 47, 120, 20, ClientUtils.localize("gui.securitycraft:blockPocketManager.outline." + (!te.showOutline ? "show" : "hide")).getFormattedText()));
		buttonList.add(offsetSlider = new GuiSlider(ClientUtils.localize("gui.securitycraft:projector.offset", te.autoBuildOffset).getFormattedText(), "", 4, guiLeft + xSize / 2 - 60, guiTop + ySize / 2 + 71, 120, 20, ClientUtils.localize("gui.securitycraft:projector.offset", "").getFormattedText(), (-size + 2) / 2, (size - 2) / 2, te.autoBuildOffset, false, true, this));
		offsetSlider.updateSlider();

		if(!te.getOwner().isOwner(Minecraft.getMinecraft().player))
			sizeButton.enabled = toggleButton.enabled = assembleButton.enabled = outlineButton.enabled = offsetSlider.enabled = false;
		else
			sizeButton.enabled = assembleButton.enabled = offsetSlider.enabled = !te.enabled;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String translation = ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name").getFormattedText();

		fontRenderer.drawString(translation, xSize / 2 - fontRenderer.getStringWidth(translation) / 2, 6, 4210752);

		if(!te.enabled)
		{
			fontRenderer.drawString(ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed").getFormattedText(), xSize / 2 - fontRenderer.getStringWidth(ClientUtils.localize("gui.securitycraft:blockPocketManager.youNeed").getFormattedText()) / 2, 83, 4210752);

			fontRenderer.drawString((size - 2) * (size - 2) * 6 + "", 42, 100, 4210752);
			GuiUtils.drawItemStackToGui(BLOCK_POCKET_WALL, 25, 96, false);

			fontRenderer.drawString((size - 2) * 12 - 1 + "", 94, 100, 4210752);
			GuiUtils.drawItemStackToGui(REINFORCED_CRYSTAL_QUARTZ_PILLAR, 77, 96, false);

			fontRenderer.drawString("8", 147, 100, 4210752);
			GuiUtils.drawItemStackToGui(REINFORCED_CHISELED_CRYSTAL_QUARTZ, 130, 96, false);

			if(mouseX >= guiLeft + 23 && mouseX < guiLeft + 48 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderToolTip(BLOCK_POCKET_WALL, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 75 && mouseX < guiLeft + 100 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderToolTip(REINFORCED_CRYSTAL_QUARTZ_PILLAR, mouseX - guiLeft, mouseY - guiTop);

			if(mouseX >= guiLeft + 128 && mouseX < guiLeft + 153 && mouseY >= guiTop + 93 && mouseY < guiTop + 115)
				renderToolTip(REINFORCED_CHISELED_CRYSTAL_QUARTZ, mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;

		drawDefaultBackground();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(TEXTURE);
		drawTexturedModalRect(startX, startY, 0, 0, xSize, ySize);
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
					PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), feedback, TextFormatting.DARK_AQUA);
			}

			Minecraft.getMinecraft().player.closeScreen();
		}
		else if(button.id == sizeButton.id)
		{
			int newOffset;
			int newMin;
			int newMax;

			size += 4;

			if(size > 25)
				size = 5;

			newMin = (-size + 2) / 2;
			newMax = (size - 2) / 2;

			if(te.autoBuildOffset > 0)
				newOffset = Math.min(te.autoBuildOffset, newMax);
			else
				newOffset = Math.max(te.autoBuildOffset, newMin);

			te.size = size;
			offsetSlider.minValue = newMin;
			offsetSlider.maxValue = newMax;
			te.autoBuildOffset = newOffset;
			offsetSlider.setValue(newOffset);
			offsetSlider.updateSlider();
			button.displayString = ClientUtils.localize("gui.securitycraft:blockPocketManager.size", size, size, size).getFormattedText();
			sync();
		}
		else if(button.id == assembleButton.id)
		{
			ITextComponent feedback;

			te.size = size;
			feedback = te.autoAssembleMultiblock(Minecraft.getMinecraft().player);

			if(feedback != null)
				PlayerUtils.sendMessageToPlayer(Minecraft.getMinecraft().player, ClientUtils.localize(SCContent.blockPocketManager.getTranslationKey() + ".name"), feedback, TextFormatting.DARK_AQUA);

			Minecraft.getMinecraft().player.closeScreen();
		}
		else if(button.id == outlineButton.id)
		{
			te.toggleOutline();
			outlineButton.displayString = ClientUtils.localize("gui.securitycraft:blockPocketManager.outline."+ (!te.showOutline ? "show" : "hide")).getFormattedText();
			sync();
		}
	}

	private void sync()
	{
		SecurityCraft.network.sendToServer(new PacketSSyncBlockPocketManager(te.getPos(), te.size, te.showOutline, te.autoBuildOffset));
	}

	@Override
	public void onMouseRelease(int id)
	{
		if(id == offsetSlider.id)
		{
			te.autoBuildOffset = offsetSlider.getValueInt();
			sync();
		}
	}

	@Override
	public void onChangeSliderValue(GuiSlider slider, String blockName, int id)
	{
		if(slider.id == offsetSlider.id)
			slider.displayString = slider.prefix + slider.getValueInt();
	}
}
