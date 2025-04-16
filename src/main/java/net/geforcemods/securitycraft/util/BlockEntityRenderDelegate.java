package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class BlockEntityRenderDelegate {
	private final Map<TileEntity, DelegateRendererInfo> renderDelegates = new HashMap<>();

	public void putDelegateFor(TileEntity originalBlockEntity, BlockState delegateState, ItemStack stack) {
		if (renderDelegates.containsKey(originalBlockEntity)) {
			DelegateRendererInfo delegateInfo = renderDelegates.get(originalBlockEntity);

			//the original be already has a delegate block entity of the same type, just update the state instead of creating a whole new be and renderer
			if (delegateInfo.delegateBlockEntity.getBlockState().getBlock() == delegateState.getBlock()) {
				delegateInfo.delegateBlockEntity.blockState = delegateState;
				return;
			}
		}

		if (delegateState != null && delegateState.hasTileEntity()) {
			Minecraft mc = Minecraft.getInstance();
			TileEntity delegateBe = delegateState.createTileEntity(mc.level);
			TileEntityRenderer<?> delegateBeRenderer;

			delegateBe.blockState = delegateState;
			delegateBe.level = mc.level;
			Utils.updateBlockEntityWithItemTag(delegateBe, stack);
			delegateBeRenderer = TileEntityRendererDispatcher.instance.getRenderer(delegateBe);

			if (delegateBeRenderer != null)
				renderDelegates.put(originalBlockEntity, new DelegateRendererInfo(delegateBe, delegateBeRenderer));
		}
	}

	public void removeDelegateOf(TileEntity originalBlockEntity) {
		renderDelegates.remove(originalBlockEntity);
	}

	public boolean tryRenderDelegate(TileEntity originalBlockEntity, float partialTicks, MatrixStack pose, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
		DelegateRendererInfo delegateRendererInfo = renderDelegates.get(originalBlockEntity);

		if (delegateRendererInfo != null) {
			try {
				MatrixStack copyPose = new MatrixStack();

				copyPose.pushPose();
				copyPose.last().pose().multiply(pose.last().pose());
				copyPose.last().normal().mul(pose.last().normal());
				delegateRendererInfo.delegateRenderer().render(delegateRendererInfo.delegateBlockEntity(), partialTicks, copyPose, buffer, combinedLight, combinedOverlay);
				copyPose.popPose();
			}
			catch (Exception e) {
				SecurityCraft.LOGGER.warn("Error when delegate-rendering {}", delegateRendererInfo.delegateBlockEntity().getType().getRegistryName());
				e.printStackTrace();
				removeDelegateOf(originalBlockEntity);
			}

			return true;
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	private static class DelegateRendererInfo {
		private final TileEntity delegateBlockEntity;
		private final TileEntityRenderer delegateRenderer;

		public DelegateRendererInfo(TileEntity delegateBlockEntity, TileEntityRenderer delegateRenderer) {
			this.delegateBlockEntity = delegateBlockEntity;
			this.delegateRenderer = delegateRenderer;
		}

		public TileEntity delegateBlockEntity() {
			return delegateBlockEntity;
		}

		public TileEntityRenderer delegateRenderer() {
			return delegateRenderer;
		}
	}
}
