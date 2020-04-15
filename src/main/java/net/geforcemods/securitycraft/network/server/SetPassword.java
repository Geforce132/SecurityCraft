package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
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

	public void toBytes(PacketBuffer buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeString(password);
	}

	public void fromBytes(PacketBuffer buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = buf.readString(Integer.MAX_VALUE / 4);
	}

	public static void encode(SetPassword message, PacketBuffer packet)
	{
		message.toBytes(packet);
	}

	public static SetPassword decode(PacketBuffer packet)
	{
		SetPassword message = new SetPassword();

		message.fromBytes(packet);
		return message;
	}

	public static void onMessage(SetPassword message, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
			String password = message.password;
			PlayerEntity player = ctx.get().getSender();

			if(getWorld(player).getTileEntity(pos) instanceof IPasswordProtected){
				((IPasswordProtected) getWorld(player).getTileEntity(pos)).setPassword(password);
				checkForAdjacentChest(pos, password, player);
			}
		});

		ctx.get().setPacketHandled(true);
	}

	private static void checkForAdjacentChest(BlockPos pos, String codeToSet, PlayerEntity player) {
		if(getWorld(player).getTileEntity(pos) instanceof KeypadChestTileEntity)
			if(getWorld(player).getTileEntity(pos.east()) instanceof KeypadChestTileEntity)
				((IPasswordProtected) getWorld(player).getTileEntity(pos.east())).setPassword(codeToSet);
			else if(getWorld(player).getTileEntity(pos.west()) instanceof KeypadChestTileEntity)
				((IPasswordProtected) getWorld(player).getTileEntity(pos.west())).setPassword(codeToSet);
			else if(getWorld(player).getTileEntity(pos.south()) instanceof KeypadChestTileEntity)
				((IPasswordProtected) getWorld(player).getTileEntity(pos.south())).setPassword(codeToSet);
			else if(getWorld(player).getTileEntity(pos.north()) instanceof KeypadChestTileEntity)
				((IPasswordProtected) getWorld(player).getTileEntity(pos.north())).setPassword(codeToSet);
	}

	private static World getWorld(PlayerEntity player) //i'm lazy
	{
		return player.world;
	}
}
