package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBriefcase extends Item {

	public ItemBriefcase()
	{
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public EnumActionResult onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.func_221532_j().x, ctx.func_221532_j().y, ctx.func_221532_j().z);
	}

	public EnumActionResult onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, float hitX, float hitY, float hitZ) {
		if(world.isRemote) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenGui(GuiHandler.BRIEFCASE_SETUP, player.getPosition()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(GuiHandler.BRIEFCASE_INSERT, player.getPosition()));
		}

		return EnumActionResult.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenGui(GuiHandler.BRIEFCASE_SETUP, player.getPosition()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(GuiHandler.BRIEFCASE_INSERT, player.getPosition()));
		}

		return ActionResult.newResult(EnumActionResult.PASS, stack);
	}

	@Override
	public ItemStack getContainerItem(ItemStack stack)
	{
		ItemStack newStack = stack.copy();

		if(newStack.getTag() != null && newStack.getTag().contains("passcode"))
			newStack.getTag().remove("passcode");

		return newStack;
	}

	@Override
	public boolean hasContainerItem()
	{
		return true;
	}
}
