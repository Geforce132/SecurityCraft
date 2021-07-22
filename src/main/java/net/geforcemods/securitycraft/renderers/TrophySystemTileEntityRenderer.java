package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import com.mojang.math.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer extends BlockEntityRenderer<TrophySystemTileEntity> {

	public TrophySystemTileEntityRenderer(BlockEntityRenderDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(TrophySystemTileEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int p_225616_5_, int p_225616_6_) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = matrix.last().pose();
		BlockPos pos = te.getBlockPos();

		//pos, color
		builder.vertex(positionMatrix, 0.5F, 0.75F, 0.5F).color(255, 0, 0, 255).endVertex();
		builder.vertex(positionMatrix, (float)(te.entityBeingTargeted.getX() - pos.getX()), (float)(te.entityBeingTargeted.getY() - pos.getY()), (float)(te.entityBeingTargeted.getZ() - pos.getZ())).color(255, 0, 0, 255).endVertex();
	}

	@Override
	public boolean shouldRenderOffScreen(TrophySystemTileEntity te)
	{
		return true;
	}

}
