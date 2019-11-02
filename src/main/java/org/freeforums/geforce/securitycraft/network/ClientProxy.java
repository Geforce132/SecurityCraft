package org.freeforums.geforce.securitycraft.network;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import org.freeforums.geforce.securitycraft.entity.EntityEMP;
import org.freeforums.geforce.securitycraft.entity.EntityEMPBackup;
import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.entity.ItemKeypadChestRenderer;
import org.freeforums.geforce.securitycraft.entity.RenderEMP;
import org.freeforums.geforce.securitycraft.entity.RenderEmpBackup;
import org.freeforums.geforce.securitycraft.entity.RenderTnTCompact;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChestRenderer;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends ServerProxy{
	
	@Override
	public void registerRenderThings(){
		RenderingRegistry.registerEntityRenderingHandler(EntityTnTCompact.class, new RenderTnTCompact());
		RenderingRegistry.registerEntityRenderingHandler(EntityEMP.class, new RenderEMP());
		RenderingRegistry.registerEntityRenderingHandler(EntityEMPBackup.class, new RenderEmpBackup());
		//RenderingRegistry.registerEntityRenderingHandler(EntityCCTV.class, new RenderCCTV());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), new ItemKeypadChestRenderer());
	}
	
	public void registerClientTickHandler(){
		
	}
	

}
