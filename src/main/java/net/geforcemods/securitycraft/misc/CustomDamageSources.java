package net.geforcemods.securitycraft.misc;

import net.minecraft.world.damagesource.DamageSource;

public class CustomDamageSources {
	public static final DamageSource LASER = new DamageSource("securitycraft.laser");
	public static final DamageSource FAKE_WATER = new DamageSource("securitycraft.fakeWater").bypassArmor();
	public static final DamageSource ELECTRICITY = new DamageSource("securitycraft.electricity").bypassArmor();
	public static final DamageSource TASER = new DamageSource("securitycraft.taser");
	public static final DamageSource INCORRECT_PASSCODE = new DamageSource("securitycraft.incorrectPasscode");
}
