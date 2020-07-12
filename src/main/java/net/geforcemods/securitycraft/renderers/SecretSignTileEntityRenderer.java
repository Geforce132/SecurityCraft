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
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.Style;
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
	public void render(SecretSignTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay)
	{
		BlockState state = te.getBlockState();
		RenderMaterial material = SignTileEntityRenderer.getMaterial(state.getBlock());
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
		model.signBoard.render(stack, builder, combinedLight, combinedOverlay);
		model.signStick.render(stack, builder, combinedLight, combinedOverlay);
		stack.pop();
		stack.translate(0.0D, 0.33333334F, 0.046666667F);
		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.getOwner().isOwner(Minecraft.getInstance().player))
		{
			int textColor = te.getTextColor().getTextColor();
			int r = (int)(NativeImage.getRed(textColor) * 0.4D);
			int g = (int)(NativeImage.getGreen(textColor) * 0.4D);
			int b = (int)(NativeImage.getBlue(textColor) * 0.4D);
			int argb = NativeImage.getCombined(0, b, g, r);

			for(int line = 0; line < 4; ++line)
			{
				ITextProperties text = te.func_235677_a_(line, textComponent -> {
					List<ITextProperties> list = font.func_238420_b_().func_238362_b_(textComponent, 90, Style.EMPTY);
					return list.isEmpty() ? ITextProperties.field_240651_c_ : list.get(0);
				});

				if(text != null)
					font.func_238416_a_(text, -font.func_238414_a_(text) / 2, line * 10 - 20, argb, false, stack.getLast().getMatrix(), buffer, false, 0, combinedLight);
			}
		}

		stack.pop();
	}
}