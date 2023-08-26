package net.geforcemods.securitycraft.screen.components;

import java.util.function.Consumer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;

// copy from vanilla's Checkbox to be able to change the text color and remove the shadow
public class CallbackCheckbox extends AbstractButton implements IToggleableButton {
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/gui/checkbox.png");
	private boolean selected;
	private final Consumer<Boolean> onChange;
	private final int textColor;

	public CallbackCheckbox(int x, int y, int width, int height, ITextComponent message, boolean selected, Consumer<Boolean> onChange, int textColor) {
		super(x, y, width, height, message);

		this.selected = selected;
		this.onChange = onChange;
		this.textColor = textColor;
	}

	@Override
	public void onPress() {
		setSelected(!selected);
	}

	@Override
	public void renderButton(MatrixStack matrix, int mouseX, int mouseY, float partialTicks) {
		Minecraft minecraft = Minecraft.getInstance();

		minecraft.getTextureManager().bind(TEXTURE);
		RenderSystem.enableDepthTest();
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, alpha);
		RenderSystem.enableBlend();
		RenderSystem.defaultBlendFunc();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		blit(matrix, x, y, width, height, isFocused() ? 20.0F : 0.0F, selected ? 20.0F : 0.0F, 20, 20, 64, 64);
		renderBg(matrix, minecraft, mouseX, mouseY);
		minecraft.font.draw(matrix, getMessage(), x + (width * 1.2F), y + (height - 8) / 2, textColor | MathHelper.ceil(alpha * 255.0F) << 24);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		onChange.accept(selected);
	}

	public boolean selected() {
		return selected;
	}

	@Override
	public int getCurrentIndex() {
		return selected() ? 1 : 0;
	}

	@Override
	public void setCurrentIndex(int newIndex) {
		setSelected(newIndex == 1);
	}
}
