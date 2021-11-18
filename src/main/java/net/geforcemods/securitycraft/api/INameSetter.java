package net.geforcemods.securitycraft.api;

import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;

/**
 * Allows {@link net.minecraft.util.INameable} tile entities to set their name
 */
public interface INameSetter extends INameable {
	/**
	 * Set the tile entity's new name
	 *
	 * @param customName The new name
	 */
	public void setCustomName(ITextComponent customName);
}
