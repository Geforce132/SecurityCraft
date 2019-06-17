package net.geforcemods.securitycraft.renderers;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockSecretSignStanding;
import net.geforcemods.securitycraft.blocks.BlockSecretSignWall;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.model.SignModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TileEntitySecretSignRenderer extends TileEntityRenderer<TileEntitySecretSign>
{
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
	private static final SignModel model = new SignModel();

	@Override
	public void render(TileEntitySecretSign te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		BlockState state = te.getBlockState();
		FontRenderer font = getFontRenderer();

		GlStateManager.pushMatrix();

		if(state.getBlock() == SCContent.secretSignStanding)
		{
			GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotatef(-(state.get(BlockSecretSignStanding.ROTATION) * 360 / 16.0F), 0.0F, 1.0F, 0.0F);
			model.getSignStick().showModel = true;
		}
		else
		{
			GlStateManager.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			GlStateManager.rotatef(-state.get(BlockSecretSignWall.FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
			GlStateManager.translatef(0.0F, -0.3125F, -0.4375F);
			model.getSignStick().showModel = false;
		}

		if(destroyStage >= 0)
		{
			bindTexture(DESTROY_STAGES[destroyStage]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scalef(4.0F, 2.0F, 1.0F);
			GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else
			bindTexture(SIGN_TEXTURE);

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scalef(0.6666667F, -0.6666667F, -0.6666667F);
		model.renderSign();
		GlStateManager.popMatrix();
		GlStateManager.translatef(0.0F, 0.33333334F, 0.046666667F);
		GlStateManager.scalef(0.010416667F, -0.010416667F, 0.010416667F);
		GlStateManager.normal3f(0.0F, 0.0F, -0.010416667F);
		GlStateManager.depthMask(false);

		if(te.getOwner().isOwner(Minecraft.getInstance().player) && destroyStage < 0)
		{
			for(int i = 0; i < 4; ++i)
			{
				String s = te.getRenderText(i, textComponent -> {
					List<ITextComponent> list = RenderComponentsUtil.splitText(textComponent, 90, font, false, true);

					return list.isEmpty() ? "" : list.get(0).getFormattedText();
				});

				if(s != null)
				{
					if(i == te.func_214064_s())
						s = "> " + s + " <";

					font.drawString(s, -font.getStringWidth(s) / 2, i * 10 - te.signText.length * 5, 0);
				}
			}
		}

		GlStateManager.depthMask(true);
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();

		if(destroyStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}