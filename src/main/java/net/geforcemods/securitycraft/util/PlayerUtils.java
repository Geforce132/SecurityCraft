package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.entity.SecurityCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.Util;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PlayerUtils{

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static PlayerEntity getPlayerFromName(String name){
		if(EffectiveSide.get() == LogicalSide.CLIENT){
			List<AbstractClientPlayerEntity> players = Minecraft.getInstance().world.getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();
				if(tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}else{
			List<?> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();
				if(tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}
	}

	/**
	 * Returns true if a player with the given name is in the world.
	 */
	public static boolean isPlayerOnline(String name) {
		if(EffectiveSide.get() == LogicalSide.CLIENT){
			for(AbstractClientPlayerEntity player : Minecraft.getInstance().world.getPlayers()){
				if(player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color){
		PlayerEntity player = getPlayerFromName(playerName);

		if(player != null)
		{
			player.sendMessage(new StringTextComponent("[")
					.append(prefix.setStyle(Style.EMPTY.setFormatting(color)))
					.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE))
					.append(text), Util.DUMMY_UUID); //appendSibling
		}
	}

	public static void sendMessageToPlayer(PlayerEntity player, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color){
		player.sendMessage(new StringTextComponent("[")
				.append(prefix.setStyle(Style.EMPTY.setFormatting(color)))
				.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE))
				.append(text), Util.DUMMY_UUID); //appendSibling
	}

	/**
	 * Sends the given {@link ICommandSender} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(ICommandSource sender, IFormattableTextComponent prefix, IFormattableTextComponent text, String link, TextFormatting color){
		sender.sendMessage(new StringTextComponent("[")
				.append(prefix.setStyle(Style.EMPTY.setFormatting(color)))
				.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE))
				.append(text)
				.append(new StringTextComponent(": "))
				.append(ForgeHooks.newChatWithLinks(link)), Util.DUMMY_UUID); //appendSibling
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(PlayerEntity player, Supplier<Item> item){
		return isHoldingItem(player, item.get());
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(PlayerEntity player, Item item){
		if(item == null && player.inventory.getCurrentItem().isEmpty())
			return true;

		return (!player.inventory.getCurrentItem().isEmpty() && player.inventory.getCurrentItem().getItem() == item);
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		return entity.getRidingEntity() instanceof SecurityCameraEntity;
	}
}
