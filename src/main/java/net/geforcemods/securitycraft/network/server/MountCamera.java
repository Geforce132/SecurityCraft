package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.SecurityCameraBlockEntity;
import net.geforcemods.securitycraft.blocks.SecurityCameraBlock;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MountCamera implements IMessage {
	private BlockPos pos;

	public MountCamera() {}

	public MountCamera(BlockPos pos) {
		this.pos = pos;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
	}

	public static class Handler implements IMessageHandler<MountCamera, IMessage> {
		@Override
		public IMessage onMessage(MountCamera message, MessageContext context) {
			LevelUtils.addScheduledTask(context.getServerHandler().player.world, (() -> {
				BlockPos pos = message.pos;
				EntityPlayerMP player = context.getServerHandler().player;
				World world = player.world;
				IBlockState state = world.getBlockState(pos);

				if (world.isBlockLoaded(pos) && state.getBlock() == SCContent.securityCamera) {
					TileEntity te = world.getTileEntity(pos);

					if (te instanceof SecurityCameraBlockEntity) {
						SecurityCameraBlockEntity cam = (SecurityCameraBlockEntity) te;

						if (cam.isOwnedBy(player) || cam.isAllowed(player))
							((SecurityCameraBlock) state.getBlock()).mountCamera(world, pos.getX(), pos.getY(), pos.getZ(), player);
						else
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.cameraMonitor.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:notOwned", cam.getOwner().getName()), TextFormatting.RED);
					}

					return;
				}

				PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.cameraMonitor.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:cameraMonitor.cameraNotAvailable", pos), TextFormatting.RED);
			}));

			return null;
		}
	}
}