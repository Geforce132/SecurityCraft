package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.containers.ModuleInventory;
import net.geforcemods.securitycraft.misc.CustomModules;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
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

	private final CustomModules module;
	private final boolean nbtCanBeModified;
	private boolean canBeCustomized;
	private int numberOfItemAddons;
	private int numberOfBlockAddons;

	public ModuleItem(CustomModules module, boolean nbtCanBeModified){
		this(module, nbtCanBeModified, false, 0, 0);
	}

	public ModuleItem(CustomModules module, boolean nbtCanBeModified, boolean canBeCustomized){
		this(module, nbtCanBeModified, canBeCustomized, 0, 0);
	}

	public ModuleItem(CustomModules module, boolean nbtCanBeModified, boolean canBeCustomized, int itemAddons, int blockAddons){
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		this.canBeCustomized = canBeCustomized;
		numberOfItemAddons = itemAddons;
		numberOfBlockAddons = blockAddons;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		try
		{
			if(canBeCustomized())
			{
				if(world.isRemote && (module == CustomModules.WHITELIST || module == CustomModules.BLACKLIST))
					SecurityCraft.proxy.displayEditModuleGui(stack);
				else if(!world.isRemote && module == CustomModules.DISGUISE)
				{
					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new DisguiseModuleContainer(windowId, inv, new ModuleInventory(player.getHeldItem(hand)));
						}

						@Override
						public ITextComponent getDisplayName()
						{
							return new TranslationTextComponent(getTranslationKey());
						}
					});
				}
			}
		}
		catch(NoSuchMethodError e) {/*:^)*/}

		return ActionResult.func_226250_c_(stack); //pass
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.modifiable").getFormattedText()));
		else
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.notModifiable").getFormattedText()));

		if(nbtCanBeModified) {
			list.add(new StringTextComponent(" "));
			list.add(new StringTextComponent(TextFormatting.GRAY + new TranslationTextComponent("tooltip.securitycraft:module.playerCustomization.players").getFormattedText() + ":"));

			if(stack.getTag() != null)
				for(int i = 1; i <= 10; i++)
					if(!stack.getTag().getString("Player" + i).isEmpty())
						list.add(new StringTextComponent(TextFormatting.GRAY + stack.getTag().getString("Player" + i)));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems").replace("#blocks", numberOfBlockAddons + "").replace("#items", numberOfItemAddons + "")));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.items").replace("#", numberOfItemAddons + "")));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks").replace("#", numberOfBlockAddons + "")));

			if(getNumberOfAddons() > 0) {
				list.add(new StringTextComponent(" "));

				list.add(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.added") + ":"));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new StringTextComponent(TextFormatting.GRAY + "- " + ClientUtils.localize(addon.getTranslationKey())));
			}
		}
	}

	public CustomModules getModule() {
		return module;
	}

	public boolean canNBTBeModified() {
		return nbtCanBeModified;
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

	public ArrayList<Item> getItemAddons(CompoundNBT tag){
		ArrayList<Item> list = new ArrayList<Item>();

		if(tag == null) return list;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfItemAddons) {
				ItemStack stack;

				if(!((stack = ItemStack.read(item)).getItem() instanceof BlockItem))
					list.add(stack.getItem());
			}
		}

		return list;
	}

	public ArrayList<Block> getBlockAddons(CompoundNBT tag){
		ArrayList<Block> list = new ArrayList<Block>();

		if(tag == null) return list;

		ListNBT items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			CompoundNBT item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons) {
				ItemStack stack;

				if((stack = ItemStack.read(item)).getItem() instanceof BlockItem)
					list.add(Block.getBlockFromItem(stack.getItem()));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(CompoundNBT tag){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

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
