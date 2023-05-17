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
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;

public class OpenBriefcaseInventory {
	private Component name;

	public OpenBriefcaseInventory() {}

	public OpenBriefcaseInventory(Component name) {
		this.name = name;
	}

	public OpenBriefcaseInventory(FriendlyByteBuf buf) {
		name = buf.readComponent();
	}

	public void encode(FriendlyByteBuf buf) {
		buf.writeComponent(name);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayer player = ctx.get().getSender();
		BlockPos pos = player.blockPosition();

		if (PlayerUtils.isHoldingItem(player, SCContent.BRIEFCASE.get(), null)) {
			NetworkHooks.openScreen(player, new MenuProvider() {
				@Override
				public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player) {
					return new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
				}

				@Override
				public Component getDisplayName() {
					return name;
				}
			}, pos);
		}
	}
}
