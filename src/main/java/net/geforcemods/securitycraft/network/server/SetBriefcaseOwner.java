package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetBriefcaseOwner implements IMessage {
	private String passcode;

	public SetBriefcaseOwner() {}

	public SetBriefcaseOwner(String passcode) {
		this.passcode = passcode;
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, passcode);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		passcode = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<SetBriefcaseOwner, IMessage> {
		@Override
		public IMessage onMessage(SetBriefcaseOwner message, MessageContext context) {
			LevelUtils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack stack = PlayerUtils.getSelectedItemStack(player, SCContent.briefcase);

				if (!stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = stack.getTagCompound();

					if (!tag.hasKey("owner")) {
						tag.setString("owner", player.getName());
						tag.setString("ownerUUID", player.getUniqueID().toString());
					}

					if (!tag.hasKey("passcode") && message.passcode.matches("[0-9]{4}"))
						tag.setString("passcode", message.passcode);
				}
			});
			return null;
		}
	}
}
