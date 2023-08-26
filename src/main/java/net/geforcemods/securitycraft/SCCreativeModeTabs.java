package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import net.geforcemods.securitycraft.api.IReinforcedBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.util.SCItemGroup;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SCCreativeModeTabs {
	public static final Map<SCItemGroup, List<ItemStack>> STACKS_FOR_ITEM_GROUPS = Util.make(new EnumMap<>(SCItemGroup.class), map -> Arrays.stream(SCItemGroup.values()).forEach(key -> map.put(key, new ArrayList<>())));
	public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SecurityCraft.MODID);
	//@formatter:off
	public static final RegistryObject<CreativeModeTab> TECHNICAL_TAB = CREATIVE_MODE_TABS.register("technical", () -> CreativeModeTab.builder()
			.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
			.icon(() -> new ItemStack(SCContent.USERNAME_LOGGER.get()))
			.title(Component.translatable("itemGroup.securitycraft.technical"))
			.displayItems((itemDisplayParameters, output) -> {
				//@formatter:on
				output.accept(new ItemStack(SCContent.SC_MANUAL.get()));
				output.accept(new ItemStack(SCContent.FRAME.get()));
				output.accept(new ItemStack(SCContent.KEY_PANEL.get()));
				output.accept(new ItemStack(SCContent.KEYPAD.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_CHEST.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_BARREL.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_FURNACE.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_SMOKER.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_BLAST_FURNACE.get()));
				output.accept(new ItemStack(SCContent.DISPLAY_CASE.get()));
				output.accept(new ItemStack(SCContent.GLOW_DISPLAY_CASE.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_READER.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_LVL_1.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_LVL_2.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_LVL_3.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_LVL_4.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_LVL_5.get()));
				output.accept(new ItemStack(SCContent.KEYCARD_HOLDER.get()));
				output.accept(new ItemStack(SCContent.LIMITED_USE_KEYCARD.get()));
				output.accept(new ItemStack(SCContent.CODEBREAKER.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_KEY_CHANGER.get()));
				output.accept(new ItemStack(SCContent.RETINAL_SCANNER.get()));
				output.accept(new ItemStack(SCContent.LASER_BLOCK.get()));
				output.accept(new ItemStack(SCContent.INVENTORY_SCANNER.get()));
				output.accept(new ItemStack(SCContent.USERNAME_LOGGER.get()));
				output.accept(new ItemStack(SCContent.PORTABLE_RADAR.get()));
				output.accept(new ItemStack(SCContent.TROPHY_SYSTEM.get()));
				output.accept(new ItemStack(SCContent.RIFT_STABILIZER.get()));
				output.accept(new ItemStack(SCContent.BLOCK_CHANGE_DETECTOR.get()));
				output.accept(new ItemStack(SCContent.PROJECTOR.get()));
				output.accept(new ItemStack(SCContent.PROTECTO.get()));
				output.accept(new ItemStack(SCContent.MOTION_ACTIVATED_LIGHT.get()));
				output.accept(new ItemStack(SCContent.SECURITY_CAMERA.get()));
				output.accept(new ItemStack(SCContent.CAMERA_MONITOR.get()));
				output.accept(new ItemStack(SCContent.ALARM.get()));
				output.accept(new ItemStack(SCContent.PANIC_BUTTON.get()));
				output.accept(new ItemStack(SCContent.SENTRY.get()));
				output.accept(new ItemStack(SCContent.REMOTE_ACCESS_SENTRY.get()));
				output.accept(new ItemStack(SCContent.REMOTE_ACCESS_MINE.get()));
				output.accept(new ItemStack(SCContent.CAGE_TRAP.get()));
				output.accept(new ItemStack(SCContent.WIRE_CUTTERS.get()));
				output.accept(new ItemStack(SCContent.IRON_FENCE.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_FENCE_GATE.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_IRON_TRAPDOOR.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_TRAPDOOR.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				output.accept(new ItemStack(SCContent.SCANNER_DOOR_ITEM.get()));
				output.accept(new ItemStack(SCContent.KEYPAD_DOOR_ITEM.get()));
				output.accept(new ItemStack(SCContent.BLOCK_POCKET_MANAGER.get()));
				output.accept(new ItemStack(SCContent.BLOCK_POCKET_WALL.get()));
				output.accept(new ItemStack(SCContent.SONIC_SECURITY_SYSTEM.get()));
				output.accept(new ItemStack(SCContent.PORTABLE_TUNE_PLAYER.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_PISTON.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_STICKY_PISTON.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_OBSERVER.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_CAULDRON.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_LADDER.get()));
				output.accept(new ItemStack(SCContent.REINFORCED_HOPPER.get()));
				output.accept(new ItemStack(SCContent.LENS.get()));

				int colorAmount = SecurityCraft.RANDOM.nextInt(1, 4);
				List<DyeItem> list = new ArrayList<>();

				for (int i = 0; i < colorAmount; i++) {
					list.add(DyeItem.byColor(DyeColor.byId(SecurityCraft.RANDOM.nextInt(16))));
				}

				output.accept(DyeableLeatherItem.dyeArmor(new ItemStack(SCContent.LENS.get()), list));
				output.accept(new ItemStack(SCContent.ALLOWLIST_MODULE.get()));
				output.accept(new ItemStack(SCContent.DENYLIST_MODULE.get()));
				output.accept(new ItemStack(SCContent.DISGUISE_MODULE.get()));
				output.accept(new ItemStack(SCContent.REDSTONE_MODULE.get()));
				output.accept(new ItemStack(SCContent.SPEED_MODULE.get()));
				output.accept(new ItemStack(SCContent.SMART_MODULE.get()));
				output.accept(new ItemStack(SCContent.STORAGE_MODULE.get()));
				output.accept(new ItemStack(SCContent.HARMING_MODULE.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_BLOCK_MODIFIER.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_OWNER_CHANGER.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get()));
				output.accept(new ItemStack(SCContent.UNIVERSAL_BLOCK_REMOVER.get()));
				output.accept(new ItemStack(SCContent.TASER.get()));
				output.accept(new ItemStack(SCContent.BRIEFCASE.get()));
				output.accept(new ItemStack(SCContent.FAKE_WATER_BUCKET.get()));
				output.accept(new ItemStack(SCContent.FAKE_LAVA_BUCKET.get()));
				output.accept(new ItemStack(SCContent.ADMIN_TOOL.get()));
				output.acceptAll(STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.TECHNICAL));
			}).build());
	//@formatter:off
	public static final RegistryObject<CreativeModeTab> MINE_TAB = CREATIVE_MODE_TABS.register("mine", () -> CreativeModeTab.builder()
			.withTabsBefore(TECHNICAL_TAB.getKey())
			.icon(() -> new ItemStack(SCContent.MINE.get()))
			.title(Component.translatable("itemGroup.securitycraft.explosives"))
			.displayItems((itemDisplayParameters, output) -> {
				//@formatter:on
				List<Item> vanillaOrderedItems = getVanillaOrderedItems();
				List<ItemStack> mineGroupItems = STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.EXPLOSIVES);

				mineGroupItems.sort((a, b) -> {
					//if a isn't an item that has a vanilla counterpart, it should appear at the front
					if (!(a.getItem() instanceof BlockItem blockItemA && blockItemA.getBlock() instanceof BaseFullMineBlock blockMineA))
						return -1;

					//same for b
					if (!(b.getItem() instanceof BlockItem blockItemB && blockItemB.getBlock() instanceof BaseFullMineBlock blockMineB))
						return 1;

					return Integer.compare(vanillaOrderedItems.indexOf(blockMineA.getBlockDisguisedAs().asItem()), vanillaOrderedItems.indexOf(blockMineB.getBlockDisguisedAs().asItem()));
				});
				output.accept(SCContent.REMOTE_ACCESS_MINE.get());
				output.accept(SCContent.WIRE_CUTTERS.get());
				output.accept(Items.FLINT_AND_STEEL);
				output.accept(SCContent.MINE.get());
				output.acceptAll(mineGroupItems);
				output.accept(new ItemStack(SCContent.ANCIENT_DEBRIS_MINE_ITEM.get()));
				output.accept(new ItemStack(SCContent.FURNACE_MINE.get()));
				output.accept(new ItemStack(SCContent.SMOKER_MINE.get()));
				output.accept(new ItemStack(SCContent.BLAST_FURNACE_MINE.get()));
			}).build());
	//@formatter:off
	public static final RegistryObject<CreativeModeTab> DECORATION_TAB = CREATIVE_MODE_TABS.register("decoration", () -> CreativeModeTab.builder()
			.withTabsBefore(MINE_TAB.getKey())
			.icon(() -> new ItemStack(SCContent.REINFORCED_OAK_STAIRS.get()))
			.title(Component.translatable("itemGroup.securitycraft.decoration"))
			.displayItems((itemDisplayParameters, output) -> {
				//@formatter:on
				List<Item> vanillaOrderedItems = getVanillaOrderedItems();
				List<ItemStack> decorationGroupItems = STACKS_FOR_ITEM_GROUPS.get(SCItemGroup.DECORATION);

				decorationGroupItems.sort((a, b) -> {
					//if a isn't an item that has a vanilla counterpart, it should appear at the back
					if (!(a.getItem() instanceof BlockItem blockItemA && blockItemA.getBlock() instanceof IReinforcedBlock reinforcedBlockA))
						return 1;

					//same for b
					if (!(b.getItem() instanceof BlockItem blockItemB && blockItemB.getBlock() instanceof IReinforcedBlock reinforcedBlockB))
						return -1;

					int indexA = vanillaOrderedItems.indexOf(reinforcedBlockA.getVanillaBlock().asItem());

					//items that have no counterpart in any of the above vanilla tabs should appear at the end
					if (indexA == -1)
						return 1;

					int indexB = vanillaOrderedItems.indexOf(reinforcedBlockB.getVanillaBlock().asItem());

					//same here
					if (indexB == -1)
						return -1;

					return Integer.compare(indexA, indexB);
				});

				//loop starts from the back, because the reinforced bookshelf is expected to be towards the end of the list
				//can't use indexOf, because ItemStack does not implement Object#equals
				for (int i = decorationGroupItems.size() - 1; i >= 0; i--) {
					//sort secret signs after the reinforced bookshelf
					if (decorationGroupItems.get(i).getItem() == SCContent.REINFORCED_BOOKSHELF.get().asItem()) {
						decorationGroupItems.addAll(i + 1, List.of( //@formatter:off
								new ItemStack(SCContent.SECRET_OAK_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_OAK_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_SPRUCE_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_SPRUCE_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_BIRCH_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_BIRCH_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_JUNGLE_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_JUNGLE_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_ACACIA_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_ACACIA_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_DARK_OAK_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_DARK_OAK_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_MANGROVE_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_MANGROVE_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_CHERRY_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_CHERRY_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_BAMBOO_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_BAMBOO_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_CRIMSON_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_CRIMSON_HANGING_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_WARPED_SIGN_ITEM.get()),
								new ItemStack(SCContent.SECRET_WARPED_HANGING_SIGN_ITEM.get())));
						//@formatter:on
						break;
					}
				}

				decorationGroupItems.add(new ItemStack(SCContent.CRYSTAL_QUARTZ_BLOCK.get()));
				decorationGroupItems.add(new ItemStack(SCContent.CRYSTAL_QUARTZ_STAIRS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.CRYSTAL_QUARTZ_SLAB.get()));
				decorationGroupItems.add(new ItemStack(SCContent.CHISELED_CRYSTAL_QUARTZ.get()));
				decorationGroupItems.add(new ItemStack(SCContent.CRYSTAL_QUARTZ_BRICKS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.CRYSTAL_QUARTZ_PILLAR.get()));
				decorationGroupItems.add(new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ.get()));
				decorationGroupItems.add(new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ_STAIRS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.SMOOTH_CRYSTAL_QUARTZ_SLAB.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_BLOCK.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_STAIRS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_SLAB.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CHISELED_CRYSTAL_QUARTZ.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_BRICKS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_CRYSTAL_QUARTZ_PILLAR.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_STAIRS.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_SMOOTH_CRYSTAL_QUARTZ_SLAB.get()));
				decorationGroupItems.add(new ItemStack(SCContent.BLOCK_POCKET_WALL.get()));
				decorationGroupItems.add(new ItemStack(SCContent.IRON_FENCE.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_FENCE_GATE.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_IRON_TRAPDOOR.get()));
				decorationGroupItems.add(new ItemStack(SCContent.REINFORCED_DOOR_ITEM.get()));
				decorationGroupItems.add(new ItemStack(SCContent.SCANNER_DOOR_ITEM.get()));
				decorationGroupItems.add(new ItemStack(SCContent.KEYPAD_DOOR_ITEM.get()));
				decorationGroupItems.add(new ItemStack(SCContent.DISPLAY_CASE.get()));
				decorationGroupItems.add(new ItemStack(SCContent.GLOW_DISPLAY_CASE.get()));
				output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_1.get());
				output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_2.get());
				output.accept(SCContent.UNIVERSAL_BLOCK_REINFORCER_LVL_3.get());
				output.accept(SCContent.UNIVERSAL_BLOCK_REMOVER.get());
				output.acceptAll(decorationGroupItems);
			}).build());

	private static List<Item> getVanillaOrderedItems() {
		List<Item> vanillaOrderedItems = new ArrayList<>();

		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.BUILDING_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.COLORED_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.NATURAL_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.FUNCTIONAL_BLOCKS));
		vanillaOrderedItems.addAll(getCreativeTabItems(CreativeModeTabs.REDSTONE_BLOCKS));
		return vanillaOrderedItems;
	}

	private static List<Item> getCreativeTabItems(ResourceKey<CreativeModeTab> tabKey) {
		return BuiltInRegistries.CREATIVE_MODE_TAB.get(tabKey).getDisplayItems().stream().map(ItemStack::getItem).toList();
	}

	private SCCreativeModeTabs() {}
}
