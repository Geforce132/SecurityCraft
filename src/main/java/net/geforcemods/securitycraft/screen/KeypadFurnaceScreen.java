package net.geforcemods.securitycraft.screen;

import java.util.Random;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends ContainerScreen<KeypadFurnaceContainer>
{
	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
	private ITextComponent title;

	public KeypadFurnaceScreen(KeypadFurnaceContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		title = new Random().nextInt(100) < 5 ? new StringTextComponent("Keypad Gurnace")
				: (container.te.hasCustomSCName() ? container.te.getCustomSCName() : ClientUtils.localize("gui.securitycraft:protectedFurnace.name"));
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);
		renderHoveredTooltip(matrix, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(MatrixStack matrix, int mouseX, int mouseY)
	{
		font.func_243248_b(matrix, title, xSize / 2 - font.getStringPropertyWidth(title) / 2, 6.0F, 4210752);
		font.drawString(matrix, playerInventory.getDisplayName().getString(), 8.0F, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(MatrixStack matrix, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
		blit(matrix, guiLeft, guiTop, 0, 0, xSize, ySize);

		if(((AbstractFurnaceContainer)container).isBurning())
		{
			int burnLeftScaled = ((AbstractFurnaceContainer)container).getBurnLeftScaled();

			blit(matrix, guiLeft + 56, guiTop + 36 + 12 - burnLeftScaled, 176, 12 - burnLeftScaled, 14, burnLeftScaled + 1);
		}

		blit(matrix, guiLeft + 79, guiTop + 34, 176, 14, ((AbstractFurnaceContainer)container).getCookProgressionScaled() + 1, 16);
	}
}