package net.geforcemods.securitycraft.renderers;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonMovingBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.PistonHeadRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ReinforcedPistonHeadRenderer implements BlockEntityRenderer<ReinforcedPistonMovingBlockEntity, PistonHeadRenderState> {
	public ReinforcedPistonHeadRenderer(BlockEntityRendererProvider.Context ctx) {}

	@Override
	public void submit(PistonHeadRenderState renderState, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (renderState.block != null) {
			pose.pushPose();
			pose.translate(renderState.xOffset, renderState.yOffset, renderState.zOffset);
			collector.submitMovingBlock(pose, renderState.block);
			pose.popPose();

			if (renderState.base != null)
				collector.submitMovingBlock(pose, renderState.base);
		}
	}

	@Override
	public PistonHeadRenderState createRenderState() {
		return new PistonHeadRenderState();
	}

	@Override
	public void extractRenderState(ReinforcedPistonMovingBlockEntity be, PistonHeadRenderState renderState, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockEntityRenderer.super.extractRenderState(be, renderState, partialTick, cameraPos, crumblingOverlay);
		renderState.xOffset = be.getOffsetX(partialTick);
		renderState.yOffset = be.getOffsetY(partialTick);
		renderState.zOffset = be.getOffsetZ(partialTick);
		renderState.block = null;
		renderState.base = null;

		BlockState state = be.getMovedState();
		Level level = be.getLevel();

		if (level != null && !state.isAir()) {
			BlockPos backPos = be.getBlockPos().relative(be.getMovementDirection().getOpposite());
			Holder<Biome> biome = level.getBiome(backPos);

			if (state.is(SCContent.REINFORCED_PISTON_HEAD.get()) && be.getProgress(partialTick) <= 4.0F) {
				state = state.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTick) <= 0.5F);
				renderState.block = createMovingBlock(backPos, state, biome, level);
			}
			else if (be.isSourcePiston() && !be.isExtending()) {
				PistonType type = state.is(SCContent.REINFORCED_STICKY_PISTON.get()) ? PistonType.STICKY : PistonType.DEFAULT;
				BlockState defaultPistonHead = SCContent.REINFORCED_PISTON_HEAD.get().defaultBlockState().setValue(PistonHeadBlock.TYPE, type).setValue(PistonHeadBlock.FACING, state.getValue(PistonBaseBlock.FACING));
				BlockPos thisPos = backPos.relative(be.getMovementDirection());

				defaultPistonHead = defaultPistonHead.setValue(PistonHeadBlock.SHORT, be.getProgress(partialTick) >= 0.5F);
				renderState.block = createMovingBlock(backPos, defaultPistonHead, biome, level);
				state = state.setValue(PistonBaseBlock.EXTENDED, true);
				renderState.base = createMovingBlock(thisPos, state, biome, level);
			}
			else
				renderState.block = createMovingBlock(backPos, state, biome, level);
		}
	}

	private static MovingBlockRenderState createMovingBlock(BlockPos pos, BlockState state, Holder<Biome> biome, Level level) {
		MovingBlockRenderState renderState = new MovingBlockRenderState();

		renderState.randomSeedPos = pos;
		renderState.blockPos = pos;
		renderState.blockState = state;
		renderState.biome = biome;
		renderState.level = level;
		return renderState;
	}

	@Override
	public int getViewDistance() {
		return 68;
	}

	@Override
	public AABB getRenderBoundingBox(ReinforcedPistonMovingBlockEntity blockEntity) {
		return AABB.INFINITE;
	}
}
