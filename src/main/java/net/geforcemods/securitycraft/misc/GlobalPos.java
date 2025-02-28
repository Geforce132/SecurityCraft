package net.geforcemods.securitycraft.misc;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public class GlobalPos {
	private final BlockPos pos;
	private final int dimension;

	private GlobalPos(BlockPos pos, int dim) {
		this.pos = pos;
		dimension = dim;
	}

	public static GlobalPos of(int x, int y, int z, int dim) {
		return new GlobalPos(new BlockPos(x, y, z), dim);
	}

	public static GlobalPos of(int dim, BlockPos pos) {
		return new GlobalPos(pos, dim);
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

		return (pos.getX() == xPos && pos.getY() == yPos && pos.getZ() == zPos && dimension() == dim);
	}

	/**
	 * @return A formatted string of this view's location. Format: "*X* *Y* *Z* *dimension ID*"
	 */
	public String toNBTString() {
		return pos.getX() + " " + pos.getY() + " " + pos.getZ() + " " + dimension();
	}

	public BlockPos pos() {
		return pos;
	}

	public int dimension() {
		return dimension;
	}

	public NBTTagCompound save() {
		NBTTagCompound tag = new NBTTagCompound();

		tag.setLong("pos", pos.toLong());
		tag.setInteger("dimension", dimension);
		return tag;
	}

	public static GlobalPos load(NBTTagCompound tag) {
		return new GlobalPos(BlockPos.fromLong(tag.getLong("pos")), tag.getInteger("dimension"));
	}
}
