package org.freeforums.geforce.securitycraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.entity.ItemKeypadChestRenderer;
import org.freeforums.geforce.securitycraft.entity.RenderTnTCompact;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChestRenderer;

public class ClientProxy extends ServerProxy{
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderThings(){
		RenderingRegistry.registerEntityRenderingHandler(EntityTnTCompact.class, new RenderTnTCompact(Minecraft.getMinecraft().getRenderManager()));
		//RenderingRegistry.registerEntityRenderingHandler(EntityCCTV.class, new RenderCCTV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		
		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), new ItemKeypadChestRenderer());
	}
	
	public void registerClientTickHandler(){
		
	}
	

}
