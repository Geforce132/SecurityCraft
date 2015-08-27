package net.breakinbad.securitycraft.items;

import cpw.mods.fml.common.FMLCommonHandler;
import net.breakinbad.securitycraft.gui.GuiSCManual;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;

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
	
			par1ItemStack.setTagInfo("pages", bookPages);
			par1ItemStack.setTagInfo("author", new NBTTagString("Geforce"));
			par1ItemStack.setTagInfo("title", new NBTTagString("SecurityCraft"));
		}
    }

}
