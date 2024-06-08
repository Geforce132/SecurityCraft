package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
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
}
