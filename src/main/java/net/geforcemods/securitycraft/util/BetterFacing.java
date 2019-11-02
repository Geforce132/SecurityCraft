package net.geforcemods.securitycraft.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import net.minecraft.util.MathHelper;

public enum BetterFacing
{
	DOWN(0, 1, -1, "down", BetterFacing.AxisDirection.NEGATIVE, BetterFacing.Axis.Y),
	UP(1, 0, -1, "up", BetterFacing.AxisDirection.POSITIVE, BetterFacing.Axis.Y),
	NORTH(2, 3, 2, "north", BetterFacing.AxisDirection.NEGATIVE, BetterFacing.Axis.Z),
	SOUTH(3, 2, 0, "south", BetterFacing.AxisDirection.POSITIVE, BetterFacing.Axis.Z),
	WEST(4, 5, 1, "west", BetterFacing.AxisDirection.NEGATIVE, BetterFacing.Axis.X),
	EAST(5, 4, 3, "east", BetterFacing.AxisDirection.POSITIVE, BetterFacing.Axis.X);

	private final int index;
	private final int opposite;
	private final int horizontalIndex;
	private final String name;
	private final BetterFacing.Axis axis;
	private final BetterFacing.AxisDirection axisDirection;
	public static final BetterFacing[] VALUES = new BetterFacing[6];
	public static final BetterFacing[] HORIZONTALS = new BetterFacing[4];
	private static final Map<String, BetterFacing> NAME_LOOKUP = Maps.<String, BetterFacing>newHashMap();

