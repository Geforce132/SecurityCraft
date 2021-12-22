package net.geforcemods.securitycraft.screen;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.KeypadFurnaceContainer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends ContainerScreen<KeypadFurnaceContainer> {
	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");

	public KeypadFurnaceScreen(KeypadFurnaceContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, new Random().nextInt(100) < 5 ? new StringTextComponent("Keypad Gurnace") : (container.te.hasCustomName() ? container.te.getCustomName() : Utils.localize(SCContent.KEYPAD_FURNACE.get().getTranslationKey())));
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		super.render(mouseX, mouseY, partialTicks);
		renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String s = title.getFormattedText();

		font.drawString(s, xSize / 2 - font.getStringWidth(s) / 2, 6.0F, 4210752);
		font.drawString(playerInventory.getDisplayName().getFormattedText(), 8.0F, ySize - 96 + 2, 4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		renderBackground();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(FURNACE_GUI_TEXTURES);
		blit(guiLeft, guiTop, 0, 0, xSize, ySize);

		if (((AbstractFurnaceContainer) container).isBurning()) {
			int burnLeftScaled = ((AbstractFurnaceContainer) container).getBurnLeftScaled();

			blit(guiLeft + 56, guiTop + 36 + 12 - burnLeftScaled, 176, 12 - burnLeftScaled, 14, burnLeftScaled + 1);
		}

		blit(guiLeft + 79, guiTop + 34, 176, 14, ((AbstractFurnaceContainer) container).getCookProgressionScaled() + 1, 16);
	}
}