package net.geforcemods.securitycraft.models;

import net.geforcemods.securitycraft.tileentity.TileEntityDisplayCase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelDisplayCase extends ModelBase {
	private final ModelRenderer main;
	private final ModelRenderer door;

	public ModelDisplayCase() {
		textureWidth = 48;
		textureHeight = 48;

		main = new ModelRenderer(this);
		main.setRotationPoint(0.0F, 24.0F, 0.0F);
		main.setTextureOffset(0, 23).addBox(-6.0F, -3.0F, -8.0F, 12, 1, 5, false);
		main.setTextureOffset(36, 0).addBox(-6.0F, -13.0F, -8.0F, 1, 10, 5, false);
		main.setTextureOffset(13, 4).addBox(-5.0F, -13.0F, -8.0F, 10, 10, 1, false);
		main.setTextureOffset(0, 0).addBox(5.0F, -13.0F, -8.0F, 1, 10, 5, false);
		main.setTextureOffset(0, 16).addBox(-6.0F, -14.0F, -8.0F, 12, 1, 5, false);

		door = new ModelRenderer(this);
		door.setRotationPoint(-6.0F, 16.0F, -3.0F);
		door.setTextureOffset(5, 31).addBox(1.0F, -5.0F, 0.0F, 10, 10, 1, false);
		door.setTextureOffset(0, 31).addBox(11.0F, -5.0F, 0.0F, 1, 10, 1, false);
		door.setTextureOffset(28, 31).addBox(0.0F, -5.0F, 0.0F, 1, 10, 1, false);
		door.setTextureOffset(0, 43).addBox(0.0F, -6.0F, 0.0F, 12, 1, 1, false);
		door.setTextureOffset(0, 46).addBox(0.0F, 5.0F, 0.0F, 12, 1, 1, false);
		door.setTextureOffset(27, 43).addBox(11.0F, -1.5F, 1.0F, 1, 3, 1, false);
	}

	@Override
	public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		GlStateManager.scale(-0.0625F, 0.0625F, -0.0625F);
		main.render(scale);
		door.render(scale);
	}

	public void setDoorYRot(TileEntityDisplayCase be, float partialTicks) {
		door.rotateAngleY = -(be.getOpenness(partialTicks) * ((float) Math.PI / 2.0F));
	}
}