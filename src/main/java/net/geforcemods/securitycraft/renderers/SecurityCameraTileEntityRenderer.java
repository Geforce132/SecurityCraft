package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.models.SecurityCameraModel;
import net.geforcemods.securitycraft.tileentity.SecurityCameraTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SecurityCameraTileEntityRenderer extends TileEntityRenderer<SecurityCameraTileEntity> {

	private static final Quaternion POSITIVE_Y_180 = new Quaternion(Vector3f.field_229181_d_, 180.0F, true);
	private static final Quaternion POSITIVE_Y_90 = new Quaternion(Vector3f.field_229181_d_, 90.0F, true);
	private static final Quaternion POSITIVE_Y_NEGATIVE_90 = new Quaternion(Vector3f.field_229181_d_, -90.0F, true);
	private static final Quaternion POSITIVE_X_180 = new Quaternion(Vector3f.field_229179_b_, 180.0F, true);

	public SecurityCameraTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/block/security_camera1.png");

	@Override
	public void func_225616_a_(SecurityCameraTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		if(te.down || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(te.getPos()))
			return;

		matrix.func_227861_a_(0.5D, 1.5D, 0.5D); //translate

		if(te.hasWorld() && BlockUtils.getBlock(te.getWorld(), te.getPos()) == SCContent.securityCamera)
		{
			Direction side = te.getWorld().getBlockState(te.getPos()).get(SecurityCameraBlock.FACING);

			if(side == Direction.NORTH)
				matrix.func_227863_a_(POSITIVE_Y_180); //rotate
			else if(side == Direction.EAST)
				matrix.func_227863_a_(POSITIVE_Y_90); //rotate
			else if(side == Direction.WEST)
				matrix.func_227863_a_(POSITIVE_Y_NEGATIVE_90); //rotate
		}

		matrix.func_227863_a_(POSITIVE_X_180); //rotate
		modelSecurityCamera.cameraRotationPoint.rotateAngleY = te.cameraRotation;
		modelSecurityCamera.func_225598_a_(matrix, buffer.getBuffer(RenderType.func_228634_a_(cameraTexture)), p_225616_5_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
