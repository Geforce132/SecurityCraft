package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;

/**
 * Marks an object as being able to be hacked with the Codebreaker.
 *
 * @author Geforce
 */
public interface ICodebreakable {
	/**
	 * Checked before any codebreaking attempt, whether the codebreaker should attempt to break the code. Useful when this
	 * currently does not accept a code at all.
	 *
	 * @param player The player trying the codebreaking attempt
	 * @return true if the codebreaking attempt should be performed, false otherwise
	 */
	public boolean shouldAttemptCodebreak(EntityPlayer player);

	/**
	 * Called when a Codebreaker has successfully broken the code
	 *
	 * @param player The player who used the Codebreaker.
	 */
	public void useCodebreaker(EntityPlayer player);

	/**
	 * Handles the actual breaking of the code alongside any player feedback.
	 *
	 * @param player The player trying the codebreaking attempt
	 * @param hand The hand holding the codebreaker
	 * @return true if the codebreaking attempt was successful, false otherwise
	 */
	public default boolean handleCodebreaking(EntityPlayer player, EnumHand hand) {
		double chance = ConfigHandler.codebreakerChance;

		if (chance < 0.0D)
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
		else {
			ItemStack codebreaker = player.getHeldItem(hand);

			if (!shouldAttemptCodebreak(player))
				return true;

			if (codebreaker.getItem() == SCContent.codebreaker) {
				boolean canBypass = player.isCreative() || player.isSpectator();

				if (this instanceof IOwnable && ((IOwnable) this).isOwnedBy(player) && !canBypass) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.owned"), TextFormatting.RED);
					return false;
				}

				if (!codebreaker.hasTagCompound())
					codebreaker.setTagCompound(new NBTTagCompound());

				if (!canBypass && CodebreakerItem.wasRecentlyUsed(codebreaker))
					return false;

				boolean isSuccessful = canBypass || SecurityCraft.RANDOM.nextDouble() < chance;
				NBTTagCompound tag = codebreaker.getTagCompound();

				if (!canBypass)
					codebreaker.damageItem(1, player);

				tag.setLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
				tag.setBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

				if (isSuccessful)
					useCodebreaker(player);
				else
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.codebreaker), Utils.localize("messages.securitycraft:codebreaker.failed"), TextFormatting.RED);
			}
		}

		return true;
	}
}
