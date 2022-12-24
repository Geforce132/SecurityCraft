package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.network.PacketDistributor;

/**
 * Implementing this interface designates a block entity as being password-protected. Implementing this allows you to use
 * {@link SetPasswordScreen} and {@link CheckPasswordScreen} to easily set your block's password. Extends
 * {@link ICodebreakable} as most password-protected blocks are likely able to be hacked using the Codebreaker by default.
 *
 * @author Geforce
 */
public interface IPasswordProtected extends ICodebreakable {
	/**
	 * Open the check password GUI if a password is set. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param player The player who the GUI should be opened to.
	 */
	public default void openPasswordGUI(Level level, BlockPos pos, Player player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.CHECK_PASSWORD, pos));
		}
	}

	/**
	 * Check that a password has been set, and if not, opens the set password screen or sends a warning message. <p>
	 *
	 * @param level The level of this block entity
	 * @param pos The position of this block entity
	 * @param ownable This block entity
	 * @param player The player who interacted with this block entity
	 * @return true if a password has been set, false otherwise
	 */
	default boolean verifyPasswordSet(Level level, BlockPos pos, IOwnable ownable, Player player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				return true;

			if (ownable.isOwnedBy(player))
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OpenScreen(DataType.SET_PASSWORD, pos));
			else
				PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
		}

		return false;
	}

	@Override
	default boolean shouldAttemptCodebreak(BlockState state, Player player) {
		if (getPassword() == null) {
			PlayerUtils.sendMessageToPlayer(player, new TextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), ChatFormatting.DARK_RED);
			return false;
		}

		return true;
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
}
