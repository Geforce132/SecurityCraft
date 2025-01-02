package net.geforcemods.securitycraft.mixin.dispenser;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

@Mixin(AbstractProjectileDispenseBehavior.class)
public interface IAbstractProjectileDispenseBehaviorAccessor {
	@Invoker("getProjectile")
	public Projectile securitycraft$callGetProjectile(Level level, Position position, ItemStack stack);
}
