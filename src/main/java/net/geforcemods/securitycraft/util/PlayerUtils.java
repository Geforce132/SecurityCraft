package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.List;

import com.mojang.authlib.GameProfile;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PlayerHeadItem;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PlayerUtils {
	private PlayerUtils() {}

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static Player getPlayerFromName(String name) {
		if (EffectiveSide.get() == LogicalSide.CLIENT) {
			List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();
			Iterator<?> iterator = players.iterator();

			while (iterator.hasNext()) {
				Player tempPlayer = (Player) iterator.next();

				if (tempPlayer.getName().getString().equals(name))
					return tempPlayer;
			}

			return null;
		}
		else {
			List<?> players = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
			Iterator<?> iterator = players.iterator();

			while (iterator.hasNext()) {
				Player tempPlayer = (Player) iterator.next();

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
			for (AbstractClientPlayer player : Minecraft.getInstance().level.players()) {
				if (player != null && player.getName().getString().equals(name))
					return true;
			}

			return false;
		}
		else
			return (ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayerByName(name) != null);
	}

	public static void sendMessageToPlayer(String playerName, MutableComponent prefix, MutableComponent text, ChatFormatting color) {
		Player player = getPlayerFromName(playerName);

		if (player != null)
			sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(Player player, MutableComponent prefix, MutableComponent text, ChatFormatting color) {
		sendMessageToPlayer(player, prefix, text, color, false);
	}

	public static void sendMessageToPlayer(Player player, MutableComponent prefix, MutableComponent text, ChatFormatting color, boolean shouldSendFromClient) {
		if (player.level().isClientSide == shouldSendFromClient) {
			//@formatter:off
			player.sendSystemMessage(Component.literal("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(Component.literal("] ")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
					.append(text));
			//@formatter:on
		}
	}

	/**
	 * Returns the ItemStack of the given item the player if they are currently holding it (both hands are checked).
	 *
	 * @param player The player to check
	 * @param item The item type that should be searched for
	 * @return The ItemStack whose item matches the given item, {@link ItemStack#EMPTY} if the player is not holding the item
	 */
	public static ItemStack getItemStackFromAnyHand(Player player, Item item) {
		Inventory inventory = player.getInventory();

		if (inventory.getSelected().is(item))
			return inventory.getSelected();

		if (inventory.offhand.get(0).is(item))
			return inventory.offhand.get(0);

		return ItemStack.EMPTY;
	}

	/**
	 * Is the entity mounted on to a security camera?
	 */
	public static boolean isPlayerMountedOnCamera(LivingEntity entity) {
		if (!(entity instanceof Player player))
			return false;

		if (player.level().isClientSide)
			return ClientHandler.isPlayerMountedOnCamera();
		else
			return ((ServerPlayer) player).getCamera() instanceof SecurityCamera;
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team,
	 * this will return the colored team name.
	 *
	 * @param owner The player who owns the block
	 * @return The component to display
	 */
	public static Component getOwnerComponent(Owner owner) {
		if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
			TeamRepresentation teamRepresentation = TeamUtils.getTeamRepresentation(owner);

			if (teamRepresentation != null)
				return Utils.localize("messages.securitycraft:teamOwner", Component.literal(teamRepresentation.name()).withStyle(Style.EMPTY.withColor(teamRepresentation.color()))).withStyle(ChatFormatting.GRAY);
		}

		return Component.literal(owner.getName());
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 *
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static Owner getSkullOwner(Player player) {
		ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);

		if (stack.getItem() == Items.PLAYER_HEAD && stack.hasTag()) {
			CompoundTag stackTag = stack.getTag();

			if (stackTag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_STRING))
				return new Owner(stackTag.getString(PlayerHeadItem.TAG_SKULL_OWNER), "ownerUUID");
			else if (stackTag.contains(PlayerHeadItem.TAG_SKULL_OWNER, Tag.TAG_COMPOUND)) {
				GameProfile profile = NbtUtils.readGameProfile(stackTag.getCompound(PlayerHeadItem.TAG_SKULL_OWNER));
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
