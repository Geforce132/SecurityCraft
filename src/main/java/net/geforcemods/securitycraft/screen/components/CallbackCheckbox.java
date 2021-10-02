package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

//copy from vanilla's Checkbox to be able to change the text color and remove the shadow
public class CallbackCheckbox extends AbstractButton
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	private boolean selected;
	private final Consumer<Boolean> onChange;
	private final int textColor;

	public CallbackCheckbox(int x, int y, int width, int height, String message, boolean selected, Consumer<Boolean> onChange, int textColor)
	{
		super(x, y, width, height, message);

		this.selected = selected;
		this.onChange = onChange;
		this.textColor = textColor;
	}

	@Override
	public void onPress()
	{
		selected = !selected;
		onChange.accept(selected);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		Minecraft minecraft = Minecraft.getInstance();

		minecraft.getTextureManager().bindTexture(TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(x, y, 0.0F, selected ? 20.0F : 0.0F, 20, height, 32, 64);
		renderBg(minecraft, mouseX, mouseY);
		minecraft.fontRenderer.drawString(getMessage(), x + 24, y + (height - 8) / 2, textColor | MathHelper.ceil(alpha * 255.0F) << 24);
	}

	public boolean selected()
	{
		return selected;
	}
}
