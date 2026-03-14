package net.geforcemods.securitycraft.renderers;

import java.util.Optional;
import java.util.function.Consumer;

import org.joml.Vector3fc;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.renderers.state.SecurityCameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public record SecurityCameraSpecialRenderer(SecurityCameraModel model, Identifier texture, float rotation, Optional<Integer> lensColor, Optional<Integer> light) implements NoDataSpecialModelRenderer {
	@Override
	public void submit(PoseStack pose, SubmitNodeCollector collector, int lightCoords, int overlayCoords, boolean hasFoil, int outlineColor) {
		SecurityCameraRenderState state = new SecurityCameraRenderState();

		lensColor.ifPresentOrElse(color -> {
			state.hasLens = true;
			state.lensColor = color;
		}, () -> state.lensColor = 0x70FFFF);
		state.cameraYRot = rotation;
		collector.submitModel(model, state, pose, RenderTypes.entitySolid(texture), light.orElse(lightCoords), overlayCoords, outlineColor, null);
	}

	@Override
	public void getExtents(Consumer<Vector3fc> extents) {
		PoseStack poseStack = new PoseStack();

		model.rotateCameraY(rotation);
		model.root().getExtentsForGui(poseStack, extents);
	}

	public record Unbaked(Identifier texture, float rotation, Optional<Integer> lensColor, Optional<Integer> light) implements SpecialModelRenderer.Unbaked<Void> {

		//@formatter:off
		public static final MapCodec<SecurityCameraSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
				i -> i.group(
						Identifier.CODEC.fieldOf("texture").forGetter(SecurityCameraSpecialRenderer.Unbaked::texture),
						Codec.FLOAT.optionalFieldOf("rotation", 0.0F).forGetter(SecurityCameraSpecialRenderer.Unbaked::rotation),
						ExtraCodecs.RGB_COLOR_CODEC.optionalFieldOf("lens_color").forGetter(SecurityCameraSpecialRenderer.Unbaked::lensColor),
						Codec.INT.optionalFieldOf("light").forGetter(SecurityCameraSpecialRenderer.Unbaked::light))
				.apply(i, SecurityCameraSpecialRenderer.Unbaked::new));
		//@formatter:on
		@Override
		public MapCodec<SecurityCameraSpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SecurityCameraSpecialRenderer bake(BakingContext ctx) {
			SecurityCameraModel model = new SecurityCameraModel(ctx.entityModelSet().bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));

			return new SecurityCameraSpecialRenderer(model, texture.withPrefix("textures/entity/security_camera/").withSuffix(".png"), rotation, lensColor, light);
		}
	}
}
