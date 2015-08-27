package net.breakinbad.securitycraft.network;

import java.util.HashMap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.breakinbad.securitycraft.entity.EntityIMSBomb;
import net.breakinbad.securitycraft.entity.EntityTnTCompact;
import net.breakinbad.securitycraft.imc.lookingglass.IWorldViewHelper;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.KeyBindings;
import net.breakinbad.securitycraft.models.ModelAlarm;
import net.breakinbad.securitycraft.models.ModelClaymore;
import net.breakinbad.securitycraft.models.ModelFrame;
import net.breakinbad.securitycraft.models.ModelIMS;
import net.breakinbad.securitycraft.models.ModelKeypadFurnaceDeactivated;
import net.breakinbad.securitycraft.models.ModelSecurityCamera;
import net.breakinbad.securitycraft.renderers.CustomModeledBlockRenderer;
import net.breakinbad.securitycraft.renderers.ItemCameraMonitorRenderer;
import net.breakinbad.securitycraft.renderers.ItemTaserRenderer;
import net.breakinbad.securitycraft.renderers.RenderIMSBomb;
import net.breakinbad.securitycraft.renderers.RenderTnTCompact;
import net.breakinbad.securitycraft.renderers.TileEntityAlarmRenderer;
import net.breakinbad.securitycraft.renderers.TileEntityClaymoreRenderer;
import net.breakinbad.securitycraft.renderers.TileEntityFrameRenderer;
import net.breakinbad.securitycraft.renderers.TileEntityIMSRenderer;
import net.breakinbad.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.breakinbad.securitycraft.renderers.TileEntityKeypadFurnaceRenderer;
import net.breakinbad.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.breakinbad.securitycraft.tileentity.TileEntityAlarm;
import net.breakinbad.securitycraft.tileentity.TileEntityClaymore;
import net.breakinbad.securitycraft.tileentity.TileEntityFrame;
import net.breakinbad.securitycraft.tileentity.TileEntityIMS;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadChest;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.breakinbad.securitycraft.tileentity.TileEntitySecurityCamera;
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
