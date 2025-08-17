package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ResolvableProfile;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.util.thread.EffectiveSide;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class PlayerUtils {
	private PlayerUtils() {}

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static <T extends Player> T getPlayerFromName(String name) {
		List<T> players = null;

		if (EffectiveSide.get() == LogicalSide.CLIENT)
			players = (List<T>) Minecraft.getInstance().level.players();
		else
			players = (List<T>) ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();

		if (players != null) {
			for (T player : players) {
				if (player.getName().getString().equals(name))
					return player;
			}
		}

		return null;
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
		if (player != null && player.level().isClientSide == shouldSendFromClient) {
			//@formatter:off
			player.displayClientMessage(Component.literal("[")
					.append(prefix.setStyle(Style.EMPTY.withColor(color)))
					.append(Component.literal("] ")).setStyle(Style.EMPTY.withColor(ChatFormatting.WHITE))
					.append(text), false);
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
		if (player.getMainHandItem().is(item))
			return player.getMainHandItem();

		if (player.getOffhandItem().is(item))
			return player.getOffhandItem();

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
		else if (player instanceof ServerPlayer serverPlayer)
			return serverPlayer.getCamera() instanceof SecurityCamera;
		else
			return false;
	}

	/**
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team,
	 * this will return the colored team name.
	 *
	 * @param owner The player who owns the block
	 * @return The component to display
	 */
	public static Component getOwnerComponent(Owner owner) {
		TeamRepresentation teamRepresentation = TeamUtils.getTeamRepresentation(owner);

		if (teamRepresentation != null)
			return Utils.localize("messages.securitycraft:teamOwner", Component.literal(teamRepresentation.name()).withStyle(Style.EMPTY.withColor(teamRepresentation.color()))).withStyle(ChatFormatting.GRAY);

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

		if (stack.getItem() == Items.PLAYER_HEAD) {
			ResolvableProfile profile = stack.get(DataComponents.PROFILE);

			if (profile != null) {
				Optional<UUID> profileUUID = profile.id();
				String uuid = "ownerUUID";

				if (profileUUID.isPresent())
					uuid = profileUUID.get().toString();

				return new Owner(profile.name().orElse("ownerName"), uuid);
			}
		}

		return null;
	}

	/**
	 * Gets a list containing the player corresponding to an Owner
	 *
	 * @param owner The owner to get the player of
	 * @return A list containing the player corresponding to the given owner, an empty list if no such player exists
	 */
	public static Collection<ServerPlayer> getPlayerListFromOwner(Owner owner) {
		ServerPlayer player = getPlayerFromName(owner.getName());

		if (player != null)
			return Arrays.asList(player);

		return new ArrayList<>();
	}
}
