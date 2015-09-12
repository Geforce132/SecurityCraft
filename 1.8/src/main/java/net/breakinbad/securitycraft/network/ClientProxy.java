package net.breakinbad.securitycraft.network;

import net.breakinbad.securitycraft.entity.EntityIMSBomb;
import net.breakinbad.securitycraft.entity.EntityTnTCompact;
import net.breakinbad.securitycraft.main.mod_SecurityCraft;
import net.breakinbad.securitycraft.misc.KeyBindings;
import net.breakinbad.securitycraft.renderers.ItemKeypadChestRenderer;
import net.breakinbad.securitycraft.renderers.ItemTaserRenderer;
import net.breakinbad.securitycraft.renderers.RenderIMSBomb;
import net.breakinbad.securitycraft.renderers.RenderTnTCompact;
import net.breakinbad.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.breakinbad.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadChest;
import net.breakinbad.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends ServerProxy{
	
	/**
	 * Register the texture files used by blocks with metadata/variants with the ModelBakery.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerTextureFiles() {
		Item reinforcedWoodPlanks = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedPlanks");
        ModelBakery.addVariantName(reinforcedWoodPlanks, "securitycraft:reinforcedPlanks_Oak", "securitycraft:reinforcedPlanks_Spruce", "securitycraft:reinforcedPlanks_Birch", "securitycraft:reinforcedPlanks_Jungle", "securitycraft:reinforcedPlanks_Acacia", "securitycraft:reinforcedPlanks_DarkOak");
        
		Item reinforcedStainedGlass = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlass");
        ModelBakery.addVariantName(reinforcedStainedGlass, "securitycraft:reinforcedStainedGlass_white", "securitycraft:reinforcedStainedGlass_orange", "securitycraft:reinforcedStainedGlass_magenta", "securitycraft:reinforcedStainedGlass_light_blue", "securitycraft:reinforcedStainedGlass_yellow",
        	"securitycraft:reinforcedStainedGlass_lime", "securitycraft:reinforcedStainedGlass_pink", "securitycraft:reinforcedStainedGlass_gray", "securitycraft:reinforcedStainedGlass_silver", "securitycraft:reinforcedStainedGlass_cyan",
        	"securitycraft:reinforcedStainedGlass_purple", "securitycraft:reinforcedStainedGlass_blue", "securitycraft:reinforcedStainedGlass_brown", "securitycraft:reinforcedStainedGlass_green", "securitycraft:reinforcedStainedGlass_red", "securitycraft:reinforcedStainedGlass_black"
        );
        
        Item reinforcedStainedGlassPanes = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlassPanes");
		ModelBakery.addVariantName(reinforcedStainedGlassPanes, "securitycraft:reinforcedStainedGlassPanes_white", "securitycraft:reinforcedStainedGlassPanes_orange", "securitycraft:reinforcedStainedGlassPanes_magenta", "securitycraft:reinforcedStainedGlassPanes_light_blue", "securitycraft:reinforcedStainedGlassPanes_yellow",
	        "securitycraft:reinforcedStainedGlassPanes_lime", "securitycraft:reinforcedStainedGlassPanes_pink", "securitycraft:reinforcedStainedGlassPanes_gray", "securitycraft:reinforcedStainedGlassPanes_silver", "securitycraft:reinforcedStainedGlassPanes_cyan",
	        "securitycraft:reinforcedStainedGlassPanes_purple", "securitycraft:reinforcedStainedGlassPanes_blue", "securitycraft:reinforcedStainedGlassPanes_brown", "securitycraft:reinforcedStainedGlassPanes_green", "securitycraft:reinforcedStainedGlassPanes_red", "securitycraft:reinforcedStainedGlassPanes_black"
	    );
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setupTextureRegistry() {
		mod_SecurityCraft.configHandler.setupTextureRegistry();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderThings(){
		KeyBindings.init();
		
		RenderingRegistry.registerEntityRenderingHandler(EntityTnTCompact.class, new RenderTnTCompact(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, new RenderIMSBomb(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());

		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();
		//MinecraftForgeClient.registerItemRenderer(mod_SecurityCraft.taser, new ItemTaserRenderer());
	}

}
