package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.screen.CheckPasscodeScreen;
import net.geforcemods.securitycraft.screen.SetPasscodeScreen;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.PacketDistributor;

/**
 * Implementing this interface designates a block entity as being passcode-protected. Implementing this allows you to use
 * {@link SetPasscodeScreen} and {@link CheckPasscodeScreen} to easily set your block's passcode. Extends
 * {@link ICodebreakable} as most passcode-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasscodeProtected extends ICodebreakable {
	/**
	 * Open the check passcode GUI if a passcode is set. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasscodeGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			if (getPasscode() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSCODE, pos));
		}
	}

	/**
	 * Check that a passcode has been set, and if not, opens the set passcode screen or sends a warning message. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who interacted with this block entity
	 * @return true if a passcode has been set, false otherwise
	 */
	default boolean verifyPasscodeSet(Level level, BlockPos pos, IOwnable ownable, Player player) {
		if (!level.isClientSide) {
			if (getPasscode() != null)
				return true;

			if (ownable.isOwnedBy(player))
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SET_PASSCODE, pos));
			else
				PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), ChatFormatting.DARK_RED);
		}

		return false;
	}

	@Override
	default boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (getPasscode() == null) {
			PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passcodeProtected.notSetUp"), ChatFormatting.DARK_RED);
			return false;
		}

		return true;
	}

	@Override
	public default void useCodebreaker(BlockState state, Player player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's passcode in the passcode GUI.<p>
	 *
	 * @param player The player who entered the passcode.
	 */
	public void activate(Player player);

	/**
	 * Return your block entity's passcode variable here. If the passcode is empty or not set yet, return null.
	 *
	 * @return The passcode.
	 */
	public String getPasscode();

	/**
	 * Save newly created passcodes to your block entity here.
	 *
	 * @param passcode The new passcode to be saved.
	 */
	public void setPasscode(String passcode);

	/**
	 * Sets this block to be on cooldown and starts the cooldown
	 */
	public void startCooldown();

	/**
	 * Checks whether this block is on cooldown, meaning a new code cannot be entered.
	 *
	 * @return true if this block is on cooldown, false otherwise
	 */
	public boolean isOnCooldown();

	/**
	 * Returns the time at which the cooldown ends
	 *
	 * @return A UNIX timestamp representing the cooldown's end time
	 */
	public long getCooldownEnd();

	/**
	 * Gets called when an incorrect passcode has been inserted.
	 *
	 * @param player The player who entered the incorrect code
	 * @param incorrectCode The incorrect code that was entered
	 */
	public default void onIncorrectPasscodeEntered(Player player, String incorrectCode) {
		if (this instanceof IModuleInventory moduleInv) {
			if (moduleInv.isModuleEnabled(ModuleType.SMART))
				startCooldown();

			if (moduleInv.isModuleEnabled(ModuleType.HARMING)) {
				if (player.hurt(CustomDamageSources.incorrectPasscode(player.level.registryAccess()), ConfigHandler.SERVER.incorrectPasscodeDamage.get()))
					player.closeContainer();
			}
		}
	}
}
