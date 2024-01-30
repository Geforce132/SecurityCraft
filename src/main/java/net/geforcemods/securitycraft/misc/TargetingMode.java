package net.geforcemods.securitycraft.misc;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

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
}