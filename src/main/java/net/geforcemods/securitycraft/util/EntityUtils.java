package net.geforcemods.securitycraft.util;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils{

	public static boolean doesEntityOwn(Entity entity, World world, BlockPos pos)
	{
		if(entity instanceof EntityPlayer)
			return doesPlayerOwn((EntityPlayer)entity, world, pos);
		else return false;
	}

	public static boolean doesPlayerOwn(EntityPlayer player, World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);

		return te instanceof IOwnable && ((IOwnable)te).getOwner().isOwner(player);
	}

	public static boolean isInvisible(EntityLivingBase entity)
	{
		return ConfigHandler.respectInvisibility && entity.isPotionActive(MobEffects.INVISIBILITY);
	}
}