package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemModule extends Item{

	private final EnumCustomModules module;
	private final boolean nbtCanBeModified;
	private boolean canBeCustomized;
	private ResourceLocation guiToOpen;
	private int numberOfItemAddons;
	private int numberOfBlockAddons;

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified){
		this(module, nbtCanBeModified, false, null, 0, 0);
	}

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified, boolean canBeCustomized, ResourceLocation guiToOpen){
		this(module, nbtCanBeModified, canBeCustomized, guiToOpen, 0, 0);
	}

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified, boolean canBeCustomized, ResourceLocation guiToOpen, int itemAddons, int blockAddons){
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		this.canBeCustomized = canBeCustomized;
		this.guiToOpen = guiToOpen;
		numberOfItemAddons = itemAddons;
		numberOfBlockAddons = blockAddons;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack stack = player.getHeldItem(hand);

		try
		{
			if(!world.isRemote) {
				if(!stack.hasTag()) {
					stack.setTag(new NBTTagCompound());
					ClientUtils.syncItemNBT(stack);
				}

				if(canBeCustomized() && player instanceof EntityPlayerMP)
					NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(guiToOpen));
			}
		}
		catch(NoSuchMethodError e) {/*:^)*/}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, World world, List<ITextComponent> list, ITooltipFlag flag) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(new TextComponentString(TextFormatting.GRAY + new TextComponentTranslation("tooltip.securitycraft:module.modifiable").getFormattedText()));
		else
			list.add(new TextComponentString(TextFormatting.GRAY + new TextComponentTranslation("tooltip.securitycraft:module.notModifiable").getFormattedText()));

		if(nbtCanBeModified) {
			list.add(new TextComponentString(TextFormatting.GRAY + new TextComponentTranslation("tooltip.securitycraft:module.playerCustomization.usage").getFormattedText()));

			list.add(new TextComponentString(" "));
			list.add(new TextComponentString(TextFormatting.GRAY + new TextComponentTranslation("tooltip.securitycraft:module.playerCustomization.players").getFormattedText() + ":"));

			if(stack.getTag() != null)
				for(int i = 1; i <= 10; i++)
					if(!stack.getTag().getString("Player" + i).isEmpty())
						list.add(new TextComponentString(stack.getTag().getString("Player" + i)));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(new TextComponentString(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems").replace("#blocks", numberOfBlockAddons + "").replace("#items", numberOfItemAddons + "")));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(new TextComponentString(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.items").replace("#", numberOfItemAddons + "")));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(new TextComponentString(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.usage.blocks").replace("#", numberOfBlockAddons + "")));

			if(getNumberOfAddons() > 0) {
				list.add(new TextComponentString(" "));

				list.add(new TextComponentString(TextFormatting.GRAY + ClientUtils.localize("tooltip.securitycraft:module.itemAddons.added") + ":"));

				for(ItemStack addon : getAddons(stack.getTag()))
					list.add(new TextComponentString(TextFormatting.GRAY + "- " + ClientUtils.localize(addon.getTranslationKey())));
			}
		}
	}

	public EnumCustomModules getModule() {
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

	public ArrayList<Item> getItemAddons(NBTTagCompound tag){
		ArrayList<Item> list = new ArrayList<Item>();

		if(tag == null) return list;

		NBTTagList items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			NBTTagCompound item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfItemAddons) {
				ItemStack stack;

				if((stack = ItemStack.read(item)).getTranslationKey().startsWith("item."))
					list.add(stack.getItem());
			}
		}

		return list;
	}

	public ArrayList<Block> getBlockAddons(NBTTagCompound tag){
		ArrayList<Block> list = new ArrayList<Block>();

		if(tag == null) return list;

		NBTTagList items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			NBTTagCompound item = items.getCompound(i);
			int slot = item.getInt("Slot");

			if(slot < numberOfBlockAddons) {
				ItemStack stack;

				if((stack = ItemStack.read(item)).getTranslationKey().startsWith("tile."))
					list.add(Block.getBlockFromItem(stack.getItem()));
			}
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(NBTTagCompound tag){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

		if(tag == null) return list;

		NBTTagList items = tag.getList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.size(); i++) {
			NBTTagCompound item = items.getCompound(i);
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
