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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
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
			List<AbstractClientPlayerEntity> players = Minecraft.getInstance().level.players();
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
			for(AbstractClientPlayerEntity player : Minecraft.getInstance().level.players()){
				if(player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color){
		PlayerEntity player = getPlayerFromName(playerName);

		if (player != null)
			sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color, boolean shouldSendFromClient){
		if (player.level.isClientSide == shouldSendFromClient) {
			player.sendMessage(new StringTextComponent("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.withColor(TextFormatting.WHITE))
					.append(text), Util.NIL_UUID); //appendSibling
		}
	}

	/**
	 * Sends the given {@link ICommandSource} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(ICommandSource sender, IFormattableTextComponent prefix, IFormattableTextComponent text, String link, TextFormatting color){
		sender.sendMessage(new StringTextComponent("[")
				.append(prefix.setStyle(Style.EMPTY.withColor(color)))
				.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.withColor(TextFormatting.WHITE))
				.append(text)
				.append(new StringTextComponent(": "))
				.append(ForgeHooks.newChatWithLinks(link)), Util.NIL_UUID); //appendSibling
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(PlayerEntity player, Supplier<Item> item, Hand hand){
		return isHoldingItem(player, item.get(), hand);
	}

	/**
	 * Returns true if the player is holding the given item.
	 * @param player The player that is checked for the item
	 * @param item The item that is checked
	 * @param hand The hand in which the item should be; if hand is null, both hands are checked
	 * @return true if the item was found in the mainhand or offhand, or if no item was found and item was null
	 */
	public static boolean isHoldingItem(PlayerEntity player, Item item, Hand hand){
		if (hand != Hand.OFF_HAND && !player.getItemInHand(Hand.MAIN_HAND).isEmpty()) {
			if (player.getItemInHand(Hand.MAIN_HAND).getItem() == item)
				return true;
		}

		if (hand != Hand.MAIN_HAND && !player.getItemInHand(Hand.OFF_HAND).isEmpty()) {
			if (player.getItemInHand(Hand.OFF_HAND).getItem() == item)
				return true;
		}

		return item == null;
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 * @param player The player holding the item
	 * @param item The item type that should be searched for
	 * @return The item stack if it has been found, ItemStack.EMPTY if not
	 */
	public static ItemStack getSelectedItemStack(PlayerEntity player, Item item) {
		return getSelectedItemStack(player.inventory, item);
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 * @param inventory The inventory that contains the item
	 * @param item The item type that should be searched for
	 * @return The respective item stack if it has been found, ItemStack.EMPTY if not
	 */
	public static ItemStack getSelectedItemStack(PlayerInventory inventory, Item item) {
		if (!inventory.getSelected().isEmpty()) {
			if (inventory.getSelected().getItem() == item)
				return inventory.getSelected();
		}

		if (!inventory.offhand.get(0).isEmpty()) {
			if (inventory.offhand.get(0).getItem() == item)
				return inventory.offhand.get(0);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		return entity != null && entity.getVehicle() instanceof SecurityCameraEntity;
	}
}
