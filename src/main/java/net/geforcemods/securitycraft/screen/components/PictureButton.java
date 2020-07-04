package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PictureButton extends ClickButton{

	private final ItemRenderer itemRenderer;
	private Block blockToRender;
	private Item itemToRender;
	private ResourceLocation textureLocation;
	private int u;
	private int v;
	private int texWidth;
	private int texHeight;

	public PictureButton(int id, int xPos, int yPos, int field_230708_k_, int field_230689_k_, ItemRenderer par7, ItemStack itemToRender) {
		this(id, xPos, yPos, field_230708_k_, field_230689_k_, par7, itemToRender, null);
	}

	public PictureButton(int id, int xPos, int yPos, int field_230708_k_, int field_230689_k_, ItemRenderer par7, ItemStack itemToRender, Consumer<ClickButton> onClick) {
		super(id, xPos, yPos, field_230708_k_, field_230689_k_, "", onClick);
		itemRenderer = par7;

		if(!itemToRender.isEmpty() && itemToRender.getItem() instanceof BlockItem)
			blockToRender = Block.getBlockFromItem(itemToRender.getItem());
		else
			this.itemToRender = itemToRender.getItem();
	}

	public PictureButton(int id, int xPos, int yPos, int field_230708_k_, int field_230689_k_, ResourceLocation texture, int textureX, int textureY, int textureWidth, int textureHeight, Consumer<ClickButton> onClick)
	{
		super(id, xPos, yPos, field_230708_k_, field_230689_k_, "", onClick);

		itemRenderer = null;
		textureLocation = texture;
		u = textureX;
		v = textureY;
		texWidth = textureWidth;
		texHeight = textureHeight;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		Minecraft mc = Minecraft.getInstance();

		if (field_230694_p_)
		{
			FontRenderer font = mc.fontRenderer;
			mc.getTextureManager().bindTexture(field_230687_i_);
			RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			field_230692_n_ = mouseX >= field_230690_l_ && mouseY >= field_230691_m_ && mouseX < field_230690_l_ + field_230688_j_ && mouseY < field_230691_m_ + field_230689_k_;
			int hoverState = !field_230693_o_ ? 0 : !field_230692_n_ ? 1 : 2;
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(770, 771, 1, 0);
			RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			this.blit(field_230690_l_, field_230691_m_, 0, 46 + hoverState * 20, field_230688_j_ / 2, field_230689_k_);
			this.blit(field_230690_l_ + field_230688_j_ / 2, field_230691_m_, 200 - field_230688_j_ / 2, 46 + hoverState * 20, field_230688_j_ / 2, field_230689_k_);

			if(blockToRender != null){
				RenderSystem.enableRescaleNormal(); //(this.field_230708_k_ / 2) - 8
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(blockToRender), field_230690_l_ + 2, field_230691_m_ + 3);
				itemRenderer.renderItemOverlayIntoGUI(font, new ItemStack(blockToRender), field_230690_l_ + 2, field_230691_m_ + 3, "");
			}else if(itemToRender != null){
				RenderSystem.enableRescaleNormal();
				itemRenderer.renderItemAndEffectIntoGUI(new ItemStack(itemToRender), field_230690_l_ + 2, field_230691_m_ + 2);
				itemRenderer.renderItemOverlayIntoGUI(font, new ItemStack(itemToRender), field_230690_l_ + 2, field_230691_m_ + 2, "");
				RenderSystem.disableLighting();
			}
			else if(textureLocation != null)
			{
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				mc.getTextureManager().bindTexture(textureLocation);
				blit(field_230690_l_, field_230691_m_ + 1, u, v, texWidth, texHeight);
			}

			onDrag(mouseX, mouseY, 0, 0);

			int color = 14737632;

			if (!field_230693_o_)
				color = 10526880;
			else if (field_230692_n_)
				color = 16777120;

			drawCenteredString(font, getMessage(), field_230690_l_ + field_230688_j_ / 2, field_230691_m_ + (field_230689_k_ - 8) / 2, color);

		}
	}

	public Item getItemStack() {
		return (blockToRender != null ? blockToRender.asItem() : itemToRender);
	}

}
