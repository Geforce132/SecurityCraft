package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemModel extends EntityModel<Entity> {
	public final ModelRenderer dish;

	public SonicSecuritySystemModel() {
		texWidth = 32;
		texHeight = 32;

		dish = new ModelRenderer(this);
		dish.setPos(0.0F, 10.5F, 0.0F);
		dish.texOffs(15, 0).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 2.0F, 0.0F, false);
		dish.texOffs(15, 3).addBox(-1.5F, -1.5F, 1.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		dish.texOffs(15, 5).addBox(0.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		dish.texOffs(15, 7).addBox(-1.5F, -0.5F, 1.0F, 1.0F, 1.0F, 1.0F, 0.0F, false);
		dish.texOffs(15, 9).addBox(-1.5F, 0.5F, 1.0F, 3.0F, 1.0F, 1.0F, 0.0F, false);
		dish.texOffs(0, 0).addBox(-2.5F, -2.5F, 1.5F, 5.0F, 1.0F, 1.0F, 0.0F, false);
		dish.texOffs(0, 2).addBox(1.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		dish.texOffs(0, 6).addBox(-2.5F, -1.5F, 1.5F, 1.0F, 3.0F, 1.0F, 0.0F, false);
		dish.texOffs(0, 10).addBox(-2.5F, 1.5F, 1.5F, 5.0F, 1.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(MatrixStack pose, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		dish.render(pose, buffer, packedLight, packedOverlay);
	}

	public void setRadarRotation(float rotation) {
		dish.yRot = rotation;
	}
}