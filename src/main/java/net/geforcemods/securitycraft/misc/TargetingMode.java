package net.geforcemods.securitycraft.misc;

import java.util.function.Predicate;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum TargetingMode {
	PLAYERS("gui.securitycraft:srat.targets3"),
	PLAYERS_AND_MOBS("gui.securitycraft:srat.targets1"),
	MOBS("gui.securitycraft:srat.targets2");

	private final String translationKey;

	private TargetingMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public ITextComponent translate() {
		return new TranslationTextComponent(translationKey);
	}

	public boolean allowsPlayers() {
		return this == PLAYERS || this == PLAYERS_AND_MOBS;
	}

	public boolean allowsMobs() {
		return this == MOBS || this == PLAYERS_AND_MOBS;
	}

	public <T extends IOwnable> boolean canAttackEntity(LivingEntity entity, T be, Predicate<LivingEntity> isInvisible) {
		if (entity == null || be instanceof IModuleInventory && ((IModuleInventory) be).isAllowed(entity))
			return false;

		boolean isPlayer = entity instanceof PlayerEntity;

		if (isPlayer && allowsPlayers() || !isPlayer && allowsMobs()) {
			return (!isPlayer || !(be.isOwnedBy(entity) && be.ignoresOwner()) && !((PlayerEntity) entity).isCreative()) //Player checks
					&& !entity.isSpectator() && !isInvisible.test(entity) && !be.allowsOwnableEntity(entity); //checks for all entities
		}
		else
			return false;
	}
}