package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.tileentity.ProjectorTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ProjectorTileEntityRenderer extends TileEntityRenderer<ProjectorTileEntity> {

	public ProjectorTileEntityRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(ProjectorTileEntity te, float partialTicks, MatrixStack stack, IRenderTypeBuffer buffer, int arg4, int arg5) {
		
	}

}
