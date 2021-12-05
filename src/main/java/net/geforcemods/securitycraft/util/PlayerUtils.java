package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.entity.camera.SecurityCameraEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
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
				if(tempPlayer.getName().getFormattedText().equals(name))
					return tempPlayer;
			}

			return null;
		}else{
			List<?> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while(iterator.hasNext()){
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();
				if(tempPlayer.getName().getFormattedText().equals(name))
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
				if(player != null && player.getName().getFormattedText().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByUsername(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, ITextComponent prefix, ITextComponent text, TextFormatting color){
		PlayerEntity player = getPlayerFromName(playerName);

		if (player != null)
			sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, ITextComponent prefix, ITextComponent text, TextFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, ITextComponent prefix, ITextComponent text, TextFormatting color, boolean shouldSendFromClient) {
		if (player.world.isRemote == shouldSendFromClient) {
			player.sendMessage(new StringTextComponent("[" + color)
					.appendSibling(prefix)
					.appendSibling(new StringTextComponent(TextFormatting.WHITE + "] "))
					.appendSibling(text));
		}
	}

	/**
	 * Sends the given {@link ICommandSource} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(ICommandSource sender, ITextComponent prefix, ITextComponent text, String link, TextFormatting color){
		sender.sendMessage(new StringTextComponent("[" + color)
				.appendSibling(prefix)
				.appendSibling(new StringTextComponent(TextFormatting.WHITE + "] "))
				.appendSibling(text)
				.appendSibling(new StringTextComponent(": "))
				.appendSibling(ForgeHooks.newChatWithLinks(link)));
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
		if (hand != Hand.OFF_HAND && !player.getHeldItem(Hand.MAIN_HAND).isEmpty()) {
			if (player.getHeldItem(Hand.MAIN_HAND).getItem() == item)
				return true;
		}

		if (hand != Hand.MAIN_HAND && !player.getHeldItem(Hand.OFF_HAND).isEmpty()) {
			if (player.getHeldItem(Hand.OFF_HAND).getItem() == item)
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
		if (!inventory.getCurrentItem().isEmpty()) {
			if (inventory.getCurrentItem().getItem() == item)
				return inventory.getCurrentItem();
		}

		if (!inventory.offHandInventory.get(0).isEmpty()) {
			if (inventory.offHandInventory.get(0).getItem() == item)
				return inventory.offHandInventory.get(0);
		}

		return ItemStack.EMPTY;
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		if(!(entity instanceof PlayerEntity))
			return false;

		PlayerEntity player = (PlayerEntity)entity;

		if(player.world.isRemote)
			return ClientHandler.isPlayerMountedOnCamera();
		else
			return ((ServerPlayerEntity)player).getSpectatingEntity() instanceof SecurityCameraEntity;
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

		ScorePlayerTeam team = getPlayersTeam(name1);

		return team != null && team.getMembershipCollection().contains(name2);
	}

	/**
	 * Gets the scoreboard team the given player is on
	 * @param playerName The player whose team to get
	 * @return The team the given player is on. null if the player is not part of a team
	 */
	public static ScorePlayerTeam getPlayersTeam(String playerName)
	{
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if(server != null)
			return server.getScoreboard().getPlayersTeam(playerName);
		else
			return ClientHandler.getClientPlayer().getWorldScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team, this will return the colored team name.
	 * @param ownerName The player who owns the block
	 * @return The component to display
	 */
	public static ITextComponent getOwnerComponent(String ownerName)
	{
		if(ConfigHandler.SERVER.enableTeamOwnership.get())
		{
			ScorePlayerTeam team = getPlayersTeam(ownerName);

			if(team != null)
				return Utils.localize("messages.securitycraft:teamOwner", new StringTextComponent("").appendSibling(team.getDisplayName()).applyTextStyle(team.getColor()));
		}

		return new StringTextComponent(ownerName);
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static String getNameOfSkull(PlayerEntity player) {
		ItemStack stack = player.getItemStackFromSlot(EquipmentSlotType.HEAD);

		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
			CompoundNBT stackTag = stack.getTag();

			if (stackTag.contains("SkullOwner", Constants.NBT.TAG_STRING))
				return stackTag.getString("SkullOwner");
			else if (stackTag.contains("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
				CompoundNBT skullOwnerTag = stackTag.getCompound("SkullOwner");

				if (skullOwnerTag.contains("Name", Constants.NBT.TAG_STRING))
					return skullOwnerTag.getString("Name");
			}
		}

		return null;
	}
}
