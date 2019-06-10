package net.geforcemods.securitycraft.commands;

import java.util.Collection;

import com.google.common.base.Predicates;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.ItemModule;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class CommandModule {
	public static void register(CommandDispatcher<CommandSource> dispatcher)
	{
		dispatcher.register(LiteralArgumentBuilder.<CommandSource>literal("module")
				.requires(Predicates.alwaysTrue())
				.then(copy())
				.then(paste())
				.then(add())
				.then(remove()));
	}

	private static ArgumentBuilder<CommandSource, ?> copy()
	{
		return Commands.literal("copy").executes(ctx -> {
			EntityPlayer player = ctx.getSource().asPlayer();

			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
				SecurityCraft.instance.setSavedModule(player.inventory.getCurrentItem().getTag());
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.saved"), TextFormatting.GREEN);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForData"), TextFormatting.RED);

			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> paste()
	{
		return Commands.literal("paste").executes(ctx -> {
			EntityPlayer player = ctx.getSource().asPlayer();

			if(SecurityCraft.instance.getSavedModule() == null){
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.nothingSaved"), TextFormatting.RED);
				return 0;
			}

			if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
				player.inventory.getCurrentItem().setTag(SecurityCraft.instance.getSavedModule());
				SecurityCraft.instance.setSavedModule(null);
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.saved"), TextFormatting.GREEN);
			}

			return 0;
		});
	}

	private static ArgumentBuilder<CommandSource, ?> add()
	{
		return Commands.literal("add")
				.then(Commands.argument("target", EntityArgument.players()))
				.executes(ctx -> {
					EntityPlayer player = ctx.getSource().asPlayer();
					Collection<EntityPlayerMP> players = EntityArgument.getPlayers(ctx, "target");

					for(EntityPlayerMP arg : players)
					{
						String name = arg.getName().getFormattedText();

						if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
							if(player.inventory.getCurrentItem().getTag() == null)
								player.inventory.getCurrentItem().setTag(new NBTTagCompound());

							for(int i = 1; i <= 10; i++)
								if(player.inventory.getCurrentItem().getTag().contains("Player" + i) && player.inventory.getCurrentItem().getTag().getString("Player" + i).equals(name)){
									PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.alreadyContained").replace("#", name), TextFormatting.RED);
									return 0;
								}

							player.inventory.getCurrentItem().getTag().putString("Player" + getNextSlot(player.inventory.getCurrentItem().getTag()), name);
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.added").replace("#", name), TextFormatting.GREEN);
							return 0;
						}else{
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForModify"), TextFormatting.RED);
							return 0;
						}
					}

					return 0;
				});
	}

	private static ArgumentBuilder<CommandSource, ?> remove()
	{
		return Commands.literal("remove")
				.then(Commands.argument("target", EntityArgument.players()))
				.executes(ctx -> {
					EntityPlayer player = ctx.getSource().asPlayer();
					Collection<EntityPlayerMP> players = EntityArgument.getPlayers(ctx, "target");

					for(EntityPlayerMP arg : players)
					{
						String name = arg.getName().getFormattedText();

						if(!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() instanceof ItemModule && ((ItemModule) player.inventory.getCurrentItem().getItem()).canNBTBeModified()){
							if(player.inventory.getCurrentItem().getTag() == null)
								player.inventory.getCurrentItem().setTag(new NBTTagCompound());

							for(int i = 1; i <= 10; i++)
								if(player.inventory.getCurrentItem().getTag().contains("Player" + i) && player.inventory.getCurrentItem().getTag().getString("Player" + i).equals(name))
									player.inventory.getCurrentItem().getTag().remove("Player" + i);

							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.removed").replace("#", name), TextFormatting.GREEN);
							return 0;
						}else{
							PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("messages.securitycraft:module.manager"), ClientUtils.localize("messages.securitycraft:module.notHoldingForModify"), TextFormatting.RED);
							return 0;
						}
					}

					return 0;
				});
	}

	private static int getNextSlot(NBTTagCompound tag) {
		for(int i = 1; i <= 10; i++)
			if(tag.getString("Player" + i) != null && !tag.getString("Player" + i).isEmpty())
				continue;
			else
				return i;

		return 0;
	}
}
