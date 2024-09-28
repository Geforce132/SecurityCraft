package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.screen.ScreenHandler.Screens;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SetBriefcasePasscodeAndOwner implements IMessage {
	private String passcode;

	public SetBriefcasePasscodeAndOwner() {}

	public SetBriefcasePasscodeAndOwner(String passcode) {
		this.passcode = passcode.isEmpty() ? passcode : PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, passcode);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		passcode = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<SetBriefcasePasscodeAndOwner, IMessage> {
		@Override
		public IMessage onMessage(SetBriefcasePasscodeAndOwner message, MessageContext context) {
			Utils.addScheduledTask(context.getServerHandler().player.world, () -> {
				EntityPlayer player = context.getServerHandler().player;
				ItemStack stack = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				if (!stack.isEmpty()) {
					if (!stack.hasTagCompound())
						stack.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = stack.getTagCompound();

					if (!tag.hasKey("owner")) {
						tag.setString("owner", player.getName());
						tag.setString("ownerUUID", player.getUniqueID().toString());
					}

					if (!message.passcode.isEmpty() && !tag.hasKey("passcode")) {
						BriefcaseItem.hashAndSetPasscode(tag, message.passcode, p -> {
							player.openGui(SecurityCraft.instance, Screens.BRIEFCASE_INSERT_CODE.ordinal(), player.world, (int) player.posX, (int) player.posY, (int) player.posZ);
						});
					}
				}
			});

			return null;
		}
	}
}
