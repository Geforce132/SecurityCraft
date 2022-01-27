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
public class SecretSignTileEntityRenderer extends TileEntityRenderer<SecretSignTileEntity> {
	private final SignModel model = new SignModel();

	public SecretSignTileEntityRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(SecretSignTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		BlockState state = te.getBlockState();
		Material material = SignTileEntityRenderer.getMaterial(state.getBlock());
		FontRenderer font = renderer.getFont();
		IVertexBuilder builder;

		stack.pushPose();

		if (state.getBlock() instanceof SecretStandingSignBlock) {
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(-(state.getValue(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.stick.visible = true;
		}
		else {
			stack.translate(0.5D, 0.5D, 0.5D);
			stack.mulPose(Vector3f.YP.rotationDegrees(-state.getValue(SecretWallSignBlock.FACING).toYRot()));
			stack.translate(0.0D, -0.3125D, -0.4375D);
			model.stick.visible = false;
		}

		stack.pushPose();
		stack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.buffer(buffer, model::renderType);
		model.sign.render(stack, builder, p_225616_5_, p_225616_6_);
		model.stick.render(stack, builder, p_225616_5_, p_225616_6_);
		stack.popPose();
		stack.translate(0.0D, 0.33333334F, 0.046666667F);
		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);

		if (te.isPlayerAllowedToSeeText(Minecraft.getInstance().player)) {
			int textColor = te.getColor().getTextColor();
			int j = (int) (NativeImage.getR(textColor) * 0.4D);
			int k = (int) (NativeImage.getG(textColor) * 0.4D);
			int l = (int) (NativeImage.getB(textColor) * 0.4D);
			int i1 = NativeImage.combine(0, l, k, j);

			for (int line = 0; line < 4; ++line) {
				String text = te.getRenderMessage(line, textComponent -> {
					List<ITextComponent> list = RenderComponentsUtil.wrapComponents(textComponent, 90, font, false, true);

					return list.isEmpty() ? "" : list.get(0).getColoredString();
				});

				if (text != null)
					font.drawInBatch(text, -font.width(text) / 2, line * 10 - te.messages.length * 5, i1, false, stack.last().pose(), buffer, false, 0, p_225616_5_);
			}
		}

		stack.popPose();
	}
}