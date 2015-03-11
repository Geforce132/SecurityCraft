package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUniversalBlockModifier extends ItemWithInfo{

	public ItemUniversalBlockModifier() {
		super("The universal block modifier enables you to insert modules into customizable blocks to modify how they work. Right-click on the block to modify it.", new String[]{"The universal block modifier requires: 1 emerald, 2 redstone, 2 iron ingots", "XY ", "YZ ", "  Z", "X = emerald, Y = redstone, Z = iron ingot"});
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		par3List.add("Customizable blocks:");
		par3List.add("- Laser Block");
		par3List.add("- Keypad");
		par3List.add("- Keycard Reader");
		par3List.add("- Inventory Scanner");
		par3List.add("- Portable Radar");
		par3List.add("- Retinal Scanner");
	}

}
