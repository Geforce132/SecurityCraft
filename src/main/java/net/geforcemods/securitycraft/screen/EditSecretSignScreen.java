package net.geforcemods.securitycraft.screen;

import java.util.Arrays;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;

import net.geforcemods.securitycraft.tileentity.SecretSignTileEntity;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer.SignModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditSecretSignScreen extends Screen
{
	private final SignModel signModel = new SignModel();
	private final SecretSignTileEntity te;
	private int updateCounter;
	private int editLine;
	private TextFieldHelper textInputUtil;
	private final String[] signText = Util.make(new String[4], i -> Arrays.fill(i, ""));

	public EditSecretSignScreen(SecretSignTileEntity te)
	{
		super(new TranslatableComponent("sign.edit"));
		this.te = te;
	}

	@Override
	protected void init()
	{
		minecraft.keyboardHandler.setSendRepeatsToGui(true);
		addRenderableWidget(new Button(width / 2 - 100, height / 4 + 120, 200, 20, CommonComponents.GUI_DONE, button -> close()));
		te.setEditable(false);
		textInputUtil = new TextFieldHelper(() -> signText[editLine], s -> {
			signText[editLine] = s;
			te.setMessage(editLine, new TextComponent(s));
		}, TextFieldHelper.createClipboardGetter(minecraft), TextFieldHelper.createClipboardSetter(minecraft), t -> minecraft.font.width(t) <= 90);
	}

	@Override
	public void removed()
	{
		minecraft.keyboardHandler.setSendRepeatsToGui(false);

		if(minecraft.getConnection() != null)
			minecraft.getConnection().send(new ServerboundSignUpdatePacket(te.getBlockPos(), signText[0], signText[1], signText[2], signText[3]));

		te.setEditable(true);
	}

	@Override
	public void tick()
	{
		++updateCounter;

		if(!te.getType().isValid(te.getBlockState().getBlock()))
			close();
	}

	private void close()
	{
		te.setChanged();
		minecraft.setScreen((Screen)null);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode)
	{
		textInputUtil.charTyped(typedChar);
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
			textInputUtil.setCursorToEnd();
			return true;
		}
		else if(keyCode != 264 && keyCode != 257 && keyCode != 335)
			return textInputUtil.keyPressed(keyCode) ? true : super.keyPressed(keyCode, p_keyPressed_2_, p_keyPressed_3_);
		else
		{
			editLine = editLine + 1 & 3;
			textInputUtil.setCursorToEnd();
			return true;
		}
	}

	@Override
	public void render(PoseStack matrix, int mouseX, int mouseY, float partialTicks)
	{
		BlockState state = this.te.getBlockState();
		boolean isStanding = state.getBlock() instanceof StandingSignBlock;
		boolean update = updateCounter / 6 % 2 == 0;
		Material material = SignRenderer.getMaterial(state.getBlock());
		int textColor = te.getColor().getTextColor();
		int k = textInputUtil.getCursorPos();
		int l = textInputUtil.getSelectionPos();
		int j1 = editLine * 10 - signText.length * 5;
		MultiBufferSource.BufferSource buffer;
		VertexConsumer builder;
		Matrix4f positionMatrix;

		Lighting.setupForFlatItems();
		renderBackground(matrix);
		drawCenteredString(matrix, font, title, width / 2, 40, 16777215);
		matrix.pushPose();
		matrix.translate(width / 2, 0.0D, 50.0D);
		matrix.scale(93.75F, -93.75F, 93.75F);
		matrix.translate(0.0D, -1.3125D, 0.0D);

		if(!isStanding)
			matrix.translate(0.0D, -0.3125D, 0.0D);

		matrix.pushPose();
		matrix.scale(0.6666667F, -0.6666667F, -0.6666667F);
		buffer = minecraft.renderBuffers().bufferSource();
		builder = material.buffer(buffer, signModel::renderType);
		signModel.sign.render(matrix, builder, 15728880, OverlayTexture.NO_OVERLAY);

		if(isStanding)
			signModel.stick.render(matrix, builder, 15728880, OverlayTexture.NO_OVERLAY);

		matrix.popPose();
		matrix.translate(0.0D, 0.33333334F, 0.046666667F);
		matrix.scale(0.010416667F, -0.010416667F, 0.010416667F);

		positionMatrix = matrix.last().pose();

		for(int k1 = 0; k1 < signText.length; ++k1)
		{
			String s = signText[k1];

			if(s != null)
			{
				if (font.isBidirectional())
					s = font.bidirectionalShaping(s);

				float f3 = -minecraft.font.width(s) / 2;

				minecraft.font.drawInBatch(s, f3, k1 * 10 - signText.length * 5, textColor, false, positionMatrix, buffer, false, 0, 15728880);

				if(k1 == editLine && k >= 0 && update)
				{
					int l1 = minecraft.font.width(s.substring(0, Math.max(Math.min(k, s.length()), 0)));
					int i2 = l1 - minecraft.font.width(s) / 2;

					if(k >= s.length())
						minecraft.font.drawInBatch("_", i2, j1, textColor, false, positionMatrix, buffer, false, 0, 15728880);
				}
			}
		}

		buffer.endBatch();

		for(int k3 = 0; k3 < signText.length; ++k3)
		{
			String s1 = signText[k3];

			if(s1 != null && k3 == editLine && k >= 0)
			{
				int l3 = minecraft.font.width(s1.substring(0, Math.max(Math.min(k, s1.length()), 0)));
				int i4 = l3 - minecraft.font.width(s1) / 2;

				if(update && k < s1.length())
					fill(matrix, i4, j1 - 1, i4 + 1, j1 + 9, -16777216 | textColor);

				if(l != k)
				{
					int j4 = Math.min(k, l);
					int j2 = Math.max(k, l);
					int k2 = minecraft.font.width(s1.substring(0, j4)) - minecraft.font.width(s1) / 2;
					int l2 = minecraft.font.width(s1.substring(0, j2)) - minecraft.font.width(s1) / 2;
					int i3 = Math.min(k2, l2);
					int j3 = Math.max(k2, l2);
					BufferBuilder buf = Tesselator.getInstance().getBuilder();

					RenderSystem.disableTexture();
					RenderSystem.enableColorLogicOp();
					RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
					buf.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
					buf.vertex(positionMatrix, i3, j1 + 9, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.vertex(positionMatrix, j3, j1 + 9, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.vertex(positionMatrix, j3, j1, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.vertex(positionMatrix, i3, j1, 0.0F).color(0, 0, 255, 255).endVertex();
					buf.end();
					BufferUploader.end(buf);
					RenderSystem.disableColorLogicOp();
					RenderSystem.enableTexture();
				}
			}
		}

		matrix.popPose();
		Lighting.setupFor3DItems();
		super.render(matrix, mouseX, mouseY, partialTicks);
	}
}