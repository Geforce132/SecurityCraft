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
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer.SignModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.Vector3f;
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
	public void render(SecretSignTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		BlockState state = te.getBlockState();
		RenderMaterial material = SignTileEntityRenderer.getMaterial(state.getBlock());
		FontRenderer font = renderDispatcher.getFontRenderer();
		IVertexBuilder builder;

		matrix.push();

		if(state.getBlock() instanceof SecretStandingSignBlock)
		{
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.rotate(Vector3f.YP.rotationDegrees(-(state.get(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.signStick.showModel = true;
		}
		else
		{
			matrix.translate(0.5D, 0.5D, 0.5D);
			matrix.rotate(Vector3f.YP.rotationDegrees(-state.get(SecretWallSignBlock.FACING).getHorizontalAngle()));
			matrix.translate(0.0D, -0.3125D, -0.4375D);
			model.signStick.showModel = false;
		}

		matrix.push();
		matrix.scale(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.getBuffer(buffer, model::getRenderType);
		model.signBoard.render(matrix, builder, combinedLight, combinedOverlay);
		model.signStick.render(matrix, builder, combinedLight, combinedOverlay);
		matrix.pop();
		matrix.translate(0.0D, 0.33333334F, 0.046666667F);
		matrix.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.isPlayerAllowedToSeeText(Minecraft.getInstance().player))
		{
			int textColor = te.getTextColor().getTextColor();
			int r = (int)(NativeImage.getRed(textColor) * 0.4D);
			int g = (int)(NativeImage.getGreen(textColor) * 0.4D);
			int b = (int)(NativeImage.getBlue(textColor) * 0.4D);
			int argb = NativeImage.getCombined(0, b, g, r);

			for(int line = 0; line < 4; ++line)
			{
				IReorderingProcessor rp = te.func_242686_a(line, (p_243502_1_) -> {
					List<IReorderingProcessor> list = font.trimStringToWidth(p_243502_1_, 90);
					return list.isEmpty() ? IReorderingProcessor.field_242232_a : list.get(0);
				});

				if(rp != null)
					font.func_238416_a_(rp, -font.func_243245_a(rp) / 2, line * 10 - 20, argb, false, matrix.getLast().getMatrix(), buffer, false, 0, combinedLight);
			}
		}

		matrix.pop();
	}
}