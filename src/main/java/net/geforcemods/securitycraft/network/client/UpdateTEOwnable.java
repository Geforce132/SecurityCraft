package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class UpdateTEOwnable
{
	private BlockPos pos;
	private String name;
	private String uuid;
	private boolean customizable;
	private CompoundNBT tag;

	public UpdateTEOwnable() {}

	public UpdateTEOwnable(BlockPos pos, String name, String uuid, boolean customizable, CompoundNBT tag)
	{
		this.pos = pos;
		this.name = name;
		this.uuid = uuid;
		this.customizable = customizable;
		this.tag = tag;
	}

	public void toBytes(PacketBuffer buf)
	{
		buf.writeLong(pos.toLong());
		buf.writeString(name);
		buf.writeString(uuid);
		buf.writeBoolean(customizable);

		if(customizable)
			buf.writeCompoundTag(tag);
	}

	public void fromBytes(PacketBuffer buf)
	{
		pos = BlockPos.fromLong(buf.readLong());
		name = buf.readString(Integer.MAX_VALUE / 4);
		uuid = buf.readString(Integer.MAX_VALUE / 4);
		customizable = buf.readBoolean();

		if(customizable)
			tag = buf.readCompoundTag();
	}

	public static void encode(UpdateTEOwnable message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static UpdateTEOwnable decode(PacketBuffer packet)
	{
		UpdateTEOwnable message = new UpdateTEOwnable();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(UpdateTEOwnable message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().world.getTileEntity(message.pos);

			if(!(te instanceof IOwnable))
				return;

			((IOwnable)te).setOwner(message.uuid, message.name);

			if(message.customizable)
				((CustomizableTileEntity)te).read(message.tag);
		});

		ctx.get().setPacketHandled(true);
	}
}
