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
		Item reinforcedWoodPlanks = findItem(mod_SecurityCraft.MODID, "reinforced_planks");
		ModelBakery.registerItemVariants(reinforcedWoodPlanks,
				new ResourceLocation("securitycraft:reinforced_planks_oak"),
				new ResourceLocation("securitycraft:reinforced_planks_spruce"),
				new ResourceLocation("securitycraft:reinforced_planks_birch"),
				new ResourceLocation("securitycraft:reinforced_planks_jungle"),
				new ResourceLocation("securitycraft:reinforced_planks_acacia"),
				new ResourceLocation("securitycraft:reinforced_planks_dark_oak"));

		Item reinforcedStainedGlass = findItem(mod_SecurityCraft.MODID, "reinforced_stained_glass");
		ModelBakery.registerItemVariants(reinforcedStainedGlass,
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

		Item reinforcedSandstone = findItem(mod_SecurityCraft.MODID, "reinforced_sandstone");
		ModelBakery.registerItemVariants(reinforcedSandstone,
				new ResourceLocation("securitycraft:reinforced_Sandstone_normal"),
				new ResourceLocation("securitycraft:reinforced_sandstone_chiseled"),
				new ResourceLocation("securitycraft:reinforced_sandstone_smooth"));

		Item reinforcedWoodSlabs = findItem(mod_SecurityCraft.MODID, "reinforced_wood_slabs");
		ModelBakery.registerItemVariants(reinforcedWoodSlabs,
				new ResourceLocation("securitycraft:reinforced_wood_slabs_oak"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_spruce"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_birch"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_jungle"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_acacia"),
				new ResourceLocation("securitycraft:reinforced_wood_slabs_darkoak"));

		Item reinforcedStoneSlabs = findItem(mod_SecurityCraft.MODID, "reinforced_stone_slabs");
		ModelBakery.registerItemVariants(reinforcedStoneSlabs,
				new ResourceLocation("securitycraft:reinforced_stone_slabs_stone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_cobblestone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_sandstone"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_stonebrick"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_brick"),
				new ResourceLocation("securitycraft:reinforced_stone_slabs_netherbrick"));

		Item reinforcedStoneBrick = findItem(mod_SecurityCraft.MODID, "reinforced_stone_brick");
		ModelBakery.registerItemVariants(reinforcedStoneBrick,
				new ResourceLocation("securitycraft:reinforced_stone_brick_default"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_mossy"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_cracked"),
				new ResourceLocation("securitycraft:reinforced_stone_brick_chiseled"));

		Item reinforcedStainedHardenedClay = findItem(mod_SecurityCraft.MODID, "reinforced_stained_hardened_clay");
		ModelBakery.registerItemVariants(reinforcedStainedHardenedClay,
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

		Item fakeWater = findItem(mod_SecurityCraft.MODID, "bogus_water");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWater, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(mod_SecurityCraft.MODID, "bogus_water_flowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusWaterFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(mod_SecurityCraft.MODID, "bogus_Lava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLava, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(mod_SecurityCraft.MODID, "bogus_lava_flowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, new ItemMeshDefinition()
		{
			@Override
			public ModelResourceLocation getModelLocation(ItemStack stack)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing");
			}
		});
		ModelLoader.setCustomStateMapper(mod_SecurityCraft.bogusLavaFlowing, new StateMapperBase()
		{
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state)
			{
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing");
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
				mod_SecurityCraft.reinforcedDirt,
				mod_SecurityCraft.reinforcedDoubleStoneSlabs,
				mod_SecurityCraft.reinforcedDoubleWoodSlabs,
				mod_SecurityCraft.reinforcedHardenedClay,
				mod_SecurityCraft.reinforcedMossyCobblestone,
				mod_SecurityCraft.reinforcedNetherBrick,
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
				mod_SecurityCraft.reinforcedStairsSandstone,
				mod_SecurityCraft.reinforcedStairsSpruce,
				mod_SecurityCraft.reinforcedStairsStone,
				mod_SecurityCraft.reinforcedStairsStoneBrick,
				mod_SecurityCraft.reinforcedStone,
				mod_SecurityCraft.reinforcedStoneBrick,
				mod_SecurityCraft.reinforcedStoneSlabs,
				mod_SecurityCraft.reinforcedWoodPlanks,
				mod_SecurityCraft.reinforcedWoodSlabs
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
