package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

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
	private boolean syncTag;
	private CompoundNBT tag;

	public UpdateTEOwnable() {}

	public UpdateTEOwnable(BlockPos pos, String name, String uuid, boolean syncTag, CompoundNBT tag)
	{
		this.pos = pos;
		this.name = name;
		this.uuid = uuid;
		this.syncTag = syncTag;
		this.tag = tag;
	}

	public static void encode(UpdateTEOwnable message, PacketBuffer buf)
	{
		buf.writeLong(message.pos.toLong());
		buf.writeString(message.name);
		buf.writeString(message.uuid);
		buf.writeBoolean(message.syncTag);

		if(message.syncTag)
			buf.writeCompoundTag(message.tag);
	}

	public static UpdateTEOwnable decode(PacketBuffer buf)
	{
		UpdateTEOwnable message = new UpdateTEOwnable();

		message.pos = BlockPos.fromLong(buf.readLong());
		message.name = buf.readString(Integer.MAX_VALUE / 4);
		message.uuid = buf.readString(Integer.MAX_VALUE / 4);
		message.syncTag = buf.readBoolean();

		if(message.syncTag)
			message.tag = buf.readCompoundTag();

		return message;
	}

	public static void onMessage(UpdateTEOwnable message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().world.getTileEntity(message.pos);

			if(!(te instanceof IOwnable))
				return;

			((IOwnable)te).setOwner(message.uuid, message.name);

			if(message.syncTag)
				te.read(Minecraft.getInstance().world.getBlockState(message.pos), message.tag);
		});

		ctx.get().setPacketHandled(true);
	}
}
