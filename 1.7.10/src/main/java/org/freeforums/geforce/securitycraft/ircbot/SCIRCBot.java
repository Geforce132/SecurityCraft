package org.freeforums.geforce.securitycraft.ircbot;

import java.io.IOException;
import java.util.Scanner;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class SCIRCBot extends PircBot{
		
	public SCIRCBot(String par1String){
		this.setName(par1String);
	}
	
	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{
		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");
	}
	
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
    	for(User user: this.getUsers(channel)){
    		if(channel.matches("#GeforceMods") && (user.hasVoice()|| user.isOp()) && (message.startsWith((this.getNick() + ":")) || message.startsWith((this.getNick() + ",")))){
    			sendMessageToPlayer(EnumChatFormatting.YELLOW + "<" + sender + " (IRC) --> " + getPlayerFromName((this.getNick().replace("SCUser_", ""))).getCommandSenderName() + "> " + EnumChatFormatting.RESET + (message.startsWith(this.getNick() + ":") ? message.replace(this.getNick() + ":", "") : message.replace(this.getNick() + ",", "")), getPlayerFromName((this.getNick().replace("SCUser_", ""))));
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
    		sendMessageToPlayer(EnumChatFormatting.YELLOW + "[Reply]: " + EnumChatFormatting.RESET + trimmedMessage, getPlayerFromName((this.getNick().replace("SCUser_", ""))));
    		scanner.close();
    	}
    }
	
	protected void onKick(String channel, String user, String login, String hostname, String userKicked, String reason){
    	if(mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")) != null){
			mod_SecurityCraft.instance.getIrcBot(this.getNick().replaceFirst("SCUser_", "")).disconnect();
		}
					
		try{
			sendMessageToPlayer(EnumChatFormatting.RED + "You have been disconnected from EsperNet for reason: " + reason, getPlayerFromName((this.getNick().replace("SCUser_", ""))));
		}catch(PlayerNotFoundException e){
			e.printStackTrace();
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
    
    private EntityPlayer getPlayerFromName(String name){
    	EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().func_152612_a(name);//MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(name);

        if (entityplayermp == null)
        {
            throw new PlayerNotFoundException();
        }
        else
        {
            return entityplayermp;
        }
    }
   
}
