package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

/**
 * This interface marks a {@link TileEntity} as "ownable". Any TileEntity that implements this interface is able to be
 * destroyed by the Universal Block Remover, and can only be broken or modified by the person who placed it down. <p> Use
 * this to set the owner, preferably in Block.onBlockPlacedBy():
 *
 * <pre>
 * ((IOwnable) world.getTileEntity(x, y, z)).setOwner(player.getGameProfile().getId().toString(), player.getCommandSenderName());
 * </pre>
 *
 * @author Geforce
 */
public interface IOwnable {
	/**
	 * @return An Owner object containing the player's name and UUID. You are responsible for reading and writing the name
	 *         and UUID variables to your TileEntity's NBTTagCompound in writeToNBT() and readFromNBT().
	 */
	public Owner getOwner();

	/**
	 * Save a new owner to your Owner object here. <p> The easiest way is to use Owner.set(UUID, name), this method is here
	 * mainly for convenience.
	 *
	 * @param uuid The UUID of the new player.
	 * @param name The name of the new player.
	 */
	public void setOwner(String uuid, String name);

	/**
	 * @return true if the owner of this IOwnable should be invalidated when changed by the Universal Owner Changer
	 */
	default boolean needsValidation() {
		return false;
	}

	/**
	 * Executes after the owner has been changed and invalidates this if it needs validation
	 *
	 * @param world The current world
	 * @param state The IOwnable's state
	 * @param pos The IOwnable's position
	 * @param player The player that changed the owner of the IOwnable
	 */
	default void onOwnerChanged(IBlockState state, World world, BlockPos pos, EntityPlayer player) {
		if (needsValidation()) {
			getOwner().setValidated(false);
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.universalOwnerChanger.getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:universalOwnerChanger.ownerInvalidated"), TextFormatting.GREEN);
		}
	}
}
