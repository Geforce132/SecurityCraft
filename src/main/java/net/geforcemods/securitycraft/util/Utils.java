package net.geforcemods.securitycraft.util;

import java.util.UUID;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class Utils {
	public static final Style GRAY_STYLE = new Style().setColor(TextFormatting.GRAY);

	private Utils() {}

	public static TextComponentTranslation getFormattedCoordinates(BlockPos pos) {
		return new TextComponentTranslation("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes the translation key of a block with the given format
	 *
	 * @param block The block to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String à la String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(Block block, Object... params) {
		return localize(block.getTranslationKey() + ".name", params);
	}

	/**
	 * Localizes the translation key of an item with the given format
	 *
	 * @param item The item to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String à la String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(Item item, Object... params) {
		return localize(item.getTranslationKey() + ".name", params);
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TextComponentTranslation localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof BlockPos)
				params[i] = getFormattedCoordinates((BlockPos) params[i]);
			else if (params[i] instanceof Block)
				params[i] = new TextComponentTranslation(((Block) params[i]).getTranslationKey() + ".name");
		}

		return new TextComponentTranslation(key, params);
	}

	public static boolean doesEntityOwn(Entity entity, World world, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);

		return te instanceof IOwnable && ((IOwnable) te).isOwnedBy(entity);
	}

	/**
	 * Correctly schedules a task for execution on the main thread depending on if the provided world is client- or serverside
	 *
	 * @param world The world to schedule the task on
	 * @param runnable The task to schedule
	 */
	public static void addScheduledTask(World world, Runnable runnable) {
		if (world.isRemote)
			Minecraft.getMinecraft().addScheduledTask(runnable);
		else
			((WorldServer) world).addScheduledTask(runnable);
	}

	public static double lerp(double delta, double start, double end) {
		return start + (end - start) * delta;
	}

	public static float lerp(float delta, float start, float end) {
		return start + (end - start) * delta;
	}

	public static Vec3d lerp(Vec3d from, Vec3d to, double delta) {
		return new Vec3d(lerp(delta, from.x, to.x), lerp(delta, from.y, to.y), lerp(delta, from.z, to.z));
	}

	public static Vec3d atCenterOf(BlockPos pos) {
		return new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
	}

	public static String getLanguageKeyDenotation(Object obj) {
		if (obj instanceof TileEntity) {
			TileEntity te = (TileEntity) obj;

			return getLanguageKeyDenotation(te.getBlockType());
		}
		if (obj instanceof Block)
			return ((Block) obj).getTranslationKey().substring(5);
		else if (obj instanceof Entity) {
			ResourceLocation name = EntityList.getKey((Entity) obj);

			return name == null ? "" : name.toString();
		}
		else if (obj instanceof IBlockState)
			return getLanguageKeyDenotation(((IBlockState) obj).getBlock());
		else
			return "";
	}

	public static void setUUID(NBTTagCompound tag, String key, UUID uuid) {
		tag.setIntArray(key, uuidToIntArray(uuid));
	}

	public static UUID getUUID(NBTTagCompound tag, String key) {
		int[] array = tag.getIntArray(key);

		if (array.length != 4)
			throw new IllegalArgumentException("Expected UUID-Array to be of length 4, but found " + array.length + ".");
		else
			return uuidFromIntArray(array);
	}

	public static boolean isInViewDistance(int centerX, int centerZ, int viewDistance, int x, int z) {
		int xDistance = Math.max(0, Math.abs(x - centerX) - 1);
		int zDistance = Math.max(0, Math.abs(z - centerZ) - 1);
		int squareDistance = xDistance * xDistance + zDistance * zDistance;
		int squareViewDistance = viewDistance * viewDistance;

		return squareDistance < squareViewDistance;
	}

	private static UUID uuidFromIntArray(int[] array) {
		return new UUID((long) array[0] << 32 | array[1] & 4294967295L, (long) array[2] << 32 | array[3] & 4294967295L);
	}

	private static int[] uuidToIntArray(UUID uuid) {
		return leastMostToIntArray(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	private static int[] leastMostToIntArray(long mostSignificantBits, long leastSignificantBits) {
		return new int[] {
				(int) (mostSignificantBits >> 32), (int) mostSignificantBits, (int) (leastSignificantBits >> 32), (int) leastSignificantBits
		};
	}
}
