package net.geforcemods.securitycraft.items;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class BriefcaseItem extends Item implements DyeableLeatherItem {
	public BriefcaseItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		handle(ctx.getItemInHand(), ctx.getLevel(), ctx.getPlayer());
		return InteractionResult.CONSUME;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);

		handle(stack, level, player);
		return InteractionResultHolder.consume(stack);
	}

	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		return enchantment == Enchantments.VANISHING_CURSE;
	}

	private void handle(ItemStack stack, Level level, Player player) {
		if (!level.isClientSide)
			SecurityCraft.CHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(stack.getOrCreateTag().contains("passcode") ? OpenScreen.DataType.CHECK_BRIEFCASE_PASSCODE : OpenScreen.DataType.SET_BRIEFCASE_PASSCODE));
	}

	@Override
	public void appendHoverText(ItemStack briefcase, Level level, List<Component> tooltip, TooltipFlag flag) {
		String ownerName = getOwnerName(briefcase);

		if (!ownerName.isEmpty())
			tooltip.add(Utils.localize("tooltip.securitycraft.component.owner", ownerName).setStyle(Utils.GRAY_STYLE));
	}

	public static void hashAndSetPasscode(CompoundTag briefcaseTag, String passcode, Consumer<byte[]> afterSet) {
		byte[] salt = PasscodeUtils.generateSalt();

		briefcaseTag.putUUID("saltKey", SaltData.putSalt(salt));
		PasscodeUtils.hashPasscode(passcode, salt, p -> {
			briefcaseTag.putString("passcode", PasscodeUtils.bytesToString(p));
			afterSet.accept(p);
		});
	}

	public static void checkPasscode(ServerPlayer player, ItemStack briefcase, String incomingCode, String briefcaseCode, CompoundTag tag) {
		UUID saltKey = tag.contains("saltKey", Tag.TAG_INT_ARRAY) ? tag.getUUID("saltKey") : null;
		byte[] salt = SaltData.getSalt(saltKey);

		if (salt == null) { //If no salt key or no salt associated with the given key can be found, a new passcode needs to be set
			PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
			return;
		}

		PasscodeUtils.hashPasscode(incomingCode, salt, p -> {
			if (Arrays.equals(PasscodeUtils.stringToBytes(briefcaseCode), p)) {
				if (!tag.contains("owner")) { //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
					tag.putString("owner", player.getName().getString());
					tag.putString("ownerUUID", player.getUUID().toString());
				}

				NetworkHooks.openScreen(player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getItemStackFromAnyHand(player, SCContent.BRIEFCASE.get())));
					}

					@Override
					public Component getDisplayName() {
						return briefcase.getHoverName();
					}
				}, player.blockPosition());
			}
		});
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
