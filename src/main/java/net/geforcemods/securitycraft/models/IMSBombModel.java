package net.geforcemods.securitycraft.models;

import net.geforcemods.securitycraft.entity.IMSBombEntity;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * IMSBomb - Geforce
 * Created using Tabula 4.1.1
 */
@OnlyIn(Dist.CLIENT)
public class IMSBombModel extends EntityModel<IMSBombEntity> {
	public RendererModel shape1;

	public IMSBombModel() {
		textureWidth = 24;
		textureHeight = 24;
		shape1 = new RendererModel(this, 0, 0);
		shape1.setRotationPoint(0.0F, 0.0F, 0.0F);
		shape1.addBox(0.0F, 0.0F, 0.0F, 3, 4, 3, 0.0F);
	}

	@Override
	public void render(IMSBombEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		shape1.render(scale);
	}
}
