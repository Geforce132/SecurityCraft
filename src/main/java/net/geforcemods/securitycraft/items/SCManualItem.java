package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SCManualItem extends Item {

	public static final List<SCManualPage> PAGES = new ArrayList<>();

	public SCManualItem(Item.Properties properties){
		super(properties);
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		if(world.isRemote)
			SecurityCraft.proxy.displaySCManualGui();

		return ActionResult.newResult(ActionResultType.SUCCESS, player.getHeldItem(hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slotIndex, boolean isSelected){
		if(stack.getTag() == null){
			ListNBT bookPages = new ListNBT();

			stack.setTagInfo("pages", bookPages);
			stack.setTagInfo("author", new StringNBT("Geforce"));
			stack.setTagInfo("title", new StringNBT("SecurityCraft"));
		}
	}

}
