package net.geforcemods.securitycraft.util;

import java.util.Iterator;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;

public class EntityUtils{

	public static boolean doesMobHavePotionEffect(LivingEntity mob, Effect effect){
		Iterator<EffectInstance> effects = mob.getActivePotionEffects().iterator();

		while(effects.hasNext()){
			String eName = effects.next().getEffectName();

			if(eName.equals(effect.getName()))
				return true;
			else
				continue;
		}

		return false;
	}

	public static void moveX(Entity entity, double amount)
	{
		entity.setMotion(entity.getMotion().mul(1, 0, 0).add(amount, 0, 0));
	}

	public static void moveY(Entity entity, double amount)
	{
		entity.setMotion(entity.getMotion().mul(0, 1, 0).add(0, amount, 0));
	}

	public static void moveZ(Entity entity, double amount)
	{
		entity.setMotion(entity.getMotion().mul(0, 0, 1).add(0, 0, amount));
	}
}