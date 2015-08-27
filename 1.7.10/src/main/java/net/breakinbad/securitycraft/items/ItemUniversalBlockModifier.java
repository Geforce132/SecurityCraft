package net.breakinbad.securitycraft.items;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemUniversalBlockModifier extends Item{

	public ItemUniversalBlockModifier() {
		super();
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
