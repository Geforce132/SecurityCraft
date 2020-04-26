package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedGrass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWall;
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
import net.geforcemods.securitycraft.renderers.TileEntityRetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityTrophySystemRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
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
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_planks"),
				new ResourceLocation("securitycraft:reinforced_planks_oak"),
				new ResourceLocation("securitycraft:reinforced_planks_spruce"),
				new ResourceLocation("securitycraft:reinforced_planks_birch"),
				new ResourceLocation("securitycraft:reinforced_planks_jungle"),
				new ResourceLocation("securitycraft:reinforced_planks_acacia"),
				new ResourceLocation("securitycraft:reinforced_planks_dark_oak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stained_glass"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_white"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_orange"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_magenta"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_light_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_yellow"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_lime"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_pink"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_gray"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_silver"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_cyan"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_purple"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_brown"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_green"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_red"),
				new ResourceLocation("securitycraft:reinforced_stained_glass_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_sandstone"),
				new ResourceLocation("securitycraft:reinforced_sandstone_normal"),
				new ResourceLocation("securitycraft:reinforced_sandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforced_sandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_wood_slabs"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_oak"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_spruce"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_birch"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_jungle"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_acacia"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_darkoak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stone_slabs"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_stone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_cobblestone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_sandstone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_stonebrick"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_brick"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_netherbrick"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_quartz"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stone_slabs2"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs2_red_sandstone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs2_purpur"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stone_brick"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_default"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_mossy"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_cracked"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_chiseled"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stained_hardened_clay"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_white"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_orange"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_magenta"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_light_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_yellow"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_lime"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_pink"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_gray"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_silver"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_cyan"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_purple"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_blue"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_brown"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_green"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_red"),
				new ResourceLocation("securitycraft:reinforced_stained_hardened_clay_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_logs"),
				new ResourceLocation("securitycraft:reinforced_logs_oak"),
				new ResourceLocation("securitycraft:reinforced_logs_spruce"),
				new ResourceLocation("securitycraft:reinforced_logs_birch"),
				new ResourceLocation("securitycraft:reinforced_logs_jungle"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_logs2"),
				new ResourceLocation("securitycraft:reinforced_logs2_acacia"),
				new ResourceLocation("securitycraft:reinforced_logs2_big_oak"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_metals"),
				new ResourceLocation("securitycraft:reinforced_metals_gold"),
				new ResourceLocation("securitycraft:reinforced_metals_iron"),
				new ResourceLocation("securitycraft:reinforced_metals_diamond"),
				new ResourceLocation("securitycraft:reinforced_metals_emerald"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_compressed_blocks"),
				new ResourceLocation("securitycraft:reinforced_compressed_blocks_lapis"),
				new ResourceLocation("securitycraft:reinforced_compressed_blocks_coal"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_wool"),
				new ResourceLocation("securitycraft:reinforced_wool_white"),
				new ResourceLocation("securitycraft:reinforced_wool_orange"),
				new ResourceLocation("securitycraft:reinforced_wool_magenta"),
				new ResourceLocation("securitycraft:reinforced_wool_light_blue"),
				new ResourceLocation("securitycraft:reinforced_wool_yellow"),
				new ResourceLocation("securitycraft:reinforced_wool_lime"),
				new ResourceLocation("securitycraft:reinforced_wool_pink"),
				new ResourceLocation("securitycraft:reinforced_wool_gray"),
				new ResourceLocation("securitycraft:reinforced_wool_silver"),
				new ResourceLocation("securitycraft:reinforced_wool_cyan"),
				new ResourceLocation("securitycraft:reinforced_wool_purple"),
				new ResourceLocation("securitycraft:reinforced_wool_blue"),
				new ResourceLocation("securitycraft:reinforced_wool_brown"),
				new ResourceLocation("securitycraft:reinforced_wool_green"),
				new ResourceLocation("securitycraft:reinforced_wool_red"),
				new ResourceLocation("securitycraft:reinforced_wool_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_quartz"),
				new ResourceLocation("securitycraft:reinforced_quartz_default"),
				new ResourceLocation("securitycraft:reinforced_quartz_chiseled"),
				new ResourceLocation("securitycraft:reinforced_quartz_pillar"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_prismarine"),
				new ResourceLocation("securitycraft:reinforced_prismarine_default"),
				new ResourceLocation("securitycraft:reinforced_prismarine_bricks"),
				new ResourceLocation("securitycraft:reinforced_prismarine_dark"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_red_sandstone"),
				new ResourceLocation("securitycraft:reinforced_red_sandstone_default"),
				new ResourceLocation("securitycraft:reinforced_red_sandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforced_red_sandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_purpur"),
				new ResourceLocation("securitycraft:reinforced_purpur_default"),
				new ResourceLocation("securitycraft:reinforced_purpur_pillar"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_concrete"),
				new ResourceLocation("securitycraft:reinforced_concrete_white"),
				new ResourceLocation("securitycraft:reinforced_concrete_orange"),
				new ResourceLocation("securitycraft:reinforced_concrete_magenta"),
				new ResourceLocation("securitycraft:reinforced_concrete_light_blue"),
				new ResourceLocation("securitycraft:reinforced_concrete_yellow"),
				new ResourceLocation("securitycraft:reinforced_concrete_lime"),
				new ResourceLocation("securitycraft:reinforced_concrete_pink"),
				new ResourceLocation("securitycraft:reinforced_concrete_gray"),
				new ResourceLocation("securitycraft:reinforced_concrete_silver"),
				new ResourceLocation("securitycraft:reinforced_concrete_cyan"),
				new ResourceLocation("securitycraft:reinforced_concrete_purple"),
				new ResourceLocation("securitycraft:reinforced_concrete_blue"),
				new ResourceLocation("securitycraft:reinforced_concrete_brown"),
				new ResourceLocation("securitycraft:reinforced_concrete_green"),
				new ResourceLocation("securitycraft:reinforced_concrete_red"),
				new ResourceLocation("securitycraft:reinforced_concrete_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_stone"),
				new ResourceLocation("securitycraft:reinforced_stone_default"),
				new ResourceLocation("securitycraft:reinforced_stone_granite"),
				new ResourceLocation("securitycraft:reinforced_stone_smooth_granite"),
				new ResourceLocation("securitycraft:reinforced_stone_diorite"),
				new ResourceLocation("securitycraft:reinforced_stone_smooth_diorite"),
				new ResourceLocation("securitycraft:reinforced_stone_andesite"),
				new ResourceLocation("securitycraft:reinforced_stone_smooth_andesite"));
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
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_carpet"),
				new ResourceLocation("securitycraft:reinforced_carpet_white"),
				new ResourceLocation("securitycraft:reinforced_carpet_orange"),
				new ResourceLocation("securitycraft:reinforced_carpet_magenta"),
				new ResourceLocation("securitycraft:reinforced_carpet_light_blue"),
				new ResourceLocation("securitycraft:reinforced_carpet_yellow"),
				new ResourceLocation("securitycraft:reinforced_carpet_lime"),
				new ResourceLocation("securitycraft:reinforced_carpet_pink"),
				new ResourceLocation("securitycraft:reinforced_carpet_gray"),
				new ResourceLocation("securitycraft:reinforced_carpet_silver"),
				new ResourceLocation("securitycraft:reinforced_carpet_cyan"),
				new ResourceLocation("securitycraft:reinforced_carpet_purple"),
				new ResourceLocation("securitycraft:reinforced_carpet_blue"),
				new ResourceLocation("securitycraft:reinforced_carpet_brown"),
				new ResourceLocation("securitycraft:reinforced_carpet_green"),
				new ResourceLocation("securitycraft:reinforced_carpet_red"),
				new ResourceLocation("securitycraft:reinforced_carpet_black"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_walls"),
				new ResourceLocation("securitycraft:reinforced_cobblestone_wall"),
				new ResourceLocation("securitycraft:reinforced_mossy_cobblestone_wall"));
		ModelBakery.registerItemVariants(findItem(SecurityCraft.MODID, "reinforced_dirt"),
				new ResourceLocation("securitycraft:reinforced_dirt"),
				new ResourceLocation("securitycraft:reinforced_coarse_dirt"),
				new ResourceLocation("securitycraft:reinforced_podzol"));

		Item fakeWater = findItem(SecurityCraft.MODID, "bogus_water");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water"));
		ModelLoader.setCustomStateMapper(SCContent.fakeWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(SecurityCraft.MODID, "bogus_water_flowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(SecurityCraft.MODID, "bogus_Lava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava"));
		ModelLoader.setCustomStateMapper(SCContent.fakeLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(SecurityCraft.MODID, "bogus_lava_flowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing");
			}
		});

		ModelLoader.setCustomStateMapper(SCContent.reinforcedStainedGlassPanes, new StateMap.Builder().withName(BlockColored.COLOR).withSuffix("_reinforced_stained_glass_panes").build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedWalls, new StateMap.Builder().withName(BlockReinforcedWall.VARIANT).withSuffix("_wall").build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedHopper, new StateMap.Builder().ignore(BlockHopper.ENABLED).build());
	}

	private Item findItem(String modid, String resourceName)
	{
		return Item.REGISTRY.getObject(new ResourceLocation(modid + ":" + resourceName));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerEntityRenderingHandlers()
	{
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, RenderBouncingBetty::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, RenderIMSBomb::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, RenderSentry::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, RenderBullet::new);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderThings(){
		KeyBindings.init();

		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRetinalScanner.class, new TileEntityRetinalScannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophySystem.class, new TileEntityTrophySystemRenderer());

		Item.getItemFromBlock(SCContent.keypadChest).setTileEntityItemStackRenderer(new ItemKeypadChestRenderer());

		for(Field field : SCContent.class.getFields())
		{
			if(field.isAnnotationPresent(Tinted.class))
			{
				int tint = field.getAnnotation(Tinted.class).value();
				int noTint = 0xFFFFFF;
				int crystalQuartzTint = 0x15B3A2;
				int reinforcedCrystalQuartzTint = 0x0E7063;

				try
				{
					Block block = (Block)field.get(null);

					//registering reinforced blocks color overlay for world
					Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
						if(state.getBlock() == SCContent.reinforcedGrass && !state.getValue(BlockReinforcedGrass.SNOWY))
						{
							if(tintIndex == 0)
								return ConfigHandler.reinforcedBlockTint ? tint : noTint;

							int grassTint = BiomeColorHelper.getGrassColorAtPos(world, pos);

							return ConfigHandler.reinforcedBlockTint ? mixTints(grassTint, tint) : grassTint;
						}
						else if(ConfigHandler.reinforcedBlockTint)
							return tint;
						else if(tint == reinforcedCrystalQuartzTint || tint == crystalQuartzTint)
							return crystalQuartzTint;
						else
							return noTint;
					}, block);
					//same thing for inventory
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
						if(block == SCContent.reinforcedGrass)
						{
							if(tintIndex == 0)
								return ConfigHandler.reinforcedBlockTint ? tint : noTint;

							int grassTint = ColorizerGrass.getGrassColor(0.5D, 1.0D);

							return ConfigHandler.reinforcedBlockTint ? mixTints(grassTint, tint) : grassTint;
						}
						else if(ConfigHandler.reinforcedBlockTint)
							return tint;
						else if(tint == reinforcedCrystalQuartzTint || tint == crystalQuartzTint)
							return crystalQuartzTint;
						else
							return noTint;
					}, block);
				}
				catch(IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
			}
		}

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(world, pos) : -1, SCContent.fakeWater, SCContent.bogusWaterFlowing);
	}

	private int mixTints(int tint1, int tint2)
	{
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float)(tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float)(tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float)(tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	@Override
	public EntityPlayer getClientPlayer()
	{
		return Minecraft.getMinecraft().player;
	}
}
