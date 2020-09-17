package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BriefcaseItem extends Item {

	public static final Style GRAY_STYLE = Style.EMPTY.setFormatting(TextFormatting.GRAY);

	public BriefcaseItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUse(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getWorld(), ctx.getPos(), ctx.getItem(), ctx.getFace(), ctx.getHitVec().x, ctx.getHitVec().y, ctx.getHitVec().z, ctx.getHand());
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, Hand hand) {
		if(world.isRemote && hand == Hand.MAIN_HAND) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), stack.getDisplayName()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), stack.getDisplayName()));
		}

		return ActionResultType.FAIL;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getHeldItem(hand);

		if(world.isRemote && hand == Hand.MAIN_HAND) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), stack.getDisplayName()));
			else
				SecurityCraft.channel.sendToServer(new OpenGui(SCContent.cTypeBriefcase.getRegistryName(), stack.getDisplayName()));
		}

		return ActionResult.resultPass(stack);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack briefcase, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		if (briefcase.hasTag() && briefcase.getTag().contains("owner"))
			tooltip.add(ClientUtils.localize("tooltip.securitycraft:briefcase.owner", briefcase.getTag().getString("owner")).setStyle(GRAY_STYLE));
	}

	public static boolean isOwnedBy(ItemStack briefcase, PlayerEntity player) {
		return !briefcase.hasTag() || !briefcase.getTag().contains("owner") || briefcase.getTag().getString("ownerUUID").equals(player.getUniqueID().toString()) || (briefcase.getTag().getString("ownerUUID").equals("ownerUUID") && briefcase.getTag().getString("owner").equals(player.getName().getString()));
	}
}
