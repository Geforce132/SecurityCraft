package org.freeforums.geforce.securitycraft.ircbot;

import java.io.IOException;
import java.util.Scanner;

import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public class SCIRCBot extends PircBot{
	
	private boolean canVoiceUsersChat = false;
	
	public SCIRCBot(String par1String){
		this.setName(par1String);
	}
	
	public void connectToChannel() throws IOException, IrcException, NickAlreadyInUseException{
		this.connect("irc.esper.net");
		this.joinChannel("#GeforceMods");
	}
	
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
    	for(User user: this.getUsers(channel)){
    		if(channel.matches("#GeforceMods") && ((user.hasVoice() && this.canVoiceUsersChat) || user.isOp()) && message.startsWith((this.getNick() + ":"))){
    			sendMessageToPlayer(EnumChatFormatting.YELLOW + sender + " replyed with: " + EnumChatFormatting.RESET + (message.startsWith(this.getNick() + ":") ? message.replaceFirst(this.getNick() + ":", "") : message.replaceFirst(this.getNick() + ",", "")), getPlayerFromName((this.getNick().replace("SCUser_", ""))));
    		}
    	}
    }
	
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
    	}
    }
    
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
    	if(!this.canVoiceUsersChat && channel.matches("#GeforceMods") && topic.contains("+vc-allowed")){
    		this.canVoiceUsersChat = true;
    	}else if(this.canVoiceUsersChat && channel.matches("#GeforceMods") && !topic.contains("+vc-allowed")){
    		this.canVoiceUsersChat = false;
    	}
    }

    
    private void sendMessageToPlayer(String par1String, EntityPlayer par2EntityPlayer){
    	ChatComponentTranslation component = new ChatComponentTranslation(par1String, new Object[0]);
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
    
    
    
//    if(showTell.toLowerCase().contains("more messages waiting")){
//		Scanner scanner = new Scanner(message);
//		
//		scanner.useDelimiter("--");
//		scanner.next();
//		System.out.println(scanner.next());
//	}

	
}
