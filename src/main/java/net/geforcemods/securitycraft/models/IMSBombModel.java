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
		texWidth = 24;
		texHeight = 24;
		shape1 = new ModelRenderer(this, 0, 0);
		shape1.setPos(0.0F, 0.0F, 0.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3);
	}

	@Override
	public void renderToBuffer(MatrixStack matrix, IVertexBuilder builder, int packedLight, int packedOverlay, float red, float green, float blue, float alpha)
	{
		shape1.render(matrix, builder, packedLight, packedOverlay, red, green, blue, alpha);
	}

	@Override
	public void setupAnim(IMSBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {}
}
