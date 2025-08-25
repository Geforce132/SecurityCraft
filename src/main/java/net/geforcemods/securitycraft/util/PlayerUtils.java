package net.geforcemods.securitycraft.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.authlib.GameProfile;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.entity.camera.SecurityCamera;
import net.geforcemods.securitycraft.network.ClientProxy;
import net.geforcemods.securitycraft.util.TeamUtils.TeamRepresentation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class PlayerUtils {
	private PlayerUtils() {}

	/**
	 * Gets the EntityPlayer instance of a player (if they're online) using their name. <p> Args: playerName.
	 */
	public static <T extends EntityPlayer> T getPlayerFromName(String name) {
		List<T> players = null;

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
			players = (List<T>) Minecraft.getMinecraft().world.playerEntities;
		else
			players = (List<T>) FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();

		if (players != null) {
			for (T player : players) {
				if (player.getName().equals(name))
					return player;
			}
		}

		return null;
	}

	/**
	 * Returns true if a player with the given name is in the world. Args: playerName.
	 */
	public static boolean isPlayerOnline(String name) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			for (int i = 0; i < Minecraft.getMinecraft().world.playerEntities.size(); i++) {
				EntityPlayer player = Minecraft.getMinecraft().world.playerEntities.get(i);

				if (player != null && player.getName().equals(name))
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

	public static void sendMessageToPlayer(EntityPlayer player, ITextComponent prefix, ITextComponent text, TextFormatting color, boolean shouldSendFromClient) {
		if (player.world.isRemote == shouldSendFromClient) {
			//@formatter:off
			player.sendMessage(new TextComponentString("[")
					.appendSibling(prefix.setStyle(new Style().setColor(color)))
					.appendSibling(new TextComponentString(TextFormatting.WHITE + "] "))
					.appendSibling(text));
			//@formatter:on
		}
	}

	/**
	 * Returns the ItemStack of the given item if the player is currently holding it (both hands are checked).
	 *
	 * @param player The player to check
	 * @param item The item type that should be searched for
	 * @return The ItemStack whose item matches the given item, {@link ItemStack#EMPTY} if the player is not holding the item
	 */
	public static ItemStack getItemStackFromAnyHand(EntityPlayer player, Item item) {
		return getItemStackFromAnyHand(player, heldItem -> heldItem == item);
	}

	/**
	 * Returns the ItemStack of the item matching the given predicate if the player is currently holding it (both hands are
	 * checked).
	 *
	 * @param player The player to check
	 * @param item The predicate to match against
	 * @return The ItemStack whose item matches the predicate, {@link ItemStack#EMPTY} if none match
	 */
	public static ItemStack getItemStackFromAnyHand(EntityPlayer player, Predicate<Item> itemCheck) {
		if (itemCheck.test(player.inventory.getCurrentItem().getItem()))
			return player.inventory.getCurrentItem();

		if (itemCheck.test(player.inventory.offHandInventory.get(0).getItem()))
			return player.inventory.offHandInventory.get(0);

		return ItemStack.EMPTY;
	}

	/**
	 * Is the entity mounted on to a security camera? Args: entity.
	 */
	public static boolean isPlayerMountedOnCamera(EntityLivingBase entity) {
		if (!(entity instanceof EntityPlayer))
			return false;

		EntityPlayer player = (EntityPlayer) entity;

		if (player.world.isRemote)
			return ClientProxy.isPlayerMountedOnCamera();
		else if (player instanceof EntityPlayerMP)
			return ((EntityPlayerMP) player).getSpectatingEntity() instanceof SecurityCamera;
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
	public static ITextComponent getOwnerComponent(Owner owner) {
		TeamRepresentation teamRepresentation = TeamUtils.getTeamRepresentation(owner);

		if (teamRepresentation != null)
			return Utils.localize("messages.securitycraft:teamOwner", new TextComponentString(teamRepresentation.name()).setStyle(new Style().setColor(teamRepresentation.color()))).setStyle(new Style().setColor(TextFormatting.GRAY));

		return new TextComponentString(owner.getName());
	}

	public static Owner getOwnerFromPlayerOrMask(EntityPlayer player) {
		ItemStack headItem = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (headItem.getItem().equals(SCContent.incognitoMask))
			return new Owner(getNameFromMask(headItem), "ownerUUID");

		return new Owner(player);
	}

	public static String getNameFromPlayerOrMask(EntityPlayer player) {
		ItemStack headItem = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (headItem.getItem().equals(SCContent.incognitoMask))
			return getNameFromMask(headItem);

		return player.getName();
	}

	private static String getNameFromMask(ItemStack mask) {
		return mask.hasDisplayName() ? mask.getDisplayName() : "owner";
	}

	/**
	 * Retrieves the name of the player head the given player may be wearing
	 *
	 * @param player The player to check
	 * @return The name of the skull owner, null if the player is not wearing a player head or the skull owner is faulty
	 */
	public static Owner getSkullOwner(EntityPlayer player) {
		ItemStack stack = player.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

		if (stack.getItem() == Items.SKULL && stack.hasTagCompound()) {
			NBTTagCompound stackTag = stack.getTagCompound();

			if (stackTag.hasKey("SkullOwner", Constants.NBT.TAG_STRING))
				return new Owner(stackTag.getString("SkullOwner"), "ownerUUID");
			else if (stackTag.hasKey("SkullOwner", Constants.NBT.TAG_COMPOUND)) {
				GameProfile profile = NBTUtil.readGameProfileFromNBT(stackTag.getCompoundTag("SkullOwner"));
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

	/**
	 * Gets a list containing the player corresponding to an Owner
	 *
	 * @param owner The owner to get the player of
	 * @return A list containing the player corresponding to the given owner, an empty list if no such player exists
	 */
	public static Collection<EntityPlayerMP> getPlayerListFromOwner(Owner owner) {
		EntityPlayerMP player = getPlayerFromName(owner.getName());

		if (player != null)
			return Arrays.asList(player);

		return new ArrayList<>();
	}

	/**
	 * @see #checkAndReportOwnership(net.minecraft.tileentity.TileEntity, net.minecraft.entity.player.EntityPlayer, net.minecraft.item.Item, String)
	 */
	public static boolean checkAndReportOwnership(TileEntity te, EntityPlayer player, Item item) {
		return checkAndReportOwnership(te, player, item, "messages.securitycraft:notOwned");
	}

	/**
	 * Sends the given player a chat message if they do not own the block. If such a block is disguised as a non-disguise able block, no message will be sent.
	 *
	 * @param te The {@link net.minecraft.tileentity.TileEntity} to check
	 * @param player The {@link net.minecraft.entity.player.EntityPlayer} player to check
	 * @param item The {@link net.minecraft.item.Item} to use for the feedback message prefix
	 * @param key The translation key of the message to send
	 * @return Whether feedback was sent to the player
	 */
	public static boolean checkAndReportOwnership(TileEntity te, EntityPlayer player, Item item, String key) {
		Block block = te.getBlockType();

		if (block instanceof IDisguisable) {
			IBlockState disguisedState = ((IDisguisable) block).getDisguisedBlockState(te);

			if (disguisedState != null && !(disguisedState.getBlock() instanceof IDisguisable))
				return false;
		}

		sendMessageToPlayer(player, Utils.localize(item), Utils.localize(key, PlayerUtils.getOwnerComponent(((IOwnable) te).getOwner())), TextFormatting.RED);
		return true;
	}
}
