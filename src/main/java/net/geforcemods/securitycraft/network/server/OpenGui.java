package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseContainer;
import net.geforcemods.securitycraft.containers.GenericContainer;
import net.geforcemods.securitycraft.inventory.BriefcaseInventory;
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

public class OpenGui {

	private ResourceLocation id;
	private ITextComponent name;

	public OpenGui(){}

	public OpenGui(ResourceLocation id, ITextComponent name){
		this.id = id;
		this.name = name;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeResourceLocation(id);
		buf.writeTextComponent(name);
	}

	public void fromBytes(PacketBuffer buf) {
		id = buf.readResourceLocation();
		name = buf.readTextComponent();
	}

	public static void encode(OpenGui message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static OpenGui decode(PacketBuffer packet)
	{
		OpenGui message = new OpenGui();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(OpenGui message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ResourceLocation id = message.id;
			ServerPlayerEntity player = ctx.get().getSender();
			BlockPos pos = player.getPosition();

			if(id.equals(SCContent.cTypeBriefcaseInventory.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new BriefcaseContainer(windowId, inv, new BriefcaseInventory(player.inventory.getCurrentItem()));
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return message.name;
					}
				}, pos);
			}
			else if(id.equals(SCContent.cTypeBriefcaseSetup.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new GenericContainer(SCContent.cTypeBriefcaseSetup, windowId);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return message.name;
					}
				}, pos);
			}
			else if(id.equals(SCContent.cTypeBriefcase.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new GenericContainer(SCContent.cTypeBriefcase, windowId);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return message.name;
					}
				}, pos);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
