package net.geforcemods.securitycraft.renderers;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blocks.SecretStandingSignBlock;
import net.geforcemods.securitycraft.blocks.SecretWallSignBlock;
import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer.SignModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecretSignTileEntityRenderer extends TileEntityRenderer<SecretSignTileEntity>
{
	private final SignModel model = new SignModel();

	public SecretSignTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(SecretSignTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		BlockState state = te.getBlockState();
		Material material = SignTileEntityRenderer.getMaterial(state.getBlock());
		FontRenderer font = renderDispatcher.getFontRenderer();
		IVertexBuilder builder;

		stack.push();

		if(state.getBlock() instanceof SecretStandingSignBlock)
		{
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.rotate(Vector3f.YP.rotationDegrees(-(state.get(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.signStick.showModel = true;
		}
		else
		{
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.rotate(Vector3f.YP.rotationDegrees(-state.get(SecretWallSignBlock.FACING).getHorizontalAngle()));
			stack.translate(0.0D, -0.3125D, -0.4375D);
			model.signStick.showModel = false;
		}

		stack.push();
		stack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.getBuffer(buffer, model::getRenderType);
		model.signBoard.render(stack, builder, p_225616_5_, p_225616_6_);
		model.signStick.render(stack, builder, p_225616_5_, p_225616_6_);
		stack.pop();
		stack.translate(0.0D, 0.33333334F, 0.046666667F);
		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.getOwner().isOwner(Minecraft.getInstance().player))
		{
			int textColor = te.getTextColor().getTextColor();
			int j = (int)(NativeImage.getRed(textColor) * 0.4D);
			int k = (int)(NativeImage.getGreen(textColor) * 0.4D);
			int l = (int)(NativeImage.getBlue(textColor) * 0.4D);
			int i1 = NativeImage.getCombined(0, l, k, j);

			for(int line = 0; line < 4; ++line)
			{
				String text = te.getRenderText(line, (p_212491_1_) -> {
					List<ITextComponent> list = RenderComponentsUtil.splitText(p_212491_1_, 90, font, false, true);

					return list.isEmpty() ? "" : list.get(0).getFormattedText();
				});

				if(text != null)
				{
					font.renderString(text, -font.getStringWidth(text) / 2, line * 10 - te.signText.length * 5, i1, false, stack.getLast().getMatrix(), buffer, false, 0, p_225616_5_);
				}
			}
		}

		stack.pop();
	}
}