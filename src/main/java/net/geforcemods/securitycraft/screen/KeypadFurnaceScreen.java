package net.geforcemods.securitycraft.screen;

import java.util.Random;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.inventory.KeypadFurnaceMenu;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeypadFurnaceScreen extends AbstractContainerScreen<KeypadFurnaceMenu>
{
	private static final ResourceLocation FURNACE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/furnace.png");
	private Component title;

	public KeypadFurnaceScreen(KeypadFurnaceMenu menu, Inventory inv, Component title)
	{
		super(menu, inv, title);

		title = new Random().nextInt(100) < 5 ? new TextComponent("Keypad Gurnace")
				: (menu.be.hasCustomName() ? menu.be.getCustomName() : Utils.localize("gui.securitycraft:protectedFurnace.name"));
	}

	@Override
	public void render(PoseStack pose, int mouseX, int mouseY, float partialTicks)
	{
		super.render(pose, mouseX, mouseY, partialTicks);
		renderTooltip(pose, mouseX, mouseY);
	}

	@Override
	protected void renderLabels(PoseStack pose, int mouseX, int mouseY)
	{
		font.draw(pose, title, imageWidth / 2 - font.width(title) / 2, 6.0F, 4210752);
		font.draw(pose, playerInventoryTitle.getString(), 8.0F, imageHeight - 96 + 2, 4210752);
	}

	@Override
	protected void renderBg(PoseStack pose, float partialTicks, int mouseX, int mouseY)
	{
		renderBackground(pose);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem._setShaderTexture(0, FURNACE_GUI_TEXTURES);
		blit(pose, leftPos, topPos, 0, 0, imageWidth, imageHeight);

		if(menu.isLit())
		{
			int burnLeftScaled = menu.getLitProgress();

			blit(pose, leftPos + 56, topPos + 36 + 12 - burnLeftScaled, 176, 12 - burnLeftScaled, 14, burnLeftScaled + 1);
		}

		blit(pose, leftPos + 79, topPos + 34, 176, 14, ((AbstractFurnaceMenu)menu).getBurnProgress() + 1, 16);
	}
}