package net.geforcemods.securitycraft.renderers;

import java.util.List;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.block.WallSignBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer.SignModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecretSignTileEntityRenderer extends TileEntityRenderer<SecretSignTileEntity>
{
	private static final ResourceLocation OAK_TEXTURE = new ResourceLocation("textures/entity/signs/oak.png");
	private static final ResourceLocation SPRUCE_TEXTURE = new ResourceLocation("textures/entity/signs/spruce.png");
	private static final ResourceLocation BIRCH_TEXTURE = new ResourceLocation("textures/entity/signs/birch.png");
	private static final ResourceLocation ACACIA_TEXTURE = new ResourceLocation("textures/entity/signs/acacia.png");
	private static final ResourceLocation JUNGLE_TEXTURE = new ResourceLocation("textures/entity/signs/jungle.png");
	private static final ResourceLocation DARK_OAK_TEXTURE = new ResourceLocation("textures/entity/signs/dark_oak.png");
	private final SignModel model = new SignModel();

	@Override
	public void render(SecretSignTileEntity te, double x, double y, double z, float partialTicks, int destroyStage)
	{
		BlockState state = te.getBlockState();
		FontRenderer font = getFontRenderer();

		RenderSystem.pushMatrix();

		if(state.getBlock() instanceof StandingSignBlock)
		{
			RenderSystem.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			RenderSystem.rotatef(-(state.get(StandingSignBlock.ROTATION) * 360 / 16.0F), 0.0F, 1.0F, 0.0F);
			model.getSignStick().showModel = true;
		}
		else
		{
			RenderSystem.translatef((float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F);
			RenderSystem.rotatef(-state.get(WallSignBlock.FACING).getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
			RenderSystem.translatef(0.0F, -0.3125F, -0.4375F);
			model.getSignStick().showModel = false;
		}

		if(destroyStage >= 0)
		{
			bindTexture(DESTROY_STAGES[destroyStage]);
			RenderSystem.matrixMode(5890);
			RenderSystem.pushMatrix();
			RenderSystem.scalef(4.0F, 2.0F, 1.0F);
			RenderSystem.translatef(0.0625F, 0.0625F, 0.0625F);
			RenderSystem.matrixMode(5888);
		}
		else
			bindTexture(getTextureForSign(state.getBlock()));

		RenderSystem.enableRescaleNormal();
		RenderSystem.pushMatrix();
		RenderSystem.scalef(0.6666667F, -0.6666667F, -0.6666667F);
		model.renderSign();
		RenderSystem.popMatrix();
		RenderSystem.translatef(0.0F, 0.33333334F, 0.046666667F);
		RenderSystem.scalef(0.010416667F, -0.010416667F, 0.010416667F);
		RenderSystem.normal3f(0.0F, 0.0F, -0.010416667F);
		RenderSystem.depthMask(false);

		int i = te.getTextColor().getTextColor();

		if(te.getOwner().isOwner(Minecraft.getInstance().player) && destroyStage < 0)
		{
			for(int j = 0; j < 4; ++j)
			{
				String s = te.getRenderText(j, textComponent -> {
					List<ITextComponent> list = RenderComponentsUtil.splitText(textComponent, 90, font, false, true);
					return list.isEmpty() ? "" : list.get(0).getFormattedText();
				});

				if(s != null)
				{
					font.drawString(s, -font.getStringWidth(s) / 2, j * 10 - te.signText.length * 5, i);

					if(j == te.getLineBeingEdited() && te.func_214065_t() >= 0)
					{
						int k = font.getStringWidth(s.substring(0, Math.max(Math.min(te.func_214065_t(), s.length()), 0)));
						int l = font.getBidiFlag() ? -1 : 1;
						int i1 = (k - font.getStringWidth(s) / 2) * l;
						int j1 = j * 10 - te.signText.length * 5;

						if(te.func_214069_r())
						{
							if(te.func_214065_t() < s.length())
								AbstractGui.fill(i1, j1 - 1, i1 + 1, j1 + 9, -16777216 | i);
							else
								font.drawString("_", i1, j1, i);
						}

						if(te.func_214067_u() != te.func_214065_t())
						{
							int k1 = Math.min(te.func_214065_t(), te.func_214067_u());
							int l1 = Math.max(te.func_214065_t(), te.func_214067_u());
							int i2 = (font.getStringWidth(s.substring(0, k1)) - font.getStringWidth(s) / 2) * l;
							int j2 = (font.getStringWidth(s.substring(0, l1)) - font.getStringWidth(s) / 2) * l;

							this.func_217657_a(Math.min(i2, j2), j1, Math.max(i2, j2), j1 + 9);
						}
					}
				}
			}
		}

		RenderSystem.depthMask(true);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.popMatrix();

		if(destroyStage >= 0)
		{
			RenderSystem.matrixMode(5890);
			RenderSystem.popMatrix();
			RenderSystem.matrixMode(5888);
		}
	}

	private ResourceLocation getTextureForSign(Block sign)
	{
		if(sign != SCContent.secretOakSign && sign != SCContent.secretOakWallSign)
		{
			if(sign != SCContent.secretSpruceSign && sign != SCContent.secretSpruceWallSign)
			{
				if(sign != SCContent.secretBirchSign && sign != SCContent.secretBirchWallSign)
				{
					if(sign != SCContent.secretAcaciaSign && sign != SCContent.secretAcaciaWallSign)
					{
						if(sign != SCContent.secretJungleSign && sign != SCContent.secretJungleWallSign)
							return sign != SCContent.secretDarkOakSign && sign != SCContent.secretDarkOakWallSign ? OAK_TEXTURE : DARK_OAK_TEXTURE;
						else return JUNGLE_TEXTURE;
					}
					else return ACACIA_TEXTURE;
				}
				else return BIRCH_TEXTURE;
			}
			else return SPRUCE_TEXTURE;
		}
		else return OAK_TEXTURE;
	}

	private void func_217657_a(int p_217657_1_, int p_217657_2_, int p_217657_3_, int p_217657_4_)
	{
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();

		RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
		RenderSystem.disableTexture();
		RenderSystem.enableColorLogicOp();
		RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
		bufferbuilder.pos(p_217657_1_, p_217657_4_, 0.0D).endVertex();
		bufferbuilder.pos(p_217657_3_, p_217657_4_, 0.0D).endVertex();
		bufferbuilder.pos(p_217657_3_, p_217657_2_, 0.0D).endVertex();
		bufferbuilder.pos(p_217657_1_, p_217657_2_, 0.0D).endVertex();
		tessellator.draw();
		RenderSystem.disableColorLogicOp();
		RenderSystem.enableTexture();
	}
}