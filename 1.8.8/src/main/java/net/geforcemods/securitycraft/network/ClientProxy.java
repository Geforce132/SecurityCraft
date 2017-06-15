package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
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
        registerItemVariants(reinforcedWoodPlanks, "securitycraft:reinforcedPlanks_Oak", "securitycraft:reinforcedPlanks_Spruce", "securitycraft:reinforcedPlanks_Birch", "securitycraft:reinforcedPlanks_Jungle", "securitycraft:reinforcedPlanks_Acacia", "securitycraft:reinforcedPlanks_DarkOak");
        
		Item reinforcedStainedGlass = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlass");
        registerItemVariants(reinforcedStainedGlass, "securitycraft:reinforcedStainedGlass_white", "securitycraft:reinforcedStainedGlass_orange", "securitycraft:reinforcedStainedGlass_magenta", "securitycraft:reinforcedStainedGlass_light_blue", "securitycraft:reinforcedStainedGlass_yellow",
        	"securitycraft:reinforcedStainedGlass_lime", "securitycraft:reinforcedStainedGlass_pink", "securitycraft:reinforcedStainedGlass_gray", "securitycraft:reinforcedStainedGlass_silver", "securitycraft:reinforcedStainedGlass_cyan",
        	"securitycraft:reinforcedStainedGlass_purple", "securitycraft:reinforcedStainedGlass_blue", "securitycraft:reinforcedStainedGlass_brown", "securitycraft:reinforcedStainedGlass_green", "securitycraft:reinforcedStainedGlass_red", "securitycraft:reinforcedStainedGlass_black"
        );
        
        Item reinforcedStainedGlassPanes = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlassPanes");
		registerItemVariants(reinforcedStainedGlassPanes, "securitycraft:reinforcedStainedGlassPanes_white", "securitycraft:reinforcedStainedGlassPanes_orange", "securitycraft:reinforcedStainedGlassPanes_magenta", "securitycraft:reinforcedStainedGlassPanes_light_blue", "securitycraft:reinforcedStainedGlassPanes_yellow",
	        "securitycraft:reinforcedStainedGlassPanes_lime", "securitycraft:reinforcedStainedGlassPanes_pink", "securitycraft:reinforcedStainedGlassPanes_gray", "securitycraft:reinforcedStainedGlassPanes_silver", "securitycraft:reinforcedStainedGlassPanes_cyan",
	        "securitycraft:reinforcedStainedGlassPanes_purple", "securitycraft:reinforcedStainedGlassPanes_blue", "securitycraft:reinforcedStainedGlassPanes_brown", "securitycraft:reinforcedStainedGlassPanes_green", "securitycraft:reinforcedStainedGlassPanes_red", "securitycraft:reinforcedStainedGlassPanes_black"
	    );
		
		Item reinforcedSandstone = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedSandstone");
		registerItemVariants(reinforcedSandstone, "securitycraft:reinforcedSandstone_normal", "securitycraft:reinforcedSandstone_chiseled", "securitycraft:reinforcedSandstone_smooth");
		
		Item reinforcedWoodSlabs = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedWoodSlabs");
		registerItemVariants(reinforcedWoodSlabs, "securitycraft:reinforcedWoodSlabs_oak", "securitycraft:reinforcedWoodSlabs_spruce", "securitycraft:reinforcedWoodSlabs_birch", "securitycraft:reinforcedWoodSlabs_jungle", "securitycraft:reinforcedWoodSlabs_acacia",
			"securitycraft:reinforcedWoodSlabs_darkoak"
		);
		
		Item reinforcedStoneSlabs = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStoneSlabs");
		registerItemVariants(reinforcedStoneSlabs, "securitycraft:reinforcedStoneSlabs_stone", "securitycraft:reinforcedStoneSlabs_cobblestone", "securitycraft:reinforcedStoneSlabs_sandstone", "securitycraft:reinforcedDirtSlab");
		
		Item fakeWater = GameRegistry.findItem(mod_SecurityCraft.MODID, "bogusWater");
		registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
            }
        });
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWater, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
            }
        });
		
		Item fakeWaterFlowing = GameRegistry.findItem(mod_SecurityCraft.MODID, "bogusWaterFlowing");
		registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
            }
        });
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWaterFlowing, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
            }
        });
		
		Item fakeLava = GameRegistry.findItem(mod_SecurityCraft.MODID, "bogusLava");
		registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
            }
        });
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLava, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
            }
        });
		
		Item fakeLavaFlowing = GameRegistry.findItem(mod_SecurityCraft.MODID, "bogusLavaFlowing");
		registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, new ItemMeshDefinition()
        {
            public ModelResourceLocation getModelLocation(ItemStack stack)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing");
            }
        });
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLavaFlowing, new StateMapperBase()
        {
            protected ModelResourceLocation getModelResourceLocation(IBlockState state)
            {
                return new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing");
            }
        });
	}
	
	private void registerItemVariants(Item item, String... variants)
	{
		ResourceLocation[] locations = new ResourceLocation[variants.length];
		
		for(int i = 0; i < variants.length; i++)
		{
			locations[i] = new ResourceLocation(variants[i]);
		}
		
		ModelBakery.<ResourceLocation>registerItemVariants(item, locations);
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
		
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, new RenderBouncingBetty(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, new RenderIMSBomb(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		
		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();
	}

}
