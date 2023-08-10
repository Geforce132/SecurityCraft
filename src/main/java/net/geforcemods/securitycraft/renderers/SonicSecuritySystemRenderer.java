package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemRenderer extends TileEntityRenderer<SonicSecuritySystemBlockEntity> {
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final SonicSecuritySystemModel MODEL = new SonicSecuritySystemModel();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/block/sonic_security_system.png");
	private static final TranslationTextComponent RECORDING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.recording");
	private static final TranslationTextComponent LISTENING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.listening");

	public SonicSecuritySystemRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(SonicSecuritySystemBlockEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		boolean recording = te.isRecording();

		matrix.translate(0.5D, 1.5D, 0.5D);

		if (recording || te.isListening() && !te.isShutDown()) {
			TranslationTextComponent text = recording ? RECORDING_TEXT : LISTENING_TEXT;
			float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			FontRenderer fontRenderer = renderer.getFont();
			float halfWidth = -fontRenderer.width(text) / 2;
			Matrix4f positionMatrix;

			matrix.pushPose();
			matrix.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			matrix.scale(-0.025F, -0.025F, 0.025F);
			positionMatrix = matrix.last().pose();
			RenderSystem.disableCull();
			fontRenderer.drawInBatch(text, halfWidth, 0, 16777215, false, positionMatrix, buffer, true, j, packedLight);
			fontRenderer.drawInBatch(text, halfWidth, 0, -1, false, positionMatrix, buffer, false, 0, packedLight);
			RenderSystem.enableCull();
			matrix.popPose();
		}

		matrix.mulPose(POSITIVE_X_180);
		MODEL.setRadarRotation(MathHelper.lerp(partialTicks, te.getOriginalRadarRotationDegrees(), te.getRadarRotationDegrees()));
		MODEL.renderToBuffer(matrix, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
