package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.entity.camera.EntitySecurityCamera;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
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

	public static void sendMessageToPlayer(EntityPlayer player, ITextComponent prefix, ITextComponent text, TextFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(EntityPlayer player, ITextComponent prefix, ITextComponent text, TextFormatting color, boolean shouldSendFromClient){
		if (player.world.isRemote == shouldSendFromClient) {
			player.sendMessage(new TextComponentString("[")
					.appendSibling(prefix.setStyle(new Style().setColor(color)))
					.appendSibling(new TextComponentString(TextFormatting.WHITE + "] "))
					.appendSibling(text));
		}
	}

	/**
	 * Sends the given {@link ICommandSender} a chat message, followed by a link prefixed with a colon. <p>
	 *
	 * Args: sender, prefix, text, link, color.
	 */
	public static void sendMessageEndingWithLink(ICommandSender sender, ITextComponent prefix, ITextComponent text, String link, TextFormatting color){
		sender.sendMessage(new TextComponentString("[")
				.appendSibling(prefix.setStyle(new Style().setColor(color)))
				.appendSibling(new TextComponentString(TextFormatting.WHITE + "] "))
				.appendSibling(text)
				.appendSibling(new TextComponentString(": "))
				.appendSibling(ForgeHooks.newChatWithLinks(link)));
	}

	/**
	 * Returns true if the player is holding the given item.
	 * @param player The player that is checked for the item
	 * @param item The item that is checked
	 * @param hand The hand in which the item should be; if hand is null, both hands are checked
	 * @return true if the item was found in the mainhand or offhand, or if no item was found and item was null
	 */
	public static boolean isHoldingItem(EntityPlayer player, Item item, EnumHand hand){
		if (hand != EnumHand.OFF_HAND && !player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
			if (player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item)
				return true;
		}

		if (hand != EnumHand.MAIN_HAND && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
			if (player.getHeldItem(EnumHand.OFF_HAND).getItem() == item)
				return true;
		}

		return item == null;

	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 * @param player The player holding the item
	 * @param item The item type that should be searched for
	 */
	public static ItemStack getSelectedItemStack(EntityPlayer player, Item item) {
		return getSelectedItemStack(player.inventory, item);
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 * @param inventory The inventory that contains the item
	 * @param item The item type that should be searched for
	 */
	public static ItemStack getSelectedItemStack(InventoryPlayer inventory, Item item) {
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
	 *
	 * Args: entity.
	 */
	public static boolean isPlayerMountedOnCamera(EntityLivingBase entity) {
		if(!(entity instanceof EntityPlayer))
			return false;

		EntityPlayer player = (EntityPlayer)entity;

		if(player.world.isRemote)
			return ClientProxy.isPlayerMountedOnCamera();
		else
			return ((EntityPlayerMP)player).getSpectatingEntity() instanceof EntitySecurityCamera;
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
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

		if(server != null)
			return server.getEntityWorld().getScoreboard().getPlayersTeam(playerName);
		else
			return SecurityCraft.proxy.getClientPlayer().world.getScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team, this will return the colored team name.
	 * @param ownerName The player who owns the block
	 * @return The component to display
	 */
	public static ITextComponent getOwnerComponent(String ownerName)
	{
		if(ConfigHandler.enableTeamOwnership)
		{
			ScorePlayerTeam team = getPlayersTeam(ownerName);

			if(team != null)
				return Utils.localize("messages.securitycraft:teamOwner", new TextComponentString(team.getDisplayName()).setStyle(new Style().setColor(team.getColor())));
		}

		return new TextComponentString(ownerName);
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static String getNameOfSkull(EntityPlayer player) {
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (stack.getItem() == Items.SKULL && stack.hasTagCompound()) {
			NBTTagCompound stackTag = stack.getTagCompound();

			if (stackTag.hasKey("SkullOwner", Constants.NBT.TAG_STRING))
				return stackTag.getString("SkullOwner");
			else if (stackTag.hasKey("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
				NBTTagCompound skullOwnerTag = stackTag.getCompoundTag("SkullOwner");

				if (skullOwnerTag.hasKey("Name", Constants.NBT.TAG_STRING))
					return skullOwnerTag.getString("Name");
			}
		}

		return null;
	}
}
