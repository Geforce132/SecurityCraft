package net.geforcemods.securitycraft.network.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.VersionChecker;
import net.minecraftforge.fml.VersionChecker.Status;
import net.minecraftforge.fml.network.NetworkEvent;

public class SendTip {
	public static final Map<String, String> TIPS_WITH_LINK = new HashMap<>();

	static {
		TIPS_WITH_LINK.put("patreon", "https://www.patreon.com/Geforce");
		TIPS_WITH_LINK.put("discord", "https://discord.gg/U8DvBAW");
		TIPS_WITH_LINK.put("outdated", "https://www.curseforge.com/minecraft/mc-mods/security-craft/files/all");
	}

	public SendTip() {}

	public SendTip(PacketBuffer packet) {}

	public void encode(PacketBuffer packet) {}

	public void handle(Supplier<NetworkEvent.Context> ctx) {
		if (!ConfigHandler.CLIENT.sayThanksMessage.get())
			return;

		//@formatter:off
		String tipKey = getRandomTip();
		IFormattableTextComponent message = new StringTextComponent("[")
				.append(new StringTextComponent("SecurityCraft").withStyle(TextFormatting.GOLD))
				.append(new StringTextComponent("] "))
				.append(Utils.localize("messages.securitycraft:thanks",
						SecurityCraft.getVersion(),
						Utils.localize("messages.securitycraft:tip"),
						Utils.localize(tipKey)));
		//@formatter:on

		if (TIPS_WITH_LINK.containsKey(tipKey.split("\\.")[2]))
			message = message.append(ForgeHooks.newChatWithLinks(TIPS_WITH_LINK.get(tipKey.split("\\.")[2])));

		ClientHandler.getClientPlayer().sendMessage(message, Util.NIL_UUID);
	}

	private String getRandomTip() {
		//@formatter:off
		String[] tips = {
				"messages.securitycraft:tip.scHelp",
				"messages.securitycraft:tip.patreon",
				"messages.securitycraft:tip.discord",
				"messages.securitycraft:tip.scserver",
				"messages.securitycraft:tip.outdated"
		};
		//@formatter:on

		return tips[SecurityCraft.RANDOM.nextInt(isOutdated() ? tips.length : tips.length - 1)];
	}

	private boolean isOutdated() {
		return VersionChecker.getResult(ModList.get().getModContainerById(SecurityCraft.MODID).get().getModInfo()).status == Status.OUTDATED;
	}
}
