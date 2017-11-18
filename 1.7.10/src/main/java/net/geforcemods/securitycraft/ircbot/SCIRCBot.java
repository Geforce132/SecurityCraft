package net.geforcemods.securitycraft.ircbot;

import java.io.IOException;
import java.util.HashMap;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import cpw.mods.fml.common.Loader;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeVersion;

public class SCIRCBot extends PircBot{

	private static final char prefix = '!';
	private boolean message = false;
	private HashMap<String, Integer> messageFrequency = new HashMap<String, Integer>();

	public SCIRCBot(String par1String){
		setName(par1String);
	}

	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{
		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");
		setVerbose(false);

		if(Minecraft.getMinecraft().getSession().getToken() == null)
			sendMessage("#GeforceMods", "I am using a cracked client! (No Session token found.)");
	}

	public void sendMessage(String message) {
		// If the message is different than the previous message sent,
		// reset the message "counter".
		if(messageFrequency.size() > 0 && !messageFrequency.containsKey(message))
			messageFrequency.clear();

		if(messageFrequency.containsKey(message))
			messageFrequency.put(message, messageFrequency.get(message) + 1);
		else
			messageFrequency.put(message, 1);

		if(messageFrequency.get(message) > 2) {
			PlayerUtils.sendMessageToPlayer(getPlayer(), "IRC", StatCollector.translateToLocal("messages.irc.spam"), EnumChatFormatting.RED);
			return;
		}

		sendMessage("#GeforceMods", message);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		for(User user: getUsers(channel))
			if(channel.matches("#GeforceMods") && (user.hasVoice() || user.isOp()) && (message.startsWith((getNick() + ":")) || message.startsWith((getNick() + ",")))){
				//commands
				if(message.split(" ")[1].equals(prefix + "info"))
				{
					sendMessage("#GeforceMods", "Minecraft version: " + Loader.MC_VERSION);
					sendMessage("#GeforceMods", "Forge version: " + ForgeVersion.getVersion());
					sendMessage("#GeforceMods", "Uses LookingGlass: " + (mod_SecurityCraft.configHandler.useLookingGlass ? "Yes (" + Loader.instance().getIndexedModList().get("LookingGlass").getVersion() + ")" : "No"));
				}
				else
					sendMessageToPlayer(EnumChatFormatting.YELLOW + "<" + sender + " (IRC) --> " + getPlayer().getCommandSenderName() + "> " + EnumChatFormatting.RESET + (message.startsWith(getNick() + ":") ? message.replace(getNick() + ":", "") : message.replace(getNick() + ",", "")), getPlayer());

				break;
			}
	}

	@Override
	protected void onServerResponse(int code, String response)
	{
		if(code == 474 && response.contains("Cannot join channel (+b) - you are banned"))
			PlayerUtils.sendMessageToPlayer(getPlayer(), "IRC", StatCollector.translateToLocal("messages.irc.banned"), EnumChatFormatting.RED);
	}

	@Override
	protected void onKick(String channel, String user, String login, String hostname, String userKicked, String reason){

		if(Minecraft.getMinecraft().getSession().getUsername().equals(userKicked.replaceFirst("SCUser_", "")))
		{
			if(mod_SecurityCraft.instance.getIrcBot(getNick().replaceFirst("SCUser_", "")) != null)
				mod_SecurityCraft.instance.getIrcBot(getNick().replaceFirst("SCUser_", "")).disconnect();

			try{
				PlayerUtils.sendMessageToPlayer(getPlayer(), "IRC", StatCollector.translateToLocal("messages.irc.disconnected").replace("#", reason), EnumChatFormatting.RED);
			}catch(PlayerNotFoundException e){
				e.printStackTrace();
			}
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

		if(sender.equals(getNick()))
			sendMessage("#GeforceMods", "SecurityCraft version: " + mod_SecurityCraft.getVersion());
	}

	public void sendMessageToPlayer(String par1String, EntityPlayer par2EntityPlayer){
		par2EntityPlayer.addChatComponentMessage(ForgeHooks.newChatWithLinks(par1String));
	}

	private EntityPlayer getPlayer() {
		return PlayerUtils.getPlayerFromName((getNick()).replaceFirst("SCUser_", ""));
	}

	public void setMessageMode(boolean enable, ICommandSender sender)
	{
		message = enable;

		if(enable)
			PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.contacted"), EnumChatFormatting.GREEN);
		else
			PlayerUtils.sendMessageToPlayer(sender, "IRC", StatCollector.translateToLocal("messages.irc.resumed"), EnumChatFormatting.GREEN);
	}

	/**
	 * @return true if sending messages to IRC, false if sending messages to Minecraft chat
	 */
	public boolean getMessageMode()
	{
		return message;
	}
}
