package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemRenderer extends TileEntityRenderer<TrophySystemBlockEntity> {
	public TrophySystemRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(TrophySystemBlockEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		if (ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(te, partialTicks, matrix, buffer, combinedLight, combinedOverlay))
			return;

		if (te.entityBeingTargeted == null)
			return;

		IVertexBuilder builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = matrix.last().pose();
		BlockPos pos = te.getBlockPos();

		//draws a line between the trophy system and the projectile that it's targeting
		builder.vertex(positionMatrix, 0.5F, 0.75F, 0.5F).color(255, 0, 0, 255).endVertex();
		builder.vertex(positionMatrix, (float) (te.entityBeingTargeted.getX() - pos.getX()), (float) (te.entityBeingTargeted.getY() - pos.getY()), (float) (te.entityBeingTargeted.getZ() - pos.getZ())).color(255, 0, 0, 255).endVertex();
	}

	@Override
	public boolean shouldRenderOffScreen(TrophySystemBlockEntity te) {
		return true;
	}
}
