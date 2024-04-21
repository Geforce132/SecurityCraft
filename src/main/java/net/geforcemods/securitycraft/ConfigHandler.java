package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

@EventBusSubscriber(modid = SecurityCraft.MODID, bus = Bus.MOD)
public class ConfigHandler {
	private static final Logger LOGGER = LogUtils.getLogger();
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
		public DoubleValue cameraSpeed;
		public BooleanValue reinforcedBlockTint;
		public IntValue reinforcedBlockTintColor;

		Client(ModConfigSpec.Builder builder) {
			//@formatter:off
			sayThanksMessage = builder
					.comment("Display a 'tip' message at spawn?")
					.define("sayThanksMessage", true);

			cameraSpeed = builder
					.comment("How fast can you rotate when mounted to a camera and holding W-A-S-D?")
					.defineInRange("cameraSpeed", 2.0D, 0.0D, Double.MAX_VALUE);

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
		public BooleanValue respectInvisibility;
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
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesAllowlist;
		public ConfigValue<List<? extends String>> sentryAttackableEntitiesDenylist;
		private ConfigValue<List<? extends String>> taserEffectsValue;
		private ConfigValue<List<? extends String>> poweredTaserEffectsValue;
		public final List<Supplier<MobEffectInstance>> taserEffects = new ArrayList<>();
		public final List<Supplier<MobEffectInstance>> poweredTaserEffects = new ArrayList<>();

		Server(ModConfigSpec.Builder builder) {
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

			respectInvisibility = builder
					.comment("Should the sentry/inventory scanner/laser block/etc. ignore players and entities that are invisible?")
					.define("respect_invisibility", false);

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

			sentryAttackableEntitiesAllowlist = builder
					.comment("Add entities to this list that the Sentry currently does not attack, but that you want the Sentry to attack. The denylist takes priority over the allowlist.")
					.defineList("sentry_attackable_entities_allowlist", List.of(), String.class::isInstance);

			sentryAttackableEntitiesDenylist = builder
					.comment("Add entities to this list that the Sentry currently attacks, but that you want the Sentry to NOT attack. The denylist takes priority over the allowlist.")
					.defineList("sentry_attackable_entities_denylist", List.of(), String.class::isInstance);
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

					if (!BuiltInRegistries.MOB_EFFECT.containsKey(effectLocation)) {
						LOGGER.warn("Effect \"{}\" does not exist, skipping", effectLocation);
						continue;
					}

					//the amplifier is actually 0-indexed, but 1-indexed in the config for ease of use
					effects.add(() -> new MobEffectInstance(BuiltInRegistries.MOB_EFFECT.getHolder(effectLocation).get(), duration, amplifier - 1));
				}
			}
			else
				LOGGER.warn("Not enough information provided for effect \"{}\", skipping", entry);
		}
	}

	private static boolean validateValue(int value, String entry) {
		if (value <= 0) {
			LOGGER.warn("Value \"{}\" cannot be less than or equal to zero for entry \"{}\", skipping", value, entry);
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
				LOGGER.warn("Error when getting config value with getOrDefault! Please report this.");
				e.printStackTrace();
			}

			return value.getDefault();
		}
	}
}
