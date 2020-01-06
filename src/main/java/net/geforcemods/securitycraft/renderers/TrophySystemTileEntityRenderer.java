package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.geforcemods.securitycraft.tileentity.TrophySystemTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TrophySystemTileEntityRenderer extends TileEntityRenderer<TrophySystemTileEntity> {

	public TrophySystemTileEntityRenderer(TileEntityRendererDispatcher terd)
	{
		super(terd);
	}

	@Override
	public void func_225616_a_(TrophySystemTileEntity te, float partialTicks, MatrixStack matrix, IRenderTypeBuffer buffer, int p_225616_5_, int p_225616_6_) {
		// The code below draws a line between the trophy system and the projectile that
		// it's targeting.

		if(te.entityBeingTargeted == null) return;

		IVertexBuilder builder = buffer.getBuffer(RenderType.func_228659_m_());
		Matrix4f m4f = matrix.func_227866_c_().func_227870_a_();
		BlockPos pos = te.getPos();

		//pos, color
		builder.func_227888_a_(m4f, 0.5F, 0.75F, 0.5F).func_225586_a_(255, 0, 0, 255).endVertex();
		builder.func_227888_a_(m4f, (float)(te.entityBeingTargeted.func_226277_ct_() - pos.getX()), (float)(te.entityBeingTargeted.func_226278_cu_() - pos.getY()), (float)(te.entityBeingTargeted.func_226281_cx_() - pos.getZ())).func_225586_a_(255, 0, 0, 255).endVertex();
	}

}
