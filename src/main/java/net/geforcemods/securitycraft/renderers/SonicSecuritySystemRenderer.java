package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SonicSecuritySystemRenderer implements BlockEntityRenderer<SonicSecuritySystemBlockEntity> {
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/block/sonic_security_system.png");
	private static final TranslatableComponent RECORDING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.recording");
	private static final TranslatableComponent LISTENING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.listening");
	private final SonicSecuritySystemModel model;

	public SonicSecuritySystemRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SonicSecuritySystemModel(ctx.bakeLayer(ClientHandler.SONIC_SECURITY_SYSTEM_LOCATION));
	}

	@Override
	public void render(SonicSecuritySystemBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay) {
		boolean recording = be.isRecording();

		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, packedLight, packedOverlay);
		pose.translate(0.5D, 1.5D, 0.5D);

		if (recording || be.isListening() && !be.isShutDown()) {
			TranslatableComponent text = recording ? RECORDING_TEXT : LISTENING_TEXT;
			float f1 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
			int j = (int) (f1 * 255.0F) << 24;
			Font fontRenderer = Minecraft.getInstance().font;
			float halfWidth = -fontRenderer.width(text) / 2;
			Matrix4f positionMatrix;

			pose.pushPose();
			pose.mulPose(Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation());
			pose.scale(-0.025F, -0.025F, 0.025F);
			positionMatrix = pose.last().pose();
			RenderSystem.disableCull();
			fontRenderer.drawInBatch(text, halfWidth, 0, 16777215, false, positionMatrix, buffer, true, j, packedLight);
			fontRenderer.drawInBatch(text, halfWidth, 0, -1, false, positionMatrix, buffer, false, 0, packedLight);
			RenderSystem.enableCull();
			pose.popPose();
		}

		if (!be.isModuleEnabled(ModuleType.DISGUISE)) {
		pose.mulPose(POSITIVE_X_180);
		model.setRadarRotation(Mth.lerp(partialTicks, be.getOriginalRadarRotationDegrees(), be.getRadarRotationDegrees()));
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
}
