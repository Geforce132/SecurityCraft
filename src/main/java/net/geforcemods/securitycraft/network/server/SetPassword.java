package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadChest;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetPassword implements IMessage{

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

	public static class Handler implements IMessageHandler<SetPassword, IMessage> {

		@Override
		public IMessage onMessage(SetPassword message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				BlockPos pos = new BlockPos(message.x, message.y, message.z);
				String password = message.password;
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(pos);

				if(te instanceof IPasswordProtected && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player))){
					((IPasswordProtected)te).setPassword(password);

					if(te instanceof TileEntityKeypadChest)
						checkAndUpdateAdjacentChest(world, pos, password, player);
				}
			});

			return null;
		}

		private void checkAndUpdateAdjacentChest(World world, BlockPos pos, String codeToSet, EntityPlayer player) {
			for(EnumFacing facing : EnumFacing.HORIZONTALS)
			{
				TileEntity te = world.getTileEntity(pos.offset(facing));

				if(te instanceof TileEntityKeypadChest)
				{
					((IPasswordProtected)te).setPassword(codeToSet);
					break;
				}
			}
		}
	}

}
