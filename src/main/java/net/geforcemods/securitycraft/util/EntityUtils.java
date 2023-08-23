package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class EntityUtils {
	private EntityUtils() {}

	public static boolean doesEntityOwn(Entity entity, Level level, BlockPos pos) {
		if (entity instanceof Player player)
			return doesPlayerOwn(player, level, pos);
		else
			return false;
	}

	public static boolean doesPlayerOwn(Player player, Level level, BlockPos pos) {
		return level.getBlockEntity(pos) instanceof IOwnable ownable && ownable.isOwnedBy(player);
	}

	public static boolean isInvisible(LivingEntity entity) {
		return ConfigHandler.SERVER.respectInvisibility.get() && entity.hasEffect(MobEffects.INVISIBILITY);
	}
}