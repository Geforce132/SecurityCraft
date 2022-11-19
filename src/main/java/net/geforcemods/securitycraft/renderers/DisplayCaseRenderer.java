package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blocks.DisplayCaseBlock;
import net.geforcemods.securitycraft.models.DisplayCaseModel;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class DisplayCaseRenderer extends TileEntityRenderer<DisplayCaseBlockEntity> {
	private static final ResourceLocation TEXTURE = new ResourceLocation(SecurityCraft.MODID, "textures/entity/display_case.png");
	private static final DisplayCaseModel MODEL = new DisplayCaseModel();

	public DisplayCaseRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(DisplayCaseBlockEntity be, float partialTick, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		BlockState state = be.getBlockState();
		Direction facing = state.getValue(DisplayCaseBlock.FACING);
		float rotation = facing.toYRot();
		ItemStack displayedStack = be.getDisplayedStack();

		pose.pushPose();
		pose.translate(0.5D, 0.5D, 0.5D);

		if (!displayedStack.isEmpty()) {
			double insertionAmount = 0.40625D;

			pose.pushPose();

			switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
				case CEILING:
					pose.translate(0.0D, insertionAmount, 0.0D);
					pose.mulPose(Vector3f.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
					break;
				case FLOOR:
					pose.translate(0.0D, -insertionAmount, 0.0D);
					pose.mulPose(Vector3f.YP.rotationDegrees(-rotation + 180.0F));
					pose.mulPose(Vector3f.XP.rotationDegrees(90.0F));
					break;
				case WALL:
					pose.mulPose(Vector3f.YP.rotationDegrees(180.0F));
					pose.mulPose(Vector3f.YP.rotationDegrees(-rotation));
					pose.translate(0.0D, 0.0D, insertionAmount);
					break;
			}

			pose.scale(0.5F, 0.5F, 0.5F);
			Minecraft.getInstance().getItemRenderer().renderStatic(displayedStack, TransformType.FIXED, packedLight, OverlayTexture.NO_OVERLAY, pose, buffer);
			pose.popPose();
		}

		pose.mulPose(Vector3f.YP.rotationDegrees(-rotation));

		switch (state.getValue(DisplayCaseBlock.ATTACH_FACE)) {
			case CEILING:
				pose.translate(0.0D, 0.0D, 1.0D);
				pose.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
				break;
			case FLOOR:
				pose.translate(0.0D, 0.0D, -1.0D);
				pose.mulPose(Vector3f.XP.rotationDegrees(90.0F));
				break;
			case WALL:
				pose.translate(0.0D, 1.0D, 0.0D);
				pose.mulPose(Vector3f.XP.rotationDegrees(180.0F));
				break;
		}

		MODEL.setDoorYRot(be, partialTick);
		MODEL.renderToBuffer(pose, buffer.getBuffer(MODEL.renderType(TEXTURE)), packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
		pose.popPose();
	}
}