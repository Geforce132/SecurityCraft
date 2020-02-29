package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils{

	public static boolean doesEntityOwn(Entity entity, World world, BlockPos pos)
	{
		if(entity instanceof PlayerEntity)
			return doesPlayerOwn((PlayerEntity)entity, world, pos);
		else return false;
	}

	public static boolean doesPlayerOwn(PlayerEntity player, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player);
	}

	public static boolean isInvisible(LivingEntity entity)
	{
		return ConfigHandler.CONFIG.respectInvisibility.get() && entity.isPotionActive(Effects.INVISIBILITY);
	}
}