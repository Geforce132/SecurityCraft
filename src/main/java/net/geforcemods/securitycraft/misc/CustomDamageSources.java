package net.geforcemods.securitycraft.misc;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class CustomDamageSources {
	public static final DamageSource LASER = new DamageSource("securitycraft.laser");
	public static final DamageSource FAKE_WATER = new DamageSource("securitycraft.fakeWater").bypassArmor();
	public static final DamageSource ELECTRICITY = new DamageSource("securitycraft.electricity").bypassArmor();
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
		public Entity getDirectEntity() {
			return null;
		}

		@Override
		public Vec3 getSourcePosition() {
			return entity.position();
		}

		@Override
		public String toString() {
			return "TaserDamageSource (" + entity + ")";
		}
	}
}
