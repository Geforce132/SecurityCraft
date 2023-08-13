package net.geforcemods.securitycraft.misc;

import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

/**
 * SecurityCraft's custom damage source handler.
 *
 * @author Geforce
 */
public class CustomDamageSources {
	public static final DamageSource LASER = new DamageSource("securitycraft.laser");
	public static final DamageSource FAKE_WATER = new DamageSource("securitycraft.fakeWater").setDamageBypassesArmor();
	public static final DamageSource ELECTRICITY = new DamageSource("securitycraft.electricity").setDamageBypassesArmor();
	public static final DamageSource INCORRECT_PASSCODE = new DamageSource("securitycraft.incorrectPasscode");

	private CustomDamageSources() {}

	public static DamageSource taser(Entity shooter) {
		return new TaserDamageSource("securitycraft.taser", shooter);
	}

	static class TaserDamageSource extends EntityDamageSource {
		public TaserDamageSource(String damageTypeId, Entity shooter) {
			super(damageTypeId, shooter);
		}

		@Override
		public Entity getImmediateSource() {
			return null;
		}
	}
}
