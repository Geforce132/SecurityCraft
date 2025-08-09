package net.geforcemods.securitycraft;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;

import net.geforcemods.securitycraft.util.TeamUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@EventBusSubscriber(modid = SecurityCraft.MODID)
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
		public final BooleanValue sayThanksMessage;
		public final BooleanValue reinforcedBlockTint;
		public final BooleanValue debugCameraResetTracing;
		public final IntValue reinforcedBlockTintColor;
		public final IntValue frameFeedRenderDistance;
		public final IntValue frameFeedResolution;
		public final IntValue frameFeedFpsLimit;

		Client(ModConfigSpec.Builder builder) {
			//@formatter:off
			sayThanksMessage = builder
					.comment("Send a welcome message containing tips when joining the world")
					.define("sayThanksMessage", true);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? Servers can force this setting on clients if the server config setting \"force_reinforced_block_tint\" is set to true.")
					.define("reinforced_block_tint", true);

			debugCameraResetTracing = builder
					.comment("If this debug feature is enabled, SecurityCraft will attempt to find and report mods that prevent the feature of viewing security cameras from working by immediately resetting the player's camera entity.")
					.define("debug_camera_reset_tracing", false);

			reinforcedBlockTintColor = builder
					.comment("Set the color that reinforced blocks' textures have when reinforced_block_tint is enabled. This cannot be overridden by servers, and will be applied the same to all blocks. Grayscale values look best.",
							"Format: 0xRRGGBB")
					.defineInRange("reinforced_block_tint_color", 0x999999, 0x000000, 0xFFFFFF);

			frameFeedRenderDistance = builder
					.comment("Set the radius in which chunks viewed in a frame camera feed should be requested from the server and rendered. If this config has a higher value than the vanilla \"Render Distance\" option or the \"view-distance\" server property, the smaller value is used instead.")
					.defineInRange("frame_feed_render_distance", 16, 2, 32);

			frameFeedResolution = builder
					.comment("Set the resolution of the Frame camera feed. This is always a square resolution. Smaller values will be less detailed, higher values may lead to diminishing returns.")
					.defineInRange("frame_feed_resolution", 512, 1, 16384);

			frameFeedFpsLimit = builder
					.comment("The maximum amount of frames per second the Frame camera feed renders at. Higher values will lead to worse performance.")
					.defineInRange("frame_feed_fps_limit", 30, 10, 260);
			//@formatter:on
		}
	}

	public static class Server {
		public final BooleanValue allowAdminTool;
		public final BooleanValue shouldSpawnFire;
		public final BooleanValue smallerMineExplosion;
		public final BooleanValue mineExplodesWhenInCreative;
		public final BooleanValue mineExplosionsBreakBlocks;
		public final IntValue laserBlockRange;
		public final IntValue inventoryScannerRange;
		public final IntValue maxAlarmRange;
		public final BooleanValue allowBlockClaim;
		public final BooleanValue reinforcedBlockTint;
		public final BooleanValue forceReinforcedBlockTint;
		public final BooleanValue retinalScannerFace;
		public final BooleanValue enableTeamOwnership;
		public final ConfigValue<List<? extends String>> teamOwnershipPrecedence;
		public final BooleanValue disableThanksMessage;
		public final BooleanValue trickScannersWithPlayerHeads;
		public final BooleanValue preventReinforcedFloorGlitching;
		public final DoubleValue taserDamage;
		public final DoubleValue poweredTaserDamage;
		public final DoubleValue laserDamage;
		public final IntValue incorrectPasscodeDamage;
		public final IntValue sentryBulletDamage;
		public final IntValue reinforcedSuffocationDamage;
		public final BooleanValue allowCameraNightVision;
		public final IntValue passcodeCheckCooldown;
		public final BooleanValue passcodeSpamLogWarningEnabled;
		public final ConfigValue<String> passcodeSpamLogWarning;
		public final BooleanValue inWorldUnReinforcing;
		public final BooleanValue frameFeedViewingEnabled;
		public final IntValue frameFeedViewDistance;
		public final BooleanValue frameFeedForceloadingLimitEnabled;
		public final IntValue frameFeedForceloadingLimit;
		public final BooleanValue vanillaToolBlockBreaking;
		public final BooleanValue alwaysDrop;
		public final BooleanValue allowBreakingNonOwnedBlocks;
		public final DoubleValue nonOwnedBreakingSlowdown;
		public final ConfigValue<List<? extends String>> sentryAttackableEntitiesAllowlist;
		public final ConfigValue<List<? extends String>> sentryAttackableEntitiesDenylist;

		Server(ModConfigSpec.Builder builder) {
			//@formatter:off
			allowAdminTool = builder
					.comment("Can the admin tool be used?")
					.define("allowAdminTool", true);

			shouldSpawnFire = builder
					.comment("Should mines spawn fire after exploding?")
					.define("shouldSpawnFire", true);

			smallerMineExplosion = builder
					.comment("Should mines' explosions be smaller than usual?")
					.define("smallerMineExplosion", false);

			mineExplodesWhenInCreative = builder
					.comment("Should mines explode if broken while in Creative mode?")
					.define("mineExplodesWhenInCreative", true);

			mineExplosionsBreakBlocks = builder
					.comment("Set this to false if you want mines to not break blocks when they explode. If this is set to true, the blockExplosionDropDecay gamerule will be respected")
					.define("mineExplosionsBreakBlocks", true);

			laserBlockRange = builder
					.comment("At most from how many blocks away can a laser block connect to another laser block?")
					.defineInRange("laserBlockRange", 5, 0, Integer.MAX_VALUE);

			inventoryScannerRange = builder
					.comment("At most from how many blocks away can an inventory scanner connect to another inventory scanner?")
					.defineInRange("inventoryScannerRange", 3, 0, Integer.MAX_VALUE);

			maxAlarmRange = builder
					.comment("What is the maximum value that can be set for an alarm's range option? Do note, that this may be limited by chunk loading distance. Higher values may also lead to the setting being less finetuneable.")
					.defineInRange("maxAlarmRange", 100, 1, Integer.MAX_VALUE);

			allowBlockClaim = builder
					.comment("Allows to claim blocks that do not have an owner by rightclicking them with the Universal Owner Changer.")
					.define("allowBlockClaim", false);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? Servers can force this setting on clients if the server config \"force_reinforced_block_tint\" is set to true.")
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

			teamOwnershipPrecedence = builder
					.comment("This list defines in which order SecurityCraft checks teams of players to determine if they're on the same team, if \"enable_team_ownership\" is set to true. First in the list means it's checked first.",
							"SecurityCraft will continue checking for teams down the list until it finds a case where the players are on the same team, or the list is over. E.g. Given the default config, if FTB Teams is installed but the players do not share a team, the mod checks if the same players are on the same vanilla team.",
							"Removing an entry makes the mod ignore that kind of team. Valid values are \"FTB_TEAMS\" and \"VANILLA\".")
					.defineListAllowEmpty("team_ownership_precedence", List.of("FTB_TEAMS", "VANILLA"), () -> "VANILLA", String.class::isInstance);

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

			reinforcedSuffocationDamage = builder
					.comment("Set the amount of damage the player receives when they are suffocating in a reinforced block. The default is two and a half hearts. If the value is set to -1, vanilla suffocation damage will be used.")
					.defineInRange("reinforced_suffocation_damage", 5, -1, Integer.MAX_VALUE);

			allowCameraNightVision = builder
					.comment("Set this to false to disallow players to activate night vision without having the potion effect when looking through cameras.")
					.define("allow_camera_night_vision", true);

			passcodeCheckCooldown = builder
					.comment("Defines the amount of time in milliseconds that needs to pass between two separate attempts from a player to enter a passcode.")
					.defineInRange("passcode_check_cooldown", 250, 0, 2000);

			passcodeSpamLogWarningEnabled = builder
					.comment("Set this to false to disable the log warning that is sent whenever a player tries to enter a passcode while on passcode cooldown.")
					.define("passcode_spam_log_warning_enabled", true);

			passcodeSpamLogWarning = builder
					.comment("The warning that is sent into the server log whenever a player tries to enter a passcode while on passcode cooldown. \"%1$s\" will be replaced with the player's name, \"%2$s\" with the passcode-protected object's name and \"%3$s\" with the object's position and dimension.")
					.define("passcode_spam_log_warning", "Player \"%1$s\" tried to enter a passcode into \"%2$s\" at position [%3$s] too quickly!");

			inWorldUnReinforcing = builder
					.comment("Setting this to false disables the ability of the Universal Block Reinforcer to (un-)reinforce blocks that are placed in the world.")
					.define("in_world_un_reinforcing", true);

			frameFeedViewingEnabled = builder
					.comment("Set this to false to disable the feature that camera feeds can be viewed in frames. While this feature is generally stable, it may also impact server performance due to loading chunks within all active frame cameras' views.")
					.define("frame_feed_viewing_enabled", true);

			frameFeedViewDistance = builder
					.comment("Set the radius in which chunks viewed in a frame camera should be loaded and sent to players. If this config has a higher value than the \"view-distance\" server property or the vanilla \"Render Distance\" option of the player requesting the chunks, the smaller value is used instead.")
					.defineInRange("frame_feed_view_distance", 16, 2, 32);

			frameFeedForceloadingLimitEnabled = builder
					.comment("Set this to true to enable a limit of chunks per dimension that may be forceloaded around frame feeds. The exact limit can be adjusted in the config option below. This does not affect chunks near frame feeds that are loaded by alternate means, e.g. through players.")
					.define("frame_feed_forceloading_limit_enabled", false);

			frameFeedForceloadingLimit = builder
					.comment("Set the limit of chunks per dimension that may be forceloaded around frame feeds. This feature may be toggled through the config option above. A value of 0 corresponds to chunk forceloading being disabled for all frames.")
					.defineInRange("frame_feed_forceloading_limit", Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

			vanillaToolBlockBreaking = builder
					.comment("Whether SecurityCraft's blocks should be broken using vanilla tools (axe, shovel, hoe, ...), instead of the Universal Block Remover. If set to true, this will disable the Universal Block Remover.")
					.define("vanilla_tool_block_breaking", true);

			alwaysDrop = builder
					.comment("Whether SecurityCraft's blocks always drop themselves no matter which tool is used. If this is set to false, the correct tool must be used for the block to drop (e.g. pickaxe for reinforced stone, or anything for reinforced dirt).",
							"This only applies when \"vanilla_tool_block_breaking\" is set to true.")
					.define("always_drop", true);

			allowBreakingNonOwnedBlocks = builder
					.comment("Whether players who are not the owner of a block can still destroy it.",
							"This applies regardless of what \"vanilla_tool_block_breaking\" is set to.")
					.define("allow_breaking_non_owned_blocks", false);

			nonOwnedBreakingSlowdown = builder
					.comment("How much slower it should be to break a block that is not owned by the player breaking it.",
							"The value is calculated as the normal block breaking speed divided by the non-owned block breaking slowdown. Example: A value of 2.0 means it takes twice as long to break the block.",
							"This only applies when \"allow_breaking_non_owned_blocks\" and \"vanilla_tool_block_breaking\" are set to true.")
					.defineInRange("non_owned_breaking_slowdown", 1.0D, 0.0D, Double.MAX_VALUE);

			sentryAttackableEntitiesAllowlist = builder
					.comment("Add entities to this list that the Sentry currently does not attack, but that you want the Sentry to attack. The denylist takes priority over the allowlist.")
					.defineListAllowEmpty("sentry_attackable_entities_allowlist", List.of(), () -> "minecraft:pig", String.class::isInstance);

			sentryAttackableEntitiesDenylist = builder
					.comment("Add entities to this list that the Sentry currently attacks, but that you want the Sentry to NOT attack. The denylist takes priority over the allowlist.")
					.defineListAllowEmpty("sentry_attackable_entities_denylist", List.of(), () -> "minecraft:pig", String.class::isInstance);
			//@formatter:on
		}
	}

	@SubscribeEvent
	public static void onModConfigReloading(ModConfigEvent.Loading event) {
		updateTeamPrecedence(event);
	}

	@SubscribeEvent
	public static void onModConfigReloading(ModConfigEvent.Reloading event) {
		updateTeamPrecedence(event);
	}

	private static void updateTeamPrecedence(ModConfigEvent event) {
		if (event.getConfig().getSpec() == SERVER_SPEC) {
			//@formatter:off
			TeamUtils.setPrecedence(SERVER.teamOwnershipPrecedence.get()
					.stream()
					.distinct()
					.map(s -> {
						try {
							return TeamUtils.TeamType.valueOf(s);
						}
						catch (IllegalArgumentException e) {}

						return TeamUtils.TeamType.NO_OP;
					})
					.map(TeamUtils.TeamType::getTeamHandler)
					.filter(Objects::nonNull)
					.toList());
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
