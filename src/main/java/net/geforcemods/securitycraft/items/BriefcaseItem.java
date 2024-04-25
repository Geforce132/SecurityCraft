package net.geforcemods.securitycraft.items;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.components.OwnerData;
import net.geforcemods.securitycraft.components.PasscodeData;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class BriefcaseItem extends Item {
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
			PacketDistributor.sendToPlayer((ServerPlayer) player, new OpenScreen(stack.has(SCContent.PASSCODE_DATA) ? OpenScreen.DataType.CHECK_BRIEFCASE_PASSCODE : OpenScreen.DataType.SET_BRIEFCASE_PASSCODE));
	}

	public static void hashAndSetPasscode(ItemStack briefcase, String passcode, Consumer<byte[]> afterSet) {
		byte[] salt = PasscodeUtils.generateSalt();
		UUID saltKey = SaltData.putSalt(salt);

		PasscodeUtils.hashPasscode(passcode, salt, p -> {
			briefcase.set(SCContent.PASSCODE_DATA, new PasscodeData(PasscodeUtils.bytesToString(p), saltKey));
			afterSet.accept(p);
		});
	}

	public static void checkPasscode(Player player, ItemStack briefcase, String incomingCode, String briefcaseCode, PasscodeData passcodeData) {
		UUID saltKey = passcodeData != null ? passcodeData.saltKey() : null;
		byte[] salt = SaltData.getSalt(saltKey);

		if (salt == null) { //If no salt key or no salt associated with the given key can be found, a new passcode needs to be set
			briefcase.remove(SCContent.PASSCODE_DATA);
			return;
		}

		PasscodeUtils.hashPasscode(incomingCode, salt, p -> {
			if (Arrays.equals(PasscodeUtils.stringToBytes(briefcaseCode), p)) {
				if (!briefcase.has(SCContent.OWNER_DATA)) //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
					briefcase.set(SCContent.OWNER_DATA, OwnerData.fromPlayer(player, true));

				player.openMenu(new MenuProvider() {
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
		OwnerData owner = briefcase.get(SCContent.OWNER_DATA);

		if (owner != null) {
			String ownerName = owner.name();
			String ownerUUID = owner.uuid();

			return ownerName.isEmpty() || ownerUUID.equals(player.getUUID().toString()) || (ownerUUID.equals("ownerUUID") && ownerName.equals(player.getName().getString()));
		}

		return false;
	}
}
