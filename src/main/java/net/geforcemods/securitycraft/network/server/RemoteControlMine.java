package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IExplosive;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
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

	public static void encode(RemoteControlMine message, FriendlyByteBuf buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeUtf(message.state);
	}

	public static RemoteControlMine decode(FriendlyByteBuf buf)
	{
		RemoteControlMine message = new RemoteControlMine();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.state = buf.readUtf(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(RemoteControlMine message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			Level world = player.level;
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			BlockState state = world.getBlockState(pos);

			if(state.getBlock() instanceof IExplosive)
			{
				IExplosive explosive = ((IExplosive) state.getBlock());
				BlockEntity te = world.getBlockEntity(pos);

				if(te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player))
				{
					if(message.state.equalsIgnoreCase("activate"))
						explosive.activateMine(world,pos);
					else if(message.state.equalsIgnoreCase("defuse"))
						explosive.defuseMine(world, pos);
					else if(message.state.equalsIgnoreCase("detonate"))
						explosive.explode(world, pos);
				}
			}
		});

		ctx.get().setPacketHandled(true);

	}

}
