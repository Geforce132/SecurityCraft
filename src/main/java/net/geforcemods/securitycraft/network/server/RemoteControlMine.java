package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;

public class RemoteControlMine{

	private int x, y, z;
	private String state;

	public RemoteControlMine(){

	}

	public RemoteControlMine(int x, int y, int z, String state){
		this.x = x;
		this.y = y;
		this.z = z;
		this.state = state;
	}

	public static void encode(RemoteControlMine message, PacketBuffer buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeString(message.state);
	}

	public static RemoteControlMine decode(PacketBuffer buf)
	{
		RemoteControlMine message = new RemoteControlMine();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.state = buf.readString(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(RemoteControlMine message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			BlockPos pos = new BlockPos(message.x, message.y, message.z);

			if(player.world.getBlockState(pos) instanceof IExplosive)
			{
				TileEntity te = player.world.getTileEntity(pos);

				if(te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player))
				{
					if(message.state.equalsIgnoreCase("activate"))
						((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).activateMine(player.world, BlockUtils.toPos(message.x, message.y, message.z));
					else if(message.state.equalsIgnoreCase("defuse"))
						((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).defuseMine(player.world, BlockUtils.toPos(message.x, message.y, message.z));
					else if(message.state.equalsIgnoreCase("detonate"))
						((IExplosive) BlockUtils.getBlock(player.world, message.x, message.y, message.z)).explode(player.world, BlockUtils.toPos(message.x, message.y, message.z));
				}
			}
		});

		ctx.get().setPacketHandled(true);

	}

}
