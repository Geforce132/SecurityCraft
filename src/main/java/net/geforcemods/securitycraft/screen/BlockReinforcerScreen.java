package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.BlockReinforcerContainer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockReinforcerScreen extends AbstractContainerScreen<BlockReinforcerContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer.png");
	private static final ResourceLocation TEXTURE_LVL1 = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer_lvl1.png");
	private final TranslatableComponent ubr = Utils.localize("gui.securitycraft:blockReinforcer.title");
	private final TranslatableComponent output = Utils.localize("gui.securitycraft:blockReinforcer.output");
	private final boolean isLvl1;

	public BlockReinforcerScreen(BlockReinforcerContainer container, Inventory inv, Component name)
	{
		super(container, inv, name);

		this.isLvl1 = container.isLvl1;
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack matrix, int mouseX, int mouseY)
	{
		NonNullList<ItemStack> inv = menu.getItems();

		font.draw(matrix, ubr, (imageWidth - font.width(ubr)) / 2, 5, 4210752);
		font.draw(matrix, Utils.localize("container.inventory"), 8, imageHeight - 96 + 2, 4210752);

		if(!inv.get(36).isEmpty())
		{
			font.draw(matrix, output, 50, 25, 4210752);
			minecraft.getItemRenderer().renderAndDecorateItem(menu.reinforcingSlot.getOutput(), 116, 20);
			minecraft.getItemRenderer().renderGuiItemDecorations(minecraft.font, menu.reinforcingSlot.getOutput(), 116, 20, null);

			if(mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 17 && mouseY < topPos + 39)
				renderTooltip(matrix, menu.reinforcingSlot.getOutput(), mouseX - leftPos, mouseY - topPos);
		}

		if(!isLvl1 && !inv.get(37).isEmpty())
		{
			font.draw(matrix, output, 50, 50, 4210752);
			minecraft.getItemRenderer().renderAndDecorateItem(menu.unreinforcingSlot.getOutput(), 116, 46);
			minecraft.getItemRenderer().renderGuiItemDecorations(minecraft.font, menu.unreinforcingSlot.getOutput(), 116, 46, null);

			if(mouseX >= leftPos + 114 && mouseX < leftPos + 134 && mouseY >= topPos + 43 && mouseY < topPos + 64)
				renderTooltip(matrix, menu.unreinforcingSlot.getOutput(), mouseX - leftPos, mouseY - topPos);
		}
	}

	@Override
	protected void renderBg(PoseStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(isLvl1 ? TEXTURE_LVL1 : TEXTURE);
		blit(matrix, leftPos, topPos, 0, 0, imageWidth, imageHeight);
	}
}
