package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.misc.CustomDamageSources;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
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
 * Implementing this interface designates a block entity as being password-protected. Implementing this allows you to use
 * {@link SetPasswordScreen} and {@link CheckPasswordScreen} to easily set your block's password. Extends
 * {@link ICodebreakable} as most password-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasswordProtected extends ICodebreakable {
	/**
	 * Open the correct password GUI depending on if a password is already set or not. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasswordGUI(Level level, BlockPos pos, IOwnable ownable, Player player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSWORD, pos));
			else {
				if (ownable.isOwnedBy(player))
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SET_PASSWORD, pos));
				else
					PlayerUtils.sendMessageToPlayer(player, Component.literal("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
			}
		}
	}

	@Override
	public default void useCodebreaker(BlockState state, Player player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's password in the password GUI.<p>
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(Player player);

	/**
	 * Return your block entity's password variable here. If the password is empty or not set yet, return null.
	 *
	 * @return The password.
	 */
	public String getPassword();

	/**
	 * Save newly created passwords to your block entity here.
	 *
	 * @param password The new password to be saved.
	 */
	public void setPassword(String password);

	/**
	 * Sets this block to be on cooldown and starts the cooldown
	 */
	public default void startCooldown() {} //TODO: only default temporarily, remove when done

	/**
	 * Checks whether this block is on cooldown, meaning a new code cannot be entered.
	 *
	 * @return true if this block is on cooldown, false otherwise
	 */
	public default boolean isOnCooldown() { //TODO: only default temporarily, remove when done
		return false;
	}

	/**
	 * Returns the time at which the cooldown ends
	 *
	 * @return A UNIX timestamp representing the cooldown's end time
	 */
	public default long getCooldownEnd() { //TODO: only default temporarily, remove when done
		return 0;
	}

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

			if (moduleInv.isModuleEnabled(ModuleType.HARMING))
				player.hurt(CustomDamageSources.INCORRECT_PASSCODE, ConfigHandler.SERVER.incorrectPasscodeDamage.get());
		}
	}
}
