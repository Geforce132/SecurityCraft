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

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		doRender((TileEntitySecretSign)te, x, y, z, partialTicks, destroyStage);
	}

	public void doRender(TileEntitySecretSign te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		Block block = te.getBlockType();
		GlStateManager.pushMatrix();
		float magicNumber1 = 0.6666667F;
		float rotation;

		if (block == SCContent.secretSignStanding)
		{
			GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * magicNumber1, (float)z + 0.5F);
			float localRotation = te.getBlockMetadata() * 360 / 16.0F;
			GlStateManager.rotate(-localRotation, 0.0F, 1.0F, 0.0F);
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

			GlStateManager.translate((float)x + 0.5F, (float)y + 0.75F * magicNumber1, (float)z + 0.5F);
			GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(0.0F, -0.3125F, -0.4375F);
			model.signStick.showModel = false;
		}

		if (destroyStage >= 0)
		{
			bindTexture(DESTROY_STAGES[destroyStage]);
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
		GlStateManager.scale(magicNumber1, -magicNumber1, -magicNumber1);
		model.renderSign();
		GlStateManager.popMatrix();

		if(te.getOwner().isOwner(Minecraft.getMinecraft().thePlayer))
		{
			FontRenderer fontRenderer = getFontRenderer();
			rotation = 0.015625F * magicNumber1;
			GlStateManager.translate(0.0F, 0.5F * magicNumber1, 0.07F * magicNumber1);
			GlStateManager.scale(rotation, -rotation, rotation);
			GL11.glNormal3f(0.0F, 0.0F, -1.0F * rotation);
			GlStateManager.depthMask(false);
			byte magicNumber2 = 0;

			if (destroyStage < 0)
			{
				for (int j = 0; j < te.signText.length; ++j)
				{
					if (te.signText[j] != null)
					{
						IChatComponent lineText = te.signText[j];
						List list = GuiUtilRenderComponents.func_178908_a(lineText, 90, fontRenderer, false, true);
						String displayedText = list != null && list.size() > 0 ? ((IChatComponent)list.get(0)).getFormattedText() : "";

						if (j == te.lineBeingEdited)
						{
							displayedText = "> " + displayedText + " <";
							fontRenderer.drawString(displayedText, -fontRenderer.getStringWidth(displayedText) / 2, j * 10 - te.signText.length * 5, magicNumber2);
						}
						else
						{
							fontRenderer.drawString(displayedText, -fontRenderer.getStringWidth(displayedText) / 2, j * 10 - te.signText.length * 5, magicNumber2);
						}
					}
				}
			}

			GlStateManager.depthMask(true);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}

		GlStateManager.popMatrix();

		if (destroyStage >= 0)
		{
			GlStateManager.matrixMode(5890);
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5888);
		}
	}
}