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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientProxy extends ServerProxy{

	/**
	 * Register the texture files used by blocks with metadata/variants with the ModelBakery.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerTextureFiles() {
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedPlanks"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Oak"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Spruce"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Birch"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Jungle"),
				new ResourceLocation("securitycraft:reinforcedPlanks_Acacia"),
				new ResourceLocation("securitycraft:reinforcedPlanks_DarkOak"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedStainedGlass"),
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
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedSandstone"),
				new ResourceLocation("securitycraft:reinforcedSandstone_normal"),
				new ResourceLocation("securitycraft:reinforcedSandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforcedSandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedWoodSlabs"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_oak"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_spruce"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_birch"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_jungle"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_acacia"),
				new ResourceLocation("securitycraft:reinforcedWoodSlabs_darkoak"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedStoneSlabs"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_stone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_cobblestone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_sandstone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_stonebrick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_brick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_netherbrick"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs_quartz"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedStoneSlabs2"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs2_red_sandstone"),
				new ResourceLocation("securitycraft:reinforcedStoneSlabs2_purpur"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedStoneBrick"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_default"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_mossy"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_cracked"),
				new ResourceLocation("securitycraft:reinforcedStoneBrick_chiseled"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedStainedHardenedClay"),
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
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedLogs"),
				new ResourceLocation("securitycraft:reinforcedLogs_oak"),
				new ResourceLocation("securitycraft:reinforcedLogs_spruce"),
				new ResourceLocation("securitycraft:reinforcedLogs_birch"),
				new ResourceLocation("securitycraft:reinforcedLogs_jungle"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedLogs2"),
				new ResourceLocation("securitycraft:reinforcedLogs2_acacia"),
				new ResourceLocation("securitycraft:reinforcedLogs2_big_oak"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedMetals"),
				new ResourceLocation("securitycraft:reinforcedMetals_gold"),
				new ResourceLocation("securitycraft:reinforcedMetals_iron"),
				new ResourceLocation("securitycraft:reinforcedMetals_diamond"),
				new ResourceLocation("securitycraft:reinforcedMetals_emerald"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedCompressedBlocks"),
				new ResourceLocation("securitycraft:reinforcedCompressedBlocks_lapis"),
				new ResourceLocation("securitycraft:reinforcedCompressedBlocks_coal"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedWool"),
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
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedQuartz"),
				new ResourceLocation("securitycraft:reinforcedQuartz_default"),
				new ResourceLocation("securitycraft:reinforcedQuartz_chiseled"),
				new ResourceLocation("securitycraft:reinforcedQuartz_pillar"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedPrismarine"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_default"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_bricks"),
				new ResourceLocation("securitycraft:reinforcedPrismarine_dark"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedRedSandstone"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_default"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforcedRedSandstone_smooth"));
		ModelBakery.registerItemVariants(findItem(mod_SecurityCraft.MODID, "reinforcedPurpur"),
				new ResourceLocation("securitycraft:reinforcedPurpur_default"),
				new ResourceLocation("securitycraft:reinforcedPurpur_pillar"));
		Item fakeWater = findItem(mod_SecurityCraft.MODID, "bogusWater");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(mod_SecurityCraft.MODID, "bogusWaterFlowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(mod_SecurityCraft.MODID, "bogusLava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(mod_SecurityCraft.MODID, "bogusLavaFlowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fakeLiquids", "lava_flowing");
			}
		});
	}

	private Item findItem(String modid, String resourceName)
	{
		return Item.REGISTRY.getObject(new ResourceLocation(modid + ":" + resourceName));
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

		Block[] blocksToTint = {
				mod_SecurityCraft.reinforcedBrick,
				mod_SecurityCraft.reinforcedCobblestone,
				mod_SecurityCraft.reinforcedCompressedBlocks,
				mod_SecurityCraft.reinforcedDirt,
				mod_SecurityCraft.reinforcedDoubleStoneSlabs,
				mod_SecurityCraft.reinforcedDoubleStoneSlabs2,
				mod_SecurityCraft.reinforcedDoubleWoodSlabs,
				mod_SecurityCraft.reinforcedEndStoneBricks,
				mod_SecurityCraft.reinforcedHardenedClay,
				mod_SecurityCraft.reinforcedMetals,
				mod_SecurityCraft.reinforcedMossyCobblestone,
				mod_SecurityCraft.reinforcedNetherBrick,
				mod_SecurityCraft.reinforcedNewLogs,
				mod_SecurityCraft.reinforcedOldLogs,
				mod_SecurityCraft.reinforcedPrismarine,
				mod_SecurityCraft.reinforcedPurpur,
				mod_SecurityCraft.reinforcedQuartz,
				mod_SecurityCraft.reinforcedRedNetherBrick,
				mod_SecurityCraft.reinforcedRedSandstone,
				mod_SecurityCraft.reinforcedSandstone,
				mod_SecurityCraft.reinforcedStainedHardenedClay,
				mod_SecurityCraft.reinforcedStairsAcacia,
				mod_SecurityCraft.reinforcedStairsBirch,
				mod_SecurityCraft.reinforcedStairsBrick,
				mod_SecurityCraft.reinforcedStairsCobblestone,
				mod_SecurityCraft.reinforcedStairsDarkoak,
				mod_SecurityCraft.reinforcedStairsJungle,
				mod_SecurityCraft.reinforcedStairsNetherBrick,
				mod_SecurityCraft.reinforcedStairsOak,
				mod_SecurityCraft.reinforcedStairsPurpur,
				mod_SecurityCraft.reinforcedStairsQuartz,
				mod_SecurityCraft.reinforcedStairsRedSandstone,
				mod_SecurityCraft.reinforcedStairsSandstone,
				mod_SecurityCraft.reinforcedStairsSpruce,
				mod_SecurityCraft.reinforcedStairsStone,
				mod_SecurityCraft.reinforcedStairsStoneBrick,
				mod_SecurityCraft.reinforcedStone,
				mod_SecurityCraft.reinforcedStoneBrick,
				mod_SecurityCraft.reinforcedStoneSlabs,
				mod_SecurityCraft.reinforcedStoneSlabs2,
				mod_SecurityCraft.reinforcedWoodPlanks,
				mod_SecurityCraft.reinforcedWoodSlabs,
				mod_SecurityCraft.reinforcedWool
			};
		//registering reinforced blocks color overlay for world
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(new IBlockColor() { //everyone who uses Java 7: fuck you and update to Java 8, this could be written so much nicer
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex)
			{
				return 0x999999;
			}
		}, blocksToTint);
		//same thing for inventory
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() { //everyone who uses Java 7: fuck you and update to Java 8, this could be written so much nicer
			@Override
			public int getColorFromItemstack(ItemStack stack, int tintIndex)
			{
				return 0x999999;
			}
		}, blocksToTint);
	}
}
