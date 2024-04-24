package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.scores.PlayerTeam;

public class ModuleItem extends Item {
	private static final MutableComponent MODIFIABLE = Component.translatable("tooltip.securitycraft:module.modifiable").setStyle(Utils.GRAY_STYLE);
	private static final MutableComponent NOT_MODIFIABLE = Component.translatable("tooltip.securitycraft:module.notModifiable").setStyle(Utils.GRAY_STYLE);
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean containsCustomData;
	private final boolean canBeCustomized;

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData) {
		this(properties, module, containsCustomData, false);
	}

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData, boolean canBeCustomized) {
		super(properties);
		this.module = module;
		this.containsCustomData = containsCustomData;
		this.canBeCustomized = canBeCustomized;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		if (ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof IModuleInventory inv) {
			ItemStack stack = ctx.getItemInHand();
			ModuleType type = ((ModuleItem) stack.getItem()).getModuleType();

			if (inv instanceof IOwnable ownable && !ownable.isOwnedBy(ctx.getPlayer()))
				return InteractionResult.PASS;

			if (inv.acceptsModule(type) && !inv.hasModule(type)) {
				inv.insertModule(stack, false);

				if (inv instanceof LinkableBlockEntity linkable)
					linkable.propagate(new ILinkedAction.ModuleInserted(stack, (ModuleItem) stack.getItem(), false), linkable);

				if (!ctx.getPlayer().isCreative())
					stack.shrink(1);

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (canBeCustomized()) {
			if (module == ModuleType.ALLOWLIST || module == ModuleType.DENYLIST) {
				if (level.isClientSide)
					ClientHandler.displayEditModuleScreen(stack);

				return InteractionResultHolder.consume(stack);
			}
			else if (module == ModuleType.DISGUISE) {
				if (!level.isClientSide) {
					player.openMenu(new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
							return new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(player.getItemInHand(hand)));
						}

						@Override
						public Component getDisplayName() {
							return Component.translatable(getDescriptionId());
						}
					});
				}

				return InteractionResultHolder.consume(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, List<Component> list, TooltipFlag flag) {
		if (containsCustomData || canBeCustomized())
			list.add(MODIFIABLE);
		else
			list.add(NOT_MODIFIABLE);

		if (canBeCustomized()) {
			Block addon = getBlockAddon(stack);

			if (addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getDescriptionId())).setStyle(Utils.GRAY_STYLE));
		}

		if (containsCustomData) {
			boolean affectsEveryone = false;
			int playerCount = 0;
			int teamCount = 0;
			CompoundTag tag = Utils.getTag(stack);

			if (tag != null && !tag.isEmpty()) {
				affectsEveryone = tag.getBoolean("affectEveryone");

				if (!affectsEveryone) {
					playerCount = ModuleItem.getPlayersFromModule(stack).size();
					teamCount = tag.getList("ListedTeams", Tag.TAG_STRING).size();
				}
			}

			if (affectsEveryone)
				list.add(Utils.localize("tooltip.securitycraft:module.affects_everyone").setStyle(Utils.GRAY_STYLE));
			else {
				list.add(Utils.localize("tooltip.securitycraft:module.added_players", playerCount).setStyle(Utils.GRAY_STYLE));
				list.add(Utils.localize("tooltip.securitycraft:module.added_teams", teamCount).setStyle(Utils.GRAY_STYLE));
			}
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public static Block getBlockAddon(ItemStack moduleStack) {
		if (!moduleStack.has(DataComponents.CONTAINER))
			return null;

		List<ItemStack> stacks = moduleStack.get(DataComponents.CONTAINER).nonEmptyStream().toList();

		if (!stacks.isEmpty() && stacks.getFirst().getItem() instanceof BlockItem blockItem)
			return blockItem.getBlock();

		return null;
	}

	public boolean canBeCustomized() {
		return canBeCustomized;
	}

	public static boolean doesModuleHaveTeamOf(ItemStack module, String name, Level level) {
		PlayerTeam team = level.getScoreboard().getPlayersTeam(name);

		//@formatter:off
		return team != null && Utils.getTag(module).getList("ListedTeams", Tag.TAG_STRING)
				.stream()
				.filter(StringTag.class::isInstance)
				.map(tag -> ((StringTag) tag).getAsString())
				.anyMatch(team.getName()::equals);
		//@formatter:on
	}

	public static List<String> getPlayersFromModule(ItemStack stack) {
		List<String> list = new ArrayList<>();

		if (stack.getItem() instanceof ModuleItem) {
			CompoundTag tag = Utils.getTag(stack);

			for (int i = 1; i <= MAX_PLAYERS; i++) {
				String player = tag.getString("Player" + i);

				if (player != null && !player.isEmpty())
					list.add(player.toLowerCase());
			}
		}

		return list;
	}
}
