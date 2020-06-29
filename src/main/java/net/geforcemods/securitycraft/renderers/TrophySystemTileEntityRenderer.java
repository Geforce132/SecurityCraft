package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer extends TileEntityRenderer<TrophySystemTileEntity> {

	public TrophySystemTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void render(TrophySystemTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		IVertexBuilder builder = buffer.getBuffer(RenderType.getLines());
		Matrix4f positionMatrix = matrix.getLast().getMatrix();
		BlockPos pos = te.getPos();

		//pos, color
		builder.pos(positionMatrix, 0.5F, 0.75F, 0.5F).color(255, 0, 0, 255).endVertex();
		builder.pos(positionMatrix, (float)(te.entityBeingTargeted.getPosX() - pos.getX()), (float)(te.entityBeingTargeted.getPosY() - pos.getY()), (float)(te.entityBeingTargeted.getPosZ() - pos.getZ())).color(255, 0, 0, 255).endVertex();
	}

	@Override
	public boolean isGlobalRenderer(TrophySystemTileEntity te)
	{
		return true;
	}

}
