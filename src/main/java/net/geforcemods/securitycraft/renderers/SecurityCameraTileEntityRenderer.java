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

	public SecurityCameraTileEntityRenderer()
	{
		super(TileEntityRendererDispatcher.instance);
	}

	private static final SecurityCameraModel modelSecurityCamera = new SecurityCameraModel();
	private static final ResourceLocation cameraTexture = new ResourceLocation("securitycraft:textures/block/security_camera1.png");

	@Override
	public void func_225616_a_(SecurityCameraTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		if(te.down || PlayerUtils.isPlayerMountedOnCamera(Minecraft.getInstance().player) && Minecraft.getInstance().player.getRidingEntity().getPosition().equals(te.getPos()))
			return;

		float rotation = 0F;

		matrix.func_227861_a_(0.5D, 1.5D, 0.5D); //translate

		if(te.hasWorld() && BlockUtils.getBlock(te.getWorld(), te.getPos()) == SCContent.securityCamera){
			Direction side = BlockUtils.getBlockPropertyAsEnum(te.getWorld(), te.getPos(), SecurityCameraBlock.FACING);

			if(side == Direction.EAST)
				rotation = -1F;
			else if(side == Direction.SOUTH)
				rotation = -10000F;
			else if(side == Direction.WEST)
				rotation = 1F;
			else if(side == Direction.NORTH)
				rotation = 0F;
		}
		else
			rotation = -10000F;

		//		RenderSystem.rotatef(180F, rotation, 0.0F, 1.0F);
		matrix.func_227863_a_(new Quaternion(Vector3f.field_229183_f_, rotation, true));
		modelSecurityCamera.cameraRotationPoint.rotateAngleY = te.cameraRotation;
		modelSecurityCamera.func_225598_a_(matrix, buffer.getBuffer(RenderType.func_228634_a_(cameraTexture)), p_225616_5_, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
