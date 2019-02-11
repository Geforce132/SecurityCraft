package net.geforcemods.securitycraft.util;

import java.util.Iterator;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class EntityUtils{

	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator<?> activeEffects = mob.getActivePotionEffects().iterator();

		while(activeEffects.hasNext()){
			PotionEffect effect = (PotionEffect) activeEffects.next();
			String eName = effect.getEffectName();

			if(eName.equals(potion.getName()))
				return true;
			else
				continue;
		}

		return false;
	}
}