package net.geforcemods.securitycraft.misc;

import net.minecraft.util.math.BlockPos;

/**
 * Simple wrapper class for LookingGlass camera views that SecurityCraft uses. Provides easy access to the view's coordinates
 * and a formatted string for storage in HashMaps, as well as a few helpful methods.
 *
 * @version 1.0.0
 * @author Geforce
 */
public class CameraView {
	private final BlockPos pos;
	private final int dimension;

	public CameraView(int x, int y, int z, int dim) {
		this(new BlockPos(x, y, z), dim);
	}

	public CameraView(BlockPos pos, int dim) {
		this.pos = pos;
		dimension = dim;
	}

	/**
	 * Checks to see if the given coordinates are the same as this view's coordinates.
	 *
	 * @param coordinates a String[] which contains the x, y, and z coordinates, and the dimension ID of the view
	 * @return true if the x, y, z and dimension match, false otherwise
	 */
	public boolean checkCoordinates(String[] coordinates) {
		int xPos = Integer.parseInt(coordinates[0]);
		int yPos = Integer.parseInt(coordinates[1]);
		int zPos = Integer.parseInt(coordinates[2]);
		int dim = (coordinates.length == 4 ? Integer.parseInt(coordinates[3]) : 0);

		return (pos.getX() == xPos && pos.getY() == yPos && pos.getZ() == zPos && getDimension() == dim);
	}

	/**
	 * @return A formatted string of this view's location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public String toNBTString() {
		return pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + getDimension();
	}

	public BlockPos getPos() {
		return pos;
	}

	public int getDimension() {
		return dimension;
	}
}
