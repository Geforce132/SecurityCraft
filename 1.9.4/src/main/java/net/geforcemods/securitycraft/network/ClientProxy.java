package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.RegistrationHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy implements IProxy {

	/**
	 * Register the texture files used by blocks with metadata/variants with the ModelBakery.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerVariants() {
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedPlanks"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Oak"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Spruce"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Birch"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Jungle"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Acacia"),
				new ResourceLocation("securitycraft:reinforcedPlanks_DarkOak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStainedGlass"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_white"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_orange"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_magenta"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_light_blue"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_yellow"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_lime"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_pink"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_gray"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_silver"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_cyan"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_purple"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_blue"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_brown"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_green"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_red"),
				new ResourceLocation("securitycraft:reinforcedStainedGlass_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedSandstone"),
				new ResourceLocation("securitycraft:reinforcedSandstone_normal"),
				new ResourceLocation("securitycraft:reinforcedSandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforcedSandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedWoodSlabs"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_oak"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_spruce"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_birch"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_jungle"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_acacia"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_darkoak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStoneSlabs"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_stone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_cobblestone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_sandstone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_stonebrick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_brick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_netherbrick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_quartz"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStoneSlabs2"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs2_red_sandstone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs2_purpur"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStoneBrick"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_default"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_mossy"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_cracked"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_chiseled"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStainedHardenedClay"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_white"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_orange"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_magenta"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_light_blue"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_yellow"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_lime"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_pink"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_gray"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_silver"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_cyan"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_purple"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_blue"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_brown"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_green"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_red"),
				new ResourceLocation("securitycraft:reinforcedStainedHardenedClay_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedLogs"),
				new ResourceLocation("securitycraft:reinforcedLogs_oak"),
				new ResourceLocation("securitycraft:reinforcedLogs_spruce"),
				new ResourceLocation("securitycraft:reinforcedLogs_birch"),
				new ResourceLocation("securitycraft:reinforcedLogs_jungle"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedLogs2"),
				new ResourceLocation("securitycraft:reinforcedLogs2_acacia"),
				new ResourceLocation("securitycraft:reinforcedLogs2_big_oak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedMetals"),
				new ResourceLocation("securitycraft:reinforcedMetals_gold"),
				new ResourceLocation("securitycraft:reinforcedMetals_iron"),
				new ResourceLocation("securitycraft:reinforcedMetals_diamond"),
				new ResourceLocation("securitycraft:reinforcedMetals_emerald"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedCompressedBlocks"),
				new ResourceLocation("securitycraft:reinforcedCompressedBlocks_lapis"),
				new ResourceLocation("securitycraft:reinforcedCompressedBlocks_coal"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedWool"),
				new ResourceLocation("securitycraft:reinforcedWool_white"),
				new ResourceLocation("securitycraft:reinforcedWool_orange"),
				new ResourceLocation("securitycraft:reinforcedWool_magenta"),
				new ResourceLocation("securitycraft:reinforcedWool_light_blue"),
				new ResourceLocation("securitycraft:reinforcedWool_yellow"),
				new ResourceLocation("securitycraft:reinforcedWool_lime"),
				new ResourceLocation("securitycraft:reinforcedWool_pink"),
				new ResourceLocation("securitycraft:reinforcedWool_gray"),
				new ResourceLocation("securitycraft:reinforcedWool_silver"),
				new ResourceLocation("securitycraft:reinforcedWool_cyan"),
				new ResourceLocation("securitycraft:reinforcedWool_purple"),
				new ResourceLocation("securitycraft:reinforcedWool_blue"),
				new ResourceLocation("securitycraft:reinforcedWool_brown"),
				new ResourceLocation("securitycraft:reinforcedWool_green"),
				new ResourceLocation("securitycraft:reinforcedWool_red"),
				new ResourceLocation("securitycraft:reinforcedWool_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedQuartz"),
				new ResourceLocation("securitycraft:reinforcedQuartz_default"),
				new ResourceLocation("securitycraft:reinforcedQuartz_chiseled"),
				new ResourceLocation("securitycraft:reinforcedQuartz_pillar"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedPrismarine"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_default"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_bricks"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_dark"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedRedSandstone"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_default"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedPurpur"),
				new ResourceLocation("securitycraft:reinforcedPurpur_default"),
				new ResourceLocation("securitycraft:reinforcedPurpur_pillar"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedStone"),
				new ResourceLocation("securitycraft:reinforcedStone_default"),
				new ResourceLocation("securitycraft:reinforcedStone_granite"),
				new ResourceLocation("securitycraft:reinforcedStone_smooth_granite"),
				new ResourceLocation("securitycraft:reinforcedStone_diorite"),
				new ResourceLocation("securitycraft:reinforcedStone_smooth_diorite"),
				new ResourceLocation("securitycraft:reinforcedStone_andesite"),
				new ResourceLocation("securitycraft:reinforcedStone_smooth_andesite"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stained_panes"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_white"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_orange"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_magenta"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_light_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_yellow"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_lime"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_pink"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_gray"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_silver"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_cyan"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_purple"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_brown"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_green"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_red"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_panes_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforcedCarpet"),
				new ResourceLocation("securitycraft:reinforcedCarpet_white"),
				new ResourceLocation("securitycraft:reinforcedCarpet_orange"),
				new ResourceLocation("securitycraft:reinforcedCarpet_magenta"),
				new ResourceLocation("securitycraft:reinforcedCarpet_light_blue"),
				new ResourceLocation("securitycraft:reinforcedCarpet_yellow"),
				new ResourceLocation("securitycraft:reinforcedCarpet_lime"),
				new ResourceLocation("securitycraft:reinforcedCarpet_pink"),
				new ResourceLocation("securitycraft:reinforcedCarpet_gray"),
				new ResourceLocation("securitycraft:reinforcedCarpet_silver"),
				new ResourceLocation("securitycraft:reinforcedCarpet_cyan"),
				new ResourceLocation("securitycraft:reinforcedCarpet_purple"),
				new ResourceLocation("securitycraft:reinforcedCarpet_blue"),
				new ResourceLocation("securitycraft:reinforcedCarpet_brown"),
				new ResourceLocation("securitycraft:reinforcedCarpet_green"),
				new ResourceLocation("securitycraft:reinforcedCarpet_red"),
				new ResourceLocation("securitycraft:reinforcedCarpet_black"));

		Item fakeWater = findItem(SecurityCraft.MODID, "bogusWater");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "water"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(SecurityCraft.MODID, "bogusWaterFlowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(SecurityCraft.MODID, "bogusLava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "lava"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(SecurityCraft.MODID, "bogusLavaFlowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, stack -> new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing");
			}
		});

		ModelLoader.setCustomStateMapper(SCContent.reinforcedStainedGlassPanes, new StateMap.Builder().withName(BlockColored.COLOR).withSuffix("_reinforced_stained_glass_panes").build());
	}

	private Item findItem(String modid, String resourceName)
	{
		return Item.REGISTRY.getObject(new ResourceLocation(modid + ":" + resourceName));
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
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, new RenderSentry(Minecraft.getMinecraft().getRenderManager()));
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, new RenderBullet(Minecraft.getMinecraft().getRenderManager()));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());

		TileEntityItemStackRenderer.instance = new ItemKeypadChestRenderer();

		for(Field field : SCContent.class.getFields())
		{
			if(field.isAnnotationPresent(Tinted.class))
			{
				int tint = field.getAnnotation(Tinted.class).tint();

				try
				{
					//registering reinforced blocks color overlay for world
					Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> tint, (Block)field.get(null));
					//same thing for inventory
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler((IItemColor)(stack, tintIndex) -> tint, (Block)field.get(null));
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
}
