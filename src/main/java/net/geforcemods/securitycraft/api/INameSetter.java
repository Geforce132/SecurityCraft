package net.geforcemods.securitycraft.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;

/**
 * Allows {@link net.minecraft.world.Nameable} block entities to set their name
 */
public interface INameSetter extends Nameable {
	/**
	 * Set the block entity's new name
	 *
	 * @param customName The new name
	 */
	public void setCustomName(Component customName);
}
