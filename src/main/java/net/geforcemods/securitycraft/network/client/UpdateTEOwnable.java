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
		buf.writeLong(message.pos.asLong());
		buf.writeUtf(message.name);
		buf.writeUtf(message.uuid);
		buf.writeBoolean(message.syncTag);

		if(message.syncTag)
			buf.writeNbt(message.tag);
	}

	public static UpdateTEOwnable decode(PacketBuffer buf)
	{
		UpdateTEOwnable message = new UpdateTEOwnable();

		message.pos = BlockPos.of(buf.readLong());
		message.name = buf.readUtf(Integer.MAX_VALUE / 4);
		message.uuid = buf.readUtf(Integer.MAX_VALUE / 4);
		message.syncTag = buf.readBoolean();

		if(message.syncTag)
			message.tag = buf.readNbt();

		return message;
	}

	public static void onMessage(UpdateTEOwnable message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			TileEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);

			if(!(te instanceof IOwnable))
				return;

			((IOwnable)te).setOwner(message.uuid, message.name);

			if(message.syncTag)
				te.load(Minecraft.getInstance().level.getBlockState(message.pos), message.tag);
		});

		ctx.get().setPacketHandled(true);
	}
}
