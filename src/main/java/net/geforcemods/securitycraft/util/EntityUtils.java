package net.geforcemods.securitycraft.util;

import java.util.Iterator;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityUtils{

	public static boolean doesMobHavePotionEffect(LivingEntity mob, Effect effect){
		Iterator<EffectInstance> effects = mob.getActivePotionEffects().iterator();

		while(effects.hasNext()){
			String eName = effects.next().getEffectName();

			if(eName.equals(effect.getName()))
				return true;
		}

		return false;
	}

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
}