package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.compat.ftbteams.FTBTeamsCompat;
import net.geforcemods.securitycraft.compat.ftbteams.TeamRepresentation;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
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
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PlayerUtils {
	private PlayerUtils() {}

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static PlayerEntity getPlayerFromName(String name) {
		if (EffectiveSide.get() == LogicalSide.CLIENT) {
			List<AbstractClientPlayerEntity> players = Minecraft.getInstance().level.players();
			Iterator<?> iterator = players.iterator();

			while (iterator.hasNext()) {
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();

				if (tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}
		else {
			List<?> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while (iterator.hasNext()) {
				PlayerEntity tempPlayer = (PlayerEntity) iterator.next();

				if (tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}
	}

	/**
	 * Returns true if a player with the given name is in the world.
	 */
	public static boolean isPlayerOnline(String name) {
		if (EffectiveSide.get() == LogicalSide.CLIENT) {
			for (AbstractClientPlayerEntity player : Minecraft.getInstance().level.players()) {
				if (player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color) {
		PlayerEntity player = getPlayerFromName(playerName);

		if (player != null)
			sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(PlayerEntity player, IFormattableTextComponent prefix, IFormattableTextComponent text, TextFormatting color, boolean shouldSendFromClient) {
		if (player.level.isClientSide == shouldSendFromClient) {
			//@formatter:off
			player.sendMessage(new StringTextComponent("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.withColor(TextFormatting.WHITE))
					.append(text), Util.NIL_UUID); //appendSibling
			//@formatter:on
		}
	}

	/**
	 * Sends the given {@link ICommandSource} a chat message, followed by a link prefixed with a colon. <p>
	 */
	public static void sendMessageEndingWithLink(ICommandSource sender, IFormattableTextComponent prefix, IFormattableTextComponent text, String link, TextFormatting color) {
		//@formatter:off
		sender.sendMessage(new StringTextComponent("[")
				.append(prefix.setStyle(Style.EMPTY.withColor(color)))
				.append(new StringTextComponent("] ")).setStyle(Style.EMPTY.withColor(TextFormatting.WHITE))
				.append(text)
				.append(new StringTextComponent(": "))
				.append(ForgeHooks.newChatWithLinks(link)), Util.NIL_UUID); //appendSibling
		//@formatter:on
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 *
	 * @param player The player holding the item
	 * @param item The item type that should be searched for
	 * @return The item stack if it has been found, ItemStack.EMPTY if not
	 */
	public static ItemStack getSelectedItemStack(PlayerEntity player, Item item) {
		return getSelectedItemStack(player.inventory, item);
	}

	/**
	 * Returns the ItemStack of the given item the player is currently holding (both hands are checked).
	 *
	 * @param inventory The inventory that contains the item
	 * @param item The item type that should be searched for
	 * @return The respective item stack if it has been found, ItemStack.EMPTY if not
	 */
	public static ItemStack getSelectedItemStack(PlayerInventory inventory, Item item) {
		if (inventory.getSelected().getItem() == item)
			return inventory.getSelected();

		if (inventory.offhand.get(0).getItem() == item)
			return inventory.offhand.get(0);

		return ItemStack.EMPTY;
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity))
			return false;

		PlayerEntity player = (PlayerEntity) entity;

		if (player.level.isClientSide)
			return ClientHandler.isPlayerMountedOnCamera();
		else
			return ((ServerPlayerEntity) player).getCamera() instanceof SecurityCamera;
	}

	/**
	 * Checks if two given players are on the same scoreboard/FTB Teams team
	 *
	 * @param owner1 The first owner object representing a player
	 * @param owner2 The second owner object representing a player
	 * @return true if both players are on the same team, false otherwise
	 */
	public static boolean areOnSameTeam(Owner owner1, Owner owner2) {
		if (owner1.equals(owner2))
			return true;

		if (ModList.get().isLoaded("ftbteams"))
			return FTBTeamsCompat.areOnSameTeam(owner1, owner2);

		ScorePlayerTeam team = getPlayersVanillaTeam(owner1.getName());

		return team != null && team.getPlayers().contains(owner2.getName());
	}

	/**
	 * Gets the scoreboard team the given player is on
	 *
	 * @param playerName The player whose team to get
	 * @return The team the given player is on. null if the player is not part of a team
	 */
	public static ScorePlayerTeam getPlayersVanillaTeam(String playerName) {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();

		if (server != null)
			return server.getScoreboard().getPlayersTeam(playerName);
		else
			return ClientHandler.getClientPlayer().getScoreboard().getPlayersTeam(playerName);
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team,
	 * this will return the colored team name.
	 *
	 * @param owner The player who owns the block
	 * @return The component to display
	 */
	public static ITextComponent getOwnerComponent(Owner owner) {
		if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
			TeamRepresentation teamRepresentation = TeamRepresentation.get(owner);

			if (teamRepresentation != null)
				return Utils.localize("messages.securitycraft:teamOwner", new StringTextComponent(teamRepresentation.name()).withStyle(Style.EMPTY.withColor(Color.fromRgb(teamRepresentation.color())))).withStyle(TextFormatting.GRAY);
		}

		return new StringTextComponent(owner.getName());
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 *
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static Owner getSkullOwner(PlayerEntity player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlotType.HEAD);

		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
			CompoundNBT stackTag = stack.getTag();

			if (stackTag.contains("SkullOwner", Constants.NBT.TAG_STRING))
				return new Owner(stackTag.getString("SkullOwner"), "ownerUUID");
			else if (stackTag.contains("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
				GameProfile profile = NBTUtil.readGameProfile(stackTag.getCompound("SkullOwner"));
				String name = "ownerName";
				String uuid = "ownerUUID";

				if (profile.getName() != null)
					name = profile.getName();

				if (profile.getId() != null)
					uuid = profile.getId().toString();

				return new Owner(name, uuid);
			}
		}

		return null;
	}
}
