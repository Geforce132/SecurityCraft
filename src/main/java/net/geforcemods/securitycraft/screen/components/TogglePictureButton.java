package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TogglePictureButton extends IdButton{

	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int[] u;
	private int[] v;
	private int currentIndex = 0;
	private final int toggleCount;

	public TogglePictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int[] textureX, int[] textureY, int toggleCount, Consumer<IdButton> onClick)
	{
		super(id, xPos, yPos, width, height, "", onClick);

		if(textureX.length != toggleCount || textureY.length != toggleCount)
			throw new RuntimeException("TogglePictureButton was set up incorrectly. Array lengths must match toggleCount!");

		textureLocation = texture;
		u = textureX;
		v = textureY;
		this.toggleCount = toggleCount;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		if (visible)
		{
			Minecraft mc = Minecraft.getInstance();
			FontRenderer font = mc.fontRenderer;
			ItemRenderer itemRenderer = mc.getItemRenderer();
			int hoverState = !active ? 0 : !isHovered ? 1 : 2;

			mc.getTextureManager().bindTexture(WIDGETS_LOCATION);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			isHovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			blit(x, y, 0, 46 + hoverState * 20, width / 2, height);
			blit(x + width / 2, y, 200 - width / 2, 46 + hoverState * 20, width / 2, height);

			if(blockToRender != null){
				RenderSystem.enableRescaleNormal(); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(font, new ItemStack(blockToRender), x + 2, y + 3, "");
			}else if(itemToRender != null){
				RenderSystem.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(font, new ItemStack(itemToRender), x + 2, y + 2, "");
				RenderSystem.disableLighting();
			}
			else if(textureLocation != null)
			{
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(textureLocation);
				blit(x + 2, y + 2, u[currentIndex], v[currentIndex], 16, 16);
			}

			int color = 0xe0e0e0;

			if (!active)
				color = 0xa0a0a0;
			else if (isHovered)
				color = 0xffffa0;

			drawCenteredString(font, getMessage(), x + width / 2, y + (height - 8) / 2, color);

		}
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		setCurrentIndex(currentIndex + 1);
		super.onClick(mouseX, mouseY);
	}

	public int getCurrentIndex()
	{
		return currentIndex;
	}

	public void setCurrentIndex(int newIndex)
	{
		currentIndex = newIndex % toggleCount;
	}

	public Item getItemStack() {
		return (blockToRender != null ? blockToRender.asItem() : itemToRender);
	}
}
