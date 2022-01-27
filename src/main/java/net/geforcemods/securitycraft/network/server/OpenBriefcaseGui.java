package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class OpenBriefcaseGui {
	private ResourceLocation id;
	private ITextComponent name;

	public OpenBriefcaseGui() {}

	public OpenBriefcaseGui(ResourceLocation id, ITextComponent name) {
		this.id = id;
		this.name = name;
	}

	public static void encode(OpenBriefcaseGui message, PacketBuffer buf) {
		buf.writeResourceLocation(message.id);
		buf.writeComponent(message.name);
	}

	public static OpenBriefcaseGui decode(PacketBuffer buf) {
		OpenBriefcaseGui message = new OpenBriefcaseGui();

		message.id = buf.readResourceLocation();
		message.name = buf.readComponent();
		return message;
	}

	public static void onMessage(OpenBriefcaseGui message, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ResourceLocation id = message.id;
			ServerPlayerEntity player = ctx.get().getSender();
			BlockPos pos = player.getCommandSenderBlockPosition();

			if (PlayerUtils.isHoldingItem(player, SCContent.BRIEFCASE.get(), null)) {
				if (id.equals(SCContent.mTypeBriefcaseInventory.getRegistryName())) {
					NetworkHooks.openGui(player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
							return new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
						}

						@Override
						public ITextComponent getDisplayName() {
							return message.name;
						}
					}, pos);
				}
				else if (id.equals(SCContent.mTypeBriefcaseSetup.getRegistryName())) {
					NetworkHooks.openGui(player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
							return new GenericMenu(SCContent.mTypeBriefcaseSetup, windowId);
						}

						@Override
						public ITextComponent getDisplayName() {
							return message.name;
						}
					}, pos);
				}
				else if (id.equals(SCContent.mTypeBriefcase.getRegistryName())) {
					NetworkHooks.openGui(player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
							return new GenericMenu(SCContent.mTypeBriefcase, windowId);
						}

						@Override
						public ITextComponent getDisplayName() {
							return message.name;
						}
					}, pos);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
