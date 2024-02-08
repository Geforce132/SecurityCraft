package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.ConfigHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class Utils {
	public static final Style GRAY_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
	public static final Component INVENTORY_TEXT = Utils.localize("container.inventory");

	private Utils() {}

	public static Component getFormattedCoordinates(BlockPos pos) {
		return Component.translatable("messages.securitycraft:formattedCoordinates", pos.getX(), pos.getY(), pos.getZ());
	}

	/**
	 * Localizes a String with the given format
	 *
	 * @param key The string to localize (aka the identifier in the .lang file)
	 * @param params The parameters to insert into the String ala String.format
	 * @return The localized String
	 */
	public static MutableComponent localize(String key, Object... params) {
		for (int i = 0; i < params.length; i++) {
			if (params[i] instanceof Component component && component.getContents() instanceof TranslatableContents translatableContents)
				params[i] = localize(translatableContents.getKey(), translatableContents.getArgs());
			else if (params[i] instanceof BlockPos pos)
				params[i] = getFormattedCoordinates(pos);
		}

		return Component.translatable(key, params);
	}

	public static ResourceLocation getRegistryName(Block block) {
		return ForgeRegistries.BLOCKS.getKey(block);
	}

	public static ResourceLocation getRegistryName(EntityType<?> entityType) {
		return ForgeRegistries.ENTITY_TYPES.getKey(entityType);
	}

	public static ResourceLocation getRegistryName(Item item) {
		return ForgeRegistries.ITEMS.getKey(item);
	}

	public static ResourceLocation getRegistryName(Potion potion) {
		return ForgeRegistries.POTIONS.getKey(potion);
	}

	public static boolean isEntityInvisible(LivingEntity entity) {
		return ConfigHandler.SERVER.respectInvisibility.get() && entity.hasEffect(MobEffects.INVISIBILITY);
	}
}
