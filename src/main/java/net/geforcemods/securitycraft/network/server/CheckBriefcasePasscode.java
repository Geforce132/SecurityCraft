package net.geforcemods.securitycraft.network.server;

import java.util.Arrays;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
		this.passcode = Utils.hashPasscodeWithoutSalt(passcode);
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
				byte[] salt = Utils.stringToBytes(tag.getString("salt"));

				if (briefcaseCode.length() == 4) { //If an old plaintext passcode is encountered, generate and store the hashed variant
					BriefcaseItem.hashAndSetPasscode(tag, Utils.hashPasscodeWithoutSalt(briefcaseCode));
					briefcaseCode = tag.getString("passcode");
				}

				if (Arrays.equals(Utils.stringToBytes(briefcaseCode), Utils.hashPasscode(passcode, salt))) {
					if (!tag.contains("owner")) { //If the briefcase doesn't have an owner (that usually gets set when assigning a new passcode), set the player that first enters the correct password as the owner
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
