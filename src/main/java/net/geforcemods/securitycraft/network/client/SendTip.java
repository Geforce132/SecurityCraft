package net.geforcemods.securitycraft.network.client;

import java.util.HashMap;
import java.util.Random;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SendTip
{
	public static HashMap<String, String> tipsWithLink = new HashMap<>();

	static
	{
		tipsWithLink.put("patreon", "https://www.patreon.com/Geforce");
		tipsWithLink.put("discord", "https://discord.gg/U8DvBAW");
		tipsWithLink.put("outdated", "https://www.curseforge.com/minecraft/mc-mods/security-craft/files/all");
	}

	public SendTip() {}

	public static void encode(SendTip message, FriendlyByteBuf packet) {}

	public static SendTip decode(FriendlyByteBuf packet)
	{
		return new SendTip();
	}

	public static void onMessage(SendTip packet, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			if(!ConfigHandler.CLIENT.sayThanksMessage.get())
				return;

			String tipKey = getRandomTip();
			MutableComponent message = new TextComponent("[")
					.append(new TextComponent("SecurityCraft").withStyle(ChatFormatting.GOLD))
					.append(new TextComponent("] "))
					.append(Utils.localize("messages.securitycraft:thanks",
							SecurityCraft.getVersion(),
							Utils.localize("messages.securitycraft:tip"),
							Utils.localize(tipKey)));

			if(tipsWithLink.containsKey(tipKey.split("\\.")[2]))
				message = message.append(ForgeHooks.newChatWithLinks(tipsWithLink.get(tipKey.split("\\.")[2])));

			SecurityCraft.proxy.getClientPlayer().sendMessage(message, Util.NIL_UUID);
		});

		ctx.get().setPacketHandled(true);
	}

	private static String getRandomTip()
	{
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver",
				"messages.securitycraft:tip.outdated"
		};

		return tips[new Random().nextInt(isOutdated() ? tips.length : tips.length - 1)];
	}

	private static boolean isOutdated()
	{
		return VersionChecker.getResult(ModList.get().getModContainerById(SecurityCraft.MODID).get().getModInfo()).status == Status.OUTDATED;
	}
}
