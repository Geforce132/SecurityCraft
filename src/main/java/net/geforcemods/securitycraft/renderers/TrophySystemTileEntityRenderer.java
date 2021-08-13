package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer implements BlockEntityRenderer<TrophySystemBlockEntity> {

	public TrophySystemTileEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(TrophySystemBlockEntity te, float partialTicks, PoseStack matrix, MultiBufferSource buffer, int packedLight, int combinedOverlay) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = matrix.last().pose();
		BlockPos pos = te.getBlockPos();

		builder.vertex(positionMatrix, 0.5F, 0.75F, 0.5F).color(255, 0, 0, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
		builder.vertex(positionMatrix, (float)(te.entityBeingTargeted.getX() - pos.getX()), (float)(te.entityBeingTargeted.getY() - pos.getY()), (float)(te.entityBeingTargeted.getZ() - pos.getZ())).color(255, 0, 0, 255).normal(1.0F, 1.0F, 1.0F).endVertex();
	}

	@Override
	public boolean shouldRenderOffScreen(TrophySystemBlockEntity te)
	{
		return true;
	}

}
