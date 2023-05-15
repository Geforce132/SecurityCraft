package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class BriefcaseItem extends Item implements DyeableLeatherItem {
	public BriefcaseItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		return onItemUse(ctx.getPlayer(), ctx.getLevel(), ctx.getClickedPos(), ctx.getItemInHand(), ctx.getClickedFace(), ctx.getClickLocation().x, ctx.getClickLocation().y, ctx.getClickLocation().z, ctx.getHand());
	}

	public InteractionResult onItemUse(Player player, Level level, BlockPos pos, ItemStack stack, Direction facing, double hitX, double hitY, double hitZ, InteractionHand hand) {
		handle(stack, level, player);
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		handle(stack, level, player);
		return InteractionResultHolder.consume(stack);
	}

	private void handle(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide)
			SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(stack.getOrCreateTag().contains("passcode") ? OpenScreen.DataType.CHECK_BRIEFCASE_PASSCODE : OpenScreen.DataType.SET_BRIEFCASE_PASSCODE, player.blockPosition()));
	}

	@Override
	public void appendHoverText(ItemStack briefcase, Level level, List<Component> tooltip, TooltipFlag flag) {
		String ownerName = getOwnerName(briefcase);

		if (!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft:briefcase.owner", ownerName).setStyle(Utils.GRAY_STYLE));
	}

	@Override
	public CompoundTag getShareTag(ItemStack stack) {
		CompoundTag tag = super.getShareTag(stack);

		return tag == null ? null : Utils.filterPasscodeAndSaltFromTag(tag.copy());
	}

	public static void hashAndSetPasscode(CompoundTag briefcaseTag, String passcode) {
		byte[] salt = Utils.generateSalt();

		briefcaseTag.putString("salt", Utils.bytesToString(salt));
		briefcaseTag.putString("passcode", Utils.hashPasscode(passcode, salt));
	}

	public static boolean isOwnedBy(ItemStack briefcase, Player player) {
		if (!briefcase.hasTag())
			return true;

		String ownerName = getOwnerName(briefcase);
		String ownerUUID = getOwnerUUID(briefcase);

		return ownerName.isEmpty() || ownerUUID.equals(player.getUUID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName().getString()));
	}

	public static String getOwnerName(ItemStack briefcase) {
		return briefcase.hasTag() ? briefcase.getTag().getString("owner") : "";
	}

	public static String getOwnerUUID(ItemStack briefcase) {
		return briefcase.hasTag() ? briefcase.getTag().getString("ownerUUID") : "";
	}
}
