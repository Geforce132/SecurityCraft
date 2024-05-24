package net.geforcemods.securitycraft.misc;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;

public enum TargetingMode {
	PLAYERS("gui.securitycraft:srat.targets3"),
	PLAYERS_AND_MOBS("gui.securitycraft:srat.targets1"),
	MOBS("gui.securitycraft:srat.targets2");

	private final String translationKey;

	private TargetingMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public Component translate() {
		return new TranslatableComponent(translationKey);
	}

	public boolean allowsPlayers() {
		return this == PLAYERS || this == PLAYERS_AND_MOBS;
	}

	public boolean allowsMobs() {
		return this == MOBS || this == PLAYERS_AND_MOBS;
	}

	public <T extends IOwnable> boolean canAttackEntity(LivingEntity entity, T be, Predicate<LivingEntity> isInvisible) {
		if (entity == null || be instanceof IModuleInventory moduleInv && moduleInv.isAllowed(entity))
			return false;

		boolean isPlayer = entity instanceof Player;

		if (isPlayer && allowsPlayers() || !isPlayer && allowsMobs()) {
			return (!isPlayer || !(be.isOwnedBy(entity) && be.ignoresOwner()) && !((Player) entity).isCreative()) //Player checks
					&& entity.canBeSeenByAnyone() && !isInvisible.test(entity) && !(entity instanceof OwnableEntity ownableEntity && be.allowsOwnableEntity(ownableEntity)); //checks for all entities
		}
		else
			return false;
	}
}