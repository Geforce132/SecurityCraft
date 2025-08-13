package net.geforcemods.securitycraft.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final Component INVENTORY_TEXT = Utils.localize("container.inventory");

	private Utils() {}

	public static TranslatableComponent getFormattedCoordinates(BlockPos pos) {
		return new TranslatableComponent("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static TranslatableComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof TranslatableComponent component)
				params[i] = localize(component.getKey(), component.getArgs());
			else if (params[i] instanceof BlockPos pos)
				params[i] = getFormattedCoordinates(pos);
		}

		return new TranslatableComponent(key, params);
	}

	public static String getLanguageKeyDenotation(Object obj) {
		if (obj instanceof BlockEntity be)
			return getLanguageKeyDenotation(be.getBlockState().getBlock());
		else if (obj instanceof Block block)
			return block.getDescriptionId().substring(6);
		else if (obj instanceof Entity entity)
			return entity.getType().toShortString();
		else if (obj instanceof BlockState state)
			return getLanguageKeyDenotation(state.getBlock());
		else
			return "";
	}

	public static boolean isInViewDistance(int centerX, int centerZ, int viewDistance, int x, int z) {
		int xDistance = Math.max(0, Math.abs(x - centerX) - 1);
		int zDistance = Math.max(0, Math.abs(z - centerZ) - 1);
		int reducedMaxDistance = Math.max(0, Math.max(xDistance, zDistance) - 1);
		int minDistance = Math.min(xDistance, zDistance);
		int squareDistance = reducedMaxDistance * reducedMaxDistance + minDistance * minDistance;
		int reducedViewDistance = viewDistance - 1;
		int squareViewDistance = reducedViewDistance * reducedViewDistance;

		return squareDistance <= squareViewDistance;
	}

	public static void updateBlockEntityWithItemTag(BlockEntity be, ItemStack stack) {
		CompoundTag tag = BlockItem.getBlockEntityData(stack);

		if (tag != null) {
			CompoundTag beData = be.saveWithoutMetadata();
			CompoundTag dataCopy = beData.copy();

			beData.merge(tag);

			if (!beData.equals(dataCopy)) {
				be.load(beData);
				be.setChanged();
			}
		}
	}
}
