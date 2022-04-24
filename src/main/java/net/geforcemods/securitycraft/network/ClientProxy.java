package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedGrass;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWall;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityBullet;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.items.ItemBriefcase;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.ModelBlockMine;
import net.geforcemods.securitycraft.renderers.ItemKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.RenderBouncingBetty;
import net.geforcemods.securitycraft.renderers.RenderBullet;
import net.geforcemods.securitycraft.renderers.RenderIMSBomb;
import net.geforcemods.securitycraft.renderers.RenderSentry;
import net.geforcemods.securitycraft.renderers.TileEntityBlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityDisguisableRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityKeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityProjectorRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityReinforcedPistonRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityRetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecretSignRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.TileEntitySonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TileEntityTrophySystemRenderer;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockChangeDetector;
import net.geforcemods.securitycraft.tileentity.TileEntityBlockPocketManager;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityProjector;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityReinforcedPiston;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntitySecretSign;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.geforcemods.securitycraft.tileentity.TileEntitySonicSecuritySystem;
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
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {
	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		//@formatter:off
		String[] mines = {
				"coal_ore",
				"cobblestone",
				"dirt",
				"emerald_ore",
				"gravel",
				"gold_ore",
				"furnace",
				"iron_ore",
				"lapis_ore",
				"redstone_ore",
				"sand",
				"stone"
		};
		//@formatter:on

		for (String mine : mines) {
			registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, mine + "_mine"), new ResourceLocation(mine));
		}

		registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, "diamond_mine"), new ResourceLocation("diamond_ore"));
		registerBlockMineModel(event, new ResourceLocation(SecurityCraft.MODID, "quartz_mine"), new ResourceLocation("nether_quartz_ore"));
	}

	private static void registerBlockMineModel(ModelBakeEvent event, ResourceLocation mineRl, ResourceLocation realBlockRl) {
		ModelResourceLocation mineMrl = new ModelResourceLocation(mineRl, "inventory");

		event.getModelRegistry().putObject(mineMrl, new ModelBlockMine(event.getModelRegistry().getObject(new ModelResourceLocation(realBlockRl, "inventory")), event.getModelRegistry().getObject(mineMrl)));
	}

	@Override
	public void registerVariants() {
		Item fakeWater = findItem(SecurityCraft.MODID, "bogus_water");
		ModelBakery.registerItemVariants(fakeWater);
		ModelLoader.setCustomMeshDefinition(fakeWater, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water"));
		ModelLoader.setCustomStateMapper(SCContent.fakeWater, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("securitycraft:fake_liquids", "water");
			}
		});

		Item fakeWaterFlowing = findItem(SecurityCraft.MODID, "bogus_water_flowing");
		ModelBakery.registerItemVariants(fakeWaterFlowing);
		ModelLoader.setCustomMeshDefinition(fakeWaterFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusWaterFlowing, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("securitycraft:fake_liquids", "water_flowing");
			}
		});

		Item fakeLava = findItem(SecurityCraft.MODID, "bogus_Lava");
		ModelBakery.registerItemVariants(fakeLava);
		ModelLoader.setCustomMeshDefinition(fakeLava, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava"));
		ModelLoader.setCustomStateMapper(SCContent.fakeLava, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava");
			}
		});

		Item fakeLavaFlowing = findItem(SecurityCraft.MODID, "bogus_lava_flowing");
		ModelBakery.registerItemVariants(fakeLavaFlowing);
		ModelLoader.setCustomMeshDefinition(fakeLavaFlowing, stack -> new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing"));
		ModelLoader.setCustomStateMapper(SCContent.bogusLavaFlowing, new StateMapperBase() {
			@Override
			protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
				return new ModelResourceLocation("securitycraft:fake_liquids", "lava_flowing");
			}
		});

		ModelLoader.setCustomStateMapper(SCContent.reinforcedStainedGlassPanes, new StateMap.Builder().withName(BlockColored.COLOR).withSuffix("_reinforced_stained_glass_panes").build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedWalls, new StateMap.Builder().withName(BlockReinforcedWall.VARIANT).withSuffix("_wall").build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedHopper, new StateMap.Builder().ignore(BlockHopper.ENABLED).build());
	}

	private Item findItem(String modid, String resourceName) {
		return Item.REGISTRY.getObject(new ResourceLocation(modid, resourceName));
	}

	@Override
	public void registerEntityRenderingHandlers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityBouncingBetty.class, RenderBouncingBetty::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityIMSBomb.class, RenderIMSBomb::new);
		RenderingRegistry.registerEntityRenderingHandler(EntitySentry.class, RenderSentry::new);
		RenderingRegistry.registerEntityRenderingHandler(EntityBullet.class, RenderBullet::new);
	}

	@Override
	public void registerRenderThings() {
		Map<Block, IBlockColor> specialBlockTint = new HashMap<>();
		Map<Item, IItemColor> specialItemTint = new HashMap<>();
		int noTint = 0xFFFFFF;

		KeyBindings.init();

		//normal tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadChest.class, new TileEntityKeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecurityCamera.class, new TileEntitySecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityRetinalScanner.class, new TileEntityRetinalScannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySecretSign.class, new TileEntitySecretSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTrophySystem.class, new TileEntityTrophySystemRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockPocketManager.class, new TileEntityBlockPocketManagerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProjector.class, new TileEntityProjectorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReinforcedPiston.class, new TileEntityReinforcedPistonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntitySonicSecuritySystem.class, new TileEntitySonicSecuritySystemRenderer());
		//disguisable tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityBlockChangeDetector.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityDisguisable.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCageTrap.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityInventoryScanner.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeycardReader.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypad.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityKeypadFurnace.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLaserBlock.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityProtecto.class, new TileEntityDisguisableRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityLogger.class, new TileEntityDisguisableRenderer<>());

		Item.getItemFromBlock(SCContent.keypadChest).setTileEntityItemStackRenderer(new ItemKeypadChestRenderer());

		specialBlockTint.put(SCContent.reinforcedGrass, (state, world, pos, tintIndex) -> {
			if (tintIndex == 1 && !state.getValue(BlockReinforcedGrass.SNOWY)) {
				int grassTint = world != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(world, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});

		specialItemTint.put(Item.getItemFromBlock(SCContent.reinforcedGrass), (stack, tintIndex) -> {
			if (tintIndex == 1) {
				int grassTint = ColorizerGrass.getGrassColor(0.5D, 1.0D);

				return mixWithReinforcedTintIfEnabled(grassTint);
			}

			return noTint;
		});

		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(Tinted.class)) {
				int tint = field.getAnnotation(Tinted.class).customTint();
				boolean hasReinforcedTint = field.getAnnotation(Tinted.class).hasReinforcedTint();

				try {
					Block block = (Block) field.get(null);

					//registering reinforced blocks color overlay for world
					Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
						if (tintIndex == 0)
							return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
						else if (specialBlockTint.containsKey(block))
							return specialBlockTint.get(block).colorMultiplier(state, world, pos, tintIndex);
						else
							return noTint;
					}, block);
					//same thing for inventory
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> {
						if (tintIndex == 0)
							return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
						else if (specialItemTint.containsKey(stack.getItem()))
							return specialItemTint.get(stack.getItem()).colorMultiplier(stack, tintIndex);
						else
							return noTint;
					}, block);
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}

		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(world, pos) : -1, SCContent.fakeWater, SCContent.bogusWaterFlowing);
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? ((ItemBriefcase) stack.getItem()).getColor(stack) : -1, SCContent.briefcase);
	}

	private int mixWithReinforcedTintIfEnabled(int tint1) {
		return ConfigHandler.reinforcedBlockTint ? mixTints(tint1, 0x999999) : tint1;
	}

	private int mixTints(int tint1, int tint2) {
		int red = (tint1 >> 0x10) & 0xFF;
		int green = (tint1 >> 0x8) & 0xFF;
		int blue = tint1 & 0xFF;

		red *= (float) (tint2 >> 0x10 & 0xFF) / 0xFF;
		green *= (float) (tint2 >> 0x8 & 0xFF) / 0xFF;
		blue *= (float) (tint2 & 0xFF) / 0xFF;

		return ((red << 8) + green << 8) + blue;
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getMinecraft().getRenderViewEntity() instanceof EntitySecurityCamera;
	}
}
