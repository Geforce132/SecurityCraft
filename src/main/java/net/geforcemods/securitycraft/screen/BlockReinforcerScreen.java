package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.util.ClientUtils;
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

	public BlockReinforcerScreen(BlockReinforcerContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);
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
		String ubr = ClientUtils.localize("gui.securitycraft:blockReinforcer.title");

		font.drawString(ubr, (xSize - font.getStringWidth(ubr)) / 2, 5, 4210752);
		font.drawString(ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);

		if(!inv.get(0).isEmpty())
		{
			font.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output"), 50, 25, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(container.reinforcingSlot.getOutput(), 116, 20);
			minecraft.getItemRenderer().renderItemOverlayIntoGUI(minecraft.fontRenderer, container.reinforcingSlot.getOutput(), 116, 20, null);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 17 && mouseY < guiTop + 39)
				renderTooltip(container.reinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}

		if(!inv.get(1).isEmpty())
		{
			font.drawString(ClientUtils.localize("gui.securitycraft:blockReinforcer.output"), 50, 50, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(container.unreinforcingSlot.getOutput(), 116, 46);
			minecraft.getItemRenderer().renderItemOverlayIntoGUI(minecraft.fontRenderer, container.unreinforcingSlot.getOutput(), 116, 46, null);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 43 && mouseY < guiTop + 64)
				renderTooltip(container.unreinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
