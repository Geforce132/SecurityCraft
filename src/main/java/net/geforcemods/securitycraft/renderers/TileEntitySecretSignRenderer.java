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

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks)
	{
		renderTileEntityAt((TileEntitySecretSign)te, x, y, z, partialTicks);
	}

	public void renderTileEntityAt(TileEntitySecretSign te, double x, double y, double z, float partialTicks)
	{
		Block block = te.getBlockType();
		GL11.glPushMatrix();
		float magicNumber1 = 0.6666667F;
		float rotation;

		if (block == SCContent.secretSignStanding)
		{
			GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * magicNumber1, (float)z + 0.5F);
			float localRotation = te.getBlockMetadata() * 360 / 16.0F;
			GL11.glRotatef(-localRotation, 0.0F, 1.0F, 0.0F);
			model.signStick.showModel = true;
		}
		else
		{
			int meta = te.getBlockMetadata();
			rotation = 0.0F;

			if (meta == 2)
			{
				rotation = 180.0F;
			}

			if (meta == 4)
			{
				rotation = 90.0F;
			}

			if (meta == 5)
			{
				rotation = -90.0F;
			}

			GL11.glTranslatef((float)x + 0.5F, (float)y + 0.75F * magicNumber1, (float)z + 0.5F);
			GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(0.0F, -0.3125F, -0.4375F);
			model.signStick.showModel = false;
		}

		bindTexture(TEXTURE);
		GL11.glPushMatrix();
		GL11.glScalef(magicNumber1, -magicNumber1, -magicNumber1);
		model.renderSign();
		GL11.glPopMatrix();

		if(te.getOwner().isOwner(Minecraft.getMinecraft().thePlayer))
		{
			FontRenderer fontRenderer = func_147498_b();
			rotation = 0.016666668F * magicNumber1;
			GL11.glTranslatef(0.0F, 0.5F * magicNumber1, 0.07F * magicNumber1);
			GL11.glScalef(rotation, -rotation, rotation);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * rotation);
			GL11.glDepthMask(false);
			byte magicNumber2 = 0;

			for (int i = 0; i < te.signText.length; ++i)
			{
				String lineText = te.signText[i];

				if (i == te.lineBeingEdited)
				{
					lineText = "> " + lineText + " <";
					fontRenderer.drawString(lineText, -fontRenderer.getStringWidth(lineText) / 2, i * 10 - te.signText.length * 5, magicNumber2);
				}
				else
				{
					fontRenderer.drawString(lineText, -fontRenderer.getStringWidth(lineText) / 2, i * 10 - te.signText.length * 5, magicNumber2);
				}
			}

			GL11.glDepthMask(true);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GL11.glPopMatrix();
	}
}