package net.geforcemods.securitycraft.ircbot;

import java.io.IOException;
import java.util.Scanner;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import net.geforcemods.securitycraft.main.Utils.PlayerUtils;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class SCIRCBot extends PircBot{

	public SCIRCBot(String par1String){
		this.setName(par1String);
	}

	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{
		String token = Minecraft.getMinecraft().getSession().getToken();

		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");

		if(token == null)
		{
			sendMessage("#GeforceMods", "I am using a cracked client! (No Session token found.)"); //TODO: Add this to Geffy
		}
	}

	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		for(User user: this.getUsers(channel)){
			if(channel.matches("#GeforceMods") && (user.hasVoice()|| user.isOp()) && (message.startsWith((this.getNick() + ":")) || message.startsWith((this.getNick() + ",")))){
				sendMessageToPlayer(EnumChatFormatting.YELLOW + "<" + sender + " (IRC) --> " + PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))).getName() + "> " + EnumChatFormatting.RESET + (message.startsWith(this.getNick() + ":") ? message.replace(this.getNick() + ":", "") : message.replace(this.getNick() + ",", "")), PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))));
				break;
			}
		}
	}

	/**
	 * Not working yet!
	 */
	@Deprecated
	protected void onPrivateMessage(String sender, String login, String hostname, String message) {
		if(sender.matches("Cadbury") && message.toLowerCase().contains("more messages waiting")){
			this.sendMessage("Cadbury", "$showtell");
		}else if(sender.matches("Cadbury") && message.contains("--")){
			mod_SecurityCraft.log("Cadbury sent message to " + this.getNick() + ": " + message);
			Scanner scanner = new Scanner(message);

			scanner.useDelimiter("--");
			scanner.next();

			String trimmedMessage = scanner.next();
			mod_SecurityCraft.log(trimmedMessage);
			PlayerUtils.sendMessageToPlayer(PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))), "IRC", "[" + StatCollector.translateToLocal("messages.irc.reply") + "]: " + EnumChatFormatting.RESET + trimmedMessage, EnumChatFormatting.YELLOW);
			scanner.close();
		}
	}

	protected void onKick(String channel, String user, String login, String hostname, String userKicked, String reason){
		if(Minecraft.getMinecraft().getSession().getUsername().equals(userKicked.replaceFirst("SCUser_", "")))
		{
			if(mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")) != null){
				mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")).disconnect();
			}

			PlayerUtils.sendMessageToPlayer(PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))), "IRC", StatCollector.translateToLocal("messages.irc.disconnected").replace("#", reason), EnumChatFormatting.RED);
		}
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname)
	{
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		if(sender.equals(this.getNick()))
			sendMessage("#GeforceMods", "SecurityCraft version: " + mod_SecurityCraft.getVersion());
	}

	private void sendMessageToPlayer(String par1String, EntityPlayer par2EntityPlayer){
		ChatComponentText component = new ChatComponentText(par1String);
		par2EntityPlayer.addChatComponentMessage(component);
	}

}
