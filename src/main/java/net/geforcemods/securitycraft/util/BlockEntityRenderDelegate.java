package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRenderDelegate {
	private final Map<BlockPos, DelegateRendererInfo> renderDelegates = new HashMap<>();

	public void putDelegateFor(BlockEntity originalBlockEntity, BlockState delegateState, ItemStack stack) {
		BlockPos pos = originalBlockEntity.getBlockPos();

		if (renderDelegates.containsKey(pos)) {
			DelegateRendererInfo delegateInfo = renderDelegates.get(pos);

			//the original be already has a delegate block entity of the same type, just update the state instead of creating a whole new be and renderer
			if (delegateInfo.delegateBlockEntity.getBlockState().getBlock() == delegateState.getBlock()) {
				delegateInfo.delegateBlockEntity.setBlockState(delegateState);
				return;
			}
		}

		if (delegateState != null && delegateState.hasBlockEntity()) {
			Minecraft mc = Minecraft.getInstance();
			BlockEntity delegateBe = ((EntityBlock) delegateState.getBlock()).newBlockEntity(BlockPos.ZERO, delegateState);

			if (delegateBe != null) {
				BlockEntityRenderer<?, ?> delegateBeRenderer;

				delegateBe.setLevel(mc.level);
				delegateBe.applyComponentsFromItemStack(stack);
				delegateBeRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(delegateBe);

				if (delegateBeRenderer != null)
					renderDelegates.put(pos, new DelegateRendererInfo(delegateBe, delegateBeRenderer));
			}
		}
	}

	public void removeDelegateOf(BlockEntity originalBlockEntity) {
		renderDelegates.remove(originalBlockEntity.getBlockPos());
	}

	public <T extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRenderState tryExtractFromDelegate(BlockEntity originalBlockEntity, float partialTick, Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
		BlockPos pos = originalBlockEntity.getBlockPos();
		DelegateRendererInfo delegateRendererInfo = renderDelegates.get(pos);

		if (delegateRendererInfo != null) {
			BlockEntity delegateBlockEntity = delegateRendererInfo.delegateBlockEntity();

			if (delegateBlockEntity == originalBlockEntity) //Prevent recursion
				return null;

			BlockEntityRenderer<T, S> delegateRenderer = delegateRendererInfo.delegateRenderer();
			S delegateState = delegateRenderer.createRenderState();
			BlockPos previousPos = delegateBlockEntity.worldPosition;

			delegateBlockEntity.worldPosition = pos;
			delegateRenderer.extractRenderState((T) delegateRendererInfo.delegateBlockEntity(), delegateState, partialTick, cameraPos, crumblingOverlay);
			delegateBlockEntity.worldPosition = previousPos;
			return delegateState;
		}

		return null;
	}

	public boolean trySubmitDelegate(BlockEntityRenderState delegateState, PoseStack pose, SubmitNodeCollector collector, CameraRenderState camera) {
		if (delegateState != null) {
			DelegateRendererInfo delegateRendererInfo = renderDelegates.get(delegateState.blockPos);

			if (delegateRendererInfo != null) {
				try {
					PoseStack copyPose = new PoseStack();

					copyPose.pushPose();
					copyPose.last().pose().mul(pose.last().pose());
					copyPose.last().normal().mul(pose.last().normal());
					delegateRendererInfo.delegateRenderer().submit(delegateState, pose, collector, camera);
					copyPose.popPose();
				}
				catch (Exception e) {
					SecurityCraft.LOGGER.warn("Error when delegate-rendering {}", BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(delegateRendererInfo.delegateBlockEntity().getType()));
					e.printStackTrace();
					renderDelegates.remove(delegateState.blockPos);
				}

				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	private static record DelegateRendererInfo(BlockEntity delegateBlockEntity, BlockEntityRenderer delegateRenderer) {}
}
