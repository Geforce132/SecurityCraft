package net.geforcemods.securitycraft.network.server;

import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.KeypadChestBlock;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
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
					checkAndUpdateAdjacentChest((KeypadChestTileEntity)te, world, pos, password, player);
			}
		});

		ctx.get().setPacketHandled(true);
	}

	private static void checkAndUpdateAdjacentChest(KeypadChestTileEntity te, World world, BlockPos pos, String codeToSet, PlayerEntity player) {
		if(te.getBlockState().get(KeypadChestBlock.TYPE) != ChestType.SINGLE)
		{
			BlockPos offsetPos = pos.offset(KeypadChestBlock.getDirectionToAttached(te.getBlockState()));
			TileEntity otherTe = world.getTileEntity(offsetPos);

			if(otherTe instanceof KeypadChestTileEntity && te.getOwner().owns((KeypadChestTileEntity)otherTe))
			{
				((KeypadChestTileEntity)otherTe).setPassword(codeToSet);
				world.notifyBlockUpdate(offsetPos, otherTe.getBlockState(), otherTe.getBlockState(), 2);
			}
		}
	}
}
