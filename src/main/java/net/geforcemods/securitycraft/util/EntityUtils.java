package net.geforcemods.securitycraft.util;

import java.util.Iterator;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils{

	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator<?> effects = mob.getActivePotionEffects().iterator();

		while(effects.hasNext()){
			PotionEffect effect = (PotionEffect) effects.next();
			String eName = effect.getEffectName();

			if(eName.equals(potion.getName()))
				return true;
		}

		return false;
	}

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
}