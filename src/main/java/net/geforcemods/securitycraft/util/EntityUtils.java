package net.geforcemods.securitycraft.util;

import java.util.Iterator;

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
}