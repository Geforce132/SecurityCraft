package net.geforcemods.securitycraft.network.client;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class UpdateTEOwnable
{
	private BlockPos pos;
	private String name;
	private String uuid;
	private boolean syncTag;
	private CompoundTag tag;

	public UpdateTEOwnable() {}

	public UpdateTEOwnable(BlockPos pos, String name, String uuid, boolean syncTag, CompoundTag tag)
	{
		this.pos = pos;
		this.name = name;
		this.uuid = uuid;
		this.syncTag = syncTag;
		this.tag = tag;
	}

	public static void encode(UpdateTEOwnable message, FriendlyByteBuf buf)
	{
		buf.writeLong(message.pos.asLong());
		buf.writeUtf(message.name);
		buf.writeUtf(message.uuid);
		buf.writeBoolean(message.syncTag);

		if(message.syncTag)
			buf.writeNbt(message.tag);
	}

	public static UpdateTEOwnable decode(FriendlyByteBuf buf)
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
			BlockEntity te = Minecraft.getInstance().level.getBlockEntity(message.pos);

			if(!(te instanceof IOwnable))
				return;

			((IOwnable)te).setOwner(message.uuid, message.name);

			if(message.syncTag)
				te.load( message.tag);
		});

		ctx.get().setPacketHandled(true);
	}
}
