package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemSCManual extends Item {

	public ItemSCManual(){
		super(new Item.Properties().maxStackSize(1).group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		if(player instanceof EntityPlayerMP)
			NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.MANUAL));

		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(par1ItemStack.getTag() == null){
			NBTTagList bookPages = new NBTTagList();

			par1ItemStack.setTagInfo("pages", bookPages);
			par1ItemStack.setTagInfo("author", new NBTTagString("Geforce"));
			par1ItemStack.setTagInfo("title", new NBTTagString("SecurityCraft"));
		}
	}

}
