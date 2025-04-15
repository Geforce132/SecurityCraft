package net.geforcemods.securitycraft.network.server;

import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SyncLaserSideConfig implements IMessage {
	private BlockPos pos;
	private NBTTagCompound sideConfig;

	public SyncLaserSideConfig() {}

	public SyncLaserSideConfig(BlockPos pos, Map<EnumFacing, Boolean> sideConfig) {
		this.pos = pos;
		this.sideConfig = LaserBlockBlockEntity.saveSideConfig(sideConfig);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		ByteBufUtils.writeTag(buf, sideConfig);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		sideConfig = ByteBufUtils.readTag(buf);
	}

	public static class Handler implements IMessageHandler<SyncLaserSideConfig, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(SyncLaserSideConfig message, MessageContext ctx) {
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayer player = ctx.getServerHandler().player;
				World world = player.world;
				TileEntity te = world.getTileEntity(message.pos);

				if (!player.isSpectator() && te instanceof LaserBlockBlockEntity) {
					LaserBlockBlockEntity laser = (LaserBlockBlockEntity) te;

					if (laser.isOwnedBy(player)) {
						IBlockState state = world.getBlockState(message.pos);

						laser.applyNewSideConfig(LaserBlockBlockEntity.loadSideConfig(message.sideConfig), player);
						world.notifyBlockUpdate(message.pos, state, state, 2);
					}
				}
			});
			return null;
		}
	}
}
