package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.GuiUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockReinforcerScreen extends ContainerScreen<BlockReinforcerContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation TEXTURE_LVL1 = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer_lvl1.png");
	private final boolean isLvl1;

	public BlockReinforcerScreen(BlockReinforcerContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		this.isLvl1 = container.isLvl1;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		NonNullList<ItemStack> inv = container.getInventory();
		String ubr = ClientUtils.localize("gui.securitycraft:blockReinforcer.title").getFormattedText();

		font.drawString(ubr, (xSize - font.getStringWidth(ubr)) / 2, 5, 4210752);
		font.drawString(ClientUtils.localize("container.inventory").getFormattedText(), 8, ySize - 96 + 2, 4210752);

		if(!inv.get(36).isEmpty())
		{
			font.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 25, 4210752);
			GuiUtils.drawItemStackToGui(container.reinforcingSlot.getOutput(), 116, 20, false);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 17 && mouseY < guiTop + 39)
				renderTooltip(container.reinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}

		if(!isLvl1 && !inv.get(37).isEmpty())
		{
			font.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output").getFormattedText(), 50, 50, 4210752);
			GuiUtils.drawItemStackToGui(container.unreinforcingSlot.getOutput(), 116, 46, false);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 43 && mouseY < guiTop + 64)
				renderTooltip(container.unreinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(isLvl1 ? TEXTURE_LVL1 : TEXTURE);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
