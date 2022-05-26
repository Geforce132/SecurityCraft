package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModuleItem extends Item {
	private static final IFormattableTextComponent MODIFIABLE = new TranslationTextComponent("tooltip.securitycraft:module.modifiable").setStyle(Utils.GRAY_STYLE);
	private static final IFormattableTextComponent NOT_MODIFIABLE = new TranslationTextComponent("tooltip.securitycraft:module.notModifiable").setStyle(Utils.GRAY_STYLE);
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean containsCustomData;
	private boolean canBeCustomized;

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
	public ActionResultType useOn(ItemUseContext ctx) {
		TileEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
		ItemStack stack = ctx.getItemInHand();

		if (te instanceof IModuleInventory) {
			IModuleInventory inv = (IModuleInventory) te;
			ModuleType type = ((ModuleItem) stack.getItem()).getModuleType();

			if (te instanceof IOwnable && !((IOwnable) te).getOwner().isOwner(ctx.getPlayer()))
				return ActionResultType.PASS;

			if (inv.acceptsModule(type) && !inv.hasModule(type)) {
				inv.insertModule(stack, false);

				if (inv instanceof LinkableBlockEntity)
					ModuleUtils.createLinkedAction(LinkedAction.MODULE_INSERTED, stack, (LinkableBlockEntity) inv, false);

				if (!ctx.getPlayer().isCreative())
					stack.shrink(1);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if (canBeCustomized()) {
			if (module == ModuleType.ALLOWLIST || module == ModuleType.DENYLIST) {
				if (world.isClientSide)
					ClientHandler.displayEditModuleScreen(stack);

				return ActionResult.consume(stack);
			}
			else if (module == ModuleType.DISGUISE) {
				if (!world.isClientSide) {
					NetworkHooks.openGui((ServerPlayerEntity) player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
							return new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(player.getItemInHand(hand)));
						}

						@Override
						public ITextComponent getDisplayName() {
							return new TranslationTextComponent(getDescriptionId());
						}
					});
				}

				return ActionResult.consume(stack);
			}
		}

		return ActionResult.pass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if (containsCustomData || canBeCustomized())
			list.add(MODIFIABLE);
		else
			list.add(NOT_MODIFIABLE);

		if (canBeCustomized()) {
			Block addon = getBlockAddon(stack.getTag());

			if (addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getDescriptionId())).setStyle(Utils.GRAY_STYLE));
		}

		if (containsCustomData) {
			boolean affectsEveryone = false;
			int playerCount = 0;
			int teamCount = 0;

			if (stack.hasTag()) {
				CompoundNBT tag = stack.getTag();

				affectsEveryone = tag.getBoolean("affectEveryone");

				if (!affectsEveryone) {
					playerCount = ModuleUtils.getPlayersFromModule(stack).size();
					teamCount = tag.getList("ListedTeams", Constants.NBT.TAG_STRING).size();
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

	public Block getBlockAddon(CompoundNBT tag) {
		if (tag == null)
			return null;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		if (items != null && !items.isEmpty()) {
			Item item = ItemStack.of(items.getCompound(0)).getItem();

			if (item instanceof BlockItem)
				return ((BlockItem) item).getBlock();
		}

		return null;
	}

	public boolean canBeCustomized() {
		return canBeCustomized;
	}
}
