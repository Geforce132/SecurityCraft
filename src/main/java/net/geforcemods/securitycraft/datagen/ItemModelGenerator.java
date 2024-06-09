package net.geforcemods.securitycraft.datagen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SCCreativeModeTabs;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.BrushableMineBlock;
import net.geforcemods.securitycraft.blocks.mines.DeepslateMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedButtonBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedChiseledBookshelfBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedFenceBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPistonBaseBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedStainedGlassPaneBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedWallBlock;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile.UncheckedModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;

public class ItemModelGenerator extends ItemModelProvider {
	public ItemModelGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, SecurityCraft.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		List<Item> mineTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES).stream().map(ItemStack::getItem).toList();
		List<Item> decorationTabItems = SCCreativeModeTabs.STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION).stream().map(ItemStack::getItem).toList();
		Map<Block, String> flatReinforcedItems = new HashMap<>();

		flatReinforcedItems.put(SCContent.REINFORCED_CAULDRON.get(), "item/cauldron");
		flatReinforcedItems.put(SCContent.REINFORCED_CHAIN.get(), "item/chain");
		flatReinforcedItems.put(SCContent.REINFORCED_COBWEB.get(), "block/cobweb");
		flatReinforcedItems.put(SCContent.REINFORCED_HOPPER.get(), "item/hopper");
		flatReinforcedItems.put(SCContent.REINFORCED_IRON_BARS.get(), "securitycraft:block/reinforced_iron_bars");
		flatReinforcedItems.put(SCContent.REINFORCED_LADDER.get(), "block/ladder");
		flatReinforcedItems.put(SCContent.REINFORCED_LANTERN.get(), "item/lantern");
		flatReinforcedItems.put(SCContent.REINFORCED_LEVER.get(), "block/lever");
		flatReinforcedItems.put(SCContent.REINFORCED_SOUL_LANTERN.get(), "item/soul_lantern");

		for (DeferredHolder<Block, ? extends Block> obj : SCContent.BLOCKS.getEntries()) {
			Block block = obj.get();
			Item item = block.asItem();

			if (decorationTabItems.contains(item)) {
				if (flatReinforcedItems.containsKey(block))
					flatReinforcedItem(block, flatReinforcedItems.get(block));
				else if (block instanceof ReinforcedStainedGlassPaneBlock)
					reinforcedStainedPane(block);
				else if (block instanceof ReinforcedWallBlock wall)
					reinforcedWallInventory(block, wall.getVanillaBlock());
				else if (block instanceof ReinforcedButtonBlock || block instanceof ReinforcedPistonBaseBlock || block instanceof ReinforcedChiseledBookshelfBlock)
					reinforcedBlockInventory(block);
				else if (block instanceof ReinforcedFenceBlock) {
					ResourceLocation registryName = Utils.getRegistryName(block);

					parent(registryName.toString(), modBlock(registryName.getPath() + "_inventory"));
				}
				else if (block instanceof IReinforcedBlock)
					simpleReinforcedParent(block);
			}
			else if (mineTabItems.contains(item) && block instanceof BaseFullMineBlock mine && !(mine instanceof DeepslateMineBlock || mine instanceof BrushableMineBlock))
				blockMine(mine.getBlockDisguisedAs(), block);
		}

		List<DeferredHolder<Item, ? extends Item>> singleTextureItems = new ArrayList<>(SCContent.ITEMS.getEntries());
		//@formatter:off
		List<DeferredItem<? extends Item>> handheldItems = List.of(
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1,
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2,
				SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3,
				SCContent.UNIVERSAL_KEY_CHANGER);
		List<DeferredItem<? extends Item>> linkingStateItems = List.of(
				SCContent.CAMERA_MONITOR,
				SCContent.MINE_REMOTE_ACCESS_TOOL,
				SCContent.SENTRY_REMOTE_ACCESS_TOOL,
				SCContent.SONIC_SECURITY_SYSTEM_ITEM);

		singleTextureItems.removeAll(List.of(
				SCContent.ANCIENT_DEBRIS_MINE_ITEM,
				SCContent.BRIEFCASE,
				SCContent.CODEBREAKER,
				SCContent.DISPLAY_CASE_ITEM,
				SCContent.GLOW_DISPLAY_CASE_ITEM,
				SCContent.KEYCARD_HOLDER,
				SCContent.KEYPAD_CHEST_ITEM,
				SCContent.LENS,
				SCContent.REDSTONE_MODULE,
				SCContent.SPEED_MODULE,
				SCContent.TASER,
				SCContent.TASER_POWERED,
				SCContent.UNIVERSAL_BLOCK_MODIFIER,
				SCContent.UNIVERSAL_BLOCK_REMOVER,
				SCContent.UNIVERSAL_OWNER_CHANGER,
				SCContent.WIRE_CUTTERS));
		singleTextureItems.removeAll(handheldItems);
		singleTextureItems.removeAll(linkingStateItems);
		//@formatter:on

		for (DeferredHolder<Item, ? extends Item> obj : singleTextureItems) {
			simpleItem(obj.get(), "item/generated");
		}

		for (DeferredHolder<Item, ? extends Item> obj : handheldItems) {
			simpleItem(obj.get(), "item/handheld");
		}

		//@formatter:off
		//gui block mine model
		getBuilder("template_block_mine")
		.parent(new UncheckedModelFile(BLOCK_FOLDER + "/block"))
		.texture("overlay", modItem("block_mine_overlay"))
		.texture("particle", "#north")
		//normal block
		.element().from(0, 0, 0).to(16, 16, 16).allFaces((dir, builder) -> builder.cullface(dir).texture("#" + dir.getName()).end()).end()
		//overlay
		.element().from(0, 0, 0).to(16, 16, 16).face(Direction.UP).cullface(Direction.UP).texture("#overlay").end().end();
		//@formatter:on

		linkingStateItems.forEach(this::linkingStateItem);
		codebreaker();
		blockMine(SCContent.ANCIENT_DEBRIS_MINE.get(), mcBlock("ancient_debris_side"), mcBlock("ancient_debris_side"), mcBlock("ancient_debris_top"));
		blockMine(SCContent.FURNACE_MINE.get(), mcBlock("furnace_side"), mcBlock("furnace_front"), mcBlock("furnace_top"));
		blockMine(SCContent.SMOKER_MINE.get(), mcBlock("smoker_side"), mcBlock("smoker_front"), mcBlock("smoker_top"));
		blockMine(SCContent.BLAST_FURNACE_MINE.get(), mcBlock("blast_furnace_side"), mcBlock("blast_furnace_front"), mcBlock("blast_furnace_top"));
		simpleParent(SCContent.BLOCK_CHANGE_DETECTOR.get());
		simpleParent(SCContent.CRYSTAL_QUARTZ_SLAB.get());
		simpleParent(SCContent.CRYSTAL_QUARTZ_STAIRS.get());
		simpleParent(SCContent.FLOOR_TRAP.get());
		simpleParent(SCContent.KEYPAD_TRAPDOOR.get(), "keypad_trapdoor_bottom");
		simpleParent(SCContent.SCANNER_TRAPDOOR.get(), "scanner_trapdoor_bottom");
		simpleParent(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get());
		simpleParent(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get());
		simpleReinforcedParent(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get());
		simpleReinforcedParent(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get());
		simpleReinforcedParent(SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get());
		simpleReinforcedParent(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get());
		simpleReinforcedParent(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get());
		simpleReinforcedParent(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get());
		simpleReinforcedParent(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get());
		simpleReinforcedParent(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get());
		simpleReinforcedParent(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get());
		simpleParent(SCContent.REINFORCED_GLASS.get());
		reinforcedPane(SCContent.REINFORCED_GLASS_PANE.get());
		simpleParent(SCContent.REINFORCED_IRON_TRAPDOOR.get(), "reinforced_iron_trapdoor_bottom");
		reinforcedWallInventory(SCContent.REINFORCED_BRICK_WALL.get(), "bricks");
		reinforcedWallInventory(SCContent.REINFORCED_MOSSY_STONE_BRICK_WALL.get(), "mossy_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_STONE_BRICK_WALL.get(), "stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_MUD_BRICK_WALL.get(), "mud_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_NETHER_BRICK_WALL.get(), "nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_RED_NETHER_BRICK_WALL.get(), "red_nether_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_END_STONE_BRICK_WALL.get(), "end_stone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_POLISHED_BLACKSTONE_BRICK_WALL.get(), "polished_blackstone_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_DEEPSLATE_BRICK_WALL.get(), "deepslate_bricks");
		reinforcedWallInventory(SCContent.REINFORCED_DEEPSLATE_TILE_WALL.get(), "deepslate_tiles");
	}

	public void linkingStateItem(DeferredHolder<Item, ? extends Item> item) {
		String hasLinkedPath = item.getId().getPath();
		String defaultPath = hasLinkedPath + "_idle";
		String notLinkedPath = hasLinkedPath + "_not_linked";
		String linkedPath = hasLinkedPath + "_linked";

		//@formatter:off
		simpleItem(defaultPath, "item/generated");
		simpleItem(notLinkedPath, "item/generated");
		simpleItem(linkedPath, "item/generated");
		simpleItem(hasLinkedPath, "item/generated")
			.override().predicate(ClientHandler.LINKING_STATE_PROPERTY, ClientHandler.EMPTY_STATE).model(new UncheckedModelFile(modItem(defaultPath))).end()
			.override().predicate(ClientHandler.LINKING_STATE_PROPERTY, ClientHandler.UNKNOWN_STATE).model(new UncheckedModelFile(modItem(hasLinkedPath))).end()
			.override().predicate(ClientHandler.LINKING_STATE_PROPERTY, ClientHandler.NOT_LINKED_STATE).model(new UncheckedModelFile(modItem(notLinkedPath))).end()
			.override().predicate(ClientHandler.LINKING_STATE_PROPERTY, ClientHandler.LINKED_STATE).model(new UncheckedModelFile(modItem(linkedPath))).end();
		//@formatter:on
	}

	public void codebreaker() {
		String defaultPath = SCContent.CODEBREAKER.getId().getPath();
		String decodingPath = defaultPath + "_decoding";
		String failurePath = defaultPath + "_failure";
		String successPath = defaultPath + "_success";

		//@formatter:off
		simpleItem(decodingPath, "item/generated");
		simpleItem(failurePath, "item/generated");
		simpleItem(successPath, "item/generated");
		simpleItem(defaultPath, "item/generated")
			.override().predicate(CodebreakerItem.STATE_PROPERTY, 0.0F).model(new UncheckedModelFile(modItem(defaultPath))).end()
			.override().predicate(CodebreakerItem.STATE_PROPERTY, 0.25F).model(new UncheckedModelFile(modItem(decodingPath))).end()
			.override().predicate(CodebreakerItem.STATE_PROPERTY, 0.5F).model(new UncheckedModelFile(modItem(failurePath))).end()
			.override().predicate(CodebreakerItem.STATE_PROPERTY, 0.75F).model(new UncheckedModelFile(modItem(successPath))).end();
		//@formatter:on
	}

	public ItemModelBuilder simpleItem(Item item, String parent) {
		return simpleItem(Utils.getRegistryName(item).getPath(), parent);
	}

	public ItemModelBuilder simpleItem(String path, String parent) {
		return singleTexture(path, mcLoc(parent), "layer0", modItem(path));
	}

	public ItemModelBuilder flatReinforcedItem(Block block, String texturePath) {
		return singleTexture(name(block), mcLoc("item/generated"), "layer0", ResourceLocation.parse(texturePath));
	}

	public ItemModelBuilder reinforcedStainedPane(Block block) {
		return reinforcedPane(block).renderType("translucent");
	}

	public ItemModelBuilder reinforcedPane(Block block) {
		String name = name(block);

		return getBuilder(name).parent(new UncheckedModelFile("item/generated")).texture("layer0", modBlock(name.replace("_pane", "")));
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, Block vanillaBlock) {
		return reinforcedWallInventory(block, Utils.getRegistryName(vanillaBlock).getPath().replace("reinforced_", "").replace("_wall", ""));
	}

	public ItemModelBuilder reinforcedWallInventory(Block block, String textureName) {
		return uncheckedSingleTexture(Utils.getRegistryName(block).toString(), modBlock("reinforced_wall_inventory"), "wall", SecurityCraft.mcResLoc("block/" + textureName));
	}

	public ItemModelBuilder reinforcedBlockInventory(Block block) {
		String path = name(block);

		return parent(path, modBlock(path + "_inventory"));
	}

	public ItemModelBuilder uncheckedSingleTexture(String name, ResourceLocation parent, String textureKey, ResourceLocation texture) {
		return parent(name, parent).texture(textureKey, texture);
	}

	public ItemModelBuilder blockMine(Block vanillaBlock, Block block) {
		ResourceLocation texture = mcBlock(Utils.getRegistryName(vanillaBlock).getPath());

		return blockMine(block, texture, texture, texture);
	}

	public ItemModelBuilder blockMine(Block block, ResourceLocation sideTexture, ResourceLocation frontTexture, ResourceLocation bottomTopTexture) {
		//@formatter:off
		return parent(Utils.getRegistryName(block).toString(), modItem("template_block_mine"))
				.texture("down", bottomTopTexture)
				.texture("up", bottomTopTexture)
				.texture("north", frontTexture)
				.texture("east", sideTexture)
				.texture("south", sideTexture)
				.texture("west", sideTexture);
		//@formatter:on
	}

	public ItemModelBuilder simpleReinforcedParent(Block block) {
		String name = name(block);

		return parent(name, modBlock(name.replace("crystal_", "")));
	}

	public ItemModelBuilder simpleParent(Block block) {
		return simpleParent(block, name(block));
	}

	public ItemModelBuilder simpleParent(Block block, String parent) {
		return parent(name(block), modBlock(parent));
	}

	public ItemModelBuilder parent(String name, ResourceLocation parent) {
		return getBuilder(name).parent(new UncheckedModelFile(parent));
	}

	public ResourceLocation mcBlock(String path) {
		return mcLoc(BLOCK_FOLDER + "/" + path);
	}

	public ResourceLocation modBlock(String path) {
		return modLoc(BLOCK_FOLDER + "/" + path);
	}

	public ResourceLocation modItem(String path) {
		return modLoc(ITEM_FOLDER + "/" + path);
	}

	@Override
	public String getName() {
		return "SecurityCraft Item Models";
	}

	private String name(Block block) {
		return Utils.getRegistryName(block).getPath();
	}
}
