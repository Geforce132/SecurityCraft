package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.TrophySystemBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
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
	private String projectileTypeLocation;
	private boolean allowed;

	public SyncTrophySystem() {}

	public SyncTrophySystem(BlockPos pos, EntityEntry projectileType, boolean allowed) {
		this.pos = pos;
		this.projectileTypeLocation = projectileType.getRegistryName().toString();
		this.allowed = allowed;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeUTF8String(buf, projectileTypeLocation);
		buf.writeBoolean(allowed);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		projectileTypeLocation = ByteBufUtils.readUTF8String(buf);
		allowed = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<SyncTrophySystem, IMessage> {
		@Override
		public IMessage onMessage(SyncTrophySystem message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityEntry projectileType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(message.projectileTypeLocation));

				if (projectileType != null) {
					EntityPlayerMP player = ctx.getServerHandler().player;
					World world = player.world;
					TileEntity te = world.getTileEntity(message.pos);

					if (!player.isSpectator() && te instanceof TrophySystemBlockEntity && ((TrophySystemBlockEntity) te).isOwnedBy(player)) {
						IBlockState state = world.getBlockState(message.pos);

						((TrophySystemBlockEntity) te).setFilter(projectileType, message.allowed);
						world.notifyBlockUpdate(message.pos, state, state, 2);
					}
				}
			});

			return null;
		}
	}
}
