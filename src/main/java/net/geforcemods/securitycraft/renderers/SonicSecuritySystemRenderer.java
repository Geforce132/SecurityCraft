package net.geforcemods.securitycraft.renderers;

import org.joml.Quaternionf;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.renderers.state.SonicSecuritySystemRenderState;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class SonicSecuritySystemRenderer implements BlockEntityRenderer<SonicSecuritySystemBlockEntity, SonicSecuritySystemRenderState> {
	private static final Quaternionf POSITIVE_X_180 = Axis.XP.rotationDegrees(180.0F);
	private static final ResourceLocation TEXTURE = SecurityCraft.resLoc("textures/block/sonic_security_system.png");
	private static final Component RECORDING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.recording");
	private static final Component LISTENING_TEXT = Utils.localize("gui.securitycraft:sonic_security_system.listening");
	private final SonicSecuritySystemModel model;
	private final EntityRenderDispatcher entityRenderDispatcher;

	public SonicSecuritySystemRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SonicSecuritySystemModel(ctx.bakeLayer(ClientHandler.SONIC_SECURITY_SYSTEM_LOCATION));
		entityRenderDispatcher = ctx.entityRenderer();
	}

	@Override
	public void submit(SonicSecuritySystemRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.trySubmitDelegate(state.disguiseRenderState, pose, collector, camera);
		pose.translate(0.5D, 1.0D, 0.5D);

		if (state.isRecording || state.isListening && !state.isShutDown) {
			Component text = state.isRecording ? RECORDING_TEXT : LISTENING_TEXT;

			collector.submitNameTag(pose, Vec3.ZERO, 0, text, true, state.lightCoords, state.distanceToCameraSqr, camera);
		}

		if (!state.isDisguised) {
			pose.translate(0.0D, 0.5D, 0.0D);
			pose.mulPose(POSITIVE_X_180);
			collector.submitModel(model, state.radarRotation, pose, RenderType.entitySolid(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
		}
	}

	@Override
	public SonicSecuritySystemRenderState createRenderState() {
		return new SonicSecuritySystemRenderState();
	}

	@Override
	public void extractRenderState(SonicSecuritySystemBlockEntity be, SonicSecuritySystemRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.disguiseRenderState = ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryExtractFromDelegate(be, partialTick, cameraPos, crumblingOverlay);
		state.isRecording = be.isRecording();
		state.isListening = be.isListening();
		state.isShutDown = be.isShutDown();
		state.isDisguised = be.isModuleEnabled(ModuleType.DISGUISE);
		state.radarRotation = Mth.lerp(partialTick, be.getOriginalRadarRotationDegrees(), be.getRadarRotationDegrees());
		state.distanceToCameraSqr = cameraPos.distanceToSqr(Vec3.upFromBottomCenterOf(be.getBlockPos(), 1.5D));
	}
}
