package net.geforcemods.securitycraft.network;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.blockentities.BlockChangeDetectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.BlockPocketManagerBlockEntity;
import net.geforcemods.securitycraft.blockentities.CageTrapBlockEntity;
import net.geforcemods.securitycraft.blockentities.ClaymoreBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeycardReaderBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadChestBlockEntity;
import net.geforcemods.securitycraft.blockentities.KeypadFurnaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGrassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.misc.KeyBindings;
import net.geforcemods.securitycraft.models.BlockMineModel;
import net.geforcemods.securitycraft.renderers.BlockEntityItemRenderer;
import net.geforcemods.securitycraft.renderers.BlockPocketManagerRenderer;
import net.geforcemods.securitycraft.renderers.BouncingBettyRenderer;
import net.geforcemods.securitycraft.renderers.BulletRenderer;
import net.geforcemods.securitycraft.renderers.ClaymoreRenderer;
import net.geforcemods.securitycraft.renderers.DisguisableBlockEntityRenderer;
import net.geforcemods.securitycraft.renderers.DisplayCaseRenderer;
import net.geforcemods.securitycraft.renderers.IMSBombRenderer;
import net.geforcemods.securitycraft.renderers.KeypadChestRenderer;
import net.geforcemods.securitycraft.renderers.ProjectorRenderer;
import net.geforcemods.securitycraft.renderers.ReinforcedPistonRenderer;
import net.geforcemods.securitycraft.renderers.RetinalScannerRenderer;
import net.geforcemods.securitycraft.renderers.SecretSignRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.SonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemRenderer;
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
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class ClientProxy implements IProxy {
	private static Map<Block, Pair<IBlockColor, IItemColor>> toTint = new HashMap<>();

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
				"stone",
				"end_stone",
				"netherrack"
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

		event.getModelRegistry().putObject(mineMrl, new BlockMineModel(event.getModelRegistry().getObject(new ModelResourceLocation(realBlockRl, "inventory")), event.getModelRegistry().getObject(mineMrl)));
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
		ModelLoader.setCustomStateMapper(SCContent.reinforcedWalls, new StateMap.Builder().withName(ReinforcedWallBlock.VARIANT).withSuffix("_wall").build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedHopper, new StateMap.Builder().ignore(BlockHopper.ENABLED).build());
	}

	private Item findItem(String modid, String resourceName) {
		return Item.REGISTRY.getObject(new ResourceLocation(modid, resourceName));
	}

	@Override
	public void registerEntityRenderingHandlers() {
		RenderingRegistry.registerEntityRenderingHandler(BouncingBetty.class, BouncingBettyRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(IMSBomb.class, IMSBombRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(Sentry.class, SentryRenderer::new);
		RenderingRegistry.registerEntityRenderingHandler(Bullet.class, BulletRenderer::new);
	}

	@Override
	public void registerRenderThings() {
		KeyBindings.init();

		//normal tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadChestBlockEntity.class, new KeypadChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SecurityCameraBlockEntity.class, new SecurityCameraRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(RetinalScannerBlockEntity.class, new RetinalScannerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SecretSignBlockEntity.class, new SecretSignRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TrophySystemBlockEntity.class, new TrophySystemRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(BlockPocketManagerBlockEntity.class, new BlockPocketManagerRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ProjectorBlockEntity.class, new ProjectorRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ReinforcedPistonBlockEntity.class, new ReinforcedPistonRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(SonicSecuritySystemBlockEntity.class, new SonicSecuritySystemRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(DisplayCaseBlockEntity.class, new DisplayCaseRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(ClaymoreBlockEntity.class, new ClaymoreRenderer());
		//disguisable tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(BlockChangeDetectorBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(DisguisableBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(CageTrapBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(InventoryScannerBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeycardReaderBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadFurnaceBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(LaserBlockBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(ProtectoBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(UsernameLoggerBlockEntity.class, new DisguisableBlockEntityRenderer<>());

		Item.getItemFromBlock(SCContent.keypadChest).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new KeypadChestBlockEntity()));
		Item.getItemFromBlock(SCContent.displayCase).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new DisplayCaseBlockEntity()));
	}

	private static void initTint() {
		for (Field field : SCContent.class.getFields()) {
			if (field.isAnnotationPresent(Tinted.class)) {
				int tint = field.getAnnotation(Tinted.class).customTint();
				boolean hasReinforcedTint = field.getAnnotation(Tinted.class).hasReinforcedTint();

				try {
					Block block = (Block) field.get(null);

					if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
						//@formatter:off
						//registering reinforced blocks color overlay for world
						toTint.put(block, Pair.of(
							(state, world, pos, tintIndex) -> {
								if (tintIndex == 0)
									return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
								else
									return 0xFFFFFF;
							},
							//same thing for inventory
							(stack, tintIndex) -> {
								if (tintIndex == 0)
									return hasReinforcedTint ? mixWithReinforcedTintIfEnabled(tint) : tint;
								else
									return 0xFFFFFF;
							}
						));
						//@formatter:on
					}
				}
				catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SubscribeEvent
	public static void onColorHandlerBlock(ColorHandlerEvent.Block event) {
		initTint();

		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			toTint.forEach((block, pair) -> event.getBlockColors().registerBlockColorHandler(pair.getLeft(), block));
			event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> {
				if (tintIndex == 1 && !state.getValue(ReinforcedGrassBlock.SNOWY)) {
					int grassTint = world != null && pos != null ? BiomeColorHelper.getGrassColorAtPos(world, pos) : ColorizerGrass.getGrassColor(0.5D, 1.0D);

					return mixWithReinforcedTintIfEnabled(grassTint);
				}

				return ConfigHandler.reinforcedBlockTintColor;
			}, SCContent.reinforcedGrass);
			event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(world, pos) : -1, SCContent.fakeWater, SCContent.bogusWaterFlowing);
		}
	}

	@SubscribeEvent
	public static void onColorHandlerItem(ColorHandlerEvent.Item event) {
		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			toTint.forEach((block, pair) -> event.getItemColors().registerItemColorHandler(pair.getRight(), block));
			event.getItemColors().registerItemColorHandler((stack, tintIndex) -> {
				if (tintIndex == 1) {
					int grassTint = ColorizerGrass.getGrassColor(0.5D, 1.0D);

					return mixWithReinforcedTintIfEnabled(grassTint);
				}

				return ConfigHandler.reinforcedBlockTintColor;
			}, SCContent.reinforcedGrass);
			event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? ((BriefcaseItem) stack.getItem()).getColor(stack) : -1, SCContent.briefcase);
			toTint = null;
		}
	}

	private static int mixWithReinforcedTintIfEnabled(int tint1) {
		return ConfigHandler.reinforcedBlockTint ? mixTints(tint1, ConfigHandler.reinforcedBlockTintColor) : tint1;
	}

	private static int mixTints(int tint1, int tint2) {
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

	@Override
	public World getClientLevel() {
		return Minecraft.getMinecraft().world;
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getMinecraft().getRenderViewEntity() instanceof SecurityCamera;
	}
}
