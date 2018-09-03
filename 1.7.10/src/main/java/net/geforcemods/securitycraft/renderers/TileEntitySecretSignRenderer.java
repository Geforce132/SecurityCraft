package net.geforcemods.securitycraft.renderers;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelSign;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class TileEntitySecretSignRenderer extends TileEntitySpecialRenderer
{
	private static final ResourceLocation TEXTURE = new ResourceLocation("textures/entity/sign.png");
	/** The ModelSign instance for use in this renderer */
	private static final ModelSign model = new ModelSign();

	public void renderTileEntityAt(TileEntitySecretSign te, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	{
		Block block = te.getBlockType();
		GL11.glPushMatrix();
		float f1 = 0.6666667F;
		float f3;

		if (block == SCContent.secretSignStanding)
		{
			GL11.glTranslatef((float)p_147500_2_ + 0.5F, (float)p_147500_4_ + 0.75F * f1, (float)p_147500_6_ + 0.5F);
			float f2 = te.getBlockMetadata() * 360 / 16.0F;
			GL11.glRotatef(-f2, 0.0F, 1.0F, 0.0F);
			model.signStick.showModel = true;
		}
		else
		{
			int j = te.getBlockMetadata();
			f3 = 0.0F;

			if (j == 2)
			{
				f3 = 180.0F;
			}

			if (j == 4)
			{
				f3 = 90.0F;
			}

			if (j == 5)
			{
				f3 = -90.0F;
			}

			GL11.glTranslatef((float)p_147500_2_ + 0.5F, (float)p_147500_4_ + 0.75F * f1, (float)p_147500_6_ + 0.5F);
			GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
			model.signStick.showModel = false;
		}

		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glScalef(f1, -f1, -f1);
		model.renderSign();
		GL11.glPopMatrix();

		if(te.getOwner().isOwner(Minecraft.getMinecraft().thePlayer))
		{
			FontRenderer fontrenderer = func_147498_b();
			f3 = 0.016666668F * f1;
			GL11.glTranslatef(0.0F, 0.5F * f1, 0.07F * f1);
			GL11.glScalef(f3, -f3, f3);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
			GL11.glDepthMask(false);
			byte b0 = 0;

			for (int i = 0; i < te.signText.length; ++i)
			{
				String s = te.signText[i];

				if (i == te.lineBeingEdited)
				{
					s = "> " + s + " <";
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - te.signText.length * 5, b0);
				}
				else
				{
					fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, i * 10 - te.signText.length * 5, b0);
				}
			}

			GL11.glDepthMask(true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GL11.glPopMatrix();
	}

	public void renderTileEntityAt(TileEntity p_147500_1_, double p_147500_2_, double p_147500_4_, double p_147500_6_, float p_147500_8_)
	{
		renderTileEntityAt((TileEntitySecretSign)p_147500_1_, p_147500_2_, p_147500_4_, p_147500_6_, p_147500_8_);
	}
}