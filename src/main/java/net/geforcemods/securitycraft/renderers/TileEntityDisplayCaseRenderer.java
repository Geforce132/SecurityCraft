package net.geforcemods.securitycraft.renderers;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.BlockDisplayCase;
import net.geforcemods.securitycraft.models.ModelDisplayCase;
import net.geforcemods.securitycraft.tileentity.TileEntityDisplayCase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityDisplayCaseRenderer extends TileEntitySpecialRenderer<TileEntityDisplayCase> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/display_case.png");
	private static final ModelDisplayCase MODEL = new ModelDisplayCase();

	@Override
	public void render(TileEntityDisplayCase te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		IBlockState state = te.getBlockState();
		EnumFacing facing = state.getValue(BlockDisplayCase.FACING);
		float rotation = facing.getHorizontalAngle();
		ItemStack displayedStack = te.getDisplayedStack();

		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x + 0.5D, y + 0.5D, z + 0.5D);

		if (!displayedStack.isEmpty()) {
			double insertionAmount = 0.40625D;

			GlStateManager.pushMatrix();

			switch (state.getValue(BlockDisplayCase.ATTACH_FACE)) {
				case CEILING:
					GlStateManager.translate(0.0D, insertionAmount, 0.0D);
					GlStateManager.rotate(-rotation + 180.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
					break;
				case FLOOR:
					GlStateManager.translate(0.0D, -insertionAmount, 0.0D);
					GlStateManager.rotate(-rotation + 180.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
					break;
				case WALL:
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
					GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
					GlStateManager.translate(0.0D, 0.0D, insertionAmount);
					break;
			}

			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			GlStateManager.pushAttrib();
			Minecraft.getMinecraft().getRenderItem().renderItem(displayedStack, TransformType.FIXED);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}

		GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);

		switch (state.getValue(BlockDisplayCase.ATTACH_FACE)) {
			case CEILING:
				GlStateManager.translate(0.0D, 0.0D, 1.0D);
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case FLOOR:
				GlStateManager.translate(0.0D, 0.0D, -1.0D);
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				break;
			case WALL:
				GlStateManager.translate(0.0D, 1.0D, 0.0D);
				GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
				break;
		}

		bindTexture(TEXTURE);
		MODEL.setDoorYRot(te, partialTicks);
		MODEL.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}
}
