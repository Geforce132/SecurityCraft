package net.geforcemods.securitycraft.renderers;

import java.util.Optional;
import java.util.Set;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemDisplayContext;

public record SecurityCameraSpecialRenderer(SecurityCameraModel model, ResourceLocation texture, float rotation, Optional<Integer> lensColor, Optional<Integer> light) implements NoDataSpecialModelRenderer {
	@Override
	public void render(ItemDisplayContext ctx, PoseStack pose, MultiBufferSource buffer, int packedLight, int packedOverlay, boolean glint) {
		int color = lensColor.orElseGet(() -> {
			model.cameraRotationPoint2.visible = false;
			return 0x70FFFF;
		});

		model.rotateCameraY(rotation);
		model.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(texture)), light.orElse(packedLight), packedOverlay, ARGB.color(255, color));
		model.cameraRotationPoint2.visible = true;
	}

	@Override
	public void getExtents(Set<Vector3f> extents) {
		PoseStack poseStack = new PoseStack();

		model.rotateCameraY(rotation);
		model.root().getExtentsForGui(poseStack, extents);
	}

	public static record Unbaked(ResourceLocation texture, float rotation, Optional<Integer> lensColor, Optional<Integer> light) implements SpecialModelRenderer.Unbaked {

		//@formatter:off
		public static final MapCodec<SecurityCameraSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
				i -> i.group(
						ResourceLocation.CODEC.fieldOf("texture").forGetter(SecurityCameraSpecialRenderer.Unbaked::texture),
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
		public SpecialModelRenderer<?> bake(EntityModelSet modelSet) {
			SecurityCameraModel model = new SecurityCameraModel(modelSet.bakeLayer(ClientHandler.SECURITY_CAMERA_LOCATION));

			return new SecurityCameraSpecialRenderer(model, texture.withPrefix("textures/entity/security_camera/").withSuffix(".png"), rotation, lensColor, light);
		}
	}
}
