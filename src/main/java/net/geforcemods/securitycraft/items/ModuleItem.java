package net.geforcemods.securitycraft.items;

import java.util.List;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.components.ListModuleData;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class ModuleItem extends Item {
	private static final MutableComponent MODIFIABLE = Component.translatable("tooltip.securitycraft:module.modifiable").setStyle(Utils.GRAY_STYLE);
	private static final MutableComponent NOT_MODIFIABLE = Component.translatable("tooltip.securitycraft:module.notModifiable").setStyle(Utils.GRAY_STYLE);
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
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (canBeCustomized()) {
			if (module == ModuleType.ALLOWLIST || module == ModuleType.DENYLIST) {
				if (level.isClientSide)
					ClientHandler.displayEditModuleScreen(stack);

				return InteractionResult.CONSUME;
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

				return InteractionResult.CONSUME;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext ctx, TooltipDisplay display, Consumer<Component> tooltipAdder, TooltipFlag flag) {
		if (containsCustomData || canBeCustomized())
			tooltipAdder.accept(MODIFIABLE);
		else
			tooltipAdder.accept(NOT_MODIFIABLE);

		if (canBeCustomized()) {
			Block addon = getBlockAddon(stack);

			if (addon != null)
				tooltipAdder.accept(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getDescriptionId())).setStyle(Utils.GRAY_STYLE));
		}

		if (containsCustomData) {
			ListModuleData listModuleData = stack.get(SCContent.LIST_MODULE_DATA);

			if (listModuleData != null)
				listModuleData.addToTooltip(ctx, tooltipAdder, flag, stack.getComponents());
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
}
