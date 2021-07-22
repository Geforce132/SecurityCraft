package net.geforcemods.securitycraft.items;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.SCManualPage;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SCManualItem extends Item {

	public static final List<SCManualPage> PAGES = new ArrayList<>();

	public SCManualItem(Item.Properties properties){
		super(properties);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
		if(world.isClientSide)
			SecurityCraft.proxy.displaySCManualGui();

		return InteractionResultHolder.consume(player.getItemInHand(hand));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level world, Entity entity, int slotIndex, boolean isSelected){
		if(stack.getTag() == null){
			ListTag bookPages = new ListTag();

			stack.addTagElement("pages", bookPages);
			stack.addTagElement("author", StringTag.valueOf("Geforce"));
			stack.addTagElement("title", StringTag.valueOf("SecurityCraft"));
		}
	}

}
