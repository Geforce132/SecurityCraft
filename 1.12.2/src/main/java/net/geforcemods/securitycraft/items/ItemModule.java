package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
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
		setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack itemStackIn = playerIn.getHeldItem(hand);

		if(!worldIn.isRemote) {
			if(!itemStackIn.hasTagCompound()) {
				itemStackIn.setTagCompound(new NBTTagCompound());
				ClientUtils.syncItemNBT(itemStackIn);
			}

			if(canBeCustomized())
				playerIn.openGui(mod_SecurityCraft.instance, guiToOpen, worldIn, (int) playerIn.posX, (int) playerIn.posY, (int) playerIn.posZ);
		}

		return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, World world, List<String> par3List, ITooltipFlag flagIn) {
		if(nbtCanBeModified || canBeCustomized())
			par3List.add(ClientUtils.localize("tooltip.module.modifiable"));
		else
			par3List.add(ClientUtils.localize("tooltip.module.notModifiable"));

		if(nbtCanBeModified) {
			par3List.add(ClientUtils.localize("tooltip.module.playerCustomization.usage"));

			par3List.add(" ");
			par3List.add(ClientUtils.localize("tooltip.module.playerCustomization.players") + ":");

			if(par1ItemStack.getTagCompound() != null)
				for(int i = 1; i <= 10; i++)
					if(!par1ItemStack.getTagCompound().getString("Player" + i).isEmpty())
						par3List.add(par1ItemStack.getTagCompound().getString("Player" + i));
		}

		if(canBeCustomized()) {
			if(numberOfItemAddons > 0 && numberOfBlockAddons > 0)
				par3List.add(ClientUtils.localize("tooltip.module.itemAddons.usage.blocksAndItems").replace("#blocks", numberOfBlockAddons + "").replace("#items", numberOfItemAddons + ""));

			if(numberOfItemAddons > 0 && numberOfBlockAddons == 0)
				par3List.add(ClientUtils.localize("tooltip.module.itemAddons.usage.items").replace("#", numberOfItemAddons + ""));

			if(numberOfItemAddons == 0 && numberOfBlockAddons > 0)
				par3List.add(ClientUtils.localize("tooltip.module.itemAddons.usage.blocks").replace("#", numberOfBlockAddons + ""));

			if(getNumberOfAddons() > 0) {
				par3List.add(" ");

				par3List.add(ClientUtils.localize("tooltip.module.itemAddons.added") + ":");
				for(Item item : getItemAddons(par1ItemStack.getTagCompound()))
					par3List.add("- " + ClientUtils.localize(item.getUnlocalizedName() + ".name"));

				for(Block block : getBlockAddons(par1ItemStack.getTagCompound()))
					par3List.add("- " + ClientUtils.localize(block.getLocalizedName()));
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

			if(slot < numberOfItemAddons) {
				ItemStack stack;

				if((stack = new ItemStack(item)).getUnlocalizedName().startsWith("item."))
					list.add(stack.getItem());
			}
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

			if(slot < numberOfBlockAddons) {
				ItemStack stack;

				if((stack = new ItemStack(item)).getUnlocalizedName().startsWith("tile."))
					list.add(Block.getBlockFromItem(stack.getItem()));
			}
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
				list.add(new ItemStack(item));
		}

		return list;
	}

	public boolean canBeCustomized(){
		return canBeCustomized;
	}

}
