package org.freeforums.geforce.securitycraft.network;

import java.util.HashMap;

import org.freeforums.geforce.securitycraft.entity.EntityIMSBomb;
import org.freeforums.geforce.securitycraft.entity.EntityTnTCompact;
import org.freeforums.geforce.securitycraft.imc.lookingglass.IWorldViewHelper;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.misc.KeyBindings;
import org.freeforums.geforce.securitycraft.models.ModelAlarm;
import org.freeforums.geforce.securitycraft.models.ModelClaymore;
import org.freeforums.geforce.securitycraft.models.ModelFrame;
import org.freeforums.geforce.securitycraft.models.ModelIMS;
import org.freeforums.geforce.securitycraft.models.ModelKeypadFurnaceDeactivated;
import org.freeforums.geforce.securitycraft.models.ModelSecurityCamera;
import org.freeforums.geforce.securitycraft.renderers.CustomModeledBlockRenderer;
import org.freeforums.geforce.securitycraft.renderers.ItemCameraMonitorRenderer;
import org.freeforums.geforce.securitycraft.renderers.ItemTaserRenderer;
import org.freeforums.geforce.securitycraft.renderers.RenderIMSBomb;
import org.freeforums.geforce.securitycraft.renderers.RenderTnTCompact;
import org.freeforums.geforce.securitycraft.renderers.TileEntityAlarmRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityClaymoreRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityFrameRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityIMSRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityKeyPanelRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityKeypadChestRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntityKeypadFurnaceRenderer;
import org.freeforums.geforce.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityClaymore;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityFrame;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityIMS;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeyPanel;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadFurnace;
import org.freeforums.geforce.securitycraft.tileentity.TileEntitySecurityCamera;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends ServerProxy{
	
	public HashMap<String, IWorldViewHelper> worldViews = new HashMap<String, IWorldViewHelper>();
			
	@Override
	public void registerRenderThings(){
		KeyBindings.init();
				
		RenderingRegistry.registerEntityRenderingHandler(EntityTnTCompact.class, new RenderTnTCompact());
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, new RenderIMSBomb());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFrame.class, new TileEntityFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadFurnace.class, new TileEntityKeypadFurnaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClaymore.class, new TileEntityClaymoreRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlarm.class, new TileEntityAlarmRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIMS.class, new TileEntityIMSRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeyPanel.class, new TileEntityKeyPanelRenderer());
		
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), new CustomModeledBlockRenderer(new TileEntityKeypadChest()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.frame), new CustomModeledBlockRenderer(new TileEntityFrame(), new ModelFrame(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadFurnace), new CustomModeledBlockRenderer(new TileEntityKeypadFurnace(), new ModelKeypadFurnaceDeactivated(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.claymoreActive), new CustomModeledBlockRenderer(new TileEntityClaymore(), new ModelClaymore(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.securityCamera), new CustomModeledBlockRenderer(new TileEntitySecurityCamera(), new ModelSecurityCamera(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.alarm), new CustomModeledBlockRenderer(new TileEntityAlarm(), new ModelAlarm(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.ims), new CustomModeledBlockRenderer(new TileEntityIMS(), new ModelIMS(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.cameraMonitor, new ItemCameraMonitorRenderer());
		MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.taser, new ItemTaserRenderer());
	}
	
	public void registerClientTickHandler(){
		
	}
	

}
