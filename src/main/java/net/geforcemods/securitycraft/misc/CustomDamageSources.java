package net.geforcemods.securitycraft.misc;

import net.minecraft.util.DamageSource;

/**
 * SecurityCraft's custom damage source handler.
 *
 * @author Geforce
 */
public class CustomDamageSources
{
	public static final DamageSource LASER = new DamageSource("securitycraft.laser").setDamageBypassesArmor();
	public static final DamageSource FAKE_WATER = new DamageSource("securitycraft.fakeWater").setDamageBypassesArmor();
	public static final DamageSource ELECTRICITY = new DamageSource("securitycraft.electricity").setDamageBypassesArmor();
}
