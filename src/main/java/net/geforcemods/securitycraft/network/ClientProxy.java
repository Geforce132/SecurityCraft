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
import net.geforcemods.securitycraft.blockentities.KeypadTrapdoorBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProjectorBlockEntity;
import net.geforcemods.securitycraft.blockentities.ProtectoBlockEntity;
import net.geforcemods.securitycraft.blockentities.ReinforcedPistonBlockEntity;
import net.geforcemods.securitycraft.blockentities.RetinalScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecretSignBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecureRedstoneInterfaceBlockEntity;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blockentities.SonicSecuritySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.blockentities.UsernameLoggerBlockEntity;
import net.geforcemods.securitycraft.blocks.InventoryScannerFieldBlock;
import net.geforcemods.securitycraft.blocks.LaserFieldBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedGrassBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.entity.BouncingBetty;
import net.geforcemods.securitycraft.entity.IMSBomb;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.entity.sentry.Bullet;
import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.items.ColorableItem;
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
import net.geforcemods.securitycraft.renderers.SecureRedstoneInterfaceRenderer;
import net.geforcemods.securitycraft.renderers.SecurityCameraRenderer;
import net.geforcemods.securitycraft.renderers.SentryRenderer;
import net.geforcemods.securitycraft.renderers.SonicSecuritySystemRenderer;
import net.geforcemods.securitycraft.renderers.TrophySystemRenderer;
import net.geforcemods.securitycraft.util.Tinted;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.world.ColorizerGrass;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
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

	@SubscribeEvent
	public static void onTextureStitch(TextureStitchEvent.Pre event) {
		event.getMap().registerSprite(new ResourceLocation(SecurityCraft.MODID, "particle/floor_trap_cloud"));
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
		ModelLoader.setCustomStateMapper(SCContent.reinforcedDispenser, new StateMap.Builder().ignore(BlockDispenser.TRIGGERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedDropper, new StateMap.Builder().ignore(BlockDispenser.TRIGGERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedOakFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedSpruceFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedBirchFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedJungleFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedDarkOakFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
		ModelLoader.setCustomStateMapper(SCContent.reinforcedAcaciaFenceGate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());
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
		ClientRegistry.bindTileEntitySpecialRenderer(SecureRedstoneInterfaceBlockEntity.class, new SecureRedstoneInterfaceRenderer());
		//disguisable tile entity renderers
		ClientRegistry.bindTileEntitySpecialRenderer(BlockChangeDetectorBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(DisguisableBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(CageTrapBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(InventoryScannerBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeycardReaderBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadFurnaceBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(KeypadTrapdoorBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(LaserBlockBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(ProtectoBlockEntity.class, new DisguisableBlockEntityRenderer<>());
		ClientRegistry.bindTileEntitySpecialRenderer(UsernameLoggerBlockEntity.class, new DisguisableBlockEntityRenderer<>());

		Item.getItemFromBlock(SCContent.keypadChest).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new KeypadChestBlockEntity()));
		Item.getItemFromBlock(SCContent.displayCase).setTileEntityItemStackRenderer(new BlockEntityItemRenderer(new DisplayCaseBlockEntity()));
	}

	private static void initTint() {
		if (toTint != null) { //apparently some mods post the color handler events again, after forge already posted them.
			for (Field field : SCContent.class.getFields()) {
				if (field.isAnnotationPresent(Tinted.class)) {
					int tint = field.getAnnotation(Tinted.class).customTint();
					boolean hasReinforcedTint = field.getAnnotation(Tinted.class).hasReinforcedTint();

					try {
						Block block = (Block) field.get(null);

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
					catch (IllegalArgumentException | IllegalAccessException e) {
						e.printStackTrace();
					}
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

				return mixWithReinforcedTintIfEnabled(0xFFFFFF);
			}, SCContent.reinforcedGrass);
			event.getBlockColors().registerBlockColorHandler((state, world, pos, tintIndex) -> world != null && pos != null ? BiomeColorHelper.getWaterColorAtPos(world, pos) : -1, SCContent.fakeWater, SCContent.bogusWaterFlowing);
			event.getBlockColors().registerBlockColorHandler((state, level, pos, tintIndex) -> {
				EnumFacing direction = LaserFieldBlock.getFieldDirection(state);
				MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

				for (int i = 0; i < ConfigHandler.laserBlockRange; i++) {
					try {
						if (level.getBlockState(mutablePos).getBlock() == SCContent.laserBlock) {
							TileEntity te = level.getTileEntity(mutablePos);

							if (te instanceof LaserBlockBlockEntity) {
								ItemStack stack = ((LaserBlockBlockEntity) te).getLensContainer().getStackInSlot(direction.getOpposite().ordinal());

								if (stack.getItem() instanceof ColorableItem)
									return ((ColorableItem) stack.getItem()).getColor(stack);

								break;
							}
						}
					}
					catch (Exception e) {}

					mutablePos.move(direction);
				}

				return -1;
			}, SCContent.laserField);
			event.getBlockColors().registerBlockColorHandler((state, level, pos, tintIndex) -> {
				EnumFacing direction = state.getValue(InventoryScannerFieldBlock.FACING);
				MutableBlockPos mutablePos = new MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

				for (int i = 0; i < ConfigHandler.inventoryScannerRange; i++) {
					if (level.getBlockState(mutablePos).getBlock() == SCContent.inventoryScanner) {
						TileEntity te = level.getTileEntity(mutablePos);

						if (te instanceof InventoryScannerBlockEntity) {
							ItemStack stack = ((InventoryScannerBlockEntity) te).getLensContainer().getStackInSlot(0);

							if (stack.getItem() instanceof ColorableItem)
								return ((ColorableItem) stack.getItem()).getColor(stack);

							break;
						}
					}

					mutablePos.move(direction);
				}

				return -1;
			}, SCContent.inventoryScannerField);
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
			event.getItemColors().registerItemColorHandler((stack, tintIndex) -> tintIndex == 0 ? ((ColorableItem) stack.getItem()).getColor(stack) : -1, SCContent.briefcase, SCContent.lens);
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
	public void addEffect(IParticleFactory factory, World level, double x, double y, double z) {
		Minecraft mc = Minecraft.getMinecraft();
		Entity entity = mc.getRenderViewEntity();
		World world = mc.world;

		if (entity != null && mc.effectRenderer != null) {
			int particleSetting = mc.gameSettings.particleSetting;

			if (particleSetting == 1 && world.rand.nextInt(3) == 0)
				particleSetting = 2;

			if (particleSetting > 1)
				return;

			double xDistance = entity.posX - x;
			double yDistance = entity.posY - y;
			double zDistance = entity.posZ - z;

			if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance <= 1024.0D)
				mc.effectRenderer.addEffect(factory.createParticle(0, world, x, y, z, 0.0D, 0.0D, 0.0D, 0));
		}
	}

	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}

	@Override
	public World getClientLevel() {
		return Minecraft.getMinecraft().world;
	}

	@Override
	public void updateBlockColorAroundPosition(BlockPos pos) {
		Minecraft.getMinecraft().renderGlobal.notifyBlockUpdate(Minecraft.getMinecraft().world, pos, null, null, 0);
	}

	public static boolean isPlayerMountedOnCamera() {
		return Minecraft.getMinecraft().getRenderViewEntity() instanceof SecurityCamera;
	}
}
