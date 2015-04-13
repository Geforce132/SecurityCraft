package org.freeforums.geforce.securitycraft.renderers;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class CustomModeledBlockRenderer implements IItemRenderer {
	
	private ModelBase model;
	private TileEntity tileEntity;
	private double x = 0D, y = 0D, z = 0D;
	private float angle = 0F;
	
	public CustomModeledBlockRenderer(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}

	public CustomModeledBlockRenderer(TileEntity tileEntity, ModelBase model, double x, double y, double z, float angle) {
		this.tileEntity = tileEntity;
		this.model = model;
		this.x = x;
		this.y = y;
		this.z = z;
		this.angle = angle;
	}

	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		TileEntityRendererDispatcher.instance.renderTileEntityAt(tileEntity, x, y, z, angle);
	}

}
