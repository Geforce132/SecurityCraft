package org.freeforums.geforce.securitycraft.network;

import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

import org.freeforums.geforce.securitycraft.entity.EntityEMP;
import org.freeforums.geforce.securitycraft.entity.EntityEMPBackup;
import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.models.ModelClaymore;
import org.freeforums.geforce.securitycraft.models.ModelKeypadFrame;
import org.freeforums.geforce.securitycraft.models.ModelKeypadFurnaceDeactivated;
import org.freeforums.geforce.securitycraft.renderers.CustomModeledBlockRenderer;
import org.freeforums.geforce.securitycraft.renderers.RenderEMP;
import org.freeforums.geforce.securitycraft.renderers.RenderEmpBackup;
import org.freeforums.geforce.securitycraft.renderers.RenderTnTCompact;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityClaymore;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityClaymoreRenderer;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChestRenderer;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFrame;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFrameRenderer;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnaceRenderer;

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
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadFrame.class, new TileEntityKeypadFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadFurnace.class, new TileEntityKeypadFurnaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClaymore.class, new TileEntityClaymoreRenderer());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), new CustomModeledBlockRenderer(new TileEntityKeypadChest()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadFrame), new CustomModeledBlockRenderer(new TileEntityKeypadFrame(), new ModelKeypadFrame(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadFurnace), new CustomModeledBlockRenderer(new TileEntityKeypadFurnace(), new ModelKeypadFurnaceDeactivated(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.claymoreActive), new CustomModeledBlockRenderer(new TileEntityClaymore(), new ModelClaymore(), 0.0D, -0.1D, 0.0D, 0.0F));
	}
	
	public void registerClientTickHandler(){
		
	}
	

}
