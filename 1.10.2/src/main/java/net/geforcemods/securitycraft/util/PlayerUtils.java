package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.entity.EntitySecurityCamera;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerUtils{

	/**
	 * Gets the EntityPlayer instance of a player (if they're online) using their name. <p>
	 *
	 * Args: playerName.
	 */
	public static EntityPlayer getPlayerFromName(String name){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			List<?> players = Minecraft.getMinecraft().world.playerEntities;
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
				if(tempPlayer.getName().equals(name))
					return tempPlayer;
			}

			return null;
		}else{
			List<?> players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
				if(tempPlayer.getName().equals(name))
					return tempPlayer;
			}

			return null;
		}
	}


	/**
	 * Returns true if a player with the given name is in the world.
	 *
	 * Args: playerName.
	 */
	public static boolean isPlayerOnline(String name) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			for(int i = 0; i < Minecraft.getMinecraft().world.playerEntities.size(); i++){
				EntityPlayer player = Minecraft.getMinecraft().world.playerEntities.get(i);

				if(player != null && player.getName().equals(name))
					return true;
			}

			return false;
		}
		else
			return (FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(name) != null);
	}

	public static void sendMessageToPlayer(EntityPlayer player, String prefix, String text, TextFormatting color){
		player.sendMessage(new TextComponentString("[" + color + prefix + TextFormatting.WHITE + "] " + text));
	}

	public static void sendMessageToPlayer(ICommandSender sender, String prefix, String text, TextFormatting color){
		sender.sendMessage(new TextComponentString("[" + color + prefix + TextFormatting.WHITE + "] " + text));
	}

	/**
	 * Sends the given {@link ICommandSender} a chat message, followed by a link prefixed with a colon. <p>
	 *
	 * Args: sender, prefix, text, link, color.
	 */
	public static void sendMessageEndingWithLink(ICommandSender sender, String prefix, String text, String link, TextFormatting color){
		sender.sendMessage(new TextComponentString("[" + color + prefix + TextFormatting.WHITE + "] " + text + ": ").appendSibling(ForgeHooks.newChatWithLinks(link)));
	}

	/**
	 * Returns true if the player is holding the given item.
	 *
	 * Args: player, item.
	 */
	public static boolean isHoldingItem(EntityPlayer player, Item item){
		if(item == null && player.inventory.getCurrentItem() == null)
			return true;

		return (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() == item);
	}

	/**
	 * Is the entity mounted on to a security camera?
	 *
	 * Args: entity.
	 */
	public static boolean isPlayerMountedOnCamera(EntityLivingBase entity) {
		return entity.getRidingEntity() != null && entity.getRidingEntity() instanceof EntitySecurityCamera;
	}
}
