package org.freeforums.geforce.securitycraft.commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

public class CommandSCLog extends CommandBase implements ICommand{

	private List<String> nicknames;
	
	public CommandSCLog(){
		this.nicknames = new ArrayList<String>();
		this.nicknames.add("log");
	}
	
	/**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }
    
	public String getCommandName() {
		return "log";
	}
	
	public List<String> getCommandAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender icommandsender) {
		return "/log <changeToLog>";
	}
	
	public boolean canCommandSenderUseCommand(ICommandSender icommandsender) {
		return true;
	}

	public void processCommand(ICommandSender icommandsender, String[] par1String) {
		if(par1String.length >= 1){
			
			if(par1String[0].matches("clear")){
				File file = new File("changelog.txt");
				if(file.exists()){
					file.delete();
					File file2 = new File("changelog.txt");
					try{
						file2.createNewFile();
					}catch(IOException e){
						e.printStackTrace();
					}
					
					return;
				}
			}
			
			try{
				String string = "";
				for(int i = 0; i < par1String.length; i++){
					if(i >= 1){
						string += " " + par1String[i];
					}else{
						string += "-" + par1String[i];
					}
				}
				
				File file = new File("changelog.txt");
				if(!file.exists()){
					file.createNewFile();
				}
				
	        	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("changelog.txt", true)));
	        	out.println(string);
	        	out.close();
				
	        }catch(IOException e){
	        	e.printStackTrace();
	        }
		}else{
			throw new WrongUsageException("/log <changeToLog>");
		}
	}
	
	public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }

}
