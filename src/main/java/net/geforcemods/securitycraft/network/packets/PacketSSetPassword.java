package net.geforcemods.securitycraft.network.packets;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSSetPassword implements IMessage{

	private String password;
	private int x, y, z;

	public PacketSSetPassword(){

	}

	public PacketSSetPassword(int x, int y, int z, String code){
		this.x = x;
		this.y = y;
		this.z = z;
		password = code;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		ByteBufUtils.writeUTF8String(buf, password);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		password = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler extends PacketHelper implements IMessageHandler<PacketSSetPassword, IMessage> {

		@Override
		public IMessage onMessage(PacketSSetPassword message, MessageContext ctx) {
			WorldUtils.addScheduledTask(getWorld(ctx.getServerHandler().player), () -> {
				BlockPos pos = BlockUtils.toPos(message.x, message.y, message.z);
				String password = message.password;
				EntityPlayer player = ctx.getServerHandler().player;

				if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof IPasswordProtected){
					((IPasswordProtected) getWorld(player).getTileEntity(pos)).setPassword(password);
					checkForAdjacentChest(pos, password, player);
				}
			});

			return null;
		}

		private void checkForAdjacentChest(BlockPos pos, String codeToSet, EntityPlayer player) {
			if(getWorld(player).getTileEntity(pos) != null && getWorld(player).getTileEntity(pos) instanceof TileEntityKeypadChest)
				if(getWorld(player).getTileEntity(pos.east()) != null && getWorld(player).getTileEntity(pos.east()) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(pos.east())).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(pos.west()) != null && getWorld(player).getTileEntity(pos.west()) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(pos.west())).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(pos.south()) != null && getWorld(player).getTileEntity(pos.south()) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(pos.south())).setPassword(codeToSet);
				else if(getWorld(player).getTileEntity(pos.north()) != null && getWorld(player).getTileEntity(pos.north()) instanceof TileEntityKeypadChest)
					((IPasswordProtected) getWorld(player).getTileEntity(pos.north())).setPassword(codeToSet);
		}
	}

}
