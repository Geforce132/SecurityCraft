package net.geforcemods.securitycraft.renderers;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

public class TrophySystemRenderer implements BlockEntityRenderer<TrophySystemBlockEntity> {
	/**
	 * The number of blocks away from the trophy system you can be for the laser beam between itself and the projectile to be
	 * rendered
	 */
	public static final int RENDER_DISTANCE = 50;

	public TrophySystemRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void render(TrophySystemBlockEntity be, float partialTicks, PoseStack pose, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
		ClientHandler.DISGUISED_BLOCK_RENDER_DELEGATE.tryRenderDelegate(be, partialTicks, pose, buffer, combinedLight, combinedOverlay);

		Entity target = be.getTarget();

		if (target == null)
			return;

		VertexConsumer builder = buffer.getBuffer(RenderType.lines());
		Matrix4f positionMatrix = pose.last().pose();
		BlockPos pos = be.getBlockPos();
		ItemStack lens = be.getLensContainer().getItem(0);
		int r = 255, g = 255, b = 255;

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			r = (color >> 0x10) & 0xFF;
			g = (color >> 0x8) & 0xFF;
			b = color & 0xFF;
		}

		//draws a line between the trophy system and the projectile that it's targeting
		builder.addVertex(positionMatrix, 0.5F, 0.75F, 0.5F).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);
		builder.addVertex(positionMatrix, (float) (target.getX() - pos.getX()), (float) (target.getY() - pos.getY()), (float) (target.getZ() - pos.getZ())).setColor(r, g, b, 255).setNormal(1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean shouldRenderOffScreen(TrophySystemBlockEntity be) {
		return true;
	}

	@Override
	public AABB getRenderBoundingBox(TrophySystemBlockEntity be) {
		return new AABB(be.getBlockPos()).inflate(RENDER_DISTANCE);
	}
}
