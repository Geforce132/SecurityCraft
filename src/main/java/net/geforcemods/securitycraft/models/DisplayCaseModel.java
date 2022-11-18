package net.geforcemods.securitycraft.models;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class DisplayCaseModel extends Model {
	private final ModelRenderer main;
	private final ModelRenderer door;

	public DisplayCaseModel() {
		super(RenderType::entityCutout);
		texWidth = 48;
		texHeight = 48;

		main = new ModelRenderer(this);
		main.setPos(0.0F, 24.0F, 0.0F);
		main.texOffs(0, 23).addBox(-6.0F, -3.0F, -8.0F, 12.0F, 1.0F, 5.0F, 0.0F, false);
		main.texOffs(36, 0).addBox(-6.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F, 0.0F, false);
		main.texOffs(13, 4).addBox(-5.0F, -13.0F, -8.0F, 10.0F, 10.0F, 1.0F, 0.0F, false);
		main.texOffs(0, 0).addBox(5.0F, -13.0F, -8.0F, 1.0F, 10.0F, 5.0F, 0.0F, false);
		main.texOffs(0, 16).addBox(-6.0F, -14.0F, -8.0F, 12.0F, 1.0F, 5.0F, 0.0F, false);

		door = new ModelRenderer(this);
		door.setPos(-6.0F, 16.0F, -3.0F);
		door.texOffs(5, 31).addBox(1.0F, -5.0F, 0.0F, 10.0F, 10.0F, 1.0F, 0.0F, false);
		door.texOffs(0, 31).addBox(11.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		door.texOffs(28, 31).addBox(0.0F, -5.0F, 0.0F, 1.0F, 10.0F, 1.0F, 0.0F, false);
		door.texOffs(0, 43).addBox(0.0F, -6.0F, 0.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		door.texOffs(0, 46).addBox(0.0F, 5.0F, 0.0F, 12.0F, 1.0F, 1.0F, 0.0F, false);
		door.texOffs(27, 43).addBox(11.0F, -1.5F, 1.0F, 1.0F, 3.0F, 1.0F, 0.0F, false);
	}

	@Override
	public void renderToBuffer(MatrixStack pose, IVertexBuilder buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
		pose.scale(-1.0F, 1.0F, -1.0F);
		main.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		door.render(pose, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}

	public void setDoorYRot(DisplayCaseBlockEntity be, float partialTick) {
		door.yRot = -(be.getOpenness(partialTick) * ((float) Math.PI / 2.0F));
	}
}