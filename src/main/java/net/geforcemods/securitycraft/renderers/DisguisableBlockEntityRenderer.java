package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;

public class DisguisableBlockEntityRenderer<T extends TileEntity> extends TileEntityRenderer<T> {
	public DisguisableBlockEntityRenderer(TileEntityRendererDispatcher terd) {
		super(terd);
	}

	@Override
	public void render(T be, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);
	}
}
