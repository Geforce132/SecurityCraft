package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemModule extends Item{

	private final EnumCustomModules module;
	private final boolean nbtCanBeModified;
	private boolean canBeCustomized;
	private int guiToOpen;
	private int numberOfItemAddons;
	private int numberOfBlockAddons;

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified){
		this(module, nbtCanBeModified, false, -1, 0, 0);
	}

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified, boolean canBeCustomized, int guiToOpen){
		this(module, nbtCanBeModified, canBeCustomized, guiToOpen, 0, 0);
	}

	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified, boolean canBeCustomized, int guiToOpen, int itemAddons, int blockAddons){
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		this.canBeCustomized = canBeCustomized;
		this.guiToOpen = guiToOpen;
		numberOfItemAddons = itemAddons;
		numberOfBlockAddons = blockAddons;

		setMaxStackSize(1);
		setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		try
		{
			if(!world.isRemote) {
				if(!stack.hasTagCompound()) {
					stack.setTagCompound(new NBTTagCompound());
					ClientUtils.syncItemNBT(stack);
				}

				if(canBeCustomized())
					player.openGui(SecurityCraft.instance, guiToOpen, world, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
		}
		catch(NoSuchMethodError e) {/*:^)*/}

		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
		if(nbtCanBeModified || canBeCustomized())
			list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.modifiable"));
		else
			list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.notModifiable"));

		if(nbtCanBeModified) {
			list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.playerCustomization.usage"));

			list.add(" ");
			list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.playerCustomization.players") + ":");

			if(stack.getTagCompound() != null)
				for(int i = 1; i <= 10; i++)
					if(!stack.getTagCompound().getString("Player" + i).isEmpty())
						list.add(stack.getTagCompound().getString("Player" + i));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.itemAddons.usage.blocksAndItems").replace("#blocks", numberOfBlockAddons + "").replace("#items", numberOfItemAddons + ""));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.itemAddons.usage.items").replace("#", numberOfItemAddons + ""));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.itemAddons.usage.blocks").replace("#", numberOfBlockAddons + ""));

			if(getNumberOfAddons() > 0) {
				list.add(" ");

				list.add(StatCollector.translateToLocal("tooltip.securitycraft:module.itemAddons.added") + ":");

				for(ItemStack addon : getAddons(stack.getTagCompound()))
					list.add("- " + StatCollector.translateToLocal(addon.getUnlocalizedName() + ".name"));
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

		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < numberOfItemAddons)
				if(ItemStack.loadItemStackFromNBT(item).getUnlocalizedName().startsWith("item."))
					list.add(ItemStack.loadItemStackFromNBT(item).getItem());
		}

		return list;
	}

	public ArrayList<Block> getBlockAddons(NBTTagCompound tag){
		ArrayList<Block> list = new ArrayList<Block>();

		if(tag == null) return list;

		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < numberOfBlockAddons)
				if(ItemStack.loadItemStackFromNBT(item).getUnlocalizedName().startsWith("tile."))
					list.add(Block.getBlockFromItem(ItemStack.loadItemStackFromNBT(item).getItem()));
		}

		return list;
	}

	public ArrayList<ItemStack> getAddons(NBTTagCompound tag){
		ArrayList<ItemStack> list = new ArrayList<ItemStack>();

		if(tag == null) return list;

		NBTTagList items = tag.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for(int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			if(slot < numberOfBlockAddons)
				list.add(ItemStack.loadItemStackFromNBT(item));
		}

		return list;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
