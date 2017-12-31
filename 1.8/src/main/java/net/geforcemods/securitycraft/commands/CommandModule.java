package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class CommandModule extends CommandBase implements ICommand {

	private List<String> nicknames;

	public CommandModule() {
		nicknames = new ArrayList<String>();
		nicknames.add("module");
	}

	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}

	@Override
	public String getCommandName() {
		return "module";
	}

	public List<String> getAliases() {
		return nicknames;
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return StatCollector.translateToLocal("messages.command.module.usage");
	}

	public boolean canCommandSenderUse(ICommandSender p_71519_1_) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender par1ICommandSender, String[] par2String) throws CommandException{
		if(par2String.length == 1){
			if(par2String[0].matches("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					SecurityCraft.instance.setSavedModule(player.getCurrentEquippedItem().getTagCompound());
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.saved"), EnumChatFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForData"), EnumChatFormatting.RED);

				return;
			}else if(par2String[0].matches("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.nothingSaved"), EnumChatFormatting.RED);
					return;
				}

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					player.getCurrentEquippedItem().setTagCompound(SecurityCraft.instance.getSavedModule());
					SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.saved"), EnumChatFormatting.GREEN);
				}

				return;
			}
		}else if(par2String.length == 2)
			if(par2String[0].matches("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					if(player.getCurrentEquippedItem().getTagCompound() == null)
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

					for(int i = 1; i <= 10; i++)
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(par2String[1])){
							PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.alreadyContained").replace("#", par2String[1]), EnumChatFormatting.RED);
							return;
						}

					player.getCurrentEquippedItem().getTagCompound().setString("Player" + getNextSlot(player.getCurrentEquippedItem().getTagCompound()), par2String[1]);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.added").replace("#", par2String[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}else if(par2String[0].matches("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(par1ICommandSender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					if(player.getCurrentEquippedItem().getTagCompound() == null)
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

					for(int i = 1; i <= 10; i++)
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).matches(par2String[1]))
							player.getCurrentEquippedItem().getTagCompound().removeTag("Player" + i);

					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.removed").replace("#", par2String[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.module.manager"), StatCollector.translateToLocal("messages.module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}

		throw new WrongUsageException(StatCollector.translateToLocal("messages.command.module.usage"));
	}

	private int getNextSlot(NBTTagCompound stackTagCompound) {
		for(int i = 1; i <= 10; i++)
			if(stackTagCompound.getString("Player" + i) != null && !stackTagCompound.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}

	@Override
	public int compareTo(Object par1Obj) {
		return this.compareTo((ICommand)par1Obj);
	}
}
