package net.geforcemods.securitycraft.renderers;

import java.util.Optional;
import java.util.Set;

import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

public record DisplayCaseSpecialRenderer(DisplayCaseModel model, ResourceLocation texture, float openness, Optional<Integer> light) implements NoDataSpecialModelRenderer {
	@Override
	public void submit(ItemDisplayContext ctx, PoseStack pose, SubmitNodeCollector collector, int packedLight, int packedOverlay, boolean glint) {
		collector.submitModel(model, openness, pose, RenderType.entityCutout(texture), light.orElse(packedLight), packedOverlay, 0, null);
	}

	@Override
	public void getExtents(Set<Vector3f> extents) {
		PoseStack poseStack = new PoseStack();

		model.setupAnim(openness);
		model.root().getExtentsForGui(poseStack, extents);
	}

	public static record Unbaked(ResourceLocation texture, float openness, Optional<Integer> light) implements SpecialModelRenderer.Unbaked {

		//@formatter:off
		public static final MapCodec<DisplayCaseSpecialRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
				i -> i.group(
						ResourceLocation.CODEC.fieldOf("texture").forGetter(DisplayCaseSpecialRenderer.Unbaked::texture),
						Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(DisplayCaseSpecialRenderer.Unbaked::openness),
						Codec.INT.optionalFieldOf("light").forGetter(DisplayCaseSpecialRenderer.Unbaked::light))
				.apply(i, DisplayCaseSpecialRenderer.Unbaked::new));

		//@formatter:on
		@Override
		public MapCodec<DisplayCaseSpecialRenderer.Unbaked> type() {
			return MAP_CODEC;
		}

		@Override
		public SpecialModelRenderer<?> bake(BakingContext ctx) {
			DisplayCaseModel model = new DisplayCaseModel(ctx.entityModelSet().bakeLayer(ClientHandler.DISPLAY_CASE_LOCATION));

			return new DisplayCaseSpecialRenderer(model, texture.withPrefix("textures/entity/display_case/").withSuffix(".png"), openness, light);
		}
	}
}
