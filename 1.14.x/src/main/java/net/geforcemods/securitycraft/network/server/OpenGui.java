package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.containers.ContainerBriefcase;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class OpenGui {

	private ResourceLocation id;
	private int dimId;
	private BlockPos pos;

	public OpenGui(){}

	public OpenGui(ResourceLocation id, int dimId, BlockPos pos){
		this.id = id;
		this.dimId = dimId;
		this.pos = pos;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeResourceLocation(id);
		buf.writeInt(dimId);
		buf.writeBlockPos(pos);
	}

	public void fromBytes(PacketBuffer buf) {
		id = buf.readResourceLocation();
		dimId = buf.readInt();
		pos = buf.readBlockPos();
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
			BlockPos pos = message.pos;
			ServerPlayerEntity player = ctx.get().getSender();

			if(id.equals(SCContent.cTypeBriefcaseInventory.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new ContainerBriefcase(windowId, inv, new BriefcaseInventory(player.inventory.getCurrentItem()));
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(SCContent.briefcase.getTranslationKey());
					}
				}, pos);
			}
			else if(id.equals(SCContent.cTypeBriefcaseSetup.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new ContainerGeneric(SCContent.cTypeBriefcaseSetup, windowId);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(SCContent.briefcase.getTranslationKey());
					}
				}, pos);
			}
			else if(id.equals(SCContent.cTypeBriefcase.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new ContainerGeneric(SCContent.cTypeBriefcase, windowId);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(SCContent.briefcase.getTranslationKey());
					}
				}, pos);
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
