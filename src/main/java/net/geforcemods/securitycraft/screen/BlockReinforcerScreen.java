package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockReinforcerScreen extends ContainerScreen<BlockReinforcerContainer>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID + ":textures/gui/container/universal_block_reinforcer.png");
	private final TranslationTextComponent ubr = ClientUtils.localize("gui.securitycraft:blockReinforcer.title");
	private final TranslationTextComponent output = ClientUtils.localize("gui.securitycraft:blockReinforcer.output");

	public BlockReinforcerScreen(BlockReinforcerContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		NonNullList<ItemStack> inv = container.getInventory();

		font.func_238422_b_(matrix, ubr, (xSize - font.func_238414_a_(ubr)) / 2, 5, 4210752);
		font.func_238422_b_(matrix, ClientUtils.localize("container.inventory"), 8, ySize - 96 + 2, 4210752);

		if(!inv.get(0).isEmpty())
		{
			font.func_238422_b_(matrix, output, 50, 25, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(container.reinforcingSlot.getOutput(), 116, 20);
			minecraft.getItemRenderer().renderItemOverlayIntoGUI(minecraft.fontRenderer, container.reinforcingSlot.getOutput(), 116, 20, null);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 17 && mouseY < guiTop + 39)
				renderTooltip(matrix, container.reinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}

		if(!inv.get(1).isEmpty())
		{
			font.func_238422_b_(matrix, output, 50, 50, 4210752);
			minecraft.getItemRenderer().renderItemAndEffectIntoGUI(container.unreinforcingSlot.getOutput(), 116, 46);
			minecraft.getItemRenderer().renderItemOverlayIntoGUI(minecraft.fontRenderer, container.unreinforcingSlot.getOutput(), 116, 46, null);

			if(mouseX >= guiLeft + 114 && mouseX < guiLeft + 134 && mouseY >= guiTop + 43 && mouseY < guiTop + 64)
				renderTooltip(matrix, container.unreinforcingSlot.getOutput(), mouseX - guiLeft, mouseY - guiTop);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);
	}
}
