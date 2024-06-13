package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class SecureRedstoneInterfaceDishModel extends EntityModel<Entity> {
	private final ModelRenderer modelParts;

	public SecureRedstoneInterfaceDishModel() {
		texWidth = 16;
		texHeight = 16;
		modelParts = new ModelRenderer(this);
		//@formatter:off
		modelParts.texOffs(0, 4).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F)
		.texOffs(0, 2).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 1.0F, 1.0F)
		.texOffs(0, 0).addBox(-2.0F, 1.0F, -2.0F, 4.0F, 1.0F, 1.0F)
		.texOffs(7, 7).addBox(1.0F, -1.0F, -2.0F, 1.0F, 2.0F, 1.0F)
		.texOffs(0, 8).addBox(-2.0F, -1.0F, -2.0F, 1.0F, 2.0F, 1.0F);
		modelParts.setPos(0.0F, 12.0F, 0.0F);
		//@formatter:on
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}

	@Override
	public void renderToBuffer(MatrixStack poseStack, IVertexBuilder vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		modelParts.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void rotate(float rotation) {
		modelParts.yRot = rotation;
	}
}