package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.entity.BulletEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BulletModel extends EntityModel<BulletEntity>
{
	public ModelRenderer bullet;

	public BulletModel()
	{
		textureWidth = 8;
		textureHeight = 4;
		bullet = new ModelRenderer(this, 0, 0);
		bullet.setRotationPoint(0.0F, 0.0F, 0.0F);
		bullet.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2);
	}

	@Override
	public void render(MatrixStack matrix, IVertexBuilder builder, int p_225598_3_, int p_225598_4_, float p_225598_5_, float p_225598_6_, float p_225598_7_, float p_225598_8_)
	{
		bullet.render(matrix, builder, p_225598_3_, p_225598_4_, p_225598_5_, p_225598_6_, p_225598_7_, p_225598_8_);
	}

	@Override
	public void setRotationAngles(BulletEntity entity, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {}
}
