package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetExplosiveState{

	private int x, y, z;
	private String state;

	public SetExplosiveState(){

	}

	public SetExplosiveState(int x, int y, int z, String state){
		this.x = x;
		this.y = y;
		this.z = z;
		this.state = state;
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		state = buf.readString(Integer.MAX_VALUE / 4);
	}

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(state);
	}

	public static void encode(SetExplosiveState message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetExplosiveState decode(PacketBuffer packet)
	{
		SetExplosiveState message = new SetExplosiveState();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetExplosiveState message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();

			if(BlockUtils.getBlock(player.world, message.x, message.y, message.z) instanceof IExplosive)
				if(message.state.equalsIgnoreCase("activate"))
					((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).activateMine(player.world, BlockUtils.toPos(message.x, message.y, message.z));
				else if(message.state.equalsIgnoreCase("defuse"))
					((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).defuseMine(player.world, BlockUtils.toPos(message.x, message.y, message.z));
				else if(message.state.equalsIgnoreCase("detonate"))
					((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).explode(player.world, BlockUtils.toPos(message.x, message.y, message.z));
		});

		ctx.get().setPacketHandled(true);

	}

}
