package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class OpenBriefcaseInventory {
	private Component name;

	public OpenBriefcaseInventory() {}

	public OpenBriefcaseInventory(Component name) {
		this.name = name;
	}

	public static void encode(OpenBriefcaseInventory message, FriendlyByteBuf buf) {
		buf.writeComponent(message.name);
	}

	public static OpenBriefcaseInventory decode(FriendlyByteBuf buf) {
		OpenBriefcaseInventory message = new OpenBriefcaseInventory();

		message.name = buf.readComponent();
		return message;
	}

	public static void onMessage(OpenBriefcaseInventory message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer player = ctx.get().getSender();
			BlockPos pos = player.blockPosition();

			if (PlayerUtils.isHoldingItem(player, SCContent.BRIEFCASE.get(), null)) {
				NetworkHooks.openGui(player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
						return new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
					}

					@Override
					public Component getDisplayName() {
						return message.name;
					}
				}, pos);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
