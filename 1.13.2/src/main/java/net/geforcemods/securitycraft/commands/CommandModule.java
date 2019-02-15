package net.geforcemods.securitycraft.commands;

import java.util.ArrayList;
import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextFormatting;

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
	public String getName() {
		return "module";
	}

	@Override
	public List<String> getAliases() {
		return nicknames;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return ClientUtils.localize("messages.securitycraft:command.module.usage");
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException{
		if(args.length == 1){
			if(args[0].equals("copy")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getName());

				if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
					SecurityCraft.instance.setSavedModule(player.inventory.getCurrentItem().getTagCompound());
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.saved"), TextFormatting.GREEN);
				}
				else
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForData"), TextFormatting.RED);

				return;
			}else if(args[0].equals("paste")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getName());

				if(SecurityCraft.instance.getSavedModule() == null){
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.nothingSaved"), TextFormatting.RED);
					return;
				}

				if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
					player.inventory.getCurrentItem().setTagCompound(SecurityCraft.instance.getSavedModule());
					SecurityCraft.instance.setSavedModule(null);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.saved"), TextFormatting.GREEN);
				}

				return;
			}
		}else if(args.length == 2)
			if(args[0].equals("add")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getName());

				if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
					if(player.inventory.getCurrentItem().getTagCompound() == null)
						player.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());

					for(int i = 1; i <= 10; i++)
						if(player.inventory.getCurrentItem().getTagCompound().hasKey("Player" + i) && player.inventory.getCurrentItem().getTagCompound().getString("Player" + i).equals(args[1])){
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.alreadyContained").replace("#", args[1]), TextFormatting.RED);
							return;
						}

					player.inventory.getCurrentItem().getTagCompound().setString("Player" + getNextSlot(player.inventory.getCurrentItem().getTagCompound()), args[1]);
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.added").replace("#", args[1]), TextFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForModify"), TextFormatting.RED);
					return;
				}
			}else if(args[0].equals("remove")){
				EntityPlayer player = PlayerUtils.getPlayerFromName(sender.getName());

				if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
					if(player.inventory.getCurrentItem().getTagCompound() == null)
						player.inventory.getCurrentItem().setTagCompound(new NBTTagCompound());

					for(int i = 1; i <= 10; i++)
						if(player.inventory.getCurrentItem().getTagCompound().hasKey("Player" + i) && player.inventory.getCurrentItem().getTagCompound().getString("Player" + i).equals(args[1]))
							player.inventory.getCurrentItem().getTagCompound().removeTag("Player" + i);

					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.removed").replace("#", args[1]), TextFormatting.GREEN);
					return;
				}else{
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForModify"), TextFormatting.RED);
					return;
				}
			}

		throw new WrongUsageException(ClientUtils.localize("messages.securitycraft:command.module.usage"));
	}

	private int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= 10; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}
