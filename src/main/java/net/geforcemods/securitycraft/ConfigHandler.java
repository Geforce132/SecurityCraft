package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class ConfigHandler {
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		Pair<Server, ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);

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

		Client(ForgeConfigSpec.Builder builder) {
			//@formatter:off
			sayThanksMessage = builder
					.comment("Send a welcome message containing tips when joining the world")
					.define("sayThanksMessage", true);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? Servers can force this setting on clients if the server config setting \"force_reinforced_block_tint\" is set to true.")
					.define("reinforced_block_tint", true);

			reinforcedBlockTintColor = builder
					.comment("Set the color that reinforced blocks' textures have when reinforced_block_tint is enabled. This cannot be overridden by servers, and will be applied the same to all blocks. Grayscale values look best.",
							"Format: 0xRRGGBB")
					.defineInRange("reinforced_block_tint_color", 0x999999, 0x000000, 0xFFFFFF);
			//@formatter:on
		}
	}

	public static class Server {
		public DoubleValue codebreakerChance;
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
		public IntValue reinforcedSuffocationDamage;
		public BooleanValue allowCameraNightVision;
		public IntValue passcodeCheckCooldown;
		public BooleanValue passcodeSpamLogWarningEnabled;
		public ConfigValue<String> passcodeSpamLogWarning;
		public BooleanValue inWorldUnReinforcing;
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesAllowlist;
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesDenylist;
		private ConfigValue<List<? extends String>> taserEffectsValue;
		private ConfigValue<List<? extends String>> poweredTaserEffectsValue;
		public final List<Supplier<MobEffectInstance>> taserEffects = new ArrayList<>();
		public final List<Supplier<MobEffectInstance>> poweredTaserEffects = new ArrayList<>();

		Server(ForgeConfigSpec.Builder builder) {
			//@formatter:off
			codebreakerChance = builder
					.comment("The chance for the codebreaker to successfully hack a block. 0.33 is 33%. Set to a negative value to disable the codebreaker.",
							"Using the codebreaker when this is set to 0.0 will still damage the item, while negative values do not damage it.")
					.defineInRange("codebreaker_chance", 0.33D, -1.0D, 1.0D);

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
					.defineInRange("inventoryScannerRange", 2, 0, Integer.MAX_VALUE);

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

			taserEffectsValue = builder
					.comment("Add effects to this list that you want the taser to inflict onto the mobs it hits. One entry corresponds to one effect, and is formatted like this:",
							"effect_namespace:effect_path|duration|amplifier",
							"Example: The entry \"minecraft:slowness|20|1\" defines slowness 1 for 1 second (20 ticks = 1 second).")
					.defineList("taser_effects", List.of("minecraft:weakness|200|2", "minecraft:nausea|200|2", "minecraft:slowness|200|2"), String.class::isInstance);

			poweredTaserEffectsValue = builder
					.comment("Add effects to this list that you want the powered taser to inflict onto the mobs it hits. One entry corresponds to one effect, and is formatted like this:",
							"effect_namespace:effect_path|duration|amplifier",
							"Example: The entry \"minecraft:slowness|20|1\" defines slowness 1 for 1 second (20 ticks = 1 second).")
					.defineList("powered_taser_effects", List.of("minecraft:weakness|400|5", "minecraft:nausea|400|5", "minecraft:slowness|400|5"), String.class::isInstance);

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

			sentryAttackableEntitiesAllowlist = builder
					.comment("Add entities to this list that the Sentry currently does not attack, but that you want the Sentry to attack. The denylist takes priority over the allowlist.")
					.defineListAllowEmpty(List.of("sentry_attackable_entities_allowlist"), List::of, String.class::isInstance);

			sentryAttackableEntitiesDenylist = builder
					.comment("Add entities to this list that the Sentry currently attacks, but that you want the Sentry to NOT attack. The denylist takes priority over the allowlist.")
					.defineListAllowEmpty(List.of("sentry_attackable_entities_denylist"), List::of, String.class::isInstance);
			//@formatter:on
		}
	}

	@SubscribeEvent
	public static void onModConfig(ModConfigEvent event) {
		if (event.getConfig().getSpec() == SERVER_SPEC && SERVER_SPEC.isLoaded()) {
			loadEffects(SERVER.taserEffectsValue, SERVER.taserEffects);
			loadEffects(SERVER.poweredTaserEffectsValue, SERVER.poweredTaserEffects);
		}
	}

	private static void loadEffects(ConfigValue<List<? extends String>> effectsValue, List<Supplier<MobEffectInstance>> effects) {
		effects.clear();

		for (String entry : effectsValue.get()) {
			String[] split = entry.split("\\|");

			if (split.length == 3) {
				int duration = Integer.parseInt(split[1]);
				int amplifier = Integer.parseInt(split[2]);

				if (validateValue(duration, entry) && validateValue(amplifier, entry)) {
					ResourceLocation effectLocation = new ResourceLocation(split[0]);

					if (!ForgeRegistries.MOB_EFFECTS.containsKey(effectLocation)) {
						SecurityCraft.LOGGER.warn("Effect \"{}\" does not exist, skipping", effectLocation);
						continue;
					}

					//the amplifier is actually 0-indexed, but 1-indexed in the config for ease of use
					effects.add(() -> new MobEffectInstance(ForgeRegistries.MOB_EFFECTS.getValue(effectLocation), duration, amplifier - 1));
				}
			}
			else
				SecurityCraft.LOGGER.warn("Not enough information provided for effect \"{}\", skipping", entry);
		}
	}

	private static boolean validateValue(int value, String entry) {
		if (value <= 0) {
			SecurityCraft.LOGGER.warn("Value \"{}\" cannot be less than or equal to zero for entry \"{}\", skipping", value, entry);
			return false;
		}

		return true;
	}

	public static <T> T getOrDefault(ConfigValue<T> value) {
		try {
			return value.get();
		}
		catch (Exception e) {
			if (!FMLLoader.getLaunchHandler().isData()) {
				SecurityCraft.LOGGER.warn("Error when getting config value with getOrDefault! Please report this.");
				e.printStackTrace();
			}

			return value.getDefault();
		}
	}
}
