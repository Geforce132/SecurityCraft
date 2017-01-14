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
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
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
        ModelBakery.registerItemVariants(reinforcedWoodPlanks, new ResourceLocation("securitycraft:reinforcedPlanks_Oak"), new ResourceLocation("securitycraft:reinforcedPlanks_Spruce"), new ResourceLocation("securitycraft:reinforcedPlanks_Birch"), new ResourceLocation("securitycraft:reinforcedPlanks_Jungle"), new ResourceLocation("securitycraft:reinforcedPlanks_Acacia"), new ResourceLocation("securitycraft:reinforcedPlanks_DarkOak"));
        
		Item reinforcedStainedGlass = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlass");
        ModelBakery.registerItemVariants(reinforcedStainedGlass, new ResourceLocation("securitycraft:reinforcedStainedGlass_white"), new ResourceLocation("securitycraft:reinforcedStainedGlass_orange"), new ResourceLocation("securitycraft:reinforcedStainedGlass_magenta"), new ResourceLocation("securitycraft:reinforcedStainedGlass_light_blue"), new ResourceLocation("securitycraft:reinforcedStainedGlass_yellow"),
        	new ResourceLocation("securitycraft:reinforcedStainedGlass_lime"), new ResourceLocation("securitycraft:reinforcedStainedGlass_pink"), new ResourceLocation("securitycraft:reinforcedStainedGlass_gray"), new ResourceLocation("securitycraft:reinforcedStainedGlass_silver"), new ResourceLocation("securitycraft:reinforcedStainedGlass_cyan"),
        	new ResourceLocation("securitycraft:reinforcedStainedGlass_purple"), new ResourceLocation("securitycraft:reinforcedStainedGlass_blue"), new ResourceLocation("securitycraft:reinforcedStainedGlass_brown"), new ResourceLocation("securitycraft:reinforcedStainedGlass_green"), new ResourceLocation("securitycraft:reinforcedStainedGlass_red"), new ResourceLocation("securitycraft:reinforcedStainedGlass_black"));
        
        Item reinforcedStainedGlassPanes = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlassPanes");
		ModelBakery.registerItemVariants(reinforcedStainedGlassPanes, new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_white"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_orange"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_magenta"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_light_blue"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_yellow"),
	        new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_lime"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_pink"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_gray"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_silver"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_cyan"),
	        new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_purple"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_blue"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_brown"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_green"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_red"), new ResourceLocation("securitycraft:reinforcedStainedGlassPanes_black"));
		
		Item reinforcedSandstone = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedSandstone");
		ModelBakery.registerItemVariants(reinforcedSandstone, new ResourceLocation("securitycraft:reinforcedSandstone_normal"), new ResourceLocation("securitycraft:reinforcedSandstone_chiseled"), new ResourceLocation("securitycraft:reinforcedSandstone_smooth"));
		
		Item reinforcedWoodSlabs = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedWoodSlabs");
		ModelBakery.registerItemVariants(reinforcedWoodSlabs, new ResourceLocation("securitycraft:reinforcedWoodSlabs_oak"), new ResourceLocation("securitycraft:reinforcedWoodSlabs_spruce"), new ResourceLocation("securitycraft:reinforcedWoodSlabs_birch"), new ResourceLocation("securitycraft:reinforcedWoodSlabs_jungle"), new ResourceLocation("securitycraft:reinforcedWoodSlabs_acacia"),
			new ResourceLocation("securitycraft:reinforcedWoodSlabs_darkoak"));
		
		Item reinforcedStoneSlabs = GameRegistry.findItem(mod_SecurityCraft.MODID, "reinforcedStoneSlabs");
		ModelBakery.registerItemVariants(reinforcedStoneSlabs, new ResourceLocation("securitycraft:reinforcedStoneSlabs_stone"), new ResourceLocation("securitycraft:reinforcedStoneSlabs_cobblestone"), new ResourceLocation("securitycraft:reinforcedStoneSlabs_sandstone"), new ResourceLocation("securitycraft:reinforcedDirtSlab"));
		
		Item fakeWater = GameRegistry.findItem(mod_SecurityCraft.MODID, "bogusWater");
		ModelBakery.registerItemVariants(fakeWater);
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
		ModelBakery.registerItemVariants(fakeWaterFlowing);
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
		ModelBakery.registerItemVariants(fakeLava);
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
		ModelBakery.registerItemVariants(fakeLavaFlowing);
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
