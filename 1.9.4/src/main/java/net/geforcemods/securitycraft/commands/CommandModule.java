package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

public class CommandModule extends CommandBase implements ICommand {

	private List<String> nicknames;

	public CommandModule() {
		this.nicknames = new ArrayList<String>();
		this.nicknames.add("module");
	}
	
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

	public String getName() {
		return "module";
	}
	
	public List<String> getAliases() {
		return this.nicknames;
	}

	public String getCommandUsage(ICommandSender p_71518_1_) {
		return I18n.translateToLocal("messages.command.module.usage");
	}
	
	public boolean canCommandSenderUse(ICommandSender p_71519_1_) {
		return true;
	}

	public void execute(ICommandSender par1ICommandSender, String[] par2String) throws CommandException{
		if(par2String.length == 1){
			if(par2String[0].matches("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getName());

				if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){		
					mod_SecurityCraft.instance.setSavedModule(player.inventory.getCurrentItem().getTagCompound());
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.saved"), TextFormatting.GREEN);
				}else{
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.notHoldingForData"), TextFormatting.RED);
				}
				
				return;
			}else if(par2String[0].matches("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getName());

				if(mod_SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.nothingSaved"), TextFormatting.RED);
					return;
				}
				
				if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){		
					player.inventory.getCurrentItem().setTagCompound(mod_SecurityCraft.instance.getSavedModule());
					mod_SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.saved"), TextFormatting.GREEN);
				}
				
				return;
			}
		}else if(par2String.length == 2){
			if(par2String[0].matches("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getName());
				
				if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){			
					if(player.inventory.getCurrentItem().getTagCompound() == null){
						player.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.inventory.getCurrentItem().getTagCompound().hasKey("Player" + i) && player.inventory.getCurrentItem().getTagCompound().getString("Player" + i).matches(par2String[1])){
							PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.alreadyContained").replace("#", par2String[1]), TextFormatting.RED);
							return;
						}
					}
                    
					player.inventory.getCurrentItem().getTagCompound().setString("Player" + getNextSlot(player.inventory.getCurrentItem().getTagCompound()), par2String[1]);
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.added").replace("#", par2String[1]), TextFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.notHoldingForModify"), TextFormatting.RED);
					return;
				}
			}else if(par2String[0].matches("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getName());
				
				if(player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){			
					if(player.inventory.getCurrentItem().getTagCompound() == null){
						player.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());				
					}
					
					for(int i = 1; i <= 10; i++){
						if(player.inventory.getCurrentItem().getTagCompound().hasKey("Player" + i) && player.inventory.getCurrentItem().getTagCompound().getString("Player" + i).matches(par2String[1])){
							player.inventory.getCurrentItem().getTagCompound().removeTag("Player" + i);
						}
					}
					
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.removed").replace("#", par2String[1]), TextFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, I18n.translateToLocal("messages.module.manager"), I18n.translateToLocal("messages.module.notHoldingForModify"), TextFormatting.RED);
					return;
				}
			}
		}
		
		throw new WrongUsageException(I18n.translateToLocal("messages.command.module.usage"));
	}

	private int getNextSlot(NBTTagCompound stackTagCompound) {
		for(int i = 1; i <= 10; i++){
			if(stackTagCompound.getString("Player" + i) != null && !stackTagCompound.getString("Player" + i).isEmpty()){
				continue;
			}else{
				return i;
			}
		}
		
		return 0;
	}
}
