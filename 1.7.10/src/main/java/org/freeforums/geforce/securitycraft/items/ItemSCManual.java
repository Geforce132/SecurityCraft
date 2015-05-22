package org.freeforums.geforce.securitycraft.items;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.gui.GuiSCManual;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;

import cpw.mods.fml.common.FMLCommonHandler;

public class ItemSCManual extends Item {
	
	public ItemSCManual(){
		super();
	}
	
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(world.isRemote){
			FMLCommonHandler.instance().showGuiScreen(new GuiSCManual());
		}
		return stack;
	}
	
	public void onUpdate(ItemStack par1ItemStack, World par2World, Entity par3Entity, int par4, boolean par5){
		if(par1ItemStack.getTagCompound() == null){
			NBTTagList bookPages = new NBTTagList();
			//bookPages.appendTag(new NBTTagString("SecurityCraft " + mod_SecurityCraft.getVersion() + " info book."));
			//bookPages.appendTag(new NBTTagString("Keypad: \n \nThe keypad is used by placing the keypad, right-clicking it, and setting a numerical passcode. Once the keycode is set, right-clicking the keypad will allow you to enter the code. If it's correct, the keypad will emit redstone power for three seconds."));
			//bookPages.appendTag(new NBTTagString("Laser block: The laser block is used by putting two of them within five blocks of each other. When the blocks are placed correctly, a laser should form between them. Whenever a player walks through the laser, both the laser blocks will emit a 15-block redstone signal."));
	
			par1ItemStack.setTagInfo("pages", bookPages);
			par1ItemStack.setTagInfo("author", new NBTTagString("Geforce"));
			par1ItemStack.setTagInfo("title", new NBTTagString("SecurityCraft"));
		}
    }

}
