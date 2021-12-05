package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.commands.CommandSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.util.thread.EffectiveSide;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;

public class PlayerUtils{

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static Player getPlayerFromName(String name){
		if(EffectiveSide.get() == LogicalSide.CLIENT){
			List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				Player tempPlayer = (Player) iterator.next();
				if(tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}else{
			List<?> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				Player tempPlayer = (Player) iterator.next();
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
			for(AbstractClientPlayer player : Minecraft.getInstance().level.players()){
				if(player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, MutableComponent prefix, MutableComponent text, ChatFormatting color){
		Player player = getPlayerFromName(playerName);

		if (player != null)
			sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(Player player, MutableComponent prefix, MutableComponent text, ChatFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(Player player, MutableComponent prefix, MutableComponent text, ChatFormatting color, boolean shouldSendFromClient){
		if (player.level.isClientSide == shouldSendFromClient) {
			player.sendMessage(new TextComponent("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(new TextComponent("] ")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
					.append(text), Util.NIL_UUID); //appendSibling
		}
	}

	/**
	 * Sends the given {@link ICommandSource} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(CommandSource sender, MutableComponent prefix, MutableComponent text, String link, ChatFormatting color){
		sender.sendMessage(new TextComponent("[")
				.append(prefix.setStyle(Style.EMPTY.withColor(color)))
				.append(new TextComponent("] ")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
				.append(text)
				.append(new TextComponent(": "))
				.append(ForgeHooks.newChatWithLinks(link)), Util.NIL_UUID); //appendSibling
	}

	/**
	 * Returns true if the player is holding the given item.
	 */
	public static boolean isHoldingItem(Player player, Supplier<Item> item, InteractionHand hand){
		return isHoldingItem(player, item.get(), hand);
	}

	/**
	 * Returns true if the player is holding the given item.
	 * @param player The player that is checked for the item
	 * @param item The item that is checked
	 * @param hand The hand in which the item should be; if hand is null, both hands are checked
	 * @return true if the item was found in the mainhand or offhand, or if no item was found and item was null
	 */
	public static boolean isHoldingItem(Player player, Item item, InteractionHand hand){
		if (hand != InteractionHand.OFF_HAND && !player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
			if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() == item)
				return true;
		}

		if (hand != InteractionHand.MAIN_HAND && !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()) {
			if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() == item)
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
	public static ItemStack getSelectedItemStack(Player player, Item item) {
		return getSelectedItemStack(player.getInventory(), item);
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 * @param inventory The inventory that contains the item
	 * @param item The item type that should be searched for
	 * @return The respective item stack if it has been found, ItemStack.EMPTY if not
	 */
	public static ItemStack getSelectedItemStack(Inventory inventory, Item item) {
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
		if(!(entity instanceof Player player))
			return false;

		if(player.level.isClientSide)
			return ClientHandler.isPlayerMountedOnCamera();
		else
			return ((ServerPlayer)player).getCamera() instanceof SecurityCamera;
	}

	/**
	 * Checks if two given players are on the same scoreboard team
	 * @param name1 The name of the first player
	 * @param name2 The name of the second player
	 * @return true if both players are on the same team, false otherwise
	 */
	public static boolean areOnSameTeam(String name1, String name2)
	{
		if(name1.equals(name2))
			return true;

		PlayerTeam team = getPlayersTeam(name1);

		return team != null && team.getPlayers().contains(name2);
	}

	/**
	 * Gets the scoreboard team the given player is on
	 * @param playerName The player whose team to get
	 * @return The team the given player is on. null if the player is not part of a team
	 */
	public static PlayerTeam getPlayersTeam(String playerName)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if(server != null)
			return server.getScoreboard().getPlayersTeam(playerName);
		else
			return ClientHandler.getClientPlayer().getScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team, this will return the colored team name.
	 * @param ownerName The player who owns the block
	 * @return The component to display
	 */
	public static Component getOwnerComponent(String ownerName)
	{
		if(ConfigHandler.SERVER.enableTeamOwnership.get())
		{
			PlayerTeam team = getPlayersTeam(ownerName);

			if(team != null)
				return Utils.localize("messages.securitycraft:teamOwner", new TextComponent("").append(team.getDisplayName()).withStyle(team.getColor()));
		}

		return new TextComponent(ownerName);
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static String getNameOfSkull(Player player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);

		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
			CompoundTag stackTag = stack.getTag();

			if (stackTag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_STRING))
				return stackTag.getString(PlayerHeadItem.TAG_SKULL_OWNER);
			else if (stackTag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_COMPOUND)) {
				CompoundTag skullOwnerTag = stackTag.getCompound(PlayerHeadItem.TAG_SKULL_OWNER);

				if (skullOwnerTag.contains("Name", Tag.TAG_STRING))
					return skullOwnerTag.getString("Name");
			}
		}

		return null;
	}
}
