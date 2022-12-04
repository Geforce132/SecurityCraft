package net.geforcemods.securitycraft.api;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.network.client.OpenScreen;
import net.geforcemods.securitycraft.network.client.OpenScreen.DataType;
import net.geforcemods.securitycraft.screen.CheckPasswordScreen;
import net.geforcemods.securitycraft.screen.SetPasswordScreen;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;

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
	public default void openPasswordGUI(World level, BlockPos pos, IOwnable ownable, PlayerEntity player) {
		if (!level.isClientSide) {
			if (getPassword() != null)
				SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.CHECK_PASSWORD, pos));
			else {
				if (ownable.isOwnedBy(player))
					SecurityCraft.channel.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), new OpenScreen(DataType.SET_PASSWORD, pos));
				else
					PlayerUtils.sendMessageToPlayer(player, new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
			}
		}
	}

	@Override
	public default void useCodebreaker(BlockState state, PlayerEntity player) {
		activate(player);
	}

	/**
	 * Called whenever a player correctly enters this block's password in the password GUI.<p>
	 *
	 * @param player The player who entered the password.
	 */
	public void activate(PlayerEntity player);

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
