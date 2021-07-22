package net.geforcemods.securitycraft.misc;

import net.minecraft.world.damagesource.DamageSource;

/**
 * SecurityCraft's custom damage source handler.
 *
 * @author Geforce
 */
public class CustomDamageSources
{
	public static final DamageSource LASER = new DamageSource("securitycraft.laser");
	public static final DamageSource FAKE_WATER = new DamageSource("securitycraft.fakeWater").bypassArmor();
	public static final DamageSource ELECTRICITY = new DamageSource("securitycraft.electricity").bypassArmor();
	public static final DamageSource TASER = new DamageSource("securitycraft.taser");
}
