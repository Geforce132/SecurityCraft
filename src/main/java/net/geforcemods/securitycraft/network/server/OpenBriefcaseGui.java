package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

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
				if(id.equals(SCContent.cTypeBriefcaseInventory.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new BriefcaseContainer(windowId, inv, new BriefcaseInventory(PlayerUtils.getSelectedItemStack(player, SCContent.BRIEFCASE.get())));
						}

						@Override
						public Component getDisplayName()
						{
							return message.name;
						}
					}, pos);
				}
				else if(id.equals(SCContent.cTypeBriefcaseSetup.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericContainer(SCContent.cTypeBriefcaseSetup, windowId);
						}

						@Override
						public Component getDisplayName()
						{
							return message.name;
						}
					}, pos);
				}
				else if(id.equals(SCContent.cTypeBriefcase.getRegistryName()))
				{
					NetworkHooks.openGui(player, new MenuProvider() {
						@Override
						public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
						{
							return new GenericContainer(SCContent.cTypeBriefcase, windowId);
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
