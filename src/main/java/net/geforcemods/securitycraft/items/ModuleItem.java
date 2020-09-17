package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.containers.DisguiseModuleContainer;
import net.geforcemods.securitycraft.containers.ModuleItemInventory;
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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class ModuleItem extends Item{

	public static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);
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
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);
		try
		{
			if(canBeCustomized())
			{
				if(world.isRemote && (module == ModuleType.WHITELIST || module == ModuleType.BLACKLIST))
					SecurityCraft.proxy.displayEditModuleGui(stack);
				else if(!world.isRemote && module == ModuleType.DISGUISE)
				{
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
			}
		}
		catch(NoSuchMethodError e) {/*:^)*/}

		return ActionResult.resultPass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(new TranslationTextComponent("tooltip.securitycraft:module.modifiable").setStyle(GRAY_STYLE));
		else
			list.add(new TranslationTextComponent("tooltip.securitycraft:module.notModifiable").setStyle(GRAY_STYLE));

		if(nbtCanBeModified && stack.getTag() != null && !stack.getTag().isEmpty()) {
			list.add(StringTextComponent.EMPTY);
			list.add(new TranslationTextComponent("tooltip.securitycraft:module.playerCustomization.players").setStyle(GRAY_STYLE));

			for(int i = 1; i <= MAX_PLAYERS; i++)
				if(!stack.getTag().getString("Player" + i).isEmpty())
					list.add(new StringTextComponent(stack.getTag().getString("Player" + i)).setStyle(GRAY_STYLE));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems", numberOfBlockAddons, numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.items", numberOfItemAddons).setStyle(GRAY_STYLE));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks", numberOfBlockAddons).setStyle(GRAY_STYLE));

			if(getNumberOfAddons() > 0 && !getAddons(stack.getTag()).isEmpty()) {
				list.add(StringTextComponent.EMPTY);

				list.add(ClientUtils.localize("tooltip.securitycraft:module.itemAddons.added").setStyle(GRAY_STYLE));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new StringTextComponent("- ").append(ClientUtils.localize(addon.getTranslationKey())).setStyle(GRAY_STYLE));
			}
		}
	}

	public ModuleType getModule() {
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
				ItemStack stack;

				if((stack = ItemStack.read(item)).getItem() instanceof BlockItem)
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
