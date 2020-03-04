package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * IMSBomb - Geforce
 * Created using Tabula 4.1.1
 */
@OnlyIn(Dist.CLIENT)
public class IMSBombModel extends EntityModel<IMSBombEntity> {
	public ModelRenderer shape1;

	public IMSBombModel() {
		textureWidth = 24;
		textureHeight = 24;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3);
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		shape1.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setRotationAngles(IMSBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
