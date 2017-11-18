package net.geforcemods.securitycraft.network;

import java.util.HashMap;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.imc.lookingglass.IWorldViewHelper;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.ModelAlarm;
import net.geforcemods.securitycraft.models.ModelClaymore;
import net.geforcemods.securitycraft.models.ModelFrame;
import net.geforcemods.securitycraft.models.ModelIMS;
import net.geforcemods.securitycraft.models.ModelKeypadFurnaceDeactivated;
import net.geforcemods.securitycraft.models.ModelProtecto;
import net.geforcemods.securitycraft.models.ModelSecurityCamera;
import net.geforcemods.securitycraft.renderers.CustomModeledBlockRenderer;
import net.geforcemods.securitycraft.renderers.ItemBriefcaseRenderer;
import net.geforcemods.securitycraft.renderers.ItemCameraMonitorRenderer;
import net.geforcemods.securitycraft.renderers.ItemTaserRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.TileEntityAlarmRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityClaymoreRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityFrameRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityIMSRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadFurnaceRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityProtectoRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.tileentity.TileEntityFrame;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends ServerProxy{

	public HashMap<String, IWorldViewHelper> worldViews = new HashMap<String, IWorldViewHelper>();

	@Override
	public void registerRenderThings(){
		KeyBindings.init();

		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, new RenderBouncingBetty());
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, new RenderIMSBomb());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFrame.class, new TileEntityFrameRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadFurnace.class, new TileEntityKeypadFurnaceRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityClaymore.class, new TileEntityClaymoreRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityAlarm.class, new TileEntityAlarmRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIMS.class, new TileEntityIMSRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProtecto.class, new TileEntityProtectoRenderer());

		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadChest), new CustomModeledBlockRenderer(new TileEntityKeypadChest()));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.frame), new CustomModeledBlockRenderer(new TileEntityFrame(), new ModelFrame(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.keypadFurnace), new CustomModeledBlockRenderer(new TileEntityKeypadFurnace(), new ModelKeypadFurnaceDeactivated(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.claymoreActive), new CustomModeledBlockRenderer(new TileEntityClaymore(), new ModelClaymore(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.securityCamera), new CustomModeledBlockRenderer(new TileEntitySecurityCamera(), new ModelSecurityCamera(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.alarm), new CustomModeledBlockRenderer(new TileEntityAlarm(), new ModelAlarm(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.ims), new CustomModeledBlockRenderer(new TileEntityIMS(), new ModelIMS(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(mod_SecurityCraft.protecto), new CustomModeledBlockRenderer(new TileEntityProtecto(), new ModelProtecto(), 0.0D, -0.1D, 0.0D, 0.0F));
		MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.cameraMonitor, new ItemCameraMonitorRenderer());
		MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.taser, new ItemTaserRenderer());
		MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.briefcase, new ItemBriefcaseRenderer());
	}

}
