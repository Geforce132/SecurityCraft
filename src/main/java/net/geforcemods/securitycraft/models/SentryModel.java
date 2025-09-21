package net.geforcemods.securitycraft.models;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.renderers.state.SentryRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;

public class SentryModel extends EntityModel<SentryRenderState> {
	private final ModelPart base;
	private final List<ModelPart> headPartList;

	public SentryModel(ModelPart modelPart) {
		super(modelPart);
		base = modelPart.getChild("base");
		//@formatter:off
		headPartList = List.of(
				modelPart.getChild("head"),
				modelPart.getChild("neck"),
				modelPart.getChild("right_eye"),
				modelPart.getChild("body"),
				modelPart.getChild("nose"),
				modelPart.getChild("left_eye"),
				modelPart.getChild("hair"));
		//@formatter:on
	}

	public static LayerDefinition createLayer() {
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("base", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 15.0F, 15.0F, 15.0F), PartPose.offset(-7.5F, 9.0F, -7.5F));
		partDefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 30).addBox(0.0F, 0.0F, 0.0F, 6.0F, 4.0F, 6.0F), PartPose.offset(-3.0F, 5.0F, -3.0F));
		partDefinition.addOrReplaceChild("neck", CubeListBuilder.create().texOffs(45, 0).addBox(0.0F, 0.0F, 0.0F, 4.0F, 4.0F, 4.0F), PartPose.offset(-2.0F, 1.0F, -2.0F));
		partDefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(24, 30).addBox(0.0F, 0.0F, 0.0F, 8.0F, 5.0F, 6.0F), PartPose.offset(-4.0F, -4.0F, -3.0F));
		partDefinition.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(0, 40).addBox(0.0F, 0.0F, 0.0F, 6.0F, 1.0F, 6.0F), PartPose.offset(-3.0F, -5.0F, -3.0F));
		partDefinition.addOrReplaceChild("right_eye", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(-2.7F, -3.0F, -3.3F));
		partDefinition.addOrReplaceChild("left_eye", CubeListBuilder.create().texOffs(6, 0).addBox(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 1.0F), PartPose.offset(0.7F, -3.0F, -3.3F));
		partDefinition.addOrReplaceChild("nose", CubeListBuilder.create().texOffs(0, 3).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 4.0F), PartPose.offset(-0.5F, -1.0F, -6.9F));
		return LayerDefinition.create(meshDefinition, 64, 64);
	}

	public void submitBase(SentryRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, RenderType renderType) {
		submitNodeCollector.submitModelPart(base, pose, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null, state.outlineColor, null);
	}

	public void submitHead(SentryRenderState state, PoseStack pose, SubmitNodeCollector submitNodeCollector, RenderType renderType) {
		headPartList.forEach(part -> submitNodeCollector.submitModelPart(part, pose, renderType, state.lightCoords, OverlayTexture.NO_OVERLAY, null, state.outlineColor, null));
	}
}
