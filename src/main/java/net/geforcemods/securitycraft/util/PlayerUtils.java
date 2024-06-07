package net.geforcemods.securitycraft.util;

import java.util.List;

import com.mojang.authlib.GameProfile;

import net.geforcemods.securitycraft.ClientHandler;
import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Util;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class PlayerUtils {
	private PlayerUtils() {}

	/**
	 * Gets the PlayerEntity instance of a player (if they're online) using their name. <p>
	 */
	public static <T extends PlayerEntity> T getPlayerFromName(String name) {
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
	 * Returns the ItemStack of the given item the player if they are currently holding it (both hands are checked).
	 *
	 * @param player The player to check
	 * @param item The item type that should be searched for
	 * @return The ItemStack whose item matches the given item, {@link ItemStack#EMPTY} if the player is not holding the item
	 */
	public static ItemStack getItemStackFromAnyHand(PlayerEntity player, Item item) {
		if (player.inventory.getSelected().getItem() == item)
			return player.inventory.getSelected();

		if (player.inventory.offhand.get(0).getItem() == item)
			return player.inventory.offhand.get(0);

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
	 * Gets the component to use for displaying a block's owner. If team ownership is enabled and the given player is on a team,
	 * this will return the colored team name.
	 *
	 * @param owner The player who owns the block
	 * @return The component to display
	 */
	public static ITextComponent getOwnerComponent(Owner owner) {
		if (ConfigHandler.SERVER.enableTeamOwnership.get()) {
			TeamRepresentation teamRepresentation = TeamUtils.getTeamRepresentation(owner);

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
