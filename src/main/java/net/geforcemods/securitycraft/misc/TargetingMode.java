package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public enum TargetingMode {
	PLAYERS("gui.securitycraft:srat.targets3"),
	PLAYERS_AND_MOBS("gui.securitycraft:srat.targets1"),
	MOBS("gui.securitycraft:srat.targets2");

	private final String translationKey;

	private TargetingMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public ITextComponent translate() {
		return new TextComponentTranslation(translationKey);
	}

	public boolean allowsPlayers() {
		return this == PLAYERS || this == PLAYERS_AND_MOBS;
	}

	public boolean allowsMobs() {
		return this == MOBS || this == PLAYERS_AND_MOBS;
	}

	public <T extends IOwnable> boolean canAttackEntity(EntityLivingBase entity, T be, boolean checkInvisibility) {
		if (entity == null || be instanceof IModuleInventory && ((IModuleInventory) be).isAllowed(entity))
			return false;

		boolean isPlayer = entity instanceof EntityPlayer;

		if (isPlayer && allowsPlayers() || !isPlayer && allowsMobs()) {
			return (!isPlayer || !(be.isOwnedBy((EntityPlayer) entity) && be.ignoresOwner()) && !((EntityPlayer) entity).isCreative() && !((EntityPlayer) entity).isSpectator()) //Player checks
					&& (!checkInvisibility || !EntityUtils.isInvisible(entity)) && !be.allowsOwnableEntity(entity); //checks for all entities
		}
		else
			return false;
	}
}