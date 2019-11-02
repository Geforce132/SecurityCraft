package net.geforcemods.securitycraft.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SecurityCraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

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
		setMaxDurability(0);
		setCreativeTab(SecurityCraft.tabSCTechnical);
	}

	public int getKeycardLvl(ItemStack stack){
		if(stack.getMetadata() == 0)
			return 1;
		else if(stack.getMetadata() == 1)
			return 2;
		else if(stack.getMetadata() == 2)
			return 3;
		else if(stack.getMetadata() == 3)
			return 6;
		else if(stack.getMetadata() == 4)
			return 4;
		else if(stack.getMetadata() == 5)
			return 5;
		else
			return 0;
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		list.add(new ItemStack(this, 1, 0)); //1
		list.add(new ItemStack(this, 1, 1)); //2
		list.add(new ItemStack(this, 1, 2)); //3
		list.add(new ItemStack(this, 1, 3)); //LU
		list.add(new ItemStack(this, 1, 4)); //4
		list.add(new ItemStack(this, 1, 5)); //5
	}



	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(stack.getMetadata() == 0)
			return "item.securitycraft:keycardOne";
		else if(stack.getMetadata() == 1)
			return "item.securitycraft:keycardTwo";
		else if(stack.getMetadata() == 2)
			return "item.securitycraft:keycardThree";
		else if(stack.getMetadata() == 4)
			return "item.securitycraft:keycardFour";
		else if(stack.getMetadata() == 5)
			return "item.securitycraft:keycardFive";
		else if(stack.getMetadata() == 3)
			return "item.securitycraft:limitedUseKeycard";
		else
			return "item.securitycraft:nullItem";

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
		if(stack.getMetadata() == 3){
			if(stack.stackTagCompound == null){
				stack.stackTagCompound = new NBTTagCompound();
				stack.stackTagCompound.setInteger("Uses", 5);
			}

			list.add(StatCollector.translateToLocal("tooltip.securitycraft:keycard.uses") + " " + stack.stackTagCompound.getInteger("Uses"));

		}
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta){
		if(meta == 0)
			return keycardOneIcon;
		else if(meta == 1)
			return keycardTwoIcon;
		else if(meta == 2)
			return keycardThreeIcon;
		else if(meta == 4)
			return keycardFourIcon;
		else if(meta == 5)
			return keycardFiveIcon;
		else if(meta == 3)
			return limitedUseKeycardIcon;
		else
			return super.getIconFromDamage(meta);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		keycardOneIcon = register.registerIcon("securitycraft:lv1Keycard");
		keycardTwoIcon = register.registerIcon("securitycraft:lv2Keycard");
		keycardThreeIcon = register.registerIcon("securitycraft:lv3Keycard");
		keycardFourIcon = register.registerIcon("securitycraft:lv4Keycard");
		keycardFiveIcon = register.registerIcon("securitycraft:lv5Keycard");
		limitedUseKeycardIcon = register.registerIcon("securitycraft:limitedUseKeycard");
	}

}
