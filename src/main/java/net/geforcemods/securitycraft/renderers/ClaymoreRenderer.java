package net.geforcemods.securitycraft.renderers;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blocks.mines.ClaymoreBlock;
import net.geforcemods.securitycraft.renderers.state.ClaymoreRenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.ARGB;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ClaymoreRenderer implements BlockEntityRenderer<ClaymoreBlockEntity, ClaymoreRenderState> {
	public ClaymoreRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(ClaymoreRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.0D, 0.5D);
		poseStack.mulPose(state.rotation);
		poseStack.translate(-0.5D, 0.0D, -0.5D);

		submitNodeCollector.submitCustomGeometry(poseStack, RenderType.lines(), (pose, builder) -> {
			Vec3i normal = state.normal;
			float multiplier = 0.0625F;
			float xzStart = 9.0F * multiplier;
			float y = 4.5F * multiplier;
			int r = state.r;
			int g = state.g;
			int b = state.b;
			builder.addVertex(pose, xzStart, y, xzStart).setColor(r, g, b, 255).setNormal(normal.getX(), normal.getY(), normal.getZ());
			builder.addVertex(pose, 11.0F * multiplier, y, 1.0F).setColor(r, g, b, 0).setNormal(normal.getX(), normal.getY(), normal.getZ());
			builder.addVertex(pose, 7.0F * multiplier, y, xzStart).setColor(r, g, b, 255).setNormal(normal.getX(), normal.getY(), normal.getZ());
			builder.addVertex(pose, 5.0F * multiplier, y, 1.0F).setColor(r, g, b, 0).setNormal(normal.getX(), normal.getY(), normal.getZ());
		});
		poseStack.popPose();
	}

	@Override
	public ClaymoreRenderState createRenderState() {
		return new ClaymoreRenderState();
	}

	@Override
	public void extractRenderState(ClaymoreBlockEntity be, ClaymoreRenderState state, float partialTick, Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		ItemStack lens = be.getLensContainer().getItem(0);
		Direction claymoreFacing = be.getBlockState().getValue(ClaymoreBlock.FACING);
		Direction rotationDirection = switch (claymoreFacing) {
			case EAST, WEST -> claymoreFacing.getOpposite();
			default -> claymoreFacing;
		};

		state.isActivated = be.getBlockState().getValue(ClaymoreBlock.DEACTIVATED);
		state.rotation = Axis.YP.rotationDegrees(rotationDirection.toYRot());
		state.normal = claymoreFacing.getUnitVec3i();

		if (lens.has(DataComponents.DYED_COLOR)) {
			int color = lens.get(DataComponents.DYED_COLOR).rgb();

			state.r = ARGB.red(color);
			state.g = ARGB.green(color);
			state.b = ARGB.blue(color);
		}
		else {
			state.r = 0xFF;
			state.g = 0xFF;
			state.b = 0xFF;
		}
	}

	@Override
	public boolean shouldRenderOffScreen() {
		return true;
	}
}
