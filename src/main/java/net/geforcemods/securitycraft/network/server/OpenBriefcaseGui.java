package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.inventory.BriefcaseContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseMenu;
import net.geforcemods.securitycraft.inventory.GenericMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class OpenBriefcaseGui {

	private ResourceLocation id;
	private Component name;

	public OpenBriefcaseGui(){}

	public OpenBriefcaseGui(ResourceLocation id, Component name){
		this.id = id;
		this.name = name;
	}

	public static void encode(OpenBriefcaseGui message, FriendlyByteBuf buf)
	{
		buf.writeResourceLocation(message.id);
		buf.writeComponent(message.name);
	}

	public static OpenBriefcaseGui decode(FriendlyByteBuf buf)
	{
		OpenBriefcaseGui message = new OpenBriefcaseGui();

		message.id = buf.readResourceLocation();
		message.name = buf.readComponent();
		return message;
	}

	public static void onMessage(OpenBriefcaseGui message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ResourceLocation id = message.id;
			ServerPlayer player = ctx.get().getSender();
			BlockPos pos = player.blockPosition();

			if(PlayerUtils.isHoldingItem(player, SCContent.BRIEFCASE.get(), null))
			{
				if(id.equals(SCContent.mTypeBriefcaseInventory.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new BriefcaseMenu(windowId, inv, new BriefcaseContainer(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
						}

						@Override
						public Component getDisplayName()
						{
							return message.name;
						}
					}, pos);
				}
				else if(id.equals(SCContent.mTypeBriefcaseSetup.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericMenu(SCContent.mTypeBriefcaseSetup, windowId);
						}

						@Override
						public Component getDisplayName()
						{
							return message.name;
						}
					}, pos);
				}
				else if(id.equals(SCContent.mTypeBriefcase.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericMenu(SCContent.mTypeBriefcase, windowId);
						}

						@Override
						public Component getDisplayName()
						{
							return message.name;
						}
					}, pos);
				}
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
