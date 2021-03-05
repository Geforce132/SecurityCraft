package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.inventory.ModuleItemInventory;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModuleItem extends Item{

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
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		TileEntity te = ctx.getWorld().getTileEntity(ctx.getPos());
		ItemStack stack = ctx.getItem();

		if(te instanceof IModuleInventory)
		{
			IModuleInventory inv = (IModuleInventory)te;
			ModuleType type = ((ModuleItem)stack.getItem()).getModuleType();

			if(inv.getAcceptedModules().contains(type) && !inv.hasModule(type))
			{
				inv.insertModule(stack);
				inv.onModuleInserted(stack, type);

				if(!ctx.getPlayer().isCreative())
					stack.shrink(1);

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(canBeCustomized())
		{
			if(module == ModuleType.WHITELIST || module == ModuleType.BLACKLIST) {
				SecurityCraft.proxy.displayEditModuleGui(stack);
				return ActionResult.newResult(ActionResultType.SUCCESS, stack);
			}
			else if(module == ModuleType.DISGUISE)
			{
				if (!world.isRemote) {
					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new DisguiseModuleContainer(windowId, inv, new ModuleItemInventory(player.getHeldItem(hand)));
						}

						@Override
						public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(getTranslationKey());
					}
					});
				}

				return ActionResult.newResult(ActionResultType.SUCCESS, stack);
			}
		}

		return ActionResult.newResult(ActionResultType.PASS, stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.modifiable").getFormattedText()));
		else
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.notModifiable").getFormattedText()));

		if(nbtCanBeModified && stack.getTag() != null && !stack.getTag().isEmpty()) {
			list.add(new StringTextComponent(" "));
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.playerCustomization.players").getFormattedText() + ":"));

			for(int i = 1; i <= MAX_PLAYERS; i++)
				if(!stack.getTag().getString("Player" + i).isEmpty())
					list.add(new StringTextComponent(TextFormatting.GRAY + stack.getTag().getString("Player" + i)));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems", numberOfBlockAddons, numberOfItemAddons).getFormattedText()));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.items", numberOfItemAddons).getFormattedText()));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks", numberOfBlockAddons).getFormattedText()));

			if(getNumberOfAddons() > 0 && !getAddons(stack.getTag()).isEmpty()) {
				list.add(new StringTextComponent(" "));

				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.added").getFormattedText() + ":"));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new StringTextComponent(TextFormatting.GRAY + "- " + ClientUtils.localize(addon.getTranslationKey()).getFormattedText()));
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

	public ArrayList<Block> getBlockAddons(CompoundNBT tag){
		ArrayList<Block> list = new ArrayList<>();

		if(tag == null) return list;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons) {
				ItemStack stack = ItemStack.read(item);

				if(stack.getItem() instanceof BlockItem)
					list.add(Block.getBlockFromItem(stack.getItem()));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(CompoundNBT tag){
		ArrayList<ItemStack> list = new ArrayList<>();

		if(tag == null) return list;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons)
				list.add(ItemStack.read(item));
		}

		return list;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
