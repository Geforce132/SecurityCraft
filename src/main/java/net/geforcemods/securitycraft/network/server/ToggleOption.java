package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class ToggleOption {

	private int x, y, z, id;

	public ToggleOption(){ }

	public ToggleOption(int x, int y, int z, int id){
		this.x = x;
		this.y = y;
		this.z = z;
		this.id = id;
	}

	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(id);
	}

	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		id = buf.readInt();
	}

	public static void encode(ToggleOption message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static ToggleOption decode(PacketBuffer packet)
	{
		ToggleOption message = new ToggleOption();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(ToggleOption message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			int id = message.id;
			PlayerEntity player = ctx.get().getSender();

			if(player.world.getTileEntity(pos) != null && player.world.getTileEntity(pos) instanceof CustomizableTileEntity) {
				((CustomizableTileEntity) player.world.getTileEntity(pos)).customOptions()[id].toggle();
				((CustomizableTileEntity) player.world.getTileEntity(pos)).onOptionChanged(((CustomizableTileEntity) player.world.getTileEntity(pos)).customOptions()[id]);
				((CustomizableTileEntity) player.world.getTileEntity(pos)).sync();
			}
		});

		ctx.get().setPacketHandled(true);
	}
}
