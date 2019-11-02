package net.geforcemods.securitycraft.util;

import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EntityUtils{

	/**
	 * Does the given entity have the specified potion effect? <p>
	 *
	 * Args: entity, potionEffect.
	 */
	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator<?> iterator = mob.getActivePotionEffects().iterator();

		while(iterator.hasNext()){
			PotionEffect effect = (PotionEffect) iterator.next();
			String eName = effect.getEffectName();

			if(eName.equals(potion.getName()))
				return true;
			else
				continue;
		}

		return false;
	}
}
