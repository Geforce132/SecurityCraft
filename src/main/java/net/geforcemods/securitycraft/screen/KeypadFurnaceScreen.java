package net.geforcemods.securitycraft.screen;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends ContainerScreen<KeypadFurnaceContainer>
{
	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
	private KeypadFurnaceTileEntity tileFurnace;
	private boolean gurnace = false;

	public KeypadFurnaceScreen(KeypadFurnaceContainer container, PlayerInventory inv, ITextComponent name)
	{
		super(container, inv, name);

		tileFurnace = container.te;

		if(new Random().nextInt(100) < 5)
			gurnace = true;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		String s = gurnace ? "Keypad Gurnace" : (tileFurnace.hasCustomSCName() ? tileFurnace.getCustomSCName().getString() : ClientUtils.localize("gui.securitycraft:protectedFurnace.name"));
		field_230712_o_.drawString(s, xSize / 2 - field_230712_o_.getStringWidth(s) / 2, 6.0F, 4210752);
		field_230712_o_.drawString(playerInventory.getDisplayName().getString(), 8.0F, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
	{
		func_230446_a_();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);

		if(((AbstractFurnaceContainer)container).isBurning())
		{
			int burnLeftScaled = ((AbstractFurnaceContainer)container).getBurnLeftScaled();

			blit(guiLeft + 56, guiTop + 36 + 12 - burnLeftScaled, 176, 12 - burnLeftScaled, 14, burnLeftScaled + 1);
		}

		blit(guiLeft + 79, guiTop + 34, 176, 14, ((AbstractFurnaceContainer)container).getCookProgressionScaled() + 1, 16);
	}
}