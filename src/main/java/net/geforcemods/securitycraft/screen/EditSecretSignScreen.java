package net.geforcemods.securitycraft.screen;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.fonts.TextInputUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.SignTileEntityRenderer.SignModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.client.CUpdateSignPacket;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditSecretSignScreen extends Screen
{
	private final SignModel signModel = new SignModel();
	private final SecretSignTileEntity te;
	private int updateCounter;
	private int editLine;
	private TextInputUtil textInputUtil;

	public EditSecretSignScreen(SecretSignTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	protected void func_231160_c_()
	{
		field_230706_i_.keyboardListener.enableRepeatEvents(true);
		func_230480_a_(new Button(field_230708_k_ / 2 - 100, field_230709_l_ / 4 + 120, 200, 20, ClientUtils.localize("gui.done"), button -> close()));
		te.setEditable(false);
		textInputUtil = new TextInputUtil(field_230706_i_, () -> te.getText(editLine).getString(), s -> te.setText(editLine, new StringTextComponent(s)), 90);
	}

	@Override
	public void removed()
	{
		field_230706_i_.keyboardListener.enableRepeatEvents(false);

		if(field_230706_i_.getConnection() != null)
			field_230706_i_.getConnection().sendPacket(new CUpdateSignPacket(te.getPos(), te.getText(0), te.getText(1), te.getText(2), te.getText(3)));

		te.setEditable(true);
	}

	@Override
	public void tick()
	{
		++updateCounter;

		if(!te.getType().isValidBlock(te.getBlockState().getBlock()))
			close();
	}

	private void close()
	{
		te.markDirty();
		field_230706_i_.displayGuiScreen((Screen)null);
	}

	@Override
	public boolean func_231042_a_(char typedChar, int keyCode)
	{
		textInputUtil.func_216894_a(typedChar);
		return true;
	}

	@Override
	public void onClose()
	{
		close();
	}

	@Override
	public boolean func_231046_a_(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
	{
		if(keyCode == 265)
		{
			editLine = editLine - 1 & 3;
			textInputUtil.func_216899_b();
			return true;
		}
		else if(keyCode != 264 && keyCode != 257 && keyCode != 335)
			return textInputUtil.func_216897_a(keyCode) ? true : super.func_231046_a_(keyCode, p_keyPressed_2_, p_keyPressed_3_);
		else
		{
			editLine = editLine + 1 & 3;
			textInputUtil.func_216899_b();
			return true;
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks)
	{
		MatrixStack stack = new MatrixStack();
		BlockState state = this.te.getBlockState();
		boolean isStanding = state.getBlock() instanceof StandingSignBlock;
		boolean update = updateCounter / 6 % 2 == 0;
		RenderMaterial material = SignTileEntityRenderer.getMaterial(state.getBlock());
		int textColor = te.getTextColor().getTextColor();
		String[] text = new String[4];
		int k = textInputUtil.func_216896_c();
		int l = textInputUtil.func_216898_d();
		int i1 = field_230706_i_.fontRenderer.getBidiFlag() ? -1 : 1;
		int j1 = editLine * 10 - te.signText.length * 5;
		IRenderTypeBuffer.Impl buffer;
		IVertexBuilder builder;
		Matrix4f positionMatrix;

		RenderHelper.setupGuiFlatDiffuseLighting();
		func_230446_a_();
		drawCenteredString(field_230712_o_, title.getFormattedText(), field_230708_k_ / 2, 40, 16777215);
		stack.push();
		stack.translate(field_230708_k_ / 2, 0.0D, 50.0D);
		stack.scale(93.75F, -93.75F, 93.75F);
		stack.translate(0.0D, -1.3125D, 0.0D);

		if(!isStanding)
			stack.translate(0.0D, -0.3125D, 0.0D);

		stack.push();
		stack.scale(0.6666667F, -0.6666667F, -0.6666667F);
		buffer = field_230706_i_.getRenderTypeBuffers().getBufferSource();
		builder = material.getBuffer(buffer, signModel::getRenderType);
		signModel.signBoard.render(stack, builder, 15728880, OverlayTexture.NO_OVERLAY);

		if(isStanding)
			signModel.signStick.render(stack, builder, 15728880, OverlayTexture.NO_OVERLAY);

		stack.pop();
		stack.translate(0.0D, 0.33333334F, 0.046666667F);
		stack.scale(0.010416667F, -0.010416667F, 0.010416667F);

		for(int j = 0; j < text.length; ++j)
		{
			text[j] = te.getRenderText(j, textComponent -> {
				List<ITextComponent> list = RenderComponentsUtil.splitText(textComponent, 90, field_230706_i_.fontRenderer, false, true);

				return list.isEmpty() ? "" : list.get(0).getFormattedText();
			});
		}

		positionMatrix = stack.getLast().getMatrix();

		for(int k1 = 0; k1 < text.length; ++k1)
		{
			String s = text[k1];

			if(s != null)
			{
				float f3 = -this.field_230706_i_.fontRenderer.getStringWidth(s) / 2;

				field_230706_i_.fontRenderer.renderString(s, f3, k1 * 10 - te.signText.length * 5, textColor, false, positionMatrix, buffer, false, 0, 15728880);

				if(k1 == this.editLine && k >= 0 && update)
				{
					int l1 = field_230706_i_.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(k, s.length()), 0)));
					int i2 = (l1 - field_230706_i_.fontRenderer.getStringWidth(s) / 2) * i1;

					if(k >= s.length())
						field_230706_i_.fontRenderer.renderString("_", i2, j1, textColor, false, positionMatrix, buffer, false, 0, 15728880);
				}
			}
		}

		buffer.finish();

		for(int k3 = 0; k3 < text.length; ++k3)
		{
			String s1 = text[k3];

			if(s1 != null && k3 == editLine && k >= 0)
			{
				int l3 = field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(k, s1.length()), 0)));
				int i4 = (l3 - field_230706_i_.fontRenderer.getStringWidth(s1) / 2) * i1;

				if(update && k < s1.length())
					fill(positionMatrix, i4, j1 - 1, i4 + 1, j1 + 9, -16777216 | textColor);

				if(l != k)
				{
					int j4 = Math.min(k, l);
					int j2 = Math.max(k, l);
					int k2 = (this.field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, j4)) - this.field_230706_i_.fontRenderer.getStringWidth(s1) / 2) * i1;
					int l2 = (this.field_230706_i_.fontRenderer.getStringWidth(s1.substring(0, j2)) - this.field_230706_i_.fontRenderer.getStringWidth(s1) / 2) * i1;
					int i3 = Math.min(k2, l2);
					int j3 = Math.max(k2, l2);
					BufferBuilder buf = Tessellator.getInstance().getBuffer();

					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					buf.begin(7, DefaultVertexFormats.POSITION_COLOR);
					buf.pos(positionMatrix, i3, j1 + 9, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.pos(positionMatrix, j3, j1 + 9, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.pos(positionMatrix, j3, j1, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.pos(positionMatrix, i3, j1, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.finishDrawing();
					WorldVertexBufferUploader.draw(buf);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		stack.pop();
		RenderHelper.setupGui3DDiffuseLighting();
		super.render(mouseX, mouseY, partialTicks);
	}
}