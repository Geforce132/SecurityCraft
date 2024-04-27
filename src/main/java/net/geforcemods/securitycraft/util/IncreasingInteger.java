package net.geforcemods.securitycraft.util;

/**
 * Meant to be used in conjunction with iteration and lambdas, to have access to an integer that increases each iteration
 */
public final class IncreasingInteger {
	private int count;

	public IncreasingInteger(int count) {
		this.count = count;
	}

	/**
	 * Gets the current integer and increases it by one
	 *
	 * @return the current integer
	 */
	public int get() {
		return count++;
	}
}