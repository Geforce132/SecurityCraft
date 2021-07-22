package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DisguiseModuleScreen extends ContainerScreen<DisguiseModuleContainer> {
	private static final ResourceLocation TEXTURE  = new ResourceLocation("securitycraft:textures/gui/container/customize1.png");
	private final TranslationTextComponent disguiseModuleName = Utils.localize(SCContent.DISGUISE_MODULE.get().getDescriptionId());

	public DisguiseModuleScreen(DisguiseModuleContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.render(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getItem().isEmpty())
			renderTooltip(matrix, getSlotUnderMouse().getItem(), mouseX, mouseY);
	}

	@Override
	protected void renderLabels(MatrixStack matrix, int mouseX, int mouseY) {
		font.draw(matrix, disguiseModuleName, imageWidth / 2 - font.width(disguiseModuleName) / 2, 6, 4210752);
	}

	@Override
	protected void renderBg(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bind(TEXTURE);
		int startX = (width - imageWidth) / 2;
		int startY = (height - imageHeight) / 2;
		this.blit(matrix, startX, startY, 0, 0, imageWidth, imageHeight);
	}

}
