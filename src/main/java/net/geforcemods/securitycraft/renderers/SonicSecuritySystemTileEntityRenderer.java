package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.SonicSecuritySystemBlock;
import net.geforcemods.securitycraft.models.SonicSecuritySystemModel;
import net.geforcemods.securitycraft.tileentity.SonicSecuritySystemTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SonicSecuritySystemTileEntityRenderer extends TileEntityRenderer<SonicSecuritySystemTileEntity> {

	private static final Quaternion POSITIVE_Y_180 = Vector3f.YP.rotationDegrees(180.0F);
	private static final Quaternion POSITIVE_Y_90 = Vector3f.YP.rotationDegrees(90.0F);
	private static final Quaternion NEGATIVE_Y_90 = Vector3f.YN.rotationDegrees(90.0F);
	private static final Quaternion POSITIVE_X_180 = Vector3f.XP.rotationDegrees(180.0F);

	public SonicSecuritySystemTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	private static final SonicSecuritySystemModel modelSonicSecuritySystem = new SonicSecuritySystemModel();
	private static final ResourceLocation sonicSecuritySystemTexture = new ResourceLocation("securitycraft:textures/block/sonic_security_system.png");

	@Override
	public void render(SonicSecuritySystemTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_)
	{
		matrix.translate(0.5D, 1.5D, 0.5D);

		if(te.hasWorld() && BlockUtils.getBlock(te.getWorld(), te.getPos()) == SCContent.SONIC_SECURITY_SYSTEM.get())
		{
			Direction side = te.getWorld().getBlockState(te.getPos()).get(SonicSecuritySystemBlock.FACING);

			if(side == Direction.NORTH)
				matrix.rotate(POSITIVE_Y_180);
			else if(side == Direction.EAST)
				matrix.rotate(POSITIVE_Y_90);
			else if(side == Direction.WEST)
				matrix.rotate(NEGATIVE_Y_90);
		}

		matrix.rotate(POSITIVE_X_180);
		modelSonicSecuritySystem.radar.rotateAngleY = (float)te.radarRotationDegrees;
		modelSonicSecuritySystem.render(matrix, buffer.getBuffer(RenderType.getEntitySolid(sonicSecuritySystemTexture)), p_225616_5_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
	}
}
