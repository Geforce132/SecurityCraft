package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
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

	@Override
	public List<String> getCommandAliases() {
		return nicknames;
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return StatCollector.translateToLocal("messages.securitycraft:command.module.usage");
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return true;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if(args.length == 1){
			if(args[0].equals("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					SecurityCraft.instance.setSavedModule(player.getCurrentEquippedItem().stackTagCompound);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.saved"), EnumChatFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.notHoldingForData"), EnumChatFormatting.RED);

				return;
			}else if(args[0].equals("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.nothingSaved"), EnumChatFormatting.RED);
					return;
				}

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					player.getCurrentEquippedItem().stackTagCompound = SecurityCraft.instance.getSavedModule();
					SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.saved"), EnumChatFormatting.GREEN);
				}

				return;
			}
		}else if(args.length == 2)
			if(args[0].equals("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					if(player.getCurrentEquippedItem().stackTagCompound == null)
						player.getCurrentEquippedItem().stackTagCompound = new NBTTagCompound();

					for(int i = 1; i <= 10; i++)
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).equals(args[1])){
							PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.alreadyContained").replace("#", args[1]), EnumChatFormatting.RED);
							return;
						}

					player.getCurrentEquippedItem().stackTagCompound.setString("Player" + getNextSlot(player.getCurrentEquippedItem().stackTagCompound), args[1]);
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.added").replace("#", args[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}else if(args[0].equals("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getCommandSenderName());

				if(player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() instanceof ItemModule && ((ItemModule) player.getCurrentEquippedItem().getItem()).canNBTBeModified()){
					if(player.getCurrentEquippedItem().getTagCompound() == null)
						player.getCurrentEquippedItem().setTagCompound(new NBTTagCompound());

					for(int i = 1; i <= 10; i++)
						if(player.getCurrentEquippedItem().getTagCompound().hasKey("Player" + i) && player.getCurrentEquippedItem().getTagCompound().getString("Player" + i).equals(args[1]))
							player.getCurrentEquippedItem().getTagCompound().removeTag("Player" + i);

					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.removed").replace("#", args[1]), EnumChatFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:module.manager"), StatCollector.translateToLocal("messages.securitycraft:module.notHoldingForModify"), EnumChatFormatting.RED);
					return;
				}
			}

		throw new WrongUsageException(StatCollector.translateToLocal("messages.securitycraft:command.module.usage"));
	}

	private int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= 10; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}

	@Override
	public int compareTo(Object o) {
		return this.compareTo((ICommand)o);
	}
}
