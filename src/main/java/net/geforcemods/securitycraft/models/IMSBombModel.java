package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IMSBombModel extends EntityModel<IMSBombEntity> {
	public ModelPart bomb;

	public IMSBombModel(ModelPart modelPart)
	{
		bomb = modelPart.getChild("ims_bomb");
	}

	public static LayerDefinition createLayer()
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("ims_bomb", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 3.0F, 4.0F, 3.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshDefinition, 24, 24);
	}

	@Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		bomb.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(IMSBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
