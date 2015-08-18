package org.freeforums.geforce.securitycraft.renderers;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

public class ItemKeypadChestRenderer extends TileEntityItemStackRenderer {

	public void renderByItem(ItemStack item) {
		Block block = Block.getBlockFromItem(item.getItem());
		 
		if (block == mod_SecurityCraft.keypadChest)
        {
            TileEntityRendererDispatcher.instance.renderTileEntityAt(new TileEntityKeypadChest(), 0.0D, 0.0D, 0.0D, 0.0F);
        }
        else
        {
            super.renderByItem(item);
        }
		
	}
	
}
