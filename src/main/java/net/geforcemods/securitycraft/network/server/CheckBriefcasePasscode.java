package net.geforcemods.securitycraft.network.server;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.items.BriefcaseItem;
import net.geforcemods.securitycraft.util.PasscodeUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
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
			Utils.addScheduledTask(ctx.getServerHandler().player.world, () -> {
				EntityPlayerMP player = ctx.getServerHandler().player;
				ItemStack briefcase = PlayerUtils.getItemStackFromAnyHand(player, SCContent.briefcase);

				if (PasscodeUtils.isOnCooldown(player))
					PlayerUtils.sendMessageToPlayer(player, new TextComponentString("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.onCooldown"), TextFormatting.RED);
				else if (!briefcase.isEmpty()) {
					if (!briefcase.hasTagCompound())
						briefcase.setTagCompound(new NBTTagCompound());

					NBTTagCompound tag = briefcase.getTagCompound();
					String tagCode = tag.getString("passcode");

					if (tagCode.length() == 4) //If an old plaintext passcode is encountered, generate and check with the hashed variant
						BriefcaseItem.hashAndSetPasscode(tag, PasscodeUtils.hashPasscodeWithoutSalt(tagCode), p -> BriefcaseItem.checkPasscode(player, message.passcode, PasscodeUtils.bytesToString(p), tag));
					else
						BriefcaseItem.checkPasscode(player, message.passcode, tagCode, tag);
				}
			});

			return null;
		}
	}
}
