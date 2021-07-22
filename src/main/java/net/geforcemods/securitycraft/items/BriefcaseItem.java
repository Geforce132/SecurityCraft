package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.server.OpenBriefcaseGui;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.CauldronBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IDyeableArmorItem;
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

public class BriefcaseItem extends Item implements IDyeableArmorItem {

	public static final Style GRAY_STYLE = Style.EMPTY.withColor(TextFormatting.GRAY);

	public BriefcaseItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext ctx)
	{
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getHand());
	}

	public ActionResultType onItemUse(PlayerEntity player, World world, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, Hand hand) {
		if(world.getBlockState(pos).getBlock() instanceof CauldronBlock) //don't open the briefcase when a cauldron is rightclicked for removing the dye
			return ActionResultType.SUCCESS;

		handle(stack, world, player, hand);
		return ActionResultType.CONSUME;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);

		handle(stack, world, player, hand);
		return ActionResult.consume(stack);
	}

	private void handle(ItemStack stack, World world, PlayerEntity player, Hand hand)
	{
		if(world.isClientSide) {
			if(!stack.hasTag()) {
				stack.setTag(new CompoundNBT());
				ClientUtils.syncItemNBT(stack);
			}

			if(!stack.getTag().contains("passcode"))
				SecurityCraft.channel.sendToServer(new OpenBriefcaseGui(SCContent.cTypeBriefcaseSetup.getRegistryName(), stack.getHoverName()));
			else
				SecurityCraft.channel.sendToServer(new OpenBriefcaseGui(SCContent.cTypeBriefcase.getRegistryName(), stack.getHoverName()));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack briefcase, World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
		String ownerName = getOwnerName(briefcase);

		if(!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft:briefcase.owner", ownerName).setStyle(GRAY_STYLE));
	}

	public static boolean isOwnedBy(ItemStack briefcase, PlayerEntity player) {
		if(!briefcase.hasTag())
			return true;

		String ownerName = getOwnerName(briefcase);
		String ownerUUID = getOwnerUUID(briefcase);

		return ownerName.isEmpty() || ownerUUID.equals(player.getUUID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName().getString()));
	}

	public static String getOwnerName(ItemStack briefcase)
	{
		return briefcase.hasTag() ? briefcase.getTag().getString("owner") : "";
	}

	public static String getOwnerUUID(ItemStack briefcase)
	{
		return briefcase.hasTag() ? briefcase.getTag().getString("ownerUUID") : "";
	}
}
