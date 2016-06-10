package net.geforcemods.securitycraft.misc;

public class CameraView {
	
	public int x = 0;
	public int y = 0;
	public int z = 0;
	public int dimension = 0;
	
	public CameraView(int x, int y, int z, int dim) {
		this.x = x;
		this.y = y;
		this.z = z;
		dimension = dim;
	}
	
	public void setLocation(int newX, int newY, int newZ, int newDim) {
		x = newX;
		y = newY;
		z = newZ;
		dimension = newDim;
	}
	
	public String toNBTString() {
		return x + " " + y + " " + z + " " + dimension;
	}

}
