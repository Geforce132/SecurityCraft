package net.geforcemods.securitycraft.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

@SuppressWarnings({"unchecked", "rawtypes"})
public class ItemKeycardBase extends Item{

	@SideOnly(Side.CLIENT)
	private IIcon keycardOneIcon;

	@SideOnly(Side.CLIENT)
	private IIcon keycardTwoIcon;

	@SideOnly(Side.CLIENT)
	private IIcon keycardThreeIcon;

	@SideOnly(Side.CLIENT)
	private IIcon keycardFourIcon;

	@SideOnly(Side.CLIENT)
	private IIcon keycardFiveIcon;

	@SideOnly(Side.CLIENT)
	private IIcon limitedUseKeycardIcon;

	public ItemKeycardBase() {
		setHasSubtypes(true);
		setMaxDamage(0);
		setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}

	public int getKeycardLV(ItemStack par1ItemStack){
		if(par1ItemStack.getItemDamage() == 0)
			return 1;
		else if(par1ItemStack.getItemDamage() == 1)
			return 2;
		else if(par1ItemStack.getItemDamage() == 2)
			return 3;
		else if(par1ItemStack.getItemDamage() == 3)
			return 6;
		else if(par1ItemStack.getItemDamage() == 4)
			return 4;
		else if(par1ItemStack.getItemDamage() == 5)
			return 5;
		else
			return 0;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1Item, CreativeTabs par2CreativeTabs, List par3List)
	{
		par3List.add(new ItemStack(this, 1, 0)); //1
		par3List.add(new ItemStack(this, 1, 1)); //2
		par3List.add(new ItemStack(this, 1, 2)); //3
		par3List.add(new ItemStack(this, 1, 3)); //LU
		par3List.add(new ItemStack(this, 1, 4)); //4
		par3List.add(new ItemStack(this, 1, 5)); //5
	}



	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack){
		if(par1ItemStack.getItemDamage() == 0)
			return "item.keycardOne";
		else if(par1ItemStack.getItemDamage() == 1)
			return "item.keycardTwo";
		else if(par1ItemStack.getItemDamage() == 2)
			return "item.keycardThree";
		else if(par1ItemStack.getItemDamage() == 4)
			return "item.keycardFour";
		else if(par1ItemStack.getItemDamage() == 5)
			return "item.keycardFive";
		else if(par1ItemStack.getItemDamage() == 3)
			return "item.limitedUseKeycard";
		else
			return "item.nullItem";

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(par1ItemStack.getItemDamage() == 3){
			if(par1ItemStack.stackTagCompound == null){
				par1ItemStack.stackTagCompound = new NBTTagCompound();
				par1ItemStack.stackTagCompound.setInteger("Uses", 5);
			}

			par3List.add(StatCollector.translateToLocal("tooltip.keycard.uses") + " " + par1ItemStack.stackTagCompound.getInteger("Uses"));

		}
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1){
		if(par1 == 0)
			return keycardOneIcon;
		else if(par1 == 1)
			return keycardTwoIcon;
		else if(par1 == 2)
			return keycardThreeIcon;
		else if(par1 == 4)
			return keycardFourIcon;
		else if(par1 == 5)
			return keycardFiveIcon;
		else if(par1 == 3)
			return limitedUseKeycardIcon;
		else
			return super.getIconFromDamage(par1);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister par1IconRegister){
		keycardOneIcon = par1IconRegister.registerIcon("securitycraft:lv1Keycard");
		keycardTwoIcon = par1IconRegister.registerIcon("securitycraft:lv2Keycard");
		keycardThreeIcon = par1IconRegister.registerIcon("securitycraft:lv3Keycard");
		keycardFourIcon = par1IconRegister.registerIcon("securitycraft:lv4Keycard");
		keycardFiveIcon = par1IconRegister.registerIcon("securitycraft:lv5Keycard");
		limitedUseKeycardIcon = par1IconRegister.registerIcon("securitycraft:limitedUseKeycard");
	}

}
