package net.geforcemods.securitycraft.network.client;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.ConfigHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateTeamPrecedence implements IMessage {
	private String[] precedence;

	public UpdateTeamPrecedence() {}

	public UpdateTeamPrecedence(String[] precedence) {
		this.precedence = precedence;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeVarInt(buf, precedence.length, 5);

		for (int i = 0; i < precedence.length; i++) {
			ByteBufUtils.writeUTF8String(buf, precedence[i]);
		}
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		precedence = new String[ByteBufUtils.readVarInt(buf, 5)];

		for (int i = 0; i < precedence.length; i++) {
			precedence[i] = ByteBufUtils.readUTF8String(buf);
		}
	}

	public static class Handler implements IMessageHandler<UpdateTeamPrecedence, IMessage> {
		@Override
		public IMessage onMessage(UpdateTeamPrecedence message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> ConfigHandler.updateTeamPrecedenceFromConfigValues(message.precedence));
			return null;
		}
	}
}
