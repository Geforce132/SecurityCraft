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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.ircbot.SCIRCBot;
import org.freeforums.geforce.securitycraft.main.HelpfulMethods;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypadChest;
import org.jibble.pircbot.User;

public class CommandSCHelp extends CommandBase implements ICommand{
	
	private Map<String, String[]> recipes = new HashMap<String, String[]>();
	private Map<String, String> helpInfo = new HashMap<String, String>();

	private List nicknames;
	
	private final String usage = "Usage: /sc connect OR /sc disconnect OR /sc bug <bug to report> OR /sc contact <message> OR /sc <help:recipe> <SecurityCraft block/item name without spaces> OR /sc changePasscode <keypad/chest X> <keypad/chest Y> <keypad/chest Z> <keypad/chest old code> <keypad/chest new code>";
	
	public CommandSCHelp(){
		this.nicknames = new ArrayList();
		this.nicknames.add("sc");
		
		this.recipes.put("keypad", new String[]{"The keypad requires: 9 stone buttons.", "XXX", "XXX", "XXX", "X = stone button"});
		this.recipes.put("laserblock", new String[]{"The laser block requires: 7 stone, 1 block of redstone, 1 glass pane", "XXX", "XYX", "XZX", "X = stone, Y = block of redstone, Z = glass pane"});
		this.recipes.put("mine", new String[]{"The mine requires: 3 iron ingots, 1 gunpowder", " X ", "XYX", "   ", "X = iron ingot, Y = gunpowder"});
		this.recipes.put("reinforcedirondoor", new String[]{"The reinforced iron door requires: 8 iron ingots, 1 iron door", "XXX", "XYX", "XXX", "X = iron ingot, Y = iron door"});
		this.recipes.put("universalblockremover", new String[]{"The universal block remover requires: 2 iron ingots, 1 shears", "XYY", "   ", "   ", "X = shears, Y = iron ingot"});
		this.recipes.put("irontrapdoor", new String[]{"The iron trapdoor requires: 8 iron ingots, 1 trapdoor", "XXX", "XYX", "XXX", "X = iron ingot, Y = trapdoor"});
		this.recipes.put("keycardreader", new String[]{"The keycard reader requires: 8 stone, 1 hopper", "XXX", "XYX", "XXX", "X = stone, Y = hopper"});
		this.recipes.put("bouncingbetty", new String[]{"The bouncing betty requires: 2 iron ingots, 1 gunpowder, 1 weighted pressure plate (heavy)", " X ", "YZY", "   ", "X = weighted pressure plate (heavy), Y = iron ingot, Z = gunpowder"});
		this.recipes.put("codebreaker", new String[]{"The codebreaker requires: 2 diamonds, 2 gold ingots, 2 redstone, 1 nether star, 1 emerald, 1 redstone torch", "UVU", "WXW", "YZY", "U = diamond, V = redstone torch, W = gold ingot, X = nether star, Y = redstone, Z = emerald"});
		this.recipes.put("level1keycard", new String[]{"The level 1 keycard requires: 3 iron ingots, 3 gold ingots", "XXX", "YYY", "   ", "X = iron ingot, Y = gold ingot"});
		this.recipes.put("level2keycard", new String[]{"The level 2 keycard requires: 3 iron ingots, 3 bricks", "XXX", "YYY", "   ", "X = iron ingot, Y = brick"});
		this.recipes.put("level3keycard", new String[]{"The level 3 keycard requires: 3 iron ingots, 3 nether bricks", "XXX", "YYY", "   ", "X = iron ingot, Y = nether brick"});
		this.recipes.put("railmine", new String[]{"The rail mine requires: 6 iron ingots, 1 stick, 1 gunpowder", "X X", "XYX", "XZX", "X = iron ingot, Y = stick, Z = gunpowder"});
		this.recipes.put("reinforcedironbars", new String[]{"The reinforced iron bars requires: 4 iron ingots, 1 iron bars", " X ", "XYX", " X ", "X = iron ingot, Y = iron bars"});
		this.recipes.put("portableradar", new String[]{"The portable radar requires: 7 iron ingots, 1 redstone torch, 1 redstone", "XXX", "XYX", "XZX", "X = iron ingot, Y = redstone torch, Z = redstone"});
		this.recipes.put("mineremoteaccesstool", new String[]{"The mine remote access tool requires: 1 redstone torch, 1 diamond, 1 gold ingot, 1 stick", " R ", " DG", "S  ", "R = redstone torch, D = diamond, G = gold ingot, S = stick"});
		this.recipes.put("retinalscanner", new String[]{"The retinal scanner requires: 8 stone, 1 eye of ender", "XXX", "XYX", "XXX", "X = stone, Y = eye of ender"});
		this.recipes.put("inventoryscanner", new String[]{"The inventory scanner requires: 7 stone, 1 laser block, 1 ender chest", "XXX", "XYX", "XZX", "X = stone, Y = laser block, Z = ender chest"});
		this.recipes.put("cagetrap", new String[]{"The cage trap requires: 3 reinforced iron bars, 2 gold ingots, 1 redstone, 3 iron blocks", "WWW", "XYX", "ZZZ", "W = reinforced iron bars, X = gold ingot, Y = redstone, Z = iron block"});
		this.recipes.put("reinforcedglasspane", new String[]{"The reinforced glass pane requires: 4 glass, 1 glass pane", " X ", "XYX", " X ", "X = glass, Y = glass pane"});
		this.recipes.put("alarm", new String[]{"The alarm requires: 7 glass, 1 note block, 1 redstone", "XXX", "XYX", "XZX", "X = glass, Y = note block, Z = redstone"});
		this.recipes.put("reinforcedstone", new String[]{"The reinforced stone requires: 4 cobblestone, 1 stone", " X ", "XYX", " X ", "X = cobblestone, Y = stone"});
		this.recipes.put("dirtmine", new String[]{"The dirt mine requires: 1 dirt, 1 mine. This is a shapeless recipe."});
		this.recipes.put("stonemine", new String[]{"The stone mine requires: 1 stone, 1 mine. This is a shapeless recipe."});
		this.recipes.put("cobblestonemine", new String[]{"The cobblestone mine requires: 1 cobblestone, 1 mine. This is a shapeless recipe."});
		this.recipes.put("diamondoremine", new String[]{"The diamond ore mine requires: 1 diamond ore (use a Silk Touch-enchanted pickaxe), 1 mine. This is a shapeless recipe."});
		this.recipes.put("sandmine", new String[]{"The sand mine requires: 1 sand, 1 mine. This is a shapeless recipe."});
		this.recipes.put("furnacemine", new String[]{"The furnace mine requires: 1 furnace, 1 mine. This is a shapeless recipe."});
		
		this.helpInfo.put("keypad", "The keypad is used by placing the keypad, right-clicking it, and setting a numerical passcode. Once the keycode is set, right-clicking the keypad will allow you to enter the code. If it's correct, the keypad will emit redstone power for three seconds.");
		this.helpInfo.put("laserblock", "The laser block is used by putting two of them within five blocks of each other. When the blocks are placed correctly, a laser should form between them. Whenever a player walks through the laser, both the laser blocks will emit a 15-block redstone signal.");
		this.helpInfo.put("mine", "The mine explodes when stepped on by any entity other then creepers, cats, and ocelots. Right-clicking the mine while holding shears will defuse the mine and allow you to break it. Alternatively, right-clicking with flint and steel equipped will re-enable it.");
		this.helpInfo.put("reinforcedirondoor", "The reinforced iron door is the same as the vanilla iron door, except it is unbreakable. The owner of the door can use a door remover to break it down.");
		this.helpInfo.put("universalblockremover", "The universal block remover serves no other purpose except to break down alarms, retinal scanners, keypads, keycard readers, and any other 'reinforced' block. Right-click the block to remove it.");
		this.helpInfo.put("irontrapdoor", "The iron trapdoor is the same as a vanilla trapdoor, except it can only be opened using a redstone signal.");
		this.helpInfo.put("keycardreader", "The keycard reader emits a 15-block redstone redstone signal if you insert a keycard with a security level equal to or higher then the level selected in the reader's GUI.");
		this.helpInfo.put("bouncingbetty", "The bouncing betty will launch up into the air and explode when touched.");
		this.helpInfo.put("codebreaker", "The codebreaker will crack any keypad's code by right-clicking on it."); 
		this.helpInfo.put("level1keycard", "The lowest security level keycard. Used in conjunction with the Keycard Reader.");
		this.helpInfo.put("level2keycard", "The medium security level keycard. Used in conjunction with the Keycard Reader.");
		this.helpInfo.put("level3keycard", "The highest security level keycard. Used in conjunction with the Keycard Reader.");
		this.helpInfo.put("trackmine", "The track mine explodes when a minecart passes on top of it.");
		this.helpInfo.put("reinforcedironbars", "The reinforced iron bars act the same as vanilla iron bars, except it is unbreakable.");
		this.helpInfo.put("portableradar", "The portable radar will send the owner a chat message whenever a player is inside of the radar's detection radius (modifiable in the config file). You can name the portable radar by right-clicking on it with a named name-tag.");
		this.helpInfo.put("dirtmine", "The dirt mine explodes when touched.");
		this.helpInfo.put("stonemine", "The stone mine explodes when touched.");
		this.helpInfo.put("cobblestonemine", "The cobblestone mine explodes when touched.");
		this.helpInfo.put("diamondoremine", "The diamond ore mine explodes when touched.");
		this.helpInfo.put("sandmine", "The sand mine explodes when touched.");
		this.helpInfo.put("furnacemine", "The furnace mine explodes when touched.");
		this.helpInfo.put("retinalscanner", "The retinal scanner emits a 15-block redstone signal when the owner of the block stands directly in front of it.");
		this.helpInfo.put("inventoryscanner", "The inventory scanner is used by placing two scanners a block apart, facing each other. When placed correctly, a laser field should spawn between them. If a player walks through the field, any blocks or items banned (entered by typing 'minecraft:<block/item name>' in the scanners GUI) in the player's inventory will be deleted.");
		this.helpInfo.put("bucketoffakelava", "The fake lava acts the same as lava, except it heals you instead of hurting you.");
		this.helpInfo.put("bucketoffakewater", "The fake water acts the same as water, expect it hurts you when touched.");
		this.helpInfo.put("cagetrap", "The cage trap will spawn a 'cage' around any player who walks on top of it. (*needs textures & recipe*)");
		this.helpInfo.put("usernamelogger", "The username logger will log any player's name within 3 blocks when it is powered by redstone.");
		this.helpInfo.put("passwordprotectedchest", "The password-protected chest is equipped with a password locking system. Whenever the password is entered correctly, the chest's inventory will open.");
		this.helpInfo.put("mineremoteaccesstool", "The mine remote access tool will allow you to access mines remotely. Right-click on a mine to 'bind' it to the tool. Right-click in the air (with the tool equipped) to open the tool's GUI, which will allow you to activate, deactivate, or detonate any bound mines.");
		this.helpInfo.put("reinforcedglasspane", "The reinforced glass panes act the same as vanilla glass panes, except it is unbreakable.");
		this.helpInfo.put("reinforcedstone", "Reinforced stone act the same as vanilla stone blocks, except it is unbreakable.");
		this.helpInfo.put("alarm", "The alarm will emit a siren sound effect whenever it is powered by redstone, and in 2-second intervals after that (modifiable in the config file).");
		this.helpInfo.put("reinforcedironfencegate", "The reinforced iron fence gate acts the same as a vanilla fence gate, except it is unbreakable, and can only be opened with redstone power.");
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
			
			if(world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.Keypad && ((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4])){
				((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).setKeypadCode(par1String[5]);
				HelpfulMethods.sendMessage(icommandsender, "Changed keypad's (at X:" + positions[0] + " Y:" + positions[1] + " Z:" + positions[2] + ") code from " + Integer.parseInt(par1String[4]) + " to " + Integer.parseInt(par1String[5]) + ".", EnumChatFormatting.GREEN);
			}
			else if((world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.Keypad  && !((TileEntityKeypad)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4])) || (world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() == mod_SecurityCraft.keypadChest  && !((TileEntityKeypadChest)world.getTileEntity(new BlockPos(positions[0], positions[1], positions[2]))).getKeypadCode().matches(par1String[4]))){
				HelpfulMethods.sendMessage(icommandsender, par1String[3] + " is not the passcode for this block.", EnumChatFormatting.RED);
			}
			else if(world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() != mod_SecurityCraft.Keypad && world.getBlockState(new BlockPos(positions[0], positions[1], positions[2])).getBlock() != mod_SecurityCraft.keypadChest){
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
			}else if(par1String[0].matches("help")){
				
				if(par1String[1] == null || par1String[1].isEmpty()){
					throw new WrongUsageException(usage);
				}
				
				String par1 = getHelpInfo(par1String[1]);
				
				if(!par1.isEmpty()){
					sendMessageToPlayer("[SCHelp] " + par1, icommandsender);
				}
			}else if(par1String[0].matches("recipe")){
				String itemname = par1String[1].toLowerCase();
				
				if(itemname.matches("level1keycard") && !mod_SecurityCraft.configHandler.ableToCraftKeycard1){
					sendMessageToPlayer("[Recipe currently disabled in config file] " + getRecipe(itemname)[0], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[1], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[2], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[3], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[4], icommandsender);
				}else if(itemname.matches("level2keycard") && !mod_SecurityCraft.configHandler.ableToCraftKeycard2){
					sendMessageToPlayer("[Recipe currently disabled in config file] " + getRecipe(itemname)[0], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[1], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[2], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[3], icommandsender);
					sendMessageToPlayer(getRecipe(itemname)[4], icommandsender);
				}else if(itemname.matches("level3keycard") && !mod_SecurityCraft.configHandler.ableToCraftKeycard3){
					sendMessageToPlayer("[Recipe currently disabled in config file] " + getRecipe(itemname)[0], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[1], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[2], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[3], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[4], icommandsender);
				}else{
					sendMessageToPlayer(getRecipe(par1String[1])[0], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[1], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[2], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[3], icommandsender);
					sendMessageToPlayer(getRecipe(par1String[1])[4], icommandsender);
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
