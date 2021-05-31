package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.tileentity.TileEntityTrophySystem;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class SyncTrophySystem implements IMessage {

	private BlockPos pos;
	private String projectileType;
	private boolean allowed;

	public SyncTrophySystem() {

	}

	public SyncTrophySystem(BlockPos pos, EntityEntry projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileType = projectileType.getRegistryName().toString();
		this.allowed = allowed;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, projectileType);
		buf.writeBoolean(allowed);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		projectileType = ByteBufUtils.readUTF8String(buf);
		allowed = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<SyncTrophySystem, IMessage> {
		@Override
		public IMessage onMessage(SyncTrophySystem message, MessageContext ctx) {
			WorldUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityEntry projectileType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(message.projectileType));

				if (projectileType != null) {
					EntityPlayerMP player = ctx.getServerHandler().player;
					World world = player.world;
					BlockPos pos = message.pos;
					boolean allowed = message.allowed;
					TileEntity te = world.getTileEntity(pos);

					if (te instanceof TileEntityTrophySystem && ((TileEntityTrophySystem)te).getOwner().isOwner(player)) {
						IBlockState state = world.getBlockState(pos);

						((TileEntityTrophySystem)te).setFilter(projectileType, allowed);
						world.notifyBlockUpdate(pos, state, state, 2);
					}
				}});


			return null;
		}
	}
}
