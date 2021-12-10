package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemTileEntityRenderer extends TileEntityRenderer<SonicSecuritySystemTileEntity> {

	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final SonicSecuritySystemModel MODEL = new SonicSecuritySystemModel();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/block/sonic_security_system.png");
	private static final TranslationTextComponent RECORDING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.recording");
	private static final TranslationTextComponent LISTENING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.listening");

	public SonicSecuritySystemTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(SonicSecuritySystemTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight, int packedOverlay)
	{
		boolean recording = te.isRecording();

		matrix.translate(0.5D, 1.5D, 0.5D);

		if(recording || te.isListening())
		{
			TranslationTextComponent text = recording ? RECORDING_TEXT : LISTENING_TEXT;
			float f1 = Minecraft.getInstance().gameSettings.getTextBackgroundOpacity(0.25F);
			int j = (int)(f1 * 255.0F) << 24;
			FontRenderer fontRenderer = renderDispatcher.getFontRenderer();
			float halfWidth = -fontRenderer.getStringPropertyWidth(text) / 2;
			Matrix4f positionMatrix;

			matrix.push();
			matrix.rotate(Minecraft.getInstance().getRenderManager().getCameraOrientation());
			matrix.scale(-0.025F, -0.025F, 0.025F);
			positionMatrix = matrix.getLast().getMatrix();
			RenderSystem.disableCull();
			fontRenderer.func_243247_a(text, halfWidth, 0, 16777215, false, positionMatrix, buffer, true, j, packedLight);
			fontRenderer.func_243247_a(text, halfWidth, 0, -1, false, positionMatrix, buffer, false, 0, packedLight);
			matrix.pop();
		}

		matrix.rotate(POSITIVE_X_180);

		if(te.isActive() || recording)
			MODEL.radar.rotateAngleY = te.radarRotationDegrees;

		MODEL.render(matrix, buffer.getBuffer(RenderType.getEntitySolid(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
