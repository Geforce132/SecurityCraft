package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BriefcaseItem extends Item {

	public BriefcaseItem()
	{
		super(new Item.Properties().group(SecurityCraft.groupSCTechnical).maxStackSize(1));
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z);
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ) {
		if(world.isRemote) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), world.getDimension().getType().getId(), player.getPosition(), stack.getDisplayName()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), world.getDimension().getType().getId(), player.getPosition(), stack.getDisplayName()));
		}

		return ActionResultType.FAIL;
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
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), world.getDimension().getType().getId(), player.getPosition(), stack.getDisplayName()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), world.getDimension().getType().getId(), player.getPosition(), stack.getDisplayName()));
		}

		return ActionResult.func_226250_c_(stack); //pass
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
