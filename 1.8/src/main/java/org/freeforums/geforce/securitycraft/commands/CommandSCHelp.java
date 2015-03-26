package org.freeforums.geforce.securitycraft.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.ircbot.SCIRCBot;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;

public class CommandSCHelp extends CommandBase implements ICommand{
	
	private Map<String, String[]> recipes = new HashMap<String, String[]>();
	private Map<String, String> helpInfo = new HashMap<String, String>();

	private List nicknames;
	
	private final String usage = "Usage: /sc connect OR /sc disconnect OR /sc bug <bug to report> OR /sc contact <message> OR /sc <help|recipe> OR /sc changePasscode <keypad/chest X> <keypad/chest Y> <keypad/chest Z> <keypad/chest old code> <keypad/chest new code>";
	
	public CommandSCHelp(){
		this.nicknames = new ArrayList();
		this.nicknames.add("sc");
		
		this.recipes.put("reinforcedglasspane", new String[]{"The reinforced glass pane requires: 4 glass, 1 glass pane", " X ", "XYX", " X ", "X = glass, Y = glass pane"});
		this.recipes.put("reinforcedstone", new String[]{"The reinforced stone requires: 4 cobblestone, 1 stone", " X ", "XYX", " X ", "X = cobblestone, Y = stone"});
		
		this.helpInfo.put("reinforcedglasspane", "The reinforced glass panes act the same as vanilla glass panes, except it is unbreakable.");
		this.helpInfo.put("reinforcedstone", "Reinforced stone act the same as vanilla stone blocks, except it is unbreakable.");
	}
	
