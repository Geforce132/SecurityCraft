package net.geforcemods.securitycraft.util;

import net.minecraft.util.MathHelper;

/**
 * Because I was lazy
 *
 * @since 19. July 2018
 *
 * @author bl4ckscor3
 */
public class BlockPos
{
	private static final int NUM_X_BITS = 1 + MathHelper.calculateLogBaseTwo(MathHelper.roundUpToPowerOfTwo(30000000));
	private static final int NUM_Z_BITS = NUM_X_BITS;
	private static final int NUM_Y_BITS = 64 - NUM_X_BITS - NUM_Z_BITS;
	private static final int Y_SHIFT = 0 + NUM_Z_BITS;
	private static final int X_SHIFT = Y_SHIFT + NUM_Y_BITS;
	private static final long X_MASK = (1L << NUM_X_BITS) - 1L;
	private static final long Y_MASK = (1L << NUM_Y_BITS) - 1L;
	private static final long Z_MASK = (1L << NUM_Z_BITS) - 1L;
	private int x;
	private int y;
	private int z;

	public BlockPos(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public BlockPos north()
	{
		return north(1);
	}

	public BlockPos south()
	{
		return south(1);
	}

	public BlockPos east()
	{
		return east(1);
	}

	public BlockPos west()
	{
		return west(1);
	}

	public BlockPos up()
	{
		return up(1);
	}

	public BlockPos down()
	{
		return down(1);
	}

	public BlockPos north(int n)
	{
		return new BlockPos(x, y, z - n);
	}

	public BlockPos south(int n)
	{
		return new BlockPos(x, y, z + n);
	}

	public BlockPos east(int n)
	{
		return new BlockPos(x + n, y, z);
	}

	public BlockPos west(int n)
	{
		return new BlockPos(x - n, y, z);
	}

	public BlockPos up(int n)
	{
		return new BlockPos(x, y + n, z);
	}

	public BlockPos down(int n)
	{
		return new BlockPos(x, y - n, z);
	}

	public BlockPos offset(BetterFacing facing)
	{
		return offset(facing, 1);
	}

	public BlockPos offset(BetterFacing facing, int n)
	{
		return n == 0 ? this : new BlockPos(getX() + facing.getXOffset() * n, getY() + facing.getYOffset() * n, getZ() + facing.getZOffset() * n);
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getZ()
	{
		return z;
	}

	public int[] asArray()
	{
		return new int[] {x, y, z};
	}

	public BlockPos copy()
	{
		return new BlockPos(x, y, z);
	}

	public long toLong()
	{
		return (getX() & X_MASK) << X_SHIFT | (getY() & Y_MASK) << Y_SHIFT | (getZ() & Z_MASK) << 0;
	}

	@Override
	public boolean equals(Object obj)
	{
		if(obj instanceof BlockPos)
		{
			BlockPos pos = (BlockPos)obj;

			return x == pos.x && y == pos.y && z == pos.z;
		}
		else return false;
	}

	public static BlockPos fromLong(long serialized)
	{
		int i = (int)(serialized << 64 - X_SHIFT - NUM_X_BITS >> 64 - NUM_X_BITS);
		int j = (int)(serialized << 64 - Y_SHIFT - NUM_Y_BITS >> 64 - NUM_Y_BITS);
		int k = (int)(serialized << 64 - NUM_Z_BITS >> 64 - NUM_Z_BITS);
		return new BlockPos(i, j, k);
	}
}
