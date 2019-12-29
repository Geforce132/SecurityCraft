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
	public void func_225616_a_(SecretSignTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		BlockState state = te.getBlockState();
		Material material = SignTileEntityRenderer.func_228877_a_(state.getBlock());
		FontRenderer font = field_228858_b_.getFontRenderer();
		IVertexBuilder builder;

		stack.func_227860_a_();

		if(state.getBlock() instanceof SecretStandingSignBlock)
		{
			stack.func_227861_a_(0.5D, 0.5D, 0.5D);
			stack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-(state.get(SecretStandingSignBlock.ROTATION) * 360 / 16.0F)));
			model.field_78165_b.showModel = true;
		}
		else
		{
			stack.func_227861_a_(0.5D, 0.5D, 0.5D);
			stack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-state.get(SecretWallSignBlock.FACING).getHorizontalAngle()));
			stack.func_227861_a_(0.0D, -0.3125D, -0.4375D);
			model.field_78165_b.showModel = false;
		}

		stack.func_227860_a_();
		stack.func_227862_a_(0.6666667F, -0.6666667F, -0.6666667F);
		builder = material.func_229311_a_(buffer, model::func_228282_a_);
		model.field_78166_a.func_228308_a_(stack, builder, p_225616_5_, p_225616_6_);
		model.field_78165_b.func_228308_a_(stack, builder, p_225616_5_, p_225616_6_);
		stack.func_227865_b_();
		stack.func_227861_a_(0.0D, 0.33333334F, 0.046666667F);
		stack.func_227862_a_(0.010416667F, -0.010416667F, 0.010416667F);

		if(te.getOwner().isOwner(Minecraft.getInstance().player))
		{
			int textColor = te.getTextColor().getTextColor();
			int j = (int)(NativeImage.func_227791_b_(textColor) * 0.4D);
			int k = (int)(NativeImage.func_227793_c_(textColor) * 0.4D);
			int l = (int)(NativeImage.func_227795_d_(textColor) * 0.4D);
			int i1 = NativeImage.func_227787_a_(0, l, k, j);

			for(int line = 0; line < 4; ++line)
			{
				String text = te.getRenderText(line, (p_212491_1_) -> {
					List<ITextComponent> list = RenderComponentsUtil.splitText(p_212491_1_, 90, font, false, true);

					return list.isEmpty() ? "" : list.get(0).getFormattedText();
				});

				if(text != null)
				{
					font.func_228079_a_(text, -font.getStringWidth(text) / 2, line * 10 - te.signText.length * 5, i1, false, stack.func_227866_c_().func_227870_a_(), buffer, false, 0, p_225616_5_);
				}
			}
		}

		stack.func_227865_b_();
	}
}