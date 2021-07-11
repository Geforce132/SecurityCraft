package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;

public class SetPassword {

	private String password;
	private int x, y, z;

	public SetPassword(){

	}

	public SetPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	public static void encode(SetPassword message, PacketBuffer buf)
	{
		buf.writeInt(message.x);
		buf.writeInt(message.y);
		buf.writeInt(message.z);
		buf.writeString(message.password);
	}

	public static SetPassword decode(PacketBuffer buf)
	{
		SetPassword message = new SetPassword();

		message.x = buf.readInt();
		message.y = buf.readInt();
		message.z = buf.readInt();
		message.password = buf.readString(Integer.MAX_VALUE / 4);
		return message;
	}

	public static void onMessage(SetPassword message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = new BlockPos(message.x, message.y, message.z);
			String password = message.password;
			PlayerEntity player = ctx.get().getSender();
			World world = player.world;
			TileEntity te = world.getTileEntity(pos);

			if(te instanceof IPasswordProtected && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player))){
				((IPasswordProtected)te).setPassword(password);

				if(te instanceof KeypadChestTileEntity)
					checkAndUpdateAdjacentChest(world, pos, password, player);
			}
		});

		ctx.get().setPacketHandled(true);
	}

	private static void checkAndUpdateAdjacentChest(World world, BlockPos pos, String codeToSet, PlayerEntity player) {
		for(Direction dir : Streams.stream(Direction.Plane.HORIZONTAL.iterator()).collect(Collectors.toList()))
		{
			TileEntity te = world.getTileEntity(pos.offset(dir));

			if(te instanceof KeypadChestTileEntity)
			{
				((IPasswordProtected)te).setPassword(codeToSet);
				return;
			}
		}
	}
}
