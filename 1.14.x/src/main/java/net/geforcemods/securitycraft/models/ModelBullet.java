package net.geforcemods.securitycraft.models;

import net.geforcemods.securitycraft.entity.EntityBullet;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelBullet extends EntityModel<EntityBullet>
{
	public RendererModel bullet;

	public ModelBullet()
	{
		textureWidth = 8;
		textureHeight = 4;
		bullet = new RendererModel(this, 0, 0);
		bullet.setRotationPoint(0.0F, 0.0F, 0.0F);
		bullet.addBox(0.0F, 0.0F, 0.0F, 1, 1, 2, 0.0F);
	}

	@Override
	public void func_78088_a(EntityBullet entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
	{
		bullet.render(scale);
	}
}
