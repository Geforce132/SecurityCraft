package net.geforcemods.securitycraft.misc;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public enum TargetingMode {
	PLAYERS("gui.securitycraft:srat.targets3", true, false),
	PLAYERS_AND_MOBS("gui.securitycraft:srat.targets1", true, true),
	MOBS("gui.securitycraft:srat.targets2", false, true);

	private final String translationKey;
	private final boolean allowsPlayers, allowsMobs;

	private TargetingMode(String translationKey, boolean allowsPlayers, boolean allowsMobs) {
		this.translationKey = translationKey;
		this.allowsPlayers = allowsPlayers;
		this.allowsMobs = allowsMobs;
	}

	public ITextComponent translate() {
		return new TranslationTextComponent(translationKey);
	}

	public boolean allowsPlayers() {
		return allowsPlayers;
	}

	public boolean allowsMobs() {
		return allowsMobs;
	}
}