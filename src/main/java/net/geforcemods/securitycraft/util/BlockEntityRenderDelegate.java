package net.geforcemods.securitycraft.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;

public class BlockEntityRenderDelegate {
	public static final BlockEntityRenderDelegate DISGUISED_BLOCK = new BlockEntityRenderDelegate();
	public static final BlockEntityRenderDelegate PROJECTOR = new BlockEntityRenderDelegate();
	private final Map<TileEntity, DelegateRendererInfo> renderDelegates = new HashMap<>();

	public static void putDisguisedTeRenderer(TileEntity disguisableTileEntity, ItemStack stack) {
		if (!stack.hasTagCompound())
			stack.setTagCompound(new NBTTagCompound());

		DISGUISED_BLOCK.putDelegateFor(disguisableTileEntity, NBTUtil.readBlockState(stack.getTagCompound().getCompoundTag("SavedState")), stack);
	}

	public void putDelegateFor(TileEntity originalTileEntity, IBlockState delegateState, ItemStack stack) {
		if (renderDelegates.containsKey(originalTileEntity)) {
			DelegateRendererInfo delegateInfo = renderDelegates.get(originalTileEntity);

			//the original te already has a delegate tile entity of the same type, just update the metadata instead of creating a whole new te and renderer
			if (delegateInfo.delegateTileEntity.getBlockType() == delegateState.getBlock()) {
				delegateInfo.delegateTileEntity.blockMetadata = delegateState.getBlock().getMetaFromState(delegateState);
				return;
			}
		}

		if (delegateState != null && delegateState.getBlock().hasTileEntity(delegateState)) {
			Minecraft mc = Minecraft.getMinecraft();
			TileEntity delegateTe = delegateState.getBlock().createTileEntity(mc.world, delegateState);
			TileEntitySpecialRenderer<?> delegateTeRenderer;

			delegateTe.blockType = delegateState.getBlock();
			delegateTe.blockMetadata = delegateState.getBlock().getMetaFromState(delegateState);
			delegateTe.setWorld(mc.world);

			if (delegateTe instanceof TileEntityBanner)
				((TileEntityBanner) delegateTe).setItemValues(stack, false);
			else
				Utils.updateBlockEntityWithItemTag(delegateTe, stack);

			delegateTeRenderer = TileEntityRendererDispatcher.instance.getRenderer(delegateTe);

			if (delegateTeRenderer != null)
				renderDelegates.put(originalTileEntity, new DelegateRendererInfo(delegateTe, delegateTeRenderer));
		}
	}

	public void removeDelegateOf(TileEntity originalTileEntity) {
		renderDelegates.remove(originalTileEntity);
	}

	public boolean tryRenderDelegate(TileEntity originalTileEntity, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		DelegateRendererInfo delegateRendererInfo = renderDelegates.get(originalTileEntity);

		if (delegateRendererInfo != null) {
			delegateRendererInfo.delegateRenderer().render(delegateRendererInfo.delegateTileEntity(), x, y, z, partialTicks, destroyStage, alpha);
			return true;
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	private static class DelegateRendererInfo {
		private final TileEntity delegateTileEntity;
		private final TileEntitySpecialRenderer delegateRenderer;

		public DelegateRendererInfo(TileEntity delegateTileEntity, TileEntitySpecialRenderer delegateRenderer) {
			this.delegateTileEntity = delegateTileEntity;
			this.delegateRenderer = delegateRenderer;
		}

		public TileEntity delegateTileEntity() {
			return delegateTileEntity;
		}

		public TileEntitySpecialRenderer delegateRenderer() {
			return delegateRenderer;
		}
	}
}
