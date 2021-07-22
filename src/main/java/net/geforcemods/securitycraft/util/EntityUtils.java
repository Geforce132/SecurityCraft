package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class EntityUtils{

	public static boolean doesEntityOwn(Entity entity, Level world, BlockPos pos)
	{
		if(entity instanceof Player)
			return doesPlayerOwn((Player)entity, world, pos);
		else return false;
	}

	public static boolean doesPlayerOwn(Player player, Level world, BlockPos pos)
	{
		BlockEntity te = world.getBlockEntity(pos);

		return te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player);
	}

	public static boolean isInvisible(LivingEntity entity)
	{
		return ConfigHandler.SERVER.respectInvisibility.get() && entity.hasEffect(MobEffects.INVISIBILITY);
	}
}