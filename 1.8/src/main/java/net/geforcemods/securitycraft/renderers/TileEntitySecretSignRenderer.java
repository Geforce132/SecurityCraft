package net.geforcemods.securitycraft.renderers;

import java.util.List;

import org.lwjgl.opengl.GL11;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntitySecretSignRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");
	/** The ModelSign instance for use in this renderer */
	private static final ModelSign model = new ModelSign();

	public void doRender(TileEntitySecretSign te, double p_180541_2_, double p_180541_4_, double p_180541_6_, float p_180541_8_, int p_180541_9_)
	{
		Block block = te.getBlockType();
		GlStateManager.pushMatrix();
		float f1 = 0.6666667F;
		float f3;

		if (block == SCContent.secretSignStanding)
		{
			GlStateManager.translate((float)p_180541_2_ + 0.5F, (float)p_180541_4_ + 0.75F * f1, (float)p_180541_6_ + 0.5F);
			float f2 = te.getBlockMetadata() * 360 / 16.0F;
			GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
			model.signStick.showModel = true;
		}
		else
		{
			int k = te.getBlockMetadata();
			f3 = 0.0F;

			if (k == 2)
			{
				f3 = 180.0F;
			}

			if (k == 4)
			{
				f3 = 90.0F;
			}

			if (k == 5)
			{
				f3 = -90.0F;
			}

			GlStateManager.translate((float)p_180541_2_ + 0.5F, (float)p_180541_4_ + 0.75F * f1, (float)p_180541_6_ + 0.5F);
			GlStateManager.rotate(-f3, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			model.signStick.showModel = false;
		}

		if (p_180541_9_ >= 0)
		{
			bindTexture(DESTROY_STAGES[p_180541_9_]);
			GlStateManager.matrixMode(5890);
			GlStateManager.pushMatrix();
			GlStateManager.scale(4.0F, 2.0F, 1.0F);
			GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
			GlStateManager.matrixMode(5888);
		}
		else
		{
			bindTexture(SIGN_TEXTURE);
		}

		GlStateManager.enableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.scale(f1, -f1, -f1);
		model.renderSign();
		GlStateManager.popMatrix();

		if(te.getOwner().isOwner(Minecraft.getMinecraft().thePlayer))
		{
			FontRenderer fontrenderer = getFontRenderer();
			f3 = 0.015625F * f1;
			GlStateManager.translate(0.0F, 0.5F * f1, 0.07F * f1);
			GlStateManager.scale(f3, -f3, f3);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
			GlStateManager.depthMask(false);
			byte b0 = 0;

			if (p_180541_9_ < 0)
			{
				for (int j = 0; j < te.signText.length; ++j)
				{
					if (te.signText[j] != null)
					{
						IChatComponent ichatcomponent = te.signText[j];
						List list = GuiUtilRenderComponents.func_178908_a(ichatcomponent, 90, fontrenderer, false, true);
						String s = list != null && list.size() > 0 ? ((IChatComponent)list.get(0)).getFormattedText() : "";

						if (j == te.lineBeingEdited)
						{
							s = "> " + s + " <";
							fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, b0);
						}
						else
						{
							fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, b0);
						}
					}
				}
			}

			GlStateManager.depthMask(true);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.popMatrix();

		if (p_180541_9_ >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		doRender((TileEntitySecretSign)te, x, y, z, partialTicks, destroyStage);
	}
}