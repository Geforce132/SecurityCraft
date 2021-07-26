package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.entity.BulletEntity;
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
public class BulletModel extends EntityModel<BulletEntity>
{
	public ModelPart bullet;

	public BulletModel(ModelPart modelPart)
	{
		bullet = modelPart.getChild("bullet");
	}

	public static LayerDefinition createLayer()
	{
		MeshDefinition meshDefinition = new MeshDefinition();
		PartDefinition partDefinition = meshDefinition.getRoot();

		partDefinition.addOrReplaceChild("bullet", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
		return LayerDefinition.create(meshDefinition, 8, 4);
	}

	@Override
	public void renderToBuffer(PoseStack matrix, VertexConsumer builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		bullet.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(BulletEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
