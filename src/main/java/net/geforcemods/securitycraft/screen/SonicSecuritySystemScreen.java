package net.geforcemods.securitycraft.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.screen.components.IdButton;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemScreen extends Screen {

	private final SonicSecuritySystemTileEntity te;
	private int xSize = 176, ySize = 166;

	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/gui/container/blank.png");

	public SonicSecuritySystemScreen(SonicSecuritySystemTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	public void init()
	{
		super.init();

		addButton(new IdButton(0, width / 2 - 38, height / 2 - 60, 60, 20, "Toggle on/off", this::actionPerformed));
		addButton(new IdButton(1, width / 2 - 38, height / 2 - 20, 60, 20, "Unlink all", this::actionPerformed));
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		renderBackground(matrix);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(TEXTURE);
		int startX = (width - xSize) / 2;
		int startY = (height - ySize) / 2;
		this.blit(matrix, startX, startY, 0, 0, xSize, ySize);

		super.render(matrix, mouseX, mouseY, partialTicks);
	}

	protected void actionPerformed(IdButton button)
	{
		System.out.println(button.id);

		if(button.id == 2)
		{
			System.out.println(te.recordedNotes.size());
		}
	}

}
