package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CheckBriefcasePasscode implements IMessage {
	private String passcode;

	public CheckBriefcasePasscode() {}

	public CheckBriefcasePasscode(String passcode) {
		this.passcode = PasscodeUtils.hashPasscodeWithoutSalt(passcode);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, passcode);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		passcode = ByteBufUtils.readUTF8String(buf);
	}

	public static class Handler implements IMessageHandler<CheckBriefcasePasscode, IMessage> {
		@Override
		public IMessage onMessage(CheckBriefcasePasscode message, MessageContext ctx) {
			LevelUtils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				ItemStack briefcase = PlayerUtils.getSelectedItemStack(player, SCContent.briefcase);

				if (!briefcase.isEmpty()) {
					if (!briefcase.hasTagCompound())
						briefcase.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = briefcase.getTagCompound();
					String tagCode = tag.getString("passcode");

					if (tagCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
						BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode), p -> BriefcaseItem.checkPasscode(player, briefcase, message.passcode, PasscodeUtils.bytesToString(p), tag));
					else
						BriefcaseItem.checkPasscode(player, briefcase, message.passcode, tagCode, tag);
				}
			});

			return null;
		}
	}
}
