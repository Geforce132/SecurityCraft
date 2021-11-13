package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.inventory.DisguiseModuleMenu;
import net.geforcemods.securitycraft.inventory.ModuleItemContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class ModuleItem extends Item{

	public static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final int MAX_PLAYERS = 50;
	private final ModuleType module;
	private final boolean containsCustomData;
	private final boolean canBeCustomized;

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData){
		this(properties, module, containsCustomData, false);
	}

	public ModuleItem(Item.Properties properties, ModuleType module, boolean containsCustomData, boolean canBeCustomized){
		super(properties);
		this.module = module;
		this.containsCustomData = containsCustomData;
		this.canBeCustomized = canBeCustomized;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		BlockEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
		ItemStack stack = ctx.getItemInHand();

		if(te instanceof IModuleInventory inv)
		{
			ModuleType type = ((ModuleItem)stack.getItem()).getModuleType();

			if(te instanceof IOwnable ownable && !ownable.getOwner().isOwner(ctx.getPlayer()))
				return InteractionResult.PASS;

			if(inv.getAcceptedModules().contains(type) && !inv.hasModule(type))
			{
				inv.insertModule(stack);
				inv.onModuleInserted(stack, type);

				if(!ctx.getPlayer().isCreative())
					stack.shrink(1);

				return InteractionResult.SUCCESS;
			}
		}

		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		if(canBeCustomized())
		{
			if(module == ModuleType.ALLOWLIST || module == ModuleType.DENYLIST) {
				if(world.isClientSide)
					ClientHandler.displayEditModuleGui(stack);

				return InteractionResultHolder.consume(stack);
			}
			else if(module == ModuleType.DISGUISE)
			{
				if (!world.isClientSide) {
					NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new DisguiseModuleMenu(windowId, inv, new ModuleItemContainer(player.getItemInHand(hand)));
						}

						@Override
						public Component getDisplayName()
						{
							return new TranslatableComponent(getDescriptionId());
						}
					});
				}

				return InteractionResultHolder.consume(stack);
			}
		}

		return InteractionResultHolder.pass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag flag) {
		if(containsCustomData || canBeCustomized())
			list.add(new TranslatableComponent("tooltip.securitycraft:module.modifiable").setStyle(GRAY_STYLE));
		else
			list.add(new TranslatableComponent("tooltip.securitycraft:module.notModifiable").setStyle(GRAY_STYLE));

		if(canBeCustomized()) {
			Block addon = getBlockAddon(stack.getTag());

			if(addon != null)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added", Utils.localize(addon.getDescriptionId())).setStyle(GRAY_STYLE));
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public Block getBlockAddon(CompoundTag tag){
		if(tag == null)
			return null;

		ListTag items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		if(items != null && !items.isEmpty())
		{
			if(ItemStack.of(items.getCompound(0)).getItem() instanceof BlockItem blockItem)
				return blockItem.getBlock();
		}

		return null;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
