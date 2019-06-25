package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.containers.BriefcaseInventory;
import net.geforcemods.securitycraft.containers.ContainerBriefcase;
import net.geforcemods.securitycraft.containers.ContainerGeneric;
import net.geforcemods.securitycraft.containers.ContainerTEGeneric;
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
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class OpenGui {

	private ResourceLocation id;
	private int dimId;
	private int x;
	private int y;
	private int z;

	public OpenGui(){}

	public OpenGui(ResourceLocation id, int dimId, BlockPos pos){
		this(id, dimId, pos.getX(), pos.getY(), pos.getZ());
	}


	public OpenGui(ResourceLocation id, int dimId, int x, int y, int z){
		this.id = id;
		this.dimId = dimId;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeString(id.toString());
		buf.writeInt(dimId);
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
	}

	public void fromBytes(PacketBuffer buf) {
		id = new ResourceLocation(buf.readString(Integer.MAX_VALUE / 4));
		dimId = buf.readInt();
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
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
			int x = message.x;
			int y = message.y;
			int z = message.z;
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
				}, new BlockPos(x, y, z));
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
				}, new BlockPos(x, y, z));
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
				}, new BlockPos(x, y, z));
			}
			else if(id.equals(SCContent.cTypeCheckPassword.getRegistryName()))
			{
				NetworkHooks.openGui(player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new ContainerTEGeneric(SCContent.cTypeCheckPassword, windowId, ServerLifecycleHooks.getCurrentServer().getWorld(DimensionType.getById(message.dimId)), new BlockPos(x, y, z));
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(SCContent.briefcase.getTranslationKey());
					}
				}, new BlockPos(x, y, z));
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
