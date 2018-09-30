package net.geforcemods.securitycraft.network;

import net.geforcemods.securitycraft.RegistrationHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
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
	public void registerVariants() {
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedPlanks"),
				"securitycraft:reinforcedPlanks_Oak",
				"securitycraft:reinforcedPlanks_Spruce",
				"securitycraft:reinforcedPlanks_Birch",
				"securitycraft:reinforcedPlanks_Jungle",
				"securitycraft:reinforcedPlanks_Acacia",
				"securitycraft:reinforcedPlanks_DarkOak");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStainedGlass"),
				"securitycraft:reinforcedStainedGlass_white",
				"securitycraft:reinforcedStainedGlass_orange",
				"securitycraft:reinforcedStainedGlass_magenta",
				"securitycraft:reinforcedStainedGlass_light_blue",
				"securitycraft:reinforcedStainedGlass_yellow",
				"securitycraft:reinforcedStainedGlass_lime",
				"securitycraft:reinforcedStainedGlass_pink",
				"securitycraft:reinforcedStainedGlass_gray",
				"securitycraft:reinforcedStainedGlass_silver",
				"securitycraft:reinforcedStainedGlass_cyan",
				"securitycraft:reinforcedStainedGlass_purple",
				"securitycraft:reinforcedStainedGlass_blue",
				"securitycraft:reinforcedStainedGlass_brown",
				"securitycraft:reinforcedStainedGlass_green",
				"securitycraft:reinforcedStainedGlass_red",
				"securitycraft:reinforcedStainedGlass_black");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID,"reinforcedStainedGlassPanes"),
				"securitycraft:reinforcedStainedGlassPanes_white",
				"securitycraft:reinforcedStainedGlassPanes_orange",
				"securitycraft:reinforcedStainedGlassPanes_magenta",
				"securitycraft:reinforcedStainedGlassPanes_light_blue",
				"securitycraft:reinforcedStainedGlassPanes_yellow",
				"securitycraft:reinforcedStainedGlassPanes_lime",
				"securitycraft:reinforcedStainedGlassPanes_pink",
				"securitycraft:reinforcedStainedGlassPanes_gray",
				"securitycraft:reinforcedStainedGlassPanes_silver",
				"securitycraft:reinforcedStainedGlassPanes_cyan",
				"securitycraft:reinforcedStainedGlassPanes_purple",
				"securitycraft:reinforcedStainedGlassPanes_blue",
				"securitycraft:reinforcedStainedGlassPanes_brown",
				"securitycraft:reinforcedStainedGlassPanes_green",
				"securitycraft:reinforcedStainedGlassPanes_red",
				"securitycraft:reinforcedStainedGlassPanes_black");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedSandstone"),
				"securitycraft:reinforcedSandstone_normal",
				"securitycraft:reinforcedSandstone_chiseled",
				"securitycraft:reinforcedSandstone_smooth");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedWoodSlabs"),
				"securitycraft:reinforcedWoodSlabs_oak",
				"securitycraft:reinforcedWoodSlabs_spruce",
				"securitycraft:reinforcedWoodSlabs_birch",
				"securitycraft:reinforcedWoodSlabs_jungle",
				"securitycraft:reinforcedWoodSlabs_acacia",
				"securitycraft:reinforcedWoodSlabs_darkoak");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStoneSlabs"),
				"securitycraft:reinforcedStoneSlabs_stone",
				"securitycraft:reinforcedStoneSlabs_cobblestone",
				"securitycraft:reinforcedStoneSlabs_sandstone",
				"securitycraft:reinforcedDirtSlab",
				"securitycraft:reinforcedStoneSlabs_stonebrick",
				"securitycraft:reinforcedStoneSlabs_brick",
				"securitycraft:reinforcedStoneSlabs_netherbrick",
				"securitycraft:reinforcedStoneSlabs_quartz");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStoneSlabs2"),
				"securitycraft:reinforcedStoneSlabs2_red_sandstone");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStoneBrick"),
				"securitycraft:reinforcedStoneBrick_default",
				"securitycraft:reinforcedStoneBrick_mossy",
				"securitycraft:reinforcedStoneBrick_cracked",
				"securitycraft:reinforcedStoneBrick_chiseled");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStainedHardenedClay"),
				"securitycraft:reinforcedStainedHardenedClay_white",
				"securitycraft:reinforcedStainedHardenedClay_orange",
				"securitycraft:reinforcedStainedHardenedClay_magenta",
				"securitycraft:reinforcedStainedHardenedClay_light_blue",
				"securitycraft:reinforcedStainedHardenedClay_yellow",
				"securitycraft:reinforcedStainedHardenedClay_lime",
				"securitycraft:reinforcedStainedHardenedClay_pink",
				"securitycraft:reinforcedStainedHardenedClay_gray",
				"securitycraft:reinforcedStainedHardenedClay_silver",
				"securitycraft:reinforcedStainedHardenedClay_cyan",
				"securitycraft:reinforcedStainedHardenedClay_purple",
				"securitycraft:reinforcedStainedHardenedClay_blue",
				"securitycraft:reinforcedStainedHardenedClay_brown",
				"securitycraft:reinforcedStainedHardenedClay_green",
				"securitycraft:reinforcedStainedHardenedClay_red",
				"securitycraft:reinforcedStainedHardenedClay_black");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedLogs"),
				"securitycraft:reinforcedLogs_oak",
				"securitycraft:reinforcedLogs_spruce",
				"securitycraft:reinforcedLogs_birch",
				"securitycraft:reinforcedLogs_jungle");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedLogs2"),
				"securitycraft:reinforcedLogs2_acacia",
				"securitycraft:reinforcedLogs2_big_oak");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedMetals"),
				"securitycraft:reinforcedMetals_gold",
				"securitycraft:reinforcedMetals_iron",
				"securitycraft:reinforcedMetals_diamond",
				"securitycraft:reinforcedMetals_emerald");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedCompressedBlocks"),
				"securitycraft:reinforcedCompressedBlocks_lapis",
				"securitycraft:reinforcedCompressedBlocks_coal");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedWool"),
				"securitycraft:reinforcedWool_white",
				"securitycraft:reinforcedWool_orange",
				"securitycraft:reinforcedWool_magenta",
				"securitycraft:reinforcedWool_light_blue",
				"securitycraft:reinforcedWool_yellow",
				"securitycraft:reinforcedWool_lime",
				"securitycraft:reinforcedWool_pink",
				"securitycraft:reinforcedWool_gray",
				"securitycraft:reinforcedWool_silver",
				"securitycraft:reinforcedWool_cyan",
				"securitycraft:reinforcedWool_purple",
				"securitycraft:reinforcedWool_blue",
				"securitycraft:reinforcedWool_brown",
				"securitycraft:reinforcedWool_green",
				"securitycraft:reinforcedWool_red",
				"securitycraft:reinforcedWool_black");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedQuartz"),
				"securitycraft:reinforcedQuartz_default",
				"securitycraft:reinforcedQuartz_chiseled",
				"securitycraft:reinforcedQuartz_pillar");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedPrismarine"),
				"securitycraft:reinforcedPrismarine_default",
				"securitycraft:reinforcedPrismarine_bricks",
				"securitycraft:reinforcedPrismarine_dark");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedRedSandstone"),
				"securitycraft:reinforcedRedSandstone_default",
				"securitycraft:reinforcedRedSandstone_chiseled",
				"securitycraft:reinforcedRedSandstone_smooth");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedStone"),
				"securitycraft:reinforcedStone_default",
				"securitycraft:reinforcedStone_granite",
				"securitycraft:reinforcedStone_smooth_granite",
				"securitycraft:reinforcedStone_diorite",
				"securitycraft:reinforcedStone_smooth_diorite",
				"securitycraft:reinforcedStone_andesite",
				"securitycraft:reinforcedStone_smooth_andesite");
		registerItemVariants(GameRegistry.findItem(SecurityCraft.MODID, "reinforcedCarpet"),
				"securitycraft:reinforcedCarpet_white",
				"securitycraft:reinforcedCarpet_orange",
				"securitycraft:reinforcedCarpet_magenta",
				"securitycraft:reinforcedCarpet_light_blue",
				"securitycraft:reinforcedCarpet_yellow",
				"securitycraft:reinforcedCarpet_lime",
				"securitycraft:reinforcedCarpet_pink",
				"securitycraft:reinforcedCarpet_gray",
				"securitycraft:reinforcedCarpet_silver",
				"securitycraft:reinforcedCarpet_cyan",
				"securitycraft:reinforcedCarpet_purple",
				"securitycraft:reinforcedCarpet_blue",
				"securitycraft:reinforcedCarpet_brown",
				"securitycraft:reinforcedCarpet_green",
				"securitycraft:reinforcedCarpet_red",
				"securitycraft:reinforcedCarpet_black");

		Item fakeWater = GameRegistry.findItem(SecurityCraft.MODID, "bogusWater");
		registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "water"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
			}
		});

		Item fakeWaterFlowing = GameRegistry.findItem(SecurityCraft.MODID, "bogusWaterFlowing");
		registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
			}
		});

		Item fakeLava = GameRegistry.findItem(SecurityCraft.MODID, "bogusLava");
		registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "lava"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
			}
		});

		Item fakeLavaFlowing = GameRegistry.findItem(SecurityCraft.MODID, "bogusLavaFlowing");
		registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
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
			locations[i] = new ResourceLocation(variants[i]);

		ModelBakery.<ResourceLocation>registerItemVariants(item, locations);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerResourceLocations() {
		RegistrationHandler.registerResourceLocations();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderThings(){
		KeyBindings.init();

		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, new RenderBouncingBetty(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, new RenderIMSBomb(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());

		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();
	}

}