	private BetterFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, BetterFacing.AxisDirection axisDirectionIn, BetterFacing.Axis axisIn)
	{
		this.index = indexIn;
		this.horizontalIndex = horizontalIndexIn;
		this.opposite = oppositeIn;
		this.name = nameIn;
		this.axis = axisIn;
		this.axisDirection = axisDirectionIn;
	}

	public int getIndex()
	{
		return this.index;
	}

	public int getHorizontalIndex()
	{
		return this.horizontalIndex;
	}

	public BetterFacing.AxisDirection getAxisDirection()
	{
		return this.axisDirection;
	}

	public BetterFacing getOpposite()
	{
		return byIndex(this.opposite);
	}

	public BetterFacing rotateY()
	{
		switch (this)
		{
			case NORTH:
				return EAST;
			case EAST:
				return SOUTH;
			case SOUTH:
				return WEST;
			case WEST:
				return NORTH;
			default:
				throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
		}
	}

	public BetterFacing rotateYCCW()
	{
		switch (this)
		{
			case NORTH:
				return WEST;
			case EAST:
				return NORTH;
			case SOUTH:
				return EAST;
			case WEST:
				return SOUTH;
			default:
				throw new IllegalStateException("Unable to get CCW facing of " + this);
		}
	}

	public int getXOffset()
	{
		return this.axis == BetterFacing.Axis.X ? this.axisDirection.getOffset() : 0;
	}

	public int getYOffset()
	{
		return this.axis == BetterFacing.Axis.Y ? this.axisDirection.getOffset() : 0;
	}

	public int getZOffset()
	{
		return this.axis == BetterFacing.Axis.Z ? this.axisDirection.getOffset() : 0;
	}

	public BetterFacing.Axis getAxis()
	{
		return this.axis;
	}

	public static BetterFacing byIndex(int index)
	{
		return VALUES[MathHelper.abs_int(index % VALUES.length)];
	}

	public float getHorizontalAngle()
	{
		return (this.horizontalIndex & 3) * 90;
	}

	@Override
	public String toString()
	{
		return this.name;
	}

	public static BetterFacing getFacingFromAxis(BetterFacing.AxisDirection axisDirectionIn, BetterFacing.Axis axisIn)
	{
		for (BetterFacing BetterFacing : values())
		{
			if (BetterFacing.getAxisDirection() == axisDirectionIn && BetterFacing.getAxis() == axisIn)
			{
				return BetterFacing;
			}
		}

		throw new IllegalArgumentException("No such direction: " + axisDirectionIn + " " + axisIn);
	}

	static
	{
		for (BetterFacing facing : values())
		{
			VALUES[facing.index] = facing;

			if (facing.getAxis().isHorizontal())
			{
				HORIZONTALS[facing.horizontalIndex] = facing;
			}

			NAME_LOOKUP.put(facing.toString().toLowerCase(Locale.ROOT), facing);
		}
	}

	public static enum Axis implements Predicate<BetterFacing>
	{
		X("x", BetterFacing.Plane.HORIZONTAL),
		Y("y", BetterFacing.Plane.VERTICAL),
		Z("z", BetterFacing.Plane.HORIZONTAL);

		private static final Map<String, BetterFacing.Axis> NAME_LOOKUP = Maps.<String, BetterFacing.Axis>newHashMap();
		private final String name;
		private final BetterFacing.Plane plane;

		private Axis(String name, BetterFacing.Plane plane)
		{
			this.name = name;
			this.plane = plane;
		}

		/**
		 * Get the axis specified by the given name
		 */
		@Nullable
		public static BetterFacing.Axis byName(String name)
		{
			return name == null ? null : (BetterFacing.Axis)NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
		}

		/**
		 * Like getName but doesn't override the method from Enum.
		 */
		public String getName2()
		{
			return this.name;
		}

		/**
		 * If this Axis is on the vertical plane (Only true for Y)
		 */
		public boolean isVertical()
		{
			return this.plane == BetterFacing.Plane.VERTICAL;
		}

		/**
		 * If this Axis is on the horizontal plane (true for X and Z)
		 */
		public boolean isHorizontal()
		{
			return this.plane == BetterFacing.Plane.HORIZONTAL;
		}

		@Override
		public String toString()
		{
			return this.name;
		}

		@Override
		public boolean apply(@Nullable BetterFacing p_apply_1_)
		{
			return p_apply_1_ != null && p_apply_1_.getAxis() == this;
		}

		/**
		 * Get this Axis' Plane (VERTICAL for Y, HORIZONTAL for X and Z)
		 */
		public BetterFacing.Plane getPlane()
		{
			return this.plane;
		}

		public String getName()
		{
			return this.name;
		}

		static
		{
			for (BetterFacing.Axis BetterFacing$axis : values())
			{
				NAME_LOOKUP.put(BetterFacing$axis.getName2().toLowerCase(Locale.ROOT), BetterFacing$axis);
			}
		}
	}

	public static enum AxisDirection
	{
		POSITIVE(1, "Towards positive"),
		NEGATIVE(-1, "Towards negative");

		private final int offset;
		private final String description;

		private AxisDirection(int offset, String description)
		{
			this.offset = offset;
			this.description = description;
		}

		public int getOffset()
		{
			return this.offset;
		}

		@Override
		public String toString()
		{
			return this.description;
		}
	}

	public static enum Plane implements Predicate<BetterFacing>, Iterable<BetterFacing>
	{
		HORIZONTAL,
		VERTICAL;

		public BetterFacing[] facings()
		{
			switch (this)
			{
				case HORIZONTAL:
					return new BetterFacing[] {BetterFacing.NORTH, BetterFacing.EAST, BetterFacing.SOUTH, BetterFacing.WEST};
				case VERTICAL:
					return new BetterFacing[] {BetterFacing.UP, BetterFacing.DOWN};
				default:
					throw new Error("Someone's been tampering with the universe!");
			}
		}

		public BetterFacing random(Random rand)
		{
			BetterFacing[] aBetterFacing = this.facings();
			return aBetterFacing[rand.nextInt(aBetterFacing.length)];
		}

		@Override
		public boolean apply(@Nullable BetterFacing p_apply_1_)
		{
			return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
		}

		@Override
		public Iterator<BetterFacing> iterator()
		{
			return Iterators.<BetterFacing>forArray(this.facings());
		}
	}
}