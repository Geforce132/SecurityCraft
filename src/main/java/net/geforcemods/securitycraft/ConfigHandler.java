package net.geforcemods.securitycraft;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

public class ConfigHandler {
	public static final ModConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	public static final ModConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		Pair<Client, ModConfigSpec> clientSpecPair = new ModConfigSpec.Builder().configure(Client::new);
		Pair<Server, ModConfigSpec> serverSpecPair = new ModConfigSpec.Builder().configure(Server::new);

		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
		SERVER_SPEC = serverSpecPair.getRight();
		SERVER = serverSpecPair.getLeft();
	}

	private ConfigHandler() {}

	public static class Client {
		public BooleanValue sayThanksMessage;
		public BooleanValue reinforcedBlockTint;
		public IntValue reinforcedBlockTintColor;

		Client(ModConfigSpec.Builder builder) {
			//@formatter:off
			sayThanksMessage = builder
					.comment("Display a 'tip' message at spawn?")
					.define("sayThanksMessage", true);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? This setting can be overridden by servers.")
					.define("reinforced_block_tint", true);

			reinforcedBlockTintColor = builder
					.comment("Set the color that reinforced blocks' textures have when reinforced_block_tint is enabled. This cannot be overridden by servers, and will be applied the same to all blocks. Grayscale values look best.",
							"Format: 0xRRGGBB")
					.defineInRange("reinforced_block_tint_color", 0x999999, 0x000000, 0xFFFFFF);
			//@formatter:on
		}
	}

	public static class Server {
		public BooleanValue allowAdminTool;
		public BooleanValue shouldSpawnFire;
		public BooleanValue ableToBreakMines;
		public BooleanValue smallerMineExplosion;
		public BooleanValue mineExplodesWhenInCreative;
		public BooleanValue mineExplosionsBreakBlocks;
		public IntValue laserBlockRange;
		public IntValue inventoryScannerRange;
		public IntValue maxAlarmRange;
		public BooleanValue allowBlockClaim;
		public BooleanValue reinforcedBlockTint;
		public BooleanValue forceReinforcedBlockTint;
		public BooleanValue retinalScannerFace;
		public BooleanValue enableTeamOwnership;
		public BooleanValue disableThanksMessage;
		public BooleanValue trickScannersWithPlayerHeads;
		public BooleanValue preventReinforcedFloorGlitching;
		public DoubleValue taserDamage;
		public DoubleValue poweredTaserDamage;
		public DoubleValue laserDamage;
		public IntValue incorrectPasscodeDamage;
		public IntValue sentryBulletDamage;
		public BooleanValue allowCameraNightVision;
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesAllowlist;
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesDenylist;

		Server(ModConfigSpec.Builder builder) {
			//@formatter:off
			allowAdminTool = builder
					.comment("Can the admin tool be used?")
					.define("allowAdminTool", true);

			shouldSpawnFire = builder
					.comment("Should mines spawn fire after exploding?")
					.define("shouldSpawnFire", true);

			ableToBreakMines = builder
					.comment("Should players be able to break a mine without it exploding?")
					.define("ableToBreakMines", true);

			smallerMineExplosion = builder
					.comment("Should mines' explosions be smaller than usual.")
					.define("smallerMineExplosion", false);

			mineExplodesWhenInCreative = builder
					.comment("Should mines explode if broken while in Creative mode?")
					.define("mineExplodesWhenInCreative", true);

			mineExplosionsBreakBlocks = builder
					.comment("Set this to false if you want mines to not break blocks when they explode. If this is set to true, the blockExplosionDropDecay gamerule will be respected")
					.define("mineExplosionsBreakBlocks", true);

			laserBlockRange = builder
					.comment("From how many blocks away can a laser block connect to another laser block?")
					.defineInRange("laserBlockRange", 5, 0, Integer.MAX_VALUE);

			inventoryScannerRange = builder
					.comment("From how many blocks away can an inventory scanner connect to another inventory scanner?")
					.defineInRange("inventoryScannerRange", 2, 0, Integer.MAX_VALUE);

			maxAlarmRange = builder
					.comment("What is the maximum value that can be set for an alarm's range option? Do note, that this may be limited by chunk loading distance. Higher values may also not be finetuneable.")
					.defineInRange("maxAlarmRange", 100, 1, Integer.MAX_VALUE);

			allowBlockClaim = builder
					.comment("Allow claiming unowned blocks?")
					.define("allowBlockClaim", false);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? This does nothing unless force_reinforced_block_tint is set to true.")
					.define("reinforced_block_tint", true);

			forceReinforcedBlockTint = builder
					.comment("Set this to true if you want to force the setting of reinforced_block_tint for players.")
					.define("force_reinforced_block_tint", false);

			retinalScannerFace = builder
					.comment("Display owner face on retinal scanner?")
					.define("retinalScannerFace", true);

			enableTeamOwnership = builder
					.comment("Set this to true to enable every player on a scoreboard team (or FTB Teams party) to own the blocks of every other player on the same team.",
							"This enables players on the same team to break each other's reinforced blocks, change options, add/remove modules, and have access to all other owner-restricted things.")
					.define("enable_team_ownership", false);

			disableThanksMessage = builder
					.comment("Set this to true to disable sending the message that SecurityCraft shows when a player joins.",
							"Note, that this stops showing the message for every player, even those that want to see them.")
					.define("disable_thanks_message", false);

			trickScannersWithPlayerHeads = builder
					.comment("Set this to true if you want players wearing a different player's skull to be able to trick their retinal scanners and scanner doors into activating.")
					.define("trick_scanners_with_player_heads", false);

			preventReinforcedFloorGlitching = builder
					.comment("Set this to true to prevent players from glitching through a floor made of reinforced blocks using a boat. This is achieved by not letting players exit a boat in a way that would place them inside reinforced blocks.")
					.define("prevent_reinforced_floor_glitching", false);

			taserDamage = builder
					.comment("Set the amount of damage the taser inflicts onto the mobs it hits. Default is half a heart.")
					.defineInRange("taser_damage", 1.0D, 0.0D, Double.MAX_VALUE);

			poweredTaserDamage = builder
					.comment("Set the amount of damage the powered taser inflicts onto the mobs it hits. Default is one heart.")
					.defineInRange("powered_taser_damage", 2.0D, 0.0D, Double.MAX_VALUE);

			laserDamage = builder
					.comment("Defines the damage inflicted to an entity if it passes through a laser with installed harming module. This is given in health points, meaning 2 health points = 1 heart")
					.defineInRange("laser_damage", 10.0, 0.0D, Double.MAX_VALUE);

			incorrectPasscodeDamage = builder
					.comment("Defines the damage that a block requiring a passcode deals to the player, if the player enters an incorrect code. This only works if a harming module is installed.",
							"Default is two hearts of damage.")
					.defineInRange("incorrectPasscodeDamage", 4, 1, Integer.MAX_VALUE);

			sentryBulletDamage = builder
					.comment("Set the amount of damage the default Sentry bullet inflicts onto the mobs it hits. This will not affect other projectiles the Sentry can use, like arrows. Default is one heart.")
					.defineInRange("sentry_bullet_damage", 2, 0, Integer.MAX_VALUE);

			allowCameraNightVision = builder
					.comment("Set this to false to disallow players to activate night vision without having the potion effect when looking through cameras.")
					.define("allow_camera_night_vision", true);

			sentryAttackableEntitiesAllowlist = builder
					.comment("Add entities to this list that the Sentry currently does not attack, but that you want the Sentry to attack. The denylist takes priority over the allowlist.")
					.defineList("sentry_attackable_entities_allowlist", List.of(), String.class::isInstance);

			sentryAttackableEntitiesDenylist = builder
					.comment("Add entities to this list that the Sentry currently attacks, but that you want the Sentry to NOT attack. The denylist takes priority over the allowlist.")
					.defineList("sentry_attackable_entities_denylist", List.of(), String.class::isInstance);
			//@formatter:on
		}
	}

	public static <T> T getOrDefault(ConfigValue<T> value) {
		try {
			return value.get();
		}
		catch (Exception e) {
			if (!DatagenModLoader.isRunningDataGen()) {
				SecurityCraft.LOGGER.warn("Error when getting config value with getOrDefault! Please report this.");
				e.printStackTrace();
			}

			return value.getDefault();
		}
	}
}
