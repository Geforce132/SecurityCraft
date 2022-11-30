package net.geforcemods.securitycraft.api;

/**
 * Marks an object as being affected by an EMP. There are no EMPs in SecurityCraft by default, however this interface is
 * provided for mods to add support for their own EMPs.
 */
public interface IEMPAffected {
	/**
	 * Disables this object when it is affected by an EMP blast
	 */
	public default void shutDown() {
		setShutDown(true);
	}

	/**
	 * Reenables this object after it has been rightclicked with redstone
	 */
	public default void reactivate() {
		setShutDown(false);
	}

	/**
	 * Checks whether this object is disabled after being affected by an EMP
	 *
	 * @return true if this object is disabled, false otherwise
	 */
	public boolean isShutDown();

	/**
	 * Switches whether this object is disabled
	 *
	 * @param shutDown true if this object should be disabled, false otherwise
	 */
	public void setShutDown(boolean shutDown);
}