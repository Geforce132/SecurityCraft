package net.geforcemods.securitycraft.gui.components;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiPictureButton extends GuiButton{

	private final ItemRenderer itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int texWidth;
	private int texHeight;
	private Consumer<GuiButton> onClick;

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ItemRenderer par7, ItemStack itemToRender) {
		this(id, xPos, yPos, width, height, par7, itemToRender, null);
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int textureWidth, int textureHeight)
	{
		this(id, xPos, yPos, width, height, texture, textureX, textureY, textureWidth, textureHeight, null);
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ItemRenderer par7, ItemStack itemToRender, Consumer<GuiButton> onClick) {
		super(id, xPos, yPos, width, height, "");
		itemRenderer = par7;

		if(!itemToRender.isEmpty() && itemToRender.getItem().getTranslationKey().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
	}

	public GuiPictureButton(int id, int xPos, int yPos, int width, int height, ResourceLocation texture, int textureX, int textureY, int textureWidth, int textureHeight, Consumer<GuiButton> onClick)
	{
		super(id, xPos, yPos, width, height, "");

		itemRenderer = null;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		texWidth = textureWidth;
		texHeight = textureHeight;
		this.onClick = onClick;
	}

	@Override
	public void onClick(double mouseX, double mouseY)
	{
		if(onClick != null)
			onClick.accept(this);
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		Minecraft mc = Minecraft.getInstance();

		if (visible)
		{
			FontRenderer fontRenderer = mc.fontRenderer;
			mc.getTextureManager().bindTexture(BUTTON_TEXTURES);
			GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			hovered = mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height;
			int hoverState = getHoverState(hovered);
			GlStateManager.enableBlend();
			OpenGlHelper.glBlendFuncSeparate(770, 771, 1, 0);
			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			this.drawTexturedModalRect(x, y, 0, 46 + hoverState * 20, width / 2, height);
			this.drawTexturedModalRect(x + width / 2, y, 200 - width / 2, 46 + hoverState * 20, width / 2, height);

			if(blockToRender != null){
				GlStateManager.enableRescaleNormal(); //(this.width / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), x + 2, y + 3);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(blockToRender), x + 2, y + 3, "");
			}else if(itemToRender != null){
				GlStateManager.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), x + 2, y + 2);
				itemRenderer.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(itemToRender), x + 2, y + 2, "");
				GlStateManager.disableLighting();
			}
			else if(textureLocation != null)
			{
				GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(textureLocation);
				drawTexturedModalRect(x, y + 1, u, v, texWidth, texHeight);
			}

			onDrag(mouseX, mouseY);

			int color = 14737632;

			if (!enabled)
				color = 10526880;
			else if (hovered)
				color = 16777120;

			drawCenteredString(fontRenderer, displayString, x + width / 2, y + (height - 8) / 2, color);

		}
	}

	public void setDisplayItem(ItemStack stack){
		blockToRender = null;
		itemToRender = null;

		if(stack.getTranslationKey().startsWith("tile."))
			blockToRender = Block.getBlockFromItem(stack.getItem());
		else
			itemToRender = stack.getItem();

	}

	public Item getItemStack() {
		return (blockToRender != null ? blockToRender.asItem() : itemToRender);
	}

}
