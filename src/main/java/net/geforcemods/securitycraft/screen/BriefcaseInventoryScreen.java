package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BriefcaseInventoryScreen extends ContainerScreen<BriefcaseContainer> {

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/briefcase_inventory.png");
	private final String formattedTitle;

	public BriefcaseInventoryScreen(BriefcaseContainer container, PlayerInventory inventory, ITextComponent name) {
		super(container, inventory, name);

		formattedTitle = name.getString();
	}

	@Override
	public void func_230430_a_(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		super.func_230430_a_(matrix, mouseX, mouseY, partialTicks);

		if(getSlotUnderMouse() != null && !getSlotUnderMouse().getStack().isEmpty())
			func_230457_a_(matrix, getSlotUnderMouse().getStack(), mouseX, mouseY);
	}

	@Override
	protected void func_230451_b_(MatrixStack matrix, int mouseX, int mouseY) {
		field_230712_o_.drawString(formattedTitle, xSize / 2 - field_230712_o_.getStringWidth(formattedTitle) / 2, 6, 4210752);
	}

	@Override
	protected void func_230450_a_(MatrixStack matrix, float partialTicks, int mouseX, int mouseY) {
		func_230446_a_(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		field_230706_i_.getTextureManager().bindTexture(TEXTURE);
		int startX = (field_230708_k_ - xSize) / 2;
		int startY = (field_230709_l_ - ySize) / 2;
		this.blit(startX, startY, 0, 0, xSize, ySize);
	}

}
