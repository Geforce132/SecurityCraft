package org.freeforums.geforce.securitycraft.misc;

import net.minecraft.util.DamageSource;

public class CustomDamageSources extends DamageSource{
	
	public static CustomDamageSources laser = (CustomDamageSources) new CustomDamageSources("securitycraft.laser").setDamageBypassesArmor();
	public static CustomDamageSources fakeWater = (CustomDamageSources) new CustomDamageSources("securitycraft.fakeWater").setDamageBypassesArmor();

	public CustomDamageSources(String customType) {
		super(customType);
	}

}
