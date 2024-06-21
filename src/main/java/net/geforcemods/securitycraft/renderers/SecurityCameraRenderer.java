package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraRenderer extends TileEntityRenderer<SecurityCameraBlockEntity> {
	private static final Quaternion POSITIVE_Y_180 = Vector3f.YP.rotationDegrees(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.YP.rotationDegrees(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.YN.rotationDegrees(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);
	private static final SecurityCameraModel MODEL = new SecurityCameraModel();
	private static final ResourceLocation TEXTURE = new ResourceLocation("securitycraft:textures/block/security_camera.png");
	private static final ResourceLocation BEING_VIEWED_TEXTURE = new ResourceLocation("securitycraft:textures/block/security_camera_viewing.png");

	public SecurityCameraRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(SecurityCameraBlockEntity be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int packedLight, int packedOverlay) {
		if (be.isDown() || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().cameraEntity.blockPosition().equals(be.getBlockPos()))
			return;

		pose.translate(0.5D, 1.5D, 0.5D);

		if (be.hasLevel()) {
			BlockState state = be.getLevel().getBlockState(be.getBlockPos());

			if (state.getBlock() == SCContent.SECURITY_CAMERA.get()) {
				Direction side = state.getValue(SecurityCameraBlock.FACING);

				if (side == Direction.NORTH)
					pose.mulPose(POSITIVE_Y_180);
				else if (side == Direction.EAST)
					pose.mulPose(POSITIVE_Y_90);
				else if (side == Direction.WEST)
					pose.mulPose(NEGATIVE_Y_90);
			}
		}

		pose.mulPose(POSITIVE_X_180);
		MODEL.rotateCameraY((float) MathHelper.lerp(partialTicks, be.getOriginalCameraRotation(), be.getCameraRotation()));

		if (be.isShutDown())
			MODEL.rotateCameraX(0.9F);
		else
			MODEL.rotateCameraX(SecurityCameraModel.DEFAULT_X_ROT);

		ItemStack lens = be.getLensContainer().getItem(0);
		Item item = lens.getItem();
		float r = 0.4392156862745098F, g = 1.0F, b = 1.0F;

		if (item instanceof IDyeableArmorItem && ((IDyeableArmorItem) item).hasCustomColor(lens)) {
			int color = ((IDyeableArmorItem) item).getColor(lens);

			r = ((color >> 0x10) & 0xFF) / 255.0F;
			g = ((color >> 0x8) & 0xFF) / 255.0F;
			b = (color & 0xFF) / 255.0F;
		}
		else
			MODEL.cameraRotationPoint2.visible = false;

		MODEL.renderToBuffer(pose, buffer.getBuffer(RenderType.entitySolid(be.getBlockState().getValue(SecurityCameraBlock.BEING_VIEWED) ? BEING_VIEWED_TEXTURE : TEXTURE)), packedLight, OverlayTexture.NO_OVERLAY, r, g, b, 1.0F);
		MODEL.cameraRotationPoint2.visible = true;
	}
}
