package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.ItemContainer;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class OpenBriefcaseInventory {
	private ITextComponent name;

	public OpenBriefcaseInventory() {}

	public OpenBriefcaseInventory(ITextComponent name) {
		this.name = name;
	}

	public OpenBriefcaseInventory(PacketBuffer buf) {
		name = buf.readComponent();
	}

	public void encode(PacketBuffer buf) {
		buf.writeComponent(name);
	}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		ServerPlayerEntity player = ctx.get().getSender();
		BlockPos pos = player.blockPosition();

		if (PlayerUtils.isHoldingItem(player, SCContent.BRIEFCASE.get(), null)) {
			NetworkHooks.openGui(player, new INamedContainerProvider() {
				@Override
				public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
					return new BriefcaseMenu(windowId, inv, ItemContainer.briefcase(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
				}

				@Override
				public ITextComponent getDisplayName() {
					return name;
				}
			}, pos);
		}
	}
}
