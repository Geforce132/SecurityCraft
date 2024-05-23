package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.items.CodebreakerItem;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
	public boolean shouldAttemptCodebreak(Player player);

	/**
	 * Called when a Codebreaker has successfully broken the code
	 *
	 * @param player The player who used the Codebreaker.
	 */
	public void useCodebreaker(Player player);

	/**
	 * Handles the actual breaking of the code alongside any player feedback.
	 *
	 * @param player The player trying the codebreaking attempt
	 * @param hand The hand holding the codebreaker
	 * @return true if the codebreaking attempt was successful, false otherwise
	 */
	public default boolean handleCodebreaking(Player player, InteractionHand hand) {
		double chance = ConfigHandler.SERVER.codebreakerChance.get();

		if (chance < 0.0D)
			PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreakerDisabled"), ChatFormatting.RED);
		else {
			ItemStack codebreaker = player.getItemInHand(hand);

			if (!shouldAttemptCodebreak(player))
				return true;

			if (codebreaker.is(SCContent.CODEBREAKER.get())) {
				if (this instanceof IOwnable ownable && ownable.isOwnedBy(player) && !player.isCreative()) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.owned"), ChatFormatting.RED);
					return false;
				}

				if (CodebreakerItem.wasRecentlyUsed(codebreaker))
					return false;

				boolean isSuccessful = player.isCreative() || SecurityCraft.RANDOM.nextDouble() < chance;
				CompoundTag tag = codebreaker.getOrCreateTag();

				codebreaker.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));
				tag.putLong(CodebreakerItem.LAST_USED_TIME, System.currentTimeMillis());
				tag.putBoolean(CodebreakerItem.WAS_SUCCESSFUL, isSuccessful);

				if (isSuccessful)
					useCodebreaker(player);
				else
					PlayerUtils.sendMessageToPlayer(player, new TranslatableComponent(SCContent.CODEBREAKER.get().getDescriptionId()), Utils.localize("messages.securitycraft:codebreaker.failed"), ChatFormatting.RED);
			}
		}

		return true;
	}
}
