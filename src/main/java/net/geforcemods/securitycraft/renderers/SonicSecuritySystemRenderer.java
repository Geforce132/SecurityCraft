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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
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

	public SonicSecuritySystemRenderer(BlockEntityRendererProvider.Context ctx) {
		model = new SonicSecuritySystemModel(ctx.bakeLayer(ClientHandler.SONIC_SECURITY_SYSTEM_LOCATION));
	}

	@Override
	public void submit(SonicSecuritySystemRenderState state, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		//TODO: render delegate
		//ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, packedLight, packedOverlay, cameraPos);

		boolean recording = state.isRecording;

		if (recording || state.isListening && !state.isShutDown) {
			Minecraft mc = Minecraft.getInstance();
			Component text = recording ? RECORDING_TEXT : LISTENING_TEXT;
			float opacity = mc.options.getBackgroundOpacity(0.25F);
			int backgroundColor = (int) (opacity * 255.0F) << 24;
			float halfWidth = -mc.font.width(text) / 2;

			pose.pushPose();
			pose.mulPose(camera.orientation);
			pose.scale(0.025F, -0.025F, 0.025F); //TODO: Can the name tag submitting be used here for the same visual effect?
			collector.submitText(pose, halfWidth, 0, text.getVisualOrderText(), false, Font.DisplayMode.SEE_THROUGH, state.lightCoords, 0x20FFFFFF, backgroundColor, 0);
			collector.submitText(pose, halfWidth, 0, text.getVisualOrderText(), false, Font.DisplayMode.NORMAL, state.lightCoords, -1, 0, 0);
			pose.popPose();
		}

		if (!state.hasDisguiseModule) {
			pose.mulPose(POSITIVE_X_180);
			model.setRadarRotation(state.radarRotationDegrees);
			collector.submitModel(model, null, pose, RenderType.entitySolid(TEXTURE), state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress); //TODO test
		}
	}

	@Override
	public SonicSecuritySystemRenderState createRenderState() {
		return new SonicSecuritySystemRenderState();
	}

	@Override
	public void extractRenderState(SonicSecuritySystemBlockEntity be, SonicSecuritySystemRenderState state, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, state, partialTick, cameraPos, crumblingOverlay);
		state.isRecording = be.isRecording();
		state.isListening = be.isListening();
		state.isShutDown = be.isShutDown();
		state.hasDisguiseModule = be.isModuleEnabled(ModuleType.DISGUISE);
		state.radarRotationDegrees = Mth.lerp(partialTick, be.getOriginalRadarRotationDegrees(), be.getRadarRotationDegrees());
	}
}