	/**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
	public String getName() {
		return "sc";
	}
	
	public List getAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return usage;
	}
	
	public boolean canCommandSenderUse(ICommandSender icommandsender) {
		return true;
	}

	public void execute(ICommandSender icommandsender, String[] par1String) throws CommandException{
		if(par1String.length == 0){
			throw new WrongUsageException(usage);
		}
		
		if((par1String[0].matches("connect") || par1String[0].matches("disconnect") || par1String[0].matches("contact") || par1String[0].matches("bug")) && !mod_SecurityCraft.instance.configHandler.isIrcBotEnabled){
			sendMessageToPlayer("The SecurityCraft IRC bot is disabled from the config file. Please enable to it to use this feature.", icommandsender);
			return;
		}
		
		if(par1String[0].matches("changePasscode") && par1String.length == 6){
			int[] positions = {Integer.parseInt(par1String[1]), Integer.parseInt(par1String[2]), Integer.parseInt(par1String[3])};
			World world = icommandsender.getEntityWorld();
			
			if(world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.keypad && ((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4])){
				((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).setKeypadCode(par1String[5]);
				HelpfulMethods.sendMessage(icommandsender, "Changed keypad's (at X:" + positions[0] + " Y:" + positions[1] + " Z:" + positions[2] + ") code from " + Integer.parseInt(par1String[4]) + " to " + Integer.parseInt(par1String[5]) + ".", EnumChatFormatting.GREEN);
			}
			else if((world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.keypad  && !((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4])) || (world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.keypadChest  && !((TileEntityKeypadChest)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4]))){
				HelpfulMethods.sendMessage(icommandsender, par1String[3] + " is not the passcode for this block.", EnumChatFormatting.RED);
			}
			else if(world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() != mod_SecurityCraft.keypad && world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() != mod_SecurityCraft.keypadChest){
				HelpfulMethods.sendMessage(icommandsender, "There is no accessable block at the specifed coordinates!", EnumChatFormatting.RED);
			}
			
			return;
		}
		
		if(par1String.length == 1){
			if(par1String[0].matches("connect")){
				
				mod_SecurityCraft.instance.setIrcBot(new SCIRCBot("SCUser_" + icommandsender.getName()));	
				
				try{
					mod_SecurityCraft.instance.getIrcBot().connectToChannel();
				}catch(Exception e){
					e.printStackTrace();
					sendMessageToPlayer("Error occurred when connecting to IRC. Do you have internet access, and access to the IRC server 'irc.esper.net'?", icommandsender);
					return;
				}
				
				sendMessageToPlayer("Bot connected successfully. You may now report bugs using '/sc bug <bug to report>' or contact me using '/sc contact <message>", icommandsender);
			}else if(par1String[0].matches("disconnect")){
				if(mod_SecurityCraft.instance.getIrcBot() != null){
					mod_SecurityCraft.instance.getIrcBot().disconnect();
				}
					
				mod_SecurityCraft.instance.setIrcBot(null); 
				sendMessageToPlayer("Bot disconnected from EsperNet successfully.", icommandsender);
			}else if(par1String[0].matches("help")){
				if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof ItemBlock && ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock() instanceof IHelpInfo){
					sendMessageToPlayer("[" + EnumChatFormatting.GOLD + "Help" + EnumChatFormatting.RESET + "] " + ((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getHelpInfo(), icommandsender);
				}else if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof Item && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof IHelpInfo){
					sendMessageToPlayer("[" + EnumChatFormatting.GOLD + "Help" + EnumChatFormatting.RESET + "] " + ((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getHelpInfo(), icommandsender);
				}else if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null){
					sendMessageToPlayer(EnumChatFormatting.RED + "There is no help info for this item.", icommandsender);
				}else{
					sendMessageToPlayer(EnumChatFormatting.RED + "Please hold the item you wish to view the help text for.", icommandsender);
				}
			}else if(par1String[0].matches("recipe")){
				if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof ItemBlock && ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock() instanceof IHelpInfo){
					if(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe().length == 5){
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[0], icommandsender);
						sendMessageToPlayer(EnumChatFormatting.GRAY + "Crafting recipe:", icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[1], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[2], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[3], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[4], icommandsender);
					}else{
						sendMessageToPlayer(((IHelpInfo) ((ItemBlock) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getBlock()).getRecipe()[0], icommandsender);
					}
				}else if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof Item && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem() instanceof IHelpInfo){
					if(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe().length == 5){
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[0], icommandsender);
						sendMessageToPlayer(EnumChatFormatting.GRAY + "Crafting recipe:", icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[1], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[2], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[3], icommandsender);
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[4], icommandsender);
					}else{
						sendMessageToPlayer(((IHelpInfo) ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem().getItem()).getRecipe()[0], icommandsender);
					}
				}else if(icommandsender.getCommandSenderEntity() instanceof EntityPlayer && ((EntityPlayer) icommandsender.getCommandSenderEntity()).getCurrentEquippedItem() != null){
					sendMessageToPlayer(EnumChatFormatting.RED + "There is no recipe for this item.", icommandsender);
				}else{
					sendMessageToPlayer(EnumChatFormatting.RED + "Please hold the item you wish to view the recipe for.", icommandsender);
				}
			}
		}else if(par1String.length >= 2){
			if(par1String[0].matches("bug")){
				if(mod_SecurityCraft.instance.getIrcBot() != null){
					mod_SecurityCraft.instance.getIrcBot().sendMessage("#GeforceMods", "[SecurityCraft " + mod_SecurityCraft.getVersion() + " bug] Geforce: "  + getMessageFromArray(par1String, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + icommandsender.getName() + " --> IRC> " + getMessageFromArray(par1String, 1) + ".", icommandsender);
				}else{
					sendMessageToPlayer("Bot is not connected to EsperNet. Use '/sc connect' to connect to IRC.", icommandsender);
				}
			}else if(par1String[0].matches("contact")){
				if(mod_SecurityCraft.instance.getIrcBot() != null){
					mod_SecurityCraft.instance.getIrcBot().sendMessage("#GeforceMods", "[SecurityCraft " + mod_SecurityCraft.getVersion() + "] Geforce: " + getMessageFromArray(par1String, 1));
					sendMessageToPlayer(EnumChatFormatting.GRAY + "<" + icommandsender.getName() + " --> IRC> " + getMessageFromArray(par1String, 1) + ".", icommandsender);
				}else{
					sendMessageToPlayer("Bot is not connected to EsperNet. Use '/sc connect' to connect to IRC.", icommandsender);
				}
			}
		}else{
			throw new WrongUsageException(usage);
		}
	}

	private static String getMessageFromArray(String[] par1String, int index) {
		String startingString = "";
		for(int i = index; i < par1String.length; i++){
			startingString += (i == index ? "" : " ") + par1String[i];
		}
		
		return startingString;
	}

	private String getHelpInfo(String string) {
		if(this.helpInfo.containsKey(string)){
			return this.helpInfo.get(string);
		}else{
			return ("There is no info for " + string);
		}
	}

	private String[] getRecipe(String string) {
		if(this.recipes.containsKey(string)){
			return this.recipes.get(string);
		}else{
			return new String[]{"There is no recipe for " + string};
		}
	}
	
	private void sendMessageToPlayer(String par1, ICommandSender par2){
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(par1, new Object[0]);
		try{
		((EntityPlayerMP) getPlayer(par2, par2.getName())).addChatComponentMessage(chatcomponenttranslation);
		}catch(PlayerNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }

}
