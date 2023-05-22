package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.misc.SaltData;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class CheckBriefcasePasscode {
	private String passcode;

	public CheckBriefcasePasscode() {}

	public CheckBriefcasePasscode(String passcode) {
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	public static void encode(CheckBriefcasePasscode message, FriendlyByteBuf buf) {
		buf.writeUtf(message.passcode);
	}

	public static CheckBriefcasePasscode decode(FriendlyByteBuf buf) {
		CheckBriefcasePasscode message = new CheckBriefcasePasscode();

		message.passcode = buf.readUtf();
		return message;
	}

	public static void onMessage(CheckBriefcasePasscode message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			BlockPos pos = player.blockPosition();
			ItemStack briefcase = PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get());
			String passcode = message.passcode;

			if (!briefcase.isEmpty()) {
				CompoundTag tag = briefcase.getOrCreateTag();
				String briefcaseCode = tag.getString("passcode");

				if (briefcaseCode.length() == 4) { //If an old plaintext passcode is encountered, generate and store the hashed variant
					BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode));
					briefcaseCode = tag.getString("passcode");
				}

				UUID saltKey = tag.contains("saltKey", Tag.TAG_INT_ARRAY) ? tag.getUUID("saltKey") : null;
				byte[] salt = SaltData.getSalt(saltKey);

				if (salt == null) { //If no salt key or no salt associated with the given key can be found, a new password needs to be set
					PasscodeUtils.filterPasscodeAndSaltFromTag(tag);
					return;
				}

				if (Arrays.equals(PasscodeUtils.stringToBytes(briefcaseCode), PasscodeUtils.hashPasscode(passcode, salt))) {
					if (!tag.contains("owner")) { //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct passcode as the owner
						tag.putString("owner", player.getName().getString());
						tag.putString("ownerUUID", player.getUUID().toString());
					}

					NetworkHooks.openScreen(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
							return new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
						}

						@Override
						public Component getDisplayName() {
							return briefcase.getHoverName();
						}
					}, pos);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
