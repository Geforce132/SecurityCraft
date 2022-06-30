package net.geforcemods.securitycraft.api;

public interface IEMPAffected {
	public default void shutDown() {
		setShutDown(true);
	}

	public default void reactivate() {
		setShutDown(false);
	}

	public boolean isShutDown();

	public void setShutDown(boolean shutDown);
}