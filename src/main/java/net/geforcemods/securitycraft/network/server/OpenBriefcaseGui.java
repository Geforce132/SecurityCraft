package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.WorldUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class OpenBriefcaseGui implements IMessage
{
	public OpenBriefcaseGui() {}

	@Override
	public void toBytes(ByteBuf buf) {}

	@Override
	public void fromBytes(ByteBuf buf) {}

	public static class Handler implements IMessageHandler<OpenBriefcaseGui, IMessage>
	{
		@Override
		public IMessage onMessage(OpenBriefcaseGui message, MessageContext context)
		{
			WorldUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayerMP player = context.getServerHandler().player;

				if(PlayerUtils.isHoldingItem(player, SCContent.briefcase, null))
					player.openGui(SecurityCraft.instance, GuiHandler.BRIEFCASE_GUI_ID, player.world, (int)player.posX, (int)player.posY, (int)player.posZ);
			});

			return null;
		}
	}
}