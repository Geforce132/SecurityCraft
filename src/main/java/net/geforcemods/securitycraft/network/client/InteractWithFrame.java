package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.blockentities.FrameBlockEntity;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class InteractWithFrame implements IMessage {
	private BlockPos pos;
	private boolean owner;

	public InteractWithFrame() {}

	public InteractWithFrame(BlockPos pos, boolean owner) {
		this.pos = pos;
		this.owner = owner;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeBoolean(owner);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		owner = buf.readBoolean();
	}

	public static class Handler implements IMessageHandler<InteractWithFrame, IMessage> {
		@Override
		@SideOnly(Side.CLIENT)
		public IMessage onMessage(InteractWithFrame message, MessageContext context) {
			Minecraft mc = Minecraft.getMinecraft();

			mc.addScheduledTask(() -> {
				EntityPlayer player = mc.player;
				World level = player.world;
				TileEntity te = level.getTileEntity(message.pos);

				if (te instanceof FrameBlockEntity) {
					FrameBlockEntity be = (FrameBlockEntity) te;

					if (!be.redstoneSignalDisabled() && !be.hasClientInteracted() && be.getCurrentCamera() != null)
						be.setCurrentCameraAndUpdate(be.getCurrentCamera());
					else {
						Screens screen = !message.owner ? Screens.FRAME_READ_ONLY : Screens.FRAME;

						mc.displayGuiScreen((GuiScreen) screen.getClient().apply(player, te));
					}
				}
			});

			return null;
		}
	}
}
