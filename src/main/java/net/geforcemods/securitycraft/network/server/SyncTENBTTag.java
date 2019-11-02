package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncTENBTTag {

	private int x, y, z;
	private CompoundNBT tag;

	public SyncTENBTTag(){

	}

	public SyncTENBTTag(int x, int y, int z, CompoundNBT tag){
		this.x = x;
		this.y = y;
		this.z = z;
		this.tag = tag;
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		tag = buf.readCompoundTag();
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeCompoundTag(tag);
	}

	public static void encode(SyncTENBTTag message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SyncTENBTTag decode(PacketBuffer packet)
	{
		SyncTENBTTag message = new SyncTENBTTag();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SyncTENBTTag message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			CompoundNBT tag = message.tag;
			PlayerEntity player = ctx.get().getSender();

			if(player.world.getTileEntity(pos) != null)
				player.world.getTileEntity(pos).read(tag);
		});

		ctx.get().setPacketHandled(true);
	}

}
