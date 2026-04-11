package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public enum TintMode {
	ALL("gui.securitycraft:blockReinforcer.tintMode.all"),
	NONE("gui.securitycraft:blockReinforcer.tintMode.none"),
	OWNED("gui.securitycraft:blockReinforcer.tintMode.owned"),
	UNOWNED("gui.securitycraft:blockReinforcer.tintMode.unowned");

	private static int currentTintColor;
	private static TintMode currentTintMode;
	private final String translationKey;

	private TintMode(String translationKey) {
		this.translationKey = translationKey;
	}

	public Component translate() {
		return Component.translatable(translationKey);
	}

	public Component tooltip() {
		return Component.translatable(translationKey + ".tooltip");
	}

	public static boolean shouldTint(Player player, IOwnable ownable) {
		if (currentTintMode == ALL)
			return true;
		else if (currentTintMode == NONE)
			return false;

		boolean isOwner = ownable.isOwnedBy(player);

		return (currentTintMode == OWNED && isOwner) || (currentTintMode == UNOWNED && !isOwner);
	}

	public static int getTintColor() {
		return currentTintColor;
	}

	public static TintMode getTintMode() {
		return currentTintMode;
	}

	public static void loadTintSettingsFromConfig() {
		currentTintColor = ConfigHandler.CLIENT.reinforcedBlockTintColor.getAsInt();
		currentTintMode = ConfigHandler.CLIENT.reinforcedBlockTintMode.get();
	}

	public static void setTintSettings(int tintColor, TintMode tintMode) {
		currentTintColor = tintColor;
		currentTintMode = tintMode;
	}
}
