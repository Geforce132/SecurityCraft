package net.geforcemods.securitycraft.misc;

import net.geforcemods.securitycraft.api.IOwnable;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.player.Player;

public enum TintMode {
	ALL("all"),
	NONE("none") {
		@Override
		public boolean shouldTint(Player player, IOwnable ownable) {
			return false;
		}
	},
	OWNED("owned") {
		@Override
		public boolean shouldTint(Player player, IOwnable ownable) {
			return ownable.isOwnedBy(player);
		}
	},
	UNOWNED("unowned") {
		@Override
		public boolean shouldTint(Player player, IOwnable ownable) {
			return !ownable.isOwnedBy(player);
		}
	};

	private static int currentTintColor;
	private static TintMode currentTintMode;
	private final String translationKey;

	TintMode(String name) {
		translationKey = "gui.securitycraft:blockReinforcer.tintMode." + name;
	}

	public boolean shouldTint(Player player, IOwnable ownable) {
		return true;
	}

	public static int tint(int baseTint, IOwnable ownable) {
		return mode().shouldTint(Minecraft.getInstance().player, ownable) ? FastColor.ARGB32.multiply(baseTint, 0xFF000000 | TintMode.color()) : baseTint;
	}

	public final Component translate() {
		return Component.translatable(translationKey);
	}

	public final Component tooltip() {
		return Component.translatable(translationKey + ".tooltip");
	}

	public static int color() {
		return currentTintColor;
	}

	public static TintMode mode() {
		return currentTintMode;
	}

	public static void setColor(int tintColor) {
		currentTintColor = tintColor;
	}

	public static void setMode(TintMode tintMode) {
		currentTintMode = tintMode;
	}
}
