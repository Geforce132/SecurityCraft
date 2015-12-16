package net.geforcemods.securitycraft.ircbot;

import java.io.IOException;
import java.util.HashMap;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;

public class SCIRCBot extends PircBot{

	private static final char prefix = '!';
	
	private HashMap<String, Integer> messageFrequency = new HashMap<String, Integer>(); 
	
	public SCIRCBot(String par1String){
		this.setName(par1String);
	}

	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{
		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");
		this.setVerbose(true);

		if(Minecraft.getMinecraft().getSession().getToken() == null)
			sendMessage("#GeforceMods", "I am using a cracked client! (No Session token found.)");
	}
	
	public void sendMessage(String message) {
		// If the message is different than the previous message sent,
		// reset the message "counter".
		if(messageFrequency.size() > 0 && !messageFrequency.containsKey(message)) {
			messageFrequency.clear();
		}
		
		if(messageFrequency.containsKey(message)) {
			messageFrequency.put(message, messageFrequency.get(message) + 1);
		}
		else {
			messageFrequency.put(message, 1);
		}
		
		if(messageFrequency.get(message) > 2) {
			PlayerUtils.sendMessageToPlayer(PlayerUtils.getPlayerFromName((this.getNick()).replaceFirst("SCUser_", "")), "IRC", StatCollector.translateToLocal("messages.irc.spam"), EnumChatFormatting.RED);
			return;
		}
		
		sendMessage("#GeforceMods", message);
	}

	@Override
	protected void onMessage(String channel, String sender, String login, String hostname, String message) {
		for(User user: this.getUsers(channel)){
			if(channel.matches("#GeforceMods") && (user.hasVoice() || user.isOp()) && (message.startsWith((this.getNick() + ":")) || message.startsWith((this.getNick() + ",")))){
				//commands
				if(message.split(" ")[1].equals(prefix + "info"))
				{
					sendMessage("#GeforceMods", "Minecraft version: " + Loader.MC_VERSION);
					sendMessage("#GeforceMods", "Forge version: " + ForgeVersion.getVersion());
//					sendMessage("#GeforceMods", "LookingGlass installed: " + (Loader.isModLoaded("LookingGlass") ? "Yes" : "No"));
				}
				else
					sendMessageToPlayer(EnumChatFormatting.YELLOW + "<" + sender + " (IRC) --> " + this.getNick().replace("SCUser_", "") + "> " + EnumChatFormatting.RESET + (message.startsWith(this.getNick() + ":") ? message.replace(this.getNick() + ":", "") : message.replace(this.getNick() + ",", "")), PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))));
				
				break;
			}
		}
	}

	@Override
	protected void onServerResponse(int code, String response)
	{
		if(code == 474 && response.contains("Cannot join channel (+b) - you are banned"))
			PlayerUtils.sendMessageToPlayer(PlayerUtils.getPlayerFromName((this.getNick().replace("SCUser_", ""))), "IRC", StatCollector.translateToLocal("messages.irc.banned"), EnumChatFormatting.RED);
	}

	@Override
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
