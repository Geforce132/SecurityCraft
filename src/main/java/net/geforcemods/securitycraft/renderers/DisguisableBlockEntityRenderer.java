package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.ClientHandler;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class DisguisableBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
	public DisguisableBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(T be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay, Vec3 cameraPos) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay, cameraPos);
	}
}
