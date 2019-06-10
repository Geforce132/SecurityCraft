package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemSCManual extends Item {

	public ItemSCManual(){
		super(new Item.Properties().maxStackSize(1).group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(player instanceof ServerPlayerEntity)
			NetworkHooks.openGui((ServerPlayerEntity)player, new BaseInteractionObject(GuiHandler.MANUAL));

		return ActionResult.newResult(EnumActionResult.PASS, player.getHeldItem(hand));
	}

	@Override
	public void inventoryTick(ItemStack par1ItemStack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(par1ItemStack.getTag() == null){
			ListNBT bookPages = new ListNBT();

			par1ItemStack.setTagInfo("pages", bookPages);
			par1ItemStack.setTagInfo("author", new NBTTagString("Geforce"));
			par1ItemStack.setTagInfo("title", new NBTTagString("SecurityCraft"));
		}
	}

}
