package net.geforcemods.securitycraft.util;

/**
 * Because I was lazy
 *
 * @since 19. July 2018
 *
 * @author bl4ckscor3
 */
public class BlockPos
{
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

	public int[] asArray()
	{
		return new int[] {x, y, z};
	}
}
