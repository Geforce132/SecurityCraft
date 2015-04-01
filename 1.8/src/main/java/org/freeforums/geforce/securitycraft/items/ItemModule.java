package org.freeforums.geforce.securitycraft.items;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.enums.EnumCustomModules;
import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

public class ItemModule extends Item implements IHelpInfo {
	
	private final EnumCustomModules module;
	private final boolean nbtCanBeModified;
	
	public ItemModule(EnumCustomModules module, boolean nbtCanBeModified){
		this.module = module;
		this.nbtCanBeModified = nbtCanBeModified;
		
		this.setMaxStackSize(1);
		this.setCreativeTab(mod_SecurityCraft.tabSCTechnical);
	}
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		if(nbtCanBeModified){
			par3List.add("Module is modifiable!");
			par3List.add("Use /module to add players.");
		}else{
			par3List.add("Module is not modifiable!");
		}
		
		if(module == EnumCustomModules.WHITELIST || module == EnumCustomModules.BLACKLIST){
			par3List.add(" ");
			par3List.add("Players:");

			if(par1ItemStack.getTagCompound() != null){
				for(int i = 1; i <= 10; i++){
					if(!par1ItemStack.getTagCompound().getString("Player" + i).isEmpty()){
						par3List.add(par1ItemStack.getTagCompound().getString("Player" + i));
					}
				}
			}
		}
	}

	public EnumCustomModules getModule() {
		return module;
	}
	
	public boolean canBeModified(){
		return this.nbtCanBeModified;
	}

	public String getHelpInfo() {
		if(module == EnumCustomModules.WHITELIST){
			return "The whitelist module can be used in the Laser Block, Keypad, Keycard Reader, Inventory Scanner, and Retinal Scanner to stop restrictions to that player. For example, adding a whitelist module with the player 'Jeb' added to a keypad will allow Jeb to use the keypad without knowing the keycode.";
		}else if(module == EnumCustomModules.BLACKLIST){
			return "The blacklist module can be used in the Keypad and the Keycard Reader to set restrictions on that player. For example, adding a blacklist module with the player 'Jeb' added to a keypad will stop Jeb from accessing the keypad's GUI.";
		}else if(module == EnumCustomModules.REDSTONE){
			return "The redstone module can be used in the Portable Radar to make it emit a redstone signal whenever a player is within the radar's search radius.";
		}else if(module == EnumCustomModules.HARMING){
			return "The harming module can be used in the Laser Block to make the laser coming out of it hurt living entities whenever they walk through it.";
		}else if(module == EnumCustomModules.SMART){
			return "The smart module can be used in the Inventory Scanner to make it check enchantments and other metadata changes when deleting items from a player's inventory.";
		}else{
			return null;
		}
	}
	
	public String[] getRecipe() {
		if(module == EnumCustomModules.WHITELIST){
			return new String[]{"The whitelist module requires: 7 iron ingots, 2 paper.", "XXX", "XYX", "XYX", "X = iron ingot, Y = paper"};
		}else if(module == EnumCustomModules.BLACKLIST){
			return new String[]{"The blacklist module requires: 7 iron ingots, 1 paper, 1 ink sac.", "XXX", "XYX", "XZX", "X = iron ingot, Y = paper, Z = ink sac"};
		}else if(module == EnumCustomModules.REDSTONE){
			return new String[]{"The redstone module requires: 7 iron ingots, 1 paper, 1 redstone.", "XXX", "XYX", "XZX", "X = iron ingot, Y = paper, Z = redstone"};
		}else if(module == EnumCustomModules.HARMING){
			return new String[]{"The harming module requires: 7 iron ingots, 1 paper, 1 arrow.", "XXX", "XYX", "XZX", "X = iron ingot, Y = paper, Z = arrow"};
		}else if(module == EnumCustomModules.SMART){
			return new String[]{"The smart module requires: 7 iron ingots, 1 paper, 1 ender pearl.", "XXX", "XYX", "XZX", "X = iron ingot, Y = paper, Z = ender pearl"};
		}else{
			return null;
		}
	}

}
