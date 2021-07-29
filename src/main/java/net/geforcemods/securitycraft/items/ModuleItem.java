package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
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
	private final boolean nbtCanBeModified;
	private boolean canBeCustomized;
	private int numberOfItemAddons;
	private int numberOfBlockAddons;

	public ModuleItem(Item.Properties properties, ModuleType module, boolean nbtCanBeModified){
		this(properties, module, nbtCanBeModified, false, 0, 0);
	}

	public ModuleItem(Item.Properties properties, ModuleType module, boolean nbtCanBeModified, boolean canBeCustomized){
		this(properties, module, nbtCanBeModified, canBeCustomized, 0, 0);
	}

	public ModuleItem(Item.Properties properties, ModuleType module, boolean nbtCanBeModified, boolean canBeCustomized, int itemAddons, int blockAddons){
		super(properties);
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		this.canBeCustomized = canBeCustomized;
		numberOfItemAddons = itemAddons;
		numberOfBlockAddons = blockAddons;
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx)
	{
		BlockEntity te = ctx.getLevel().getBlockEntity(ctx.getClickedPos());
		ItemStack stack = ctx.getItemInHand();

		if(te instanceof IModuleInventory inv)
		{
			ModuleType type = ((ModuleItem)stack.getItem()).getModuleType();

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
							return new DisguiseModuleContainer(windowId, inv, new ModuleItemInventory(player.getItemInHand(hand)));
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
		if(nbtCanBeModified || canBeCustomized())
			list.add(new TranslatableComponent("tooltip.securitycraft:module.modifiable").setStyle(GRAY_STYLE));
		else
			list.add(new TranslatableComponent("tooltip.securitycraft:module.notModifiable").setStyle(GRAY_STYLE));

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems", numberOfBlockAddons, numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.usage.items", numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks", numberOfBlockAddons).setStyle(GRAY_STYLE));

			if(getNumberOfAddons() > 0 && !getAddons(stack.getTag()).isEmpty()) {
				list.add(TextComponent.EMPTY);

				list.add(Utils.localize("tooltip.securitycraft:module.itemAddons.added").setStyle(GRAY_STYLE));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new TextComponent("- ").append(Utils.localize(addon.getDescriptionId())).setStyle(GRAY_STYLE));
			}
		}
	}

	public ModuleType getModuleType() {
		return module;
	}

	public int getNumberOfAddons(){
		return numberOfItemAddons + numberOfBlockAddons;
	}

	public int getNumberOfItemAddons(){
		return numberOfItemAddons;
	}

	public int getNumberOfBlockAddons(){
		return numberOfBlockAddons;
	}

	public ArrayList<Block> getBlockAddons(CompoundTag tag){
		ArrayList<Block> list = new ArrayList<>();

		if(tag == null) return list;

		ListTag items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons) {
				ItemStack stack = ItemStack.of(item);

				if(stack.getItem() instanceof BlockItem)
					list.add(Block.byItem(stack.getItem()));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(CompoundTag tag){
		ArrayList<ItemStack> list = new ArrayList<>();

		if(tag == null) return list;

		ListTag items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundTag item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons)
				list.add(ItemStack.of(item));
		}

		return list;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
