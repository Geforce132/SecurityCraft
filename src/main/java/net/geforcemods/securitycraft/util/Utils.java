package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(TextFormatting.GRAY);
	public static final ITextComponent INVENTORY_TEXT = Utils.localize("container.inventory");

	private Utils() {}

	public static TranslationTextComponent getFormattedCoordinates(BlockPos pos) {
		return new TranslationTextComponent("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslationTextComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof TranslationTextComponent)
				params[i] = localize(((TranslationTextComponent) params[i]).getKey(), ((TranslationTextComponent) params[i]).getArgs());
			else if (params[i] instanceof BlockPos)
				params[i] = getFormattedCoordinates((BlockPos) params[i]);
		}

		return new TranslationTextComponent(key, params);
	}

	public static boolean doesEntityOwn(Entity entity, World level, BlockPos pos) {
		TileEntity te = level.getBlockEntity(pos);

		return te instanceof IOwnable && ((IOwnable) te).isOwnedBy(entity);
	}

	public static Vector3d lerp(Vector3d from, Vector3d to, double delta) {
		return new Vector3d(MathHelper.lerp(delta, from.x, to.x), MathHelper.lerp(delta, from.y, to.y), MathHelper.lerp(delta, from.z, to.z));
	}

	public static String getLanguageKeyDenotation(Object obj) {
		if (obj instanceof TileEntity)
			return getLanguageKeyDenotation(((TileEntity) obj).getBlockState().getBlock());
		else if (obj instanceof Block)
			return ((Block) obj).getDescriptionId().substring(6);
		else if (obj instanceof Entity)
			return toShortString(((Entity) obj).getType().getDescriptionId());
		else if (obj instanceof BlockState)
			return getLanguageKeyDenotation(((BlockState) obj).getBlock());
		else
			return "";
	}

	private static String toShortString(String descriptionId) {
		int i = descriptionId.lastIndexOf(46);

		return i == -1 ? descriptionId : descriptionId.substring(i + 1);
	}
}
