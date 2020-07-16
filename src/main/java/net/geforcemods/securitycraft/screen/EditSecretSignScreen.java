package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.StandingSignBlock;
import net.minecraft.client.gui.DialogTexts;
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
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Matrix4f;
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
	private final String[] signText = Util.make(new String[4], i -> Arrays.fill(i, ""));

	public EditSecretSignScreen(SecretSignTileEntity te)
	{
		super(new TranslationTextComponent("sign.edit"));
		this.te = te;
	}

	@Override
	protected void init()
	{
		minecraft.keyboardListener.enableRepeatEvents(true);
		addButton(new Button(width / 2 - 100, height / 4 + 120, 200, 20, DialogTexts.field_240632_c_, button -> close()));
		te.setEditable(false);
		textInputUtil = new TextInputUtil(() -> signText[editLine], s -> {
			signText[editLine] = s;
			te.setText(editLine, new StringTextComponent(s));
		}, TextInputUtil.func_238570_a_(minecraft), TextInputUtil.func_238582_c_(minecraft), t -> minecraft.fontRenderer.getStringWidth(t) <= 90);
	}

	@Override
	public void removed()
	{
		minecraft.keyboardListener.enableRepeatEvents(false);

		if(minecraft.getConnection() != null)
			minecraft.getConnection().sendPacket(new CUpdateSignPacket(te.getPos(), signText[0], signText[1], signText[2], signText[3]));

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
		minecraft.displayGuiScreen((Screen)null);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode)
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
	public boolean keyPressed(int keyCode, int p_keyPressed_2_, int p_keyPressed_3_)
	{
		if(keyCode == 265)
		{
			editLine = editLine - 1 & 3;
			textInputUtil.func_238588_f_();
			return true;
		}
		else if(keyCode != 264 && keyCode != 257 && keyCode != 335)
			return textInputUtil.func_216897_a(keyCode) ? true : super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
		else
		{
			editLine = editLine + 1 & 3;
			textInputUtil.func_238588_f_();
			return true;
		}
	}

	@Override
	public void render(MatrixStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		BlockState state = this.te.getBlockState();
		boolean isStanding = state.getBlock() instanceof StandingSignBlock;
		boolean update = updateCounter / 6 % 2 == 0;
		RenderMaterial material = SignTileEntityRenderer.getMaterial(state.getBlock());
		int textColor = te.getTextColor().getTextColor();
		int k = textInputUtil.func_216896_c();
		int l = textInputUtil.func_216898_d();
		int j1 = editLine * 10 - signText.length * 5;
		IRenderTypeBuffer.Impl buffer;
		IVertexBuilder builder;
		Matrix4f positionMatrix;

		RenderHelper.setupGuiFlatDiffuseLighting();
		renderBackground(matrix);
		drawCenteredString(matrix, font, title, width / 2, 40, 16777215);
		matrix.push();
		matrix.translate(width / 2, 0.0D, 50.0D);
		matrix.scale(93.75F, -93.75F, 93.75F);
		matrix.translate(0.0D, -1.3125D, 0.0D);

		if(!isStanding)
			matrix.translate(0.0D, -0.3125D, 0.0D);

		matrix.push();
		matrix.scale(0.6666667F, -0.6666667F, -0.6666667F);
		buffer = minecraft.getRenderTypeBuffers().getBufferSource();
		builder = material.getBuffer(buffer, signModel::getRenderType);
		signModel.signBoard.render(matrix, builder, 15728880, OverlayTexture.NO_OVERLAY);

		if(isStanding)
			signModel.signStick.render(matrix, builder, 15728880, OverlayTexture.NO_OVERLAY);

		matrix.pop();
		matrix.translate(0.0D, 0.33333334F, 0.046666667F);
		matrix.scale(0.010416667F, -0.010416667F, 0.010416667F);

		positionMatrix = matrix.getLast().getMatrix();

		for(int k1 = 0; k1 < signText.length; ++k1)
		{
			String s = signText[k1];

			if(s != null)
			{
				if (font.getBidiFlag())
					s = font.bidiReorder(s);

				float f3 = -minecraft.fontRenderer.getStringWidth(s) / 2;

				minecraft.fontRenderer.renderString(s, f3, k1 * 10 - signText.length * 5, textColor, false, positionMatrix, buffer, false, 0, 15728880);

				if(k1 == editLine && k >= 0 && update)
				{
					int l1 = minecraft.fontRenderer.getStringWidth(s.substring(0, Math.max(Math.min(k, s.length()), 0)));
					int i2 = l1 - minecraft.fontRenderer.getStringWidth(s) / 2;

					if(k >= s.length())
						minecraft.fontRenderer.renderString("_", i2, j1, textColor, false, positionMatrix, buffer, false, 0, 15728880);
				}
			}
		}

		buffer.finish();

		for(int k3 = 0; k3 < signText.length; ++k3)
		{
			String s1 = signText[k3];

			if(s1 != null && k3 == editLine && k >= 0)
			{
				int l3 = minecraft.fontRenderer.getStringWidth(s1.substring(0, Math.max(Math.min(k, s1.length()), 0)));
				int i4 = l3 - minecraft.fontRenderer.getStringWidth(s1) / 2;

				if(update && k < s1.length())
					fill(matrix, i4, j1 - 1, i4 + 1, j1 + 9, -16777216 | textColor);

				if(l != k)
				{
					int j4 = Math.min(k, l);
					int j2 = Math.max(k, l);
					int k2 = minecraft.fontRenderer.getStringWidth(s1.substring(0, j4)) - minecraft.fontRenderer.getStringWidth(s1) / 2;
					int l2 = minecraft.fontRenderer.getStringWidth(s1.substring(0, j2)) - minecraft.fontRenderer.getStringWidth(s1) / 2;
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

		matrix.pop();
		RenderHelper.setupGui3DDiffuseLighting();
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
}