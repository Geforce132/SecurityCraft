package net.geforcemods.securitycraft.api;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IWorldNameable;

/**
 * Allows {@link net.minecraft.world.IWorldNameable} tile entities to set their name
 */
public interface INameSetter extends IWorldNameable {
	/**
	 * Set the tile entity's new name
	 *
	 * @param customName The new name
	 */
	public void setCustomName(String customName);

	/**
	 * Gets the tile entity's name used when there is no custom name set
	 *
	 * @return The tile entity's default name
	 */
	public ITextComponent getDefaultName();
}
