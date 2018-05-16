package net.geforcemods.securitycraft;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.entity.EntityBouncingBetty;
import net.geforcemods.securitycraft.entity.EntityIMSBomb;
import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.geforcemods.securitycraft.entity.EntityTaserBullet;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedCompressedBlocks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedLog;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedMetals;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPlanks;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedPrismarine;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedQuartz;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSandstone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedSlabs2;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedBlock;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedGlass;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStainedGlassPanes;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStone;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedStoneBrick;
import net.geforcemods.securitycraft.itemblocks.ItemBlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.itemblocks.ItemBlockTinted;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.geforcemods.securitycraft.network.packets.PacketCChangeStackSize;
import net.geforcemods.securitycraft.network.packets.PacketCPlaySoundAtPos;
import net.geforcemods.securitycraft.network.packets.PacketCSetPlayerPositionAndRotation;
import net.geforcemods.securitycraft.network.packets.PacketCUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketGivePotionEffect;
import net.geforcemods.securitycraft.network.packets.PacketSAddModules;
import net.geforcemods.securitycraft.network.packets.PacketSCheckPassword;
import net.geforcemods.securitycraft.network.packets.PacketSMountCamera;
import net.geforcemods.securitycraft.network.packets.PacketSOpenGui;
import net.geforcemods.securitycraft.network.packets.PacketSRemoveCameraTag;
import net.geforcemods.securitycraft.network.packets.PacketSSetCameraRotation;
import net.geforcemods.securitycraft.network.packets.PacketSSetOwner;
import net.geforcemods.securitycraft.network.packets.PacketSSetPassword;
import net.geforcemods.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSToggleOption;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateNBTTag;
import net.geforcemods.securitycraft.network.packets.PacketSUpdateSliderValue;
import net.geforcemods.securitycraft.network.packets.PacketSetBlock;
import net.geforcemods.securitycraft.network.packets.PacketSetExplosiveState;
import net.geforcemods.securitycraft.network.packets.PacketSetISType;
import net.geforcemods.securitycraft.network.packets.PacketSetKeycardLevel;
import net.geforcemods.securitycraft.network.packets.PacketUpdateLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityAlarm;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityClaymore;
import net.geforcemods.securitycraft.tileentity.TileEntityIMS;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypad;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityLaserBlock;
import net.geforcemods.securitycraft.tileentity.TileEntityLogger;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityPortableRadar;
import net.geforcemods.securitycraft.tileentity.TileEntityProtecto;
import net.geforcemods.securitycraft.tileentity.TileEntityRetinalScanner;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.geforcemods.securitycraft.tileentity.TileEntitySecurityCamera;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RegistrationHandler
{
	private static final int[] HARMING_POTIONS = {8268, 8236, 16460, 16428};
	private static final int[] HEALING_POTIONS = {8261, 8229, 16453, 16421};

	public static void registerContent()
	{
		registerBlock(SCContent.laserBlock);
		GameRegistry.registerBlock(SCContent.laserField, SCContent.laserField.getUnlocalizedName().substring(5));
		registerBlock(SCContent.keypad);
		registerBlock(SCContent.mine);
		GameRegistry.registerBlock(SCContent.mineCut,SCContent.mineCut.getUnlocalizedName().substring(5));
		registerBlock(SCContent.dirtMine);
		GameRegistry.registerBlock(SCContent.stoneMine, SCContent.stoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.cobblestoneMine, SCContent.cobblestoneMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.diamondOreMine, SCContent.diamondOreMine.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.sandMine, SCContent.sandMine.getUnlocalizedName().substring(5));
		registerBlock(SCContent.furnaceMine);
		registerBlock(SCContent.retinalScanner);
		GameRegistry.registerBlock(SCContent.reinforcedDoor, SCContent.reinforcedDoor.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusLava, SCContent.bogusLava.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusLavaFlowing, SCContent.bogusLavaFlowing.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusWater, SCContent.bogusWater.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.bogusWaterFlowing, SCContent.bogusWaterFlowing.getUnlocalizedName().substring(5));
		registerBlock(SCContent.keycardReader);
		registerBlock(SCContent.ironTrapdoor);
		registerBlock(SCContent.bouncingBetty);
		registerBlock(SCContent.inventoryScanner);
		GameRegistry.registerBlock(SCContent.inventoryScannerField, SCContent.inventoryScannerField.getUnlocalizedName().substring(5));
		registerBlock(SCContent.trackMine);
		registerBlock(SCContent.cageTrap);
		registerBlock(SCContent.portableRadar);
		registerReinforcedBlock(SCContent.reinforcedStone, ItemBlockReinforcedStone.class);
		registerBlock(SCContent.keypadChest);
		registerBlock(SCContent.usernameLogger);
		registerReinforcedBlock(SCContent.reinforcedGlassPane);
		registerBlock(SCContent.alarm);
		GameRegistry.registerBlock(SCContent.alarmLit, SCContent.alarmLit.getUnlocalizedName().substring(5));
		registerReinforcedBlock(SCContent.unbreakableIronBars);
		registerReinforcedBlock(SCContent.reinforcedSandstone, ItemBlockReinforcedSandstone.class);
		registerReinforcedBlock(SCContent.reinforcedDirt, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedCobblestone);
		registerBlock(SCContent.reinforcedFencegate);
		registerReinforcedBlock(SCContent.reinforcedWoodPlanks, ItemBlockReinforcedPlanks.class);
		registerBlock(SCContent.panicButton);
		registerBlock(SCContent.frame);
		registerBlock(SCContent.claymore);
		registerBlock(SCContent.keypadFurnace);
		registerBlock(SCContent.securityCamera);
		GameRegistry.registerBlock(SCContent.reinforcedStairsOak, ItemBlockTinted.class, SCContent.reinforcedStairsOak.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsSpruce, ItemBlockTinted.class, SCContent.reinforcedStairsSpruce.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsCobblestone, ItemBlockTinted.class, SCContent.reinforcedStairsCobblestone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsSandstone, ItemBlockTinted.class, SCContent.reinforcedStairsSandstone.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsBirch, ItemBlockTinted.class, SCContent.reinforcedStairsBirch.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsJungle, ItemBlockTinted.class, SCContent.reinforcedStairsJungle.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsAcacia, ItemBlockTinted.class, SCContent.reinforcedStairsAcacia.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedStairsDarkoak, ItemBlockTinted.class, SCContent.reinforcedStairsDarkoak.getUnlocalizedName().substring(5));
		registerBlock(SCContent.reinforcedStairsStone, ItemBlockTinted.class);
		registerBlock(SCContent.ironFence);
		registerBlock(SCContent.ims);
		registerReinforcedBlock(SCContent.reinforcedGlass);
		registerBlock(SCContent.reinforcedStainedGlass, ItemBlockReinforcedStainedGlass.class);
		registerBlock(SCContent.reinforcedStainedGlassPanes, ItemBlockReinforcedStainedGlassPanes.class);
		registerBlock(SCContent.reinforcedWoodSlabs, ItemBlockReinforcedWoodSlabs.class);
		GameRegistry.registerBlock(SCContent.reinforcedDoubleWoodSlabs, ItemBlockTinted.class, SCContent.reinforcedDoubleWoodSlabs.getUnlocalizedName().substring(5));
		registerBlock(SCContent.reinforcedStoneSlabs, ItemBlockReinforcedSlabs.class);
		GameRegistry.registerBlock(SCContent.reinforcedDoubleStoneSlabs, ItemBlockTinted.class, SCContent.reinforcedDoubleStoneSlabs.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedDirtSlab, ItemBlockReinforcedSlabs.class, SCContent.reinforcedDirtSlab.getUnlocalizedName().substring(5));
		GameRegistry.registerBlock(SCContent.reinforcedDoubleDirtSlab, ItemBlockTinted.class, SCContent.reinforcedDoubleDirtSlab.getUnlocalizedName().substring(5));
		registerBlock(SCContent.protecto);
		GameRegistry.registerBlock(SCContent.scannerDoor, SCContent.scannerDoor.getUnlocalizedName().substring(5));
		registerReinforcedBlock(SCContent.reinforcedStoneBrick, ItemBlockReinforcedStoneBrick.class);
		registerBlock(SCContent.reinforcedStairsStoneBrick, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedMossyCobblestone, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedBrick, ItemBlockTinted.class);
		registerBlock(SCContent.reinforcedStairsBrick, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedNetherBrick, ItemBlockTinted.class);
		registerBlock(SCContent.reinforcedStairsNetherBrick, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedHardenedClay, ItemBlockTinted.class);
		registerBlock(SCContent.reinforcedStainedHardenedClay, ItemBlockReinforcedStainedBlock.class);
		registerReinforcedBlock(SCContent.reinforcedOldLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(SCContent.reinforcedNewLogs, ItemBlockReinforcedLog.class);
		registerReinforcedBlock(SCContent.reinforcedMetals, ItemBlockReinforcedMetals.class);
		registerReinforcedBlock(SCContent.reinforcedCompressedBlocks, ItemBlockReinforcedCompressedBlocks.class);
		registerReinforcedBlock(SCContent.reinforcedWool, ItemBlockReinforcedStainedBlock.class);
		registerReinforcedBlock(SCContent.reinforcedQuartz, ItemBlockReinforcedQuartz.class);
		registerBlock(SCContent.reinforcedStairsQuartz, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedPrismarine, ItemBlockReinforcedPrismarine.class);
		registerReinforcedBlock(SCContent.reinforcedRedSandstone, ItemBlockReinforcedSandstone.class);
		registerReinforcedBlock(SCContent.reinforcedStairsRedSandstone, ItemBlockTinted.class);
		registerReinforcedBlock(SCContent.reinforcedStoneSlabs2, ItemBlockReinforcedSlabs2.class); //technically not a reinforced block, but doesn't need a page
		GameRegistry.registerBlock(SCContent.reinforcedDoubleStoneSlabs2, ItemBlockTinted.class, SCContent.reinforcedDoubleStoneSlabs2.getUnlocalizedName().substring(5));

		registerItem(SCContent.codebreaker);
		registerItem(SCContent.reinforcedDoorItem, SCContent.reinforcedDoorItem.getUnlocalizedName().substring(5));
		registerItem(SCContent.scannerDoorItem, SCContent.scannerDoorItem.getUnlocalizedName().substring(5));
		registerItem(SCContent.universalBlockRemover);
		registerItem(SCContent.keycardLV1, SecurityCraft.config.ableToCraftKeycard1);
		registerItem(SCContent.keycardLV2, SecurityCraft.config.ableToCraftKeycard2);
		registerItem(SCContent.keycardLV3, SecurityCraft.config.ableToCraftKeycard3);
		registerItem(SCContent.keycardLV4, SecurityCraft.config.ableToCraftKeycard4);
		registerItem(SCContent.keycardLV5, SecurityCraft.config.ableToCraftKeycard5);
		registerItem(SCContent.limitedUseKeycard, SecurityCraft.config.ableToCraftLUKeycard);
		registerItem(SCContent.remoteAccessMine);
		registerItem(SCContent.fWaterBucket);
		registerItem(SCContent.fLavaBucket);
		registerItem(SCContent.universalBlockModifier);
		registerItem(SCContent.redstoneModule);
		registerItem(SCContent.whitelistModule);
		registerItem(SCContent.blacklistModule);
		registerItem(SCContent.harmingModule);
		registerItem(SCContent.smartModule);
		registerItem(SCContent.storageModule);
		registerItem(SCContent.disguiseModule);
		registerItem(SCContent.wireCutters);
		registerItem(SCContent.adminTool);
		registerItem(SCContent.keyPanel);
		registerItem(SCContent.cameraMonitor);
		registerItem(SCContent.taser);
		registerItem(SCContent.scManual);
		registerItem(SCContent.universalOwnerChanger);
		registerItem(SCContent.universalBlockReinforcerLvL1);
		registerItem(SCContent.universalBlockReinforcerLvL2);
		registerItem(SCContent.universalBlockReinforcerLvL3);
		registerItem(SCContent.briefcase);
		registerItem(SCContent.universalKeyChanger);
		GameRegistry.registerItem(SCContent.taserPowered, "taserPowered"); //won't show up in the manual
	}

	public static void registerTileEntities()
	{
		GameRegistry.registerTileEntity(TileEntityOwnable.class, "abstractOwnable");
		GameRegistry.registerTileEntity(TileEntitySCTE.class, "abstractSC");
		GameRegistry.registerTileEntity(TileEntityKeypad.class, "keypad");
		GameRegistry.registerTileEntity(TileEntityLaserBlock.class, "laserBlock");
		GameRegistry.registerTileEntity(TileEntityCageTrap.class, "cageTrap");
		GameRegistry.registerTileEntity(TileEntityKeycardReader.class, "keycardReader");
		GameRegistry.registerTileEntity(TileEntityInventoryScanner.class, "inventoryScanner");
		GameRegistry.registerTileEntity(TileEntityPortableRadar.class, "portableRadar");
		GameRegistry.registerTileEntity(TileEntitySecurityCamera.class, "securityCamera");
		GameRegistry.registerTileEntity(TileEntityLogger.class, "usernameLogger");
		GameRegistry.registerTileEntity(TileEntityRetinalScanner.class, "retinalScanner");
		GameRegistry.registerTileEntity(TileEntityKeypadChest.class, "keypadChest");
		GameRegistry.registerTileEntity(TileEntityAlarm.class, "alarm");
		GameRegistry.registerTileEntity(TileEntityClaymore.class, "claymore");
		GameRegistry.registerTileEntity(TileEntityKeypadFurnace.class, "keypadFurnace");
		GameRegistry.registerTileEntity(TileEntityIMS.class, "ims");
		GameRegistry.registerTileEntity(TileEntityProtecto.class, "protecto");
		GameRegistry.registerTileEntity(CustomizableSCTE.class, "customizableSCTE");
		GameRegistry.registerTileEntity(TileEntityScannerDoor.class, "scannerDoor");
	}

	public static void registerRecipes()
	{
		if(SecurityCraft.config.useOldKeypadRecipe)
			GameRegistry.addRecipe(new ItemStack(SCContent.keypad, 1), new Object[]{
					"III", "III", "III", 'I', Blocks.stone_button
			});
		else{
			GameRegistry.addRecipe(new ItemStack(SCContent.keyPanel, 1), new Object[]{
					"III", "IBI", "III", 'I', Blocks.stone_button, 'B', Blocks.heavy_weighted_pressure_plate
			});

			GameRegistry.addRecipe(new ItemStack(SCContent.frame, 1), new Object[]{
					"III", "IBI", "I I", 'I', Items.iron_ingot, 'B', Items.redstone
			});
		}

		GameRegistry.addRecipe(new ItemStack(SCContent.laserBlock, 1), new Object[]{
				"III", "IBI", "IPI", 'I', Blocks.stone, 'B', Blocks.redstone_block, 'P', Blocks.glass_pane
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.mine, 3), new Object[]{
				" I ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedDoorItem, 1), new Object[]{
				"III", "IDI", "III", 'I', Items.iron_ingot, 'D', Items.iron_door
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockRemover, 1), new Object[]{
				"SII",'I', Items.iron_ingot, 'S', Items.shears
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.ironTrapdoor, 1), new Object[]{
				"###", "#P#", "###", '#', Items.iron_ingot, 'P', Blocks.trapdoor
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.keycardReader, 1), new Object[]{
				"SSS", "SHS", "SSS", 'S', Blocks.stone, 'H', Blocks.hopper
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.bouncingBetty, 1), new Object[]{
				" P ", "IBI", 'I', Items.iron_ingot, 'B', Items.gunpowder, 'P', Blocks.heavy_weighted_pressure_plate
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.codebreaker, 1), new Object[]{
				"DTD", "GSG", "RER", 'D', Items.diamond, 'T', Blocks.redstone_torch, 'G', Items.gold_ingot, 'S', Items.nether_star, 'R', Items.redstone, 'E', Items.emerald
		});

		if(SecurityCraft.config.ableToCraftKeycard1)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycardLV1, 1), new Object[]{
					"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.gold_ingot
			});

		if(SecurityCraft.config.ableToCraftKeycard2)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycardLV2, 1), new Object[]{
					"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.brick
			});

		if(SecurityCraft.config.ableToCraftKeycard3)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycardLV3, 1), new Object[]{
					"III", "YYY", 'I', Items.iron_ingot, 'Y', Items.netherbrick
			});

		if(SecurityCraft.config.ableToCraftKeycard4)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycardLV4, 1), new Object[]{
					"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 13)
			});

		if(SecurityCraft.config.ableToCraftKeycard5)
			GameRegistry.addRecipe(new ItemStack(SCContent.keycardLV5, 1), new Object[]{
					"III", "DDD", 'I', Items.iron_ingot, 'D', new ItemStack(Items.dye, 1, 5)
			});

		if(SecurityCraft.config.ableToCraftLUKeycard)
			GameRegistry.addRecipe(new ItemStack(SCContent.limitedUseKeycard, 1), new Object[]{
					"III", "LLL", 'I', Items.iron_ingot, 'L', new ItemStack(Items.dye, 1, 4)
			});

		GameRegistry.addRecipe(new ItemStack(SCContent.trackMine, 4), new Object[]{
				"X X", "X#X", "XGX", 'X', Items.iron_ingot, '#', Items.stick, 'G', Items.gunpowder
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.portableRadar, 1), new Object[]{
				"III", "ITI", "IRI", 'I', Items.iron_ingot, 'T', Blocks.redstone_torch, 'R', Items.redstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.remoteAccessMine, 1), new Object[]{
				" R ", " DG", "S  ", 'R', Blocks.redstone_torch, 'D', Items.diamond, 'G', Items.gold_ingot, 'S', Items.stick
		});

		for(int i = 0; i < 4; i++)
			GameRegistry.addRecipe(new ItemStack(SCContent.fWaterBucket, 1), new Object[]{
					"P", "B", 'P', new ItemStack(Items.potionitem, 1, HARMING_POTIONS[i]), 'B', Items.water_bucket
			});

		for(int i = 0; i < 4; i++)
			GameRegistry.addRecipe(new ItemStack(SCContent.fLavaBucket, 1), new Object[]{
					"P", "B", 'P', new ItemStack(Items.potionitem, 1, HEALING_POTIONS[i]), 'B', Items.lava_bucket
			});

		GameRegistry.addRecipe(new ItemStack(SCContent.retinalScanner, 1), new Object[]{
				"SSS", "SES", "SSS", 'S', Blocks.stone, 'E', Items.ender_eye
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.inventoryScanner, 1), new Object[]{
				"SSS", "SLS", "SCS", 'S', Blocks.stone, 'L', SCContent.laserBlock, 'C', Blocks.ender_chest
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.cageTrap, 1), new Object[]{
				"BBB", "GRG", "III", 'B', SCContent.unbreakableIronBars, 'G', Items.gold_ingot, 'R', Items.redstone, 'I', Blocks.iron_block
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.alarm, 1), new Object[]{
				"GGG", "GNG", "GRG", 'G', Blocks.glass, 'R', Items.redstone, 'N', Blocks.noteblock
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.acacia_fence_gate
		});
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.birch_fence_gate
		});
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.dark_oak_fence_gate
		});
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.jungle_fence_gate
		});
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.oak_fence_gate
		});
		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedFencegate, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.spruce_fence_gate
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.wireCutters, 1), new Object[]{
				"SI ", "I I", " I ", 'I', Items.iron_ingot, 'S', Items.shears
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.panicButton, 1), new Object[]{
				" I ", "IBI", " R ", 'I', Items.iron_ingot, 'B', Blocks.stone_button, 'R', Items.redstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.whitelistModule, 1), new Object[]{
				"III", "IPI", "IPI", 'I', Items.iron_ingot, 'P', Items.paper
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.blacklistModule, 1), new Object[]{
				"III", "IPI", "IDI", 'I', Items.iron_ingot, 'P', Items.paper, 'D', new ItemStack(Items.dye, 1, 0)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.redstoneModule, 1), new Object[]{
				"III", "IPI", "IRI", 'I', Items.iron_ingot, 'P', Items.paper, 'R', Items.redstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.harmingModule, 1), new Object[]{
				"III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.arrow
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.smartModule, 1), new Object[]{
				"III", "IPI", "IEI", 'I', Items.iron_ingot, 'P', Items.paper, 'E', Items.ender_pearl
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.storageModule, 1), new Object[]{
				"III", "IPI", "ICI", 'I', Items.iron_ingot, 'P', Items.paper, 'C', SCContent.keypadChest
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.disguiseModule, 1), new Object[]{
				"III", "IPI", "IAI", 'I', Items.iron_ingot, 'P', Items.paper, 'A', Items.painting
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockModifier, 1), new Object[]{
				"ER ", "RI ", "  I", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockModifier, 1), new Object[]{
				" RE", " IR", "I  ", 'E', Items.emerald, 'R', Items.redstone, 'I', Items.iron_ingot
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.usernameLogger, 1), new Object[]{
				"SPS", "SRS", "SSS", 'S', Blocks.stone, 'P', SCContent.portableRadar, 'R', Items.redstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.claymore, 1), new Object[]{
				"HSH", "SBS", "RGR", 'H', Blocks.tripwire_hook, 'S', Items.string, 'B', SCContent.bouncingBetty, 'R', Items.redstone, 'G', Items.gunpowder
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.ironFence, 1), new Object[]{
				" I ", "IFI", " I ", 'I', Items.iron_ingot, 'F', Blocks.oak_fence
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsStone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', new ItemStack(SCContent.reinforcedStone, 1, 0)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsCobblestone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedCobblestone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsSandstone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedSandstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsOak, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 0)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsSpruce, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 1)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsBirch, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 2)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsJungle, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 3)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsAcacia, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 4)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsDarkoak, 4), new Object[]{
				"W  ", "WW ", "WWW", 'W', new ItemStack(SCContent.reinforcedWoodPlanks, 1, 5)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsStoneBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedStoneBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsNetherBrick, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedNetherBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsQuartz, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedQuartz
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStairsRedSandstone, 4), new Object[]{
				"S  ", "SS ", "SSS", 'S', SCContent.reinforcedRedSandstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.ims, 1), new Object[]{
				"BPB", " I ", "B B", 'B', SCContent.bouncingBetty, 'P', SCContent.portableRadar, 'I', Blocks.iron_block
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.cameraMonitor, 1), new Object[]{
				"III", "IGI", "III", 'I', Items.iron_ingot, 'G', Blocks.glass_pane
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.taser, 1), new Object[]{
				"BGI", "RSG", "  S", 'B', Items.bow, 'G', Items.gold_ingot, 'I', Items.iron_ingot, 'R', Items.redstone, 'S', Items.stick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.securityCamera, 1), new Object[]{
				"III", "GRI", "IIS", 'I', Items.iron_ingot, 'G', SCContent.reinforcedGlassPane, 'R', Blocks.redstone_block, 'S', Items.stick
		});

		for(int i = 0; i < 16; i++){
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedGlass, 8, 15 - i), new Object[]{
					"###", "#X#", "###", '#', new ItemStack(SCContent.reinforcedGlass), 'X', new ItemStack(Items.dye, 1, i)
			});

			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedGlassPanes, 16, i - 1), new Object[]{
					"###", "###", '#', new ItemStack(SCContent.reinforcedStainedGlass, 1, i)
			});

			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStainedHardenedClay, 8, ~i & 15), new Object[]{
					"###", "#X#", "###", '#', new ItemStack(SCContent.reinforcedHardenedClay), 'X', new ItemStack(Items.dye, 1, i)
			});
		}

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL1, 1), new Object[]{
				" DG", "RLD", "SR ", 'G', Blocks .glass, 'D', Items.diamond, 'L', SCContent.laserBlock, 'R', Items.redstone, 'S', Items.stick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL2, 1), new Object[]{
				" DG", "RLD", "SR ", 'G', new ItemStack(SCContent.reinforcedStainedGlass, 1, 15), 'D', Blocks.diamond_block, 'L', SCContent.laserBlock, 'R', Items.redstone, 'S', Items.stick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.universalBlockReinforcerLvL3, 1), new Object[]{
				" EG", "RNE", "SR ", 'G', new ItemStack(SCContent.reinforcedStainedGlass, 1, 6), 'E', Blocks.emerald_block, 'N', Items.nether_star, 'R', Blocks.redstone_block, 'S', Items.stick
		});

		for(int i = 0; i < 6; i++)
			GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedWoodSlabs, 6, i), new Object[]{
					"MMM", 'M', new ItemStack(SCContent.reinforcedWoodPlanks, 1, i)
			});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 0), new Object[]{
				"MMM", 'M', new ItemStack(SCContent.reinforcedStone, 1, 0)
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 1), new Object[]{
				"MMM", 'M', SCContent.reinforcedCobblestone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 2), new Object[]{
				"MMM", 'M', SCContent.reinforcedSandstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedDirtSlab, 6, 3), new Object[]{
				"MMM", 'M', SCContent.reinforcedDirt
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 4), new Object[]{
				"MMM", 'M', SCContent.reinforcedStoneBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 5), new Object[]{
				"MMM", 'M', SCContent.reinforcedBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 6), new Object[]{
				"MMM", 'M', SCContent.reinforcedNetherBrick
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs, 6, 7), new Object[]{
				"MMM", 'M', SCContent.reinforcedQuartz
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.reinforcedStoneSlabs2, 6, 0), new Object[]{
				"MMM", 'M', SCContent.reinforcedRedSandstone
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.protecto, 1), new Object[]{
				"ODO", "OEO", "OOO", 'O', Blocks.obsidian, 'D', Blocks.daylight_detector, 'E', Items.ender_eye
		});

		GameRegistry.addRecipe(new ItemStack(SCContent.briefcase, 1), new Object[]{
				"SSS", "ICI", "III", 'S', Items.stick, 'I', Items.iron_ingot, 'C', SCContent.keypadChest
		});


		GameRegistry.addRecipe(new ItemStack(SCContent.universalKeyChanger, 1), new Object[]{
				" RL", " IR", "I  ", 'R', Items.redstone, 'L', SCContent.laserBlock, 'I', Items.iron_ingot
		});

		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.dirtMine, 1), new Object[] {Blocks.dirt, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.stoneMine, 1), new Object[] {Blocks.stone, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.cobblestoneMine, 1), new Object[] {Blocks.cobblestone, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.diamondOreMine, 1), new Object[] {Blocks.diamond_ore, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.sandMine, 1), new Object[] {Blocks.sand, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.furnaceMine, 1), new Object[] {Blocks.furnace, SCContent.mine});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.universalOwnerChanger, 1), new Object[] {SCContent.universalBlockModifier, Items.name_tag});
		GameRegistry.addShapelessRecipe(new ItemStack(SCContent.scannerDoorItem), new Object[]{SCContent.reinforcedDoorItem, SCContent.retinalScanner});
	}

	public static void registerEntities()
	{
		EntityRegistry.registerModEntity(EntityBouncingBetty.class, "BBetty", 0, SecurityCraft.instance, 128, 1, true);
		EntityRegistry.registerModEntity(EntityTaserBullet.class, "TazerBullet", 2, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntityIMSBomb.class, "IMSBomb", 3, SecurityCraft.instance, 256, 1, true);
		EntityRegistry.registerModEntity(EntitySecurityCamera.class, "SecurityCamera", 4, SecurityCraft.instance, 256, 20, false);
	}

	public static void registerPackets(SimpleNetworkWrapper network)
	{
		network.registerMessage(PacketSetBlock.Handler.class, PacketSetBlock.class, 1, Side.SERVER);
		network.registerMessage(PacketSetISType.Handler.class, PacketSetISType.class, 2, Side.SERVER);
		network.registerMessage(PacketSetKeycardLevel.Handler.class, PacketSetKeycardLevel.class, 3, Side.SERVER);
		network.registerMessage(PacketUpdateLogger.Handler.class, PacketUpdateLogger.class, 4, Side.CLIENT);
		network.registerMessage(PacketCUpdateNBTTag.Handler.class, PacketCUpdateNBTTag.class, 5, Side.CLIENT);
		network.registerMessage(PacketSUpdateNBTTag.Handler.class, PacketSUpdateNBTTag.class, 6, Side.SERVER);
		network.registerMessage(PacketCPlaySoundAtPos.Handler.class, PacketCPlaySoundAtPos.class, 7, Side.CLIENT);
		network.registerMessage(PacketSetExplosiveState.Handler.class, PacketSetExplosiveState.class, 8, Side.SERVER);
		network.registerMessage(PacketGivePotionEffect.Handler.class, PacketGivePotionEffect.class, 9, Side.SERVER);
		network.registerMessage(PacketSSetOwner.Handler.class, PacketSSetOwner.class, 10, Side.SERVER);
		network.registerMessage(PacketSAddModules.Handler.class, PacketSAddModules.class, 11, Side.SERVER);
		network.registerMessage(PacketSSetPassword.Handler.class, PacketSSetPassword.class, 12, Side.SERVER);
		network.registerMessage(PacketSCheckPassword.Handler.class, PacketSCheckPassword.class, 13, Side.SERVER);
		network.registerMessage(PacketSSyncTENBTTag.Handler.class, PacketSSyncTENBTTag.class, 14, Side.SERVER);
		network.registerMessage(PacketSMountCamera.Handler.class, PacketSMountCamera.class, 15, Side.SERVER);
		network.registerMessage(PacketSSetCameraRotation.Handler.class, PacketSSetCameraRotation.class, 16, Side.SERVER);
		network.registerMessage(PacketCSetPlayerPositionAndRotation.Handler.class, PacketCSetPlayerPositionAndRotation.class, 17, Side.CLIENT);
		network.registerMessage(PacketSOpenGui.Handler.class, PacketSOpenGui.class, 18, Side.SERVER);
		network.registerMessage(PacketSToggleOption.Handler.class, PacketSToggleOption.class, 19, Side.SERVER);
		network.registerMessage(PacketSUpdateSliderValue.Handler.class, PacketSUpdateSliderValue.class, 20, Side.SERVER);
		network.registerMessage(PacketSRemoveCameraTag.Handler.class, PacketSRemoveCameraTag.class, 21, Side.SERVER);
		network.registerMessage(PacketCChangeStackSize.Handler.class, PacketCChangeStackSize.class, 22, Side.CLIENT);
	}

	@SideOnly(Side.CLIENT)
	public static void registerResourceLocations()
	{
		//Blocks
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.keypad), 0, new ModelResourceLocation("securitycraft:keypad", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.frame), 0, new ModelResourceLocation("securitycraft:keypadFrame", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 0, new ModelResourceLocation("securitycraft:reinforcedStone_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 1, new ModelResourceLocation("securitycraft:reinforcedStone_granite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 2, new ModelResourceLocation("securitycraft:reinforcedStone_smooth_granite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 3, new ModelResourceLocation("securitycraft:reinforcedStone_diorite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 4, new ModelResourceLocation("securitycraft:reinforcedStone_smooth_diorite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 5, new ModelResourceLocation("securitycraft:reinforcedStone_andesite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStone), 6, new ModelResourceLocation("securitycraft:reinforcedStone_smooth_andesite", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.laserBlock), 0, new ModelResourceLocation("securitycraft:laserBlock", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.laserField), 0, new ModelResourceLocation("securitycraft:laser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedDoor), 0, new ModelResourceLocation("securitycraft:reinforcedIronDoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.ironTrapdoor), 0, new ModelResourceLocation("securitycraft:reinforcedIronTrapdoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.keycardReader), 0, new ModelResourceLocation("securitycraft:keycardReader", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.inventoryScanner), 0, new ModelResourceLocation("securitycraft:inventoryScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.cageTrap), 0, new ModelResourceLocation("securitycraft:cageTrap", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.inventoryScannerField), 0, new ModelResourceLocation("securitycraft:inventoryScannerField", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.retinalScanner), 0, new ModelResourceLocation("securitycraft:retinalScanner", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedGlassPane), 0, new ModelResourceLocation("securitycraft:reinforcedGlass", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.unbreakableIronBars), 0, new ModelResourceLocation("securitycraft:reinforcedIronBars", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.portableRadar), 0, new ModelResourceLocation("securitycraft:portableRadar", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.alarm), 0, new ModelResourceLocation("securitycraft:alarm", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.alarmLit), 0, new ModelResourceLocation("securitycraft:alarmLit", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.usernameLogger), 0, new ModelResourceLocation("securitycraft:usernameLogger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedFencegate), 0, new ModelResourceLocation("securitycraft:reinforcedFenceGate", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.ironFence), 0, new ModelResourceLocation("securitycraft:electrifiedIronFence", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 0, new ModelResourceLocation("securitycraft:reinforcedPlanks_Oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 1, new ModelResourceLocation("securitycraft:reinforcedPlanks_Spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 2, new ModelResourceLocation("securitycraft:reinforcedPlanks_Birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 3, new ModelResourceLocation("securitycraft:reinforcedPlanks_Jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 4, new ModelResourceLocation("securitycraft:reinforcedPlanks_Acacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodPlanks), 5, new ModelResourceLocation("securitycraft:reinforcedPlanks_DarkOak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsStone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsStone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsOak), 0, new ModelResourceLocation("securitycraft:reinforcedStairsOak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsSpruce), 0, new ModelResourceLocation("securitycraft:reinforcedStairsSpruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsBirch), 0, new ModelResourceLocation("securitycraft:reinforcedStairsBirch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsJungle), 0, new ModelResourceLocation("securitycraft:reinforcedStairsJungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsAcacia), 0, new ModelResourceLocation("securitycraft:reinforcedStairsAcacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsDarkoak), 0, new ModelResourceLocation("securitycraft:reinforcedStairsDarkoak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedGlass), 0, new ModelResourceLocation("securitycraft:reinforcedGlassBlock", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 0, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_white", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 1, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_orange", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 2, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_magenta", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 3, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_light_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 4, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_yellow", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 5, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_lime", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 6, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_pink", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 7, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_gray", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 8, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_silver", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 9, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_cyan", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 10, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_purple", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 11, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 12, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_brown", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 13, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_green", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 14, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_red", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlass), 15, new ModelResourceLocation("securitycraft:reinforcedStainedGlass_black", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 0, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_white", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 1, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_orange", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 2, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_magenta", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 3, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_light_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 4, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_yellow", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 5, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_lime", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 6, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_pink", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 7, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_gray", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 8, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_silver", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 9, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_cyan", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 10, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_purple", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 11, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 12, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_brown", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 13, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_green", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 14, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_red", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedGlassPanes), 15, new ModelResourceLocation("securitycraft:reinforcedStainedGlassPanes_black", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.keypadChest), 0, new ModelResourceLocation("securitycraft:keypadChest", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.keypadFurnace), 0, new ModelResourceLocation("securitycraft:keypadFurnace", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.panicButton), 0, new ModelResourceLocation("securitycraft:panicButton", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.securityCamera), 0, new ModelResourceLocation("securitycraft:securityCamera", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedDirt), 0, new ModelResourceLocation("securitycraft:reinforcedDirt", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedSandstone_normal", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedSandstone), 1, new ModelResourceLocation("securitycraft:reinforcedSandstone_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedSandstone), 2, new ModelResourceLocation("securitycraft:reinforcedSandstone_smooth", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 0, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 1, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 2, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 3, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 4, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_acacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWoodSlabs), 5, new ModelResourceLocation("securitycraft:reinforcedWoodSlabs_darkoak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsSandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 0, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_stone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 1, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_cobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 2, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_sandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedDirtSlab), 3, new ModelResourceLocation("securitycraft:reinforcedDirtSlab", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 4, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_stonebrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 5, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_brick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 6, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_netherbrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs), 7, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs_quartz", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneSlabs2), 0, new ModelResourceLocation("securitycraft:reinforcedStoneSlabs2_red_sandstone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.protecto), 0, new ModelResourceLocation("securitycraft:protecto", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.scannerDoor), 0, new ModelResourceLocation("securitycraft:scannerDoor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 1, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_mossy", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 2, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_cracked", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStoneBrick), 3, new ModelResourceLocation("securitycraft:reinforcedStoneBrick_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsStoneBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsStoneBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedMossyCobblestone), 0, new ModelResourceLocation("securitycraft:reinforcedMossyCobblestone", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedBrick), 0, new ModelResourceLocation("securitycraft:reinforcedBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforcedNetherBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsNetherBrick), 0, new ModelResourceLocation("securitycraft:reinforcedStairsNetherBrick", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforcedHardenedClay", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 0, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_white", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 1, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_orange", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 2, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_magenta", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 3, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_light_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 4, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_yellow", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 5, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_lime", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 6, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_pink", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 7, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_gray", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 8, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_silver", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 9, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_cyan", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 10, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_purple", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 11, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 12, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_brown", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 13, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_green", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 14, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_red", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStainedHardenedClay), 15, new ModelResourceLocation("securitycraft:reinforcedStainedHardenedClay_black", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 0, new ModelResourceLocation("securitycraft:reinforcedLogs_oak", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 1, new ModelResourceLocation("securitycraft:reinforcedLogs_spruce", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 2, new ModelResourceLocation("securitycraft:reinforcedLogs_birch", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedOldLogs), 3, new ModelResourceLocation("securitycraft:reinforcedLogs_jungle", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedNewLogs), 0, new ModelResourceLocation("securitycraft:reinforcedLogs2_acacia", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedNewLogs), 1, new ModelResourceLocation("securitycraft:reinforcedLogs2_big_oak", "inventory"));;
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedMetals), 0, new ModelResourceLocation("securitycraft:reinforcedMetals_gold", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedMetals), 1, new ModelResourceLocation("securitycraft:reinforcedMetals_iron", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedMetals), 2, new ModelResourceLocation("securitycraft:reinforcedMetals_diamond", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedMetals), 3, new ModelResourceLocation("securitycraft:reinforcedMetals_emerald", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedCompressedBlocks), 0, new ModelResourceLocation("securitycraft:reinforcedCompressedBlocks_lapis", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedCompressedBlocks), 1, new ModelResourceLocation("securitycraft:reinforcedCompressedBlocks_coal", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 0, new ModelResourceLocation("securitycraft:reinforcedWool_white", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 1, new ModelResourceLocation("securitycraft:reinforcedWool_orange", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 2, new ModelResourceLocation("securitycraft:reinforcedWool_magenta", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 3, new ModelResourceLocation("securitycraft:reinforcedWool_light_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 4, new ModelResourceLocation("securitycraft:reinforcedWool_yellow", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 5, new ModelResourceLocation("securitycraft:reinforcedWool_lime", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 6, new ModelResourceLocation("securitycraft:reinforcedWool_pink", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 7, new ModelResourceLocation("securitycraft:reinforcedWool_gray", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 8, new ModelResourceLocation("securitycraft:reinforcedWool_silver", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 9, new ModelResourceLocation("securitycraft:reinforcedWool_cyan", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 10, new ModelResourceLocation("securitycraft:reinforcedWool_purple", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 11, new ModelResourceLocation("securitycraft:reinforcedWool_blue", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 12, new ModelResourceLocation("securitycraft:reinforcedWool_brown", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 13, new ModelResourceLocation("securitycraft:reinforcedWool_green", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 14, new ModelResourceLocation("securitycraft:reinforcedWool_red", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedWool), 15, new ModelResourceLocation("securitycraft:reinforcedWool_black", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedQuartz), 0, new ModelResourceLocation("securitycraft:reinforcedQuartz_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedQuartz), 1, new ModelResourceLocation("securitycraft:reinforcedQuartz_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedQuartz), 2, new ModelResourceLocation("securitycraft:reinforcedQuartz_pillar", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsQuartz), 0, new ModelResourceLocation("securitycraft:reinforcedStairsQuartz", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 0, new ModelResourceLocation("securitycraft:reinforcedPrismarine_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 1, new ModelResourceLocation("securitycraft:reinforcedPrismarine_bricks", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedPrismarine), 2, new ModelResourceLocation("securitycraft:reinforcedPrismarine_dark", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_default", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 1, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_chiseled", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedRedSandstone), 2, new ModelResourceLocation("securitycraft:reinforcedRedSandstone_smooth", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.reinforcedStairsRedSandstone), 0, new ModelResourceLocation("securitycraft:reinforcedStairsRedSandstone", "inventory"));

		//Items
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.codebreaker, 0, new ModelResourceLocation("securitycraft:codebreaker", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.remoteAccessMine, 0, new ModelResourceLocation("securitycraft:remoteAccessMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.reinforcedDoorItem, 0, new ModelResourceLocation("securitycraft:doorIndestructibleIronItem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.fWaterBucket, 0, new ModelResourceLocation("securitycraft:bucketFWater", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.fLavaBucket, 0, new ModelResourceLocation("securitycraft:bucketFLava", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keycardLV1, 0, new ModelResourceLocation("securitycraft:keycardLV1", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keycardLV2, 0, new ModelResourceLocation("securitycraft:keycardLV2", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keycardLV3, 0, new ModelResourceLocation("securitycraft:keycardLV3", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keycardLV4, 0, new ModelResourceLocation("securitycraft:keycardLV4", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keycardLV5, 0, new ModelResourceLocation("securitycraft:keycardLV5", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.limitedUseKeycard, 0, new ModelResourceLocation("securitycraft:limitedUseKeycard", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalBlockRemover, 0, new ModelResourceLocation("securitycraft:universalBlockRemover", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalBlockModifier, 0, new ModelResourceLocation("securitycraft:universalBlockModifier", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.whitelistModule, 0, new ModelResourceLocation("securitycraft:whitelistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.blacklistModule, 0, new ModelResourceLocation("securitycraft:blacklistModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.redstoneModule, 0, new ModelResourceLocation("securitycraft:redstoneModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.harmingModule, 0, new ModelResourceLocation("securitycraft:harmingModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.storageModule, 0, new ModelResourceLocation("securitycraft:storageModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.smartModule, 0, new ModelResourceLocation("securitycraft:smartModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.disguiseModule, 0, new ModelResourceLocation("securitycraft:disguiseModule", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.wireCutters, 0, new ModelResourceLocation("securitycraft:wireCutters", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.keyPanel, 0, new ModelResourceLocation("securitycraft:keypadItem", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.adminTool, 0, new ModelResourceLocation("securitycraft:adminTool", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.cameraMonitor, 0, new ModelResourceLocation("securitycraft:cameraMonitor", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.scManual, 0, new ModelResourceLocation("securitycraft:scManual", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.taser, 0, new ModelResourceLocation("securitycraft:taser", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.taserPowered, 0, new ModelResourceLocation("securitycraft:taserPowered", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalOwnerChanger, 0, new ModelResourceLocation("securitycraft:universalOwnerChanger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalBlockReinforcerLvL1, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL1", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalBlockReinforcerLvL2, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL2", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalBlockReinforcerLvL3, 0, new ModelResourceLocation("securitycraft:universalBlockReinforcerLvL3", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.briefcase, 0, new ModelResourceLocation("securitycraft:briefcase", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.universalKeyChanger, 0, new ModelResourceLocation("securitycraft:universalKeyChanger", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(SCContent.scannerDoorItem, 0, new ModelResourceLocation("securitycraft:scannerDoorItem", "inventory"));

		//Mines
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.mine), 0, new ModelResourceLocation("securitycraft:mine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.dirtMine), 0, new ModelResourceLocation("securitycraft:dirtMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.stoneMine), 0, new ModelResourceLocation("securitycraft:stoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.cobblestoneMine), 0, new ModelResourceLocation("securitycraft:cobblestoneMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.sandMine), 0, new ModelResourceLocation("securitycraft:sandMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.diamondOreMine), 0, new ModelResourceLocation("securitycraft:diamondMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.furnaceMine), 0, new ModelResourceLocation("securitycraft:furnaceMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.trackMine), 0, new ModelResourceLocation("securitycraft:trackMine", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.bouncingBetty), 0, new ModelResourceLocation("securitycraft:bouncingBetty", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.claymore), 0, new ModelResourceLocation("securitycraft:claymore", "inventory"));
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(SCContent.ims), 0, new ModelResourceLocation("securitycraft:ims", "inventory"));
	}

	/**
	 * Registers the given block with GameRegistry.registerBlock(), and adds the help info for the block to the SecurityCraft manual item.
	 */
	private static void registerBlock(Block block)
	{
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));

		SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}

	private static void registerBlock(Block block, Class<? extends ItemBlock> itemClass)
	{
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));

		SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help." + block.getUnlocalizedName().substring(5) + ".info"));
	}

	static boolean hasReinforcedPage = false;

	private static void registerReinforcedBlock(Block block)
	{
		GameRegistry.registerBlock(block, block.getUnlocalizedName().substring(5));

		if(!hasReinforcedPage)
		{
			SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.reinforced.info"));
			hasReinforcedPage = true;
		}
	}

	private static void registerReinforcedBlock(Block block, Class<? extends ItemBlock> itemClass)
	{
		GameRegistry.registerBlock(block, itemClass, block.getUnlocalizedName().substring(5));

		if(!hasReinforcedPage)
		{
			SecurityCraft.instance.manualPages.add(new SCManualPage(Item.getItemFromBlock(block), "help.reinforced.info"));
			hasReinforcedPage = true;
		}
	}

	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(Item item)
	{
		registerItem(item, item.getUnlocalizedName().substring(5));
	}

	/**
	 * Registers the given item with GameData.register_implItem(), and adds the help info for the item to the SecurityCraft manual item.
	 * Additionally, a configuration value can be set to have this item's recipe show as disabled in the manual.
	 */
	private static void registerItem(Item item, boolean configValue)
	{
		GameRegistry.registerItem(item, item.getUnlocalizedName().substring(5));
		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info", configValue));
	}

	/**
	 * Registers the given item with GameRegistry.registerItem(), and adds the help info for the item to the SecurityCraft manual item.
	 */
	private static void registerItem(Item item, String customName)
	{
		GameRegistry.registerItem(item, customName);

		SecurityCraft.instance.manualPages.add(new SCManualPage(item, "help." + item.getUnlocalizedName().substring(5) + ".info"));
	}
}
