package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.geforcemods.securitycraft.network.client.UpdateTeamPrecedence;
import net.geforcemods.securitycraft.util.TeamUtils;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Config(modid = SecurityCraft.MODID, category = "options")
@EventBusSubscriber(modid = SecurityCraft.MODID)
public class ConfigHandler {
	//@formatter:off
	@Name("codebreaker_chance")
	@RangeDouble(min = -1.0D, max = 1.0D)
	@Comment({"The chance for the codebreaker to successfully hack a block. 0.33 is 33%. Set to a negative value to disable the codebreaker.",
		"Using the codebreaker when this is set to 0.0 will still damage the item, while negative values do not damage it."})
	public static double codebreakerChance = 0.33D;

	@Name("Is admin tool allowed?")
	@LangKey("config.securitycraft:allowAdminTool")
	public static boolean allowAdminTool = true;

	@Name("Mine(s) spawn fire when detonated?")
	@LangKey("config.securitycraft:shouldSpawnFire")
	public static boolean shouldSpawnFire = true;

	@Name("Craftable level 1 keycard?")
	@LangKey("config.securitycraft:ableToCraftKeycard1")
	@RequiresMcRestart
	public static boolean ableToCraftKeycard1 = true;

	@Name("Craftable level 2 keycard?")
	@LangKey("config.securitycraft:ableToCraftKeycard2")
	@RequiresMcRestart
	public static boolean ableToCraftKeycard2 = true;

	@Name("Craftable level 3 keycard?")
	@LangKey("config.securitycraft:ableToCraftKeycard3")
	@RequiresMcRestart
	public static boolean ableToCraftKeycard3 = true;

	@Name("Craftable level 4 keycard?")
	@LangKey("config.securitycraft:ableToCraftKeycard4")
	@RequiresMcRestart
	public static boolean ableToCraftKeycard4 = true;

	@Name("Craftable level 5 keycard?")
	@LangKey("config.securitycraft:ableToCraftKeycard5")
	@RequiresMcRestart
	public static boolean ableToCraftKeycard5 = true;

	@Name("Craftable Limited Use keycard?")
	@LangKey("config.securitycraft:ableToCraftLUKeycard")
	@RequiresMcRestart
	public static boolean ableToCraftLUKeycard = true;

	@Name("Mines use a smaller explosion?")
	@LangKey("config.securitycraft:smallerMineExplosion")
	public static boolean smallerMineExplosion = false;

	@Name("Mines explode when broken in Creative?")
	@LangKey("config.securitycraft:mineExplodesWhenInCreative")
	public static boolean mineExplodesWhenInCreative = true;

	@Name("Do mines' explosions break blocks?")
	@LangKey("config.securitycraft:mineExplosionsBreakBlocks")
	public static boolean mineExplosionsBreakBlocks = true;

	@Name("Display a 'tip' message at spawn?")
	@LangKey("config.securitycraft:sayThanksMessage")
	public static boolean sayThanksMessage = true;

	@Name("Should check for updates on Github?")
	@LangKey("config.securitycraft:checkForUpdates")
	public static boolean checkForUpdates = true;

	@Name("Laser range:")
	@LangKey("config.securitycraft:laserBlockRange")
	public static int laserBlockRange = 5;

	@Name("Inventory Scanner range:")
	@LangKey("config.securitycraft:inventoryScannerRange")
	public static int inventoryScannerRange = 3;

	@Name("Maximum Alarm range:")
	@LangKey("config.securitycraft:maxAlarmRange")
	@RangeInt(min = 1)
	public static int maxAlarmRange = 100;

	@Name("Allow claiming unowned blocks?")
	@LangKey("config.securitycraft:allowBlockClaim")
	public static boolean allowBlockClaim = false;

	@Name("Darker reinforced block textures?")
	@LangKey("config.securitycraft:reinforcedBlockTint")
	public static boolean reinforcedBlockTint = true;

	@Name("Craftable mines?")
	@LangKey("config.securitycraft:ableToCraftMines")
	public static boolean ableToCraftMines = true;

	@Name("Display owner face on retinal scanner?")
	@LangKey("config.securitycraft:retinalScannerFace")
	public static boolean retinalScannerFace = true;

	@Name("Enable team ownership?")
	@LangKey("config.securitycraft:enableTeamOwnership")
	public static boolean enableTeamOwnership = false;

	/**
	 * @deprecated SecurityCraft handles this internally in {@link TeamUtils}
	 */
	@Deprecated
	@Name("Team Ownership Precedence")
	@Comment({"This list defines in which order SecurityCraft checks teams of players to determine if they're on the same team, if \"enable_team_ownership\" is set to true. First in the list means it's checked first.",
		"SecurityCraft will continue checking for teams down the list until it finds a case where the players are on the same team, or the list is over. E.g. Given the default config, if FTB Teams is installed but the players do not share a team, the mod checks if the same players are on the same vanilla team.",
		"Removing an entry makes the mod ignore that kind of team. Valid values are \"FTB_TEAMS\" and \"VANILLA\"."})
	public static String[] teamOwnershipPrecedence = {
			"FTB_TEAMS",
			"VANILLA"
	};

	@Name("Trick scanners with player heads?")
	@LangKey("config.securitycraft:trickScannersWithPlayerHeads")
	public static boolean trickScannersWithPlayerHeads = false;

	@Name("Prevent reinforced floor glitching?")
	@LangKey("config.securitycraft:preventReinforcedFloorGlitching")
	public static boolean preventReinforcedFloorGlitching = false;

	@Name("Taser damage")
	@LangKey("config.securitycraft:taser_damage")
	@RangeDouble(min = 0.0D)
	public static double taserDamage = 1.0D;

	@Name("Powered taser damage")
	@LangKey("config.securitycraft:powered_taser_damage")
	@RangeDouble(min = 0.0D)
	public static double poweredTaserDamage = 2.0D;

	@Name("Reinforced block tint color")
	@RangeInt(min = 0x000000, max = 0xFFFFFF)
	@Comment({
		"Set the color that reinforced blocks' textures have when reinforced_block_tint is enabled. This cannot be overridden by servers, and will be applied the same to all blocks. Grayscale values look best.",
		"Format: 0xRRGGBB"})
	public static int reinforcedBlockTintColor = 0x999999;

	@Name("Laser damage")
	@RangeDouble(min = 0.0D)
	@Comment("Defines the damage inflicted to an entity if it passes through a laser with installed harming module. This is given in health points, meaning 2 health points = 1 heart")
	public static double laserDamage = 10.0D;

	@Name("Incorrect Passcode Damage")
	@RangeInt(min = 1)
	@Comment({
		"Defines the damage that a block requiring a passcode deals to the player, if the player enters an incorrect code. This only works if a harming module is installed.",
		"Default is two hearts of damage."})
	public static int incorrectPasscodeDamage = 4;

	@Name("Sentry Bullet Damage")
	@RangeInt(min = 0)
	@Comment({
			"Set the amount of damage the default Sentry bullet inflicts onto the mobs it hits. This will not affect other projectiles the Sentry can use, like arrows.",
			"Default is one heart."})
	public static int sentryBulletDamage = 2;

	@Name("Sentry Attackable Entities Allowlist")
	@Comment("Add entities to this list that the Sentry currently does not attack, but that you want the Sentry to attack. The denylist takes priority over the allowlist.")
	public static String[] sentryAttackableEntitiesAllowlist = {};

	@Name("Sentry Attackable Entities Denylist")
	@Comment("Add entities to this list that the Sentry currently attacks, but that you want the Sentry to NOT attack. The denylist takes priority over the allowlist.")
	public static String[] sentryAttackableEntitiesDenylist = {};

	@Name("Reinforced Suffocation Damage")
	@Comment("Set the amount of damage the player receives when they are suffocating in a reinforced block. The default is two and a half hearts. If the value is set to -1, vanilla suffocation damage will be used.")
	@RangeInt(min = -1)
	public static int reinforcedSuffocationDamage = 5;

	@Name("Allow Camera Night Vision")
	@Comment("Set this to false to disallow players to activate night vision without having the potion effect when looking through cameras.")
	public static boolean allowCameraNightVision = true;

	@Name("Passcode Check Cooldown")
	@RangeInt(min = 0, max = 2000)
	@Comment("Defines the amount of time in milliseconds that needs to pass between two separate attempts from a player to enter a passcode.")
	public static int passcodeCheckCooldown = 250;

	@Name("Passcode Spam Log Warning Enabled")
	@Comment("Set this to false to disable the log warning that is sent whenever a player tries to enter a passcode while on passcode cooldown.")
	public static boolean passcodeSpamLogWarningEnabled = true;

	@Name("Passcode Spam Log Warning")
	@Comment("The warning that is sent into the server log whenever a player tries to enter a passcode while on passcode cooldown. \"%1$s\" will be replaced with the player's name, \"%2$s\" with the passcode-protected object's name and \"%3$s\" with the object's position and dimension.")
	public static String passcodeSpamLogWarning = "Player \"%1$s\" tried to enter a passcode into \"%2$s\" at position [%3$s] too quickly!";

	@Name("Disable In-world Un-/reinforcing")
	@Comment("Setting this to false disables the ability of the Universal Block Reinforcer to (un-)reinforce blocks that are placed in the world.")
	public static boolean inWorldUnReinforcing = true;

	@Name("Vanilla Tool Block Breaking")
	@Comment("Whether SecurityCraft's blocks should be broken using vanilla tools (axe, shovel, hoe, ...), instead of the Universal Block Remover. If set to true, this will disable the Universal Block Remover.")
	public static boolean vanillaToolBlockBreaking = true;

	@Name("Always Drop")
	@Comment({
		"Whether SecurityCraft's blocks always drop themselves no matter which tool is used. If this is set to false, the correct tool must be used for the block to drop (e.g. pickaxe for reinforced stone, or anything for reinforced dirt).",
		"This only applies when \"Vanilla Tool Block Breaking\" is set to true."
	})
	public static boolean alwaysDrop = true;

	@Name("Debug Camera Reset Tracing")
	@Comment("If this debug feature is enabled, SecurityCraft will attempt to find and report mods that prevent the feature of viewing security cameras from working when they immediately reset the player's camera entity.")
	public static boolean debugCameraResetTracing = false;

	@Name("Frame Feed Render Distance")
	@RangeInt(min = 2, max = 32)
	@Comment("Set the radius in which chunks viewed in a frame camera feed should be requested from the server and rendered. If this config has a higher value than the vanilla \"Render Distance\" option or the \"view-distance\" server property, the smaller value is used instead.")
	public static int frameFeedRenderDistance = 16;

	@Name("Frame Feed Resolution")
	@RangeInt(min = 1, max = 16384)
	@Comment("Set the resolution of the Frame camera feed. This is always a square resolution. Smaller values will be less detailed, higher values may lead to diminishing returns.")
	public static int frameFeedResolution = 512;

	@Name("Frame Feed FPS Limit")
	@RangeInt(min = 10, max = 260)
	@Comment("The maximum amount of frames per second the Frame camera feed renders at. Higher values will lead to worse performance.")
	public static int frameFeedFpsLimit = 30;

	@Name("Frame Feed Viewing Enabled")
	@Comment("Set this to false to disable the feature that camera feeds can be viewed in frames. While this feature is generally stable, it may also impact server performance due to loading chunks within all active frame cameras' views.")
	public static boolean frameFeedViewingEnabled = true;

	@Name("Frame Feed View Distance")
	@RangeInt(min = 2, max = 32)
	@Comment("Set the radius in which chunks viewed in a frame camera should be loaded and sent to players. If this config has a higher value than the \"view-distance\" server property or the vanilla \"Render Distance\" option of the player requesting the chunks, the smaller value is used instead.")
	public static int frameFeedViewDistance = 16;

	@Name("Frame Feed Forceloading Limit")
	@RangeInt(min = -1)
	@Comment("Set the limit of chunks per dimension that may be forceloaded around frame feeds. A value of 0 will prevent any frame feed from forceloading chunks. A value of -1 will allow an unlimited number of chunks to be forceloaded by frame feeds.")
	public static int frameFeedForceloadingLimit = -1;

	@Name("Allow Breaking Non-owned Blocks")
	@Comment({
		"Whether players who are not the owner of a block can still destroy it.",
		"This applies regardless of what \"Vanilla Tool Block Breaking\" is set to."
	})
	public static boolean allowBreakingNonOwnedBlocks = false;

	@Name("Non-owned Breaking Slowdown")
	@RangeDouble(min = 0.0D)
	@Comment({
		"How much slower it should be to break a block that is not owned by the player breaking it.",
		"The value is calculated as the normal block breaking speed divided by the non-owned block breaking slowdown. Example: A value of 2.0 means it takes twice as long to break the block.",
		"This only applies when \"Allow Breaking Non-owned Blocks\" and \"Vanilla Tool Block Breaking\" are set to true."
	})
	public static double nonOwnedBreakingSlowdown = 1.0D;

	/**
	 * @deprecated Use {@link #TASER_EFFECTS}
	 */
	@Deprecated
	@Name("Taser effects")
	@Comment({
		"Add effects to this list that you want the taser to inflict onto the mobs it hits. One entry corresponds to one effect, and is formatted like this:",
		"effect_namespace:effect_path|duration|amplifier",
		"Example: The entry \"minecraft:slowness|20|1\" defines slowness 1 for 1 second (20 ticks = 1 second)."})
	public static String[] taserEffectsValue = {
			"minecraft:weakness|200|2",
			"minecraft:nausea|200|2",
			"minecraft:slowness|200|2"
	};

	/**
	 * @deprecated Use {@link #POWERED_TASER_EFFECTS}
	 */
	@Deprecated
	@Name("Powered taser effects")
	@Comment({
		"Add effects to this list that you want the powered taser to inflict onto the mobs it hits. One entry corresponds to one effect, and is formatted like this:",
		"effect_namespace:effect_path|duration|amplifier",
		"Example: The entry \"minecraft:slowness|20|1\" defines slowness 1 for 1 second (20 ticks = 1 second)."})
	public static String[] poweredTaserEffectsValue = {
			"minecraft:weakness|400|5",
			"minecraft:nausea|400|5",
			"minecraft:slowness|400|5"
	};
	//@formatter:on
	@Ignore
	public static final List<Supplier<PotionEffect>> TASER_EFFECTS = new ArrayList<>();
	@Ignore
	public static final List<Supplier<PotionEffect>> POWERED_TASER_EFFECTS = new ArrayList<>();

	private ConfigHandler() {}

	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event) {
		if (SecurityCraft.MODID.equals(event.getModID())) {
			loadEffects();
			updateTeamPrecedenceFromConfigValues(teamOwnershipPrecedence);
		}
	}

	public static void loadEffects() {
		loadEffects(taserEffectsValue, TASER_EFFECTS);
		loadEffects(poweredTaserEffectsValue, POWERED_TASER_EFFECTS);
	}

	private static void loadEffects(String[] effectsValue, List<Supplier<PotionEffect>> effects) {
		effects.clear();

		for (String entry : effectsValue) {
			String[] split = entry.split("\\|");

			if (split.length != 3) {
				SecurityCraft.LOGGER.warn("Not enough information provided for effect \"{}\", skipping", entry);
				continue;
			}

			int duration = Integer.parseInt(split[1]);
			int amplifier = Integer.parseInt(split[2]);

			if (!validateValue(duration, entry) || !validateValue(amplifier, entry))
				continue;

			ResourceLocation effectLocation = new ResourceLocation(split[0]);

			if (!ForgeRegistries.POTIONS.containsKey(effectLocation)) {
				SecurityCraft.LOGGER.warn("Effect \"{}\" does not exist, skipping", effectLocation);
				continue;
			}

			//the amplifier is actually 0-indexed, but 1-indexed in the config for ease of use
			effects.add(() -> new PotionEffect(ForgeRegistries.POTIONS.getValue(effectLocation), duration, amplifier - 1));
		}
	}

	private static boolean validateValue(int value, String entry) {
		if (value <= 0) {
			SecurityCraft.LOGGER.warn("Value \"{}\" cannot be less than or equal to zero for entry \"{}\", skipping", value, entry);
			return false;
		}

		return true;
	}

	public static void updateTeamPrecedenceFromConfigValues(String[] precedence) {
		//@formatter:off
		TeamUtils.setPrecedence(Arrays.stream(precedence)
				.distinct()
				.map(s -> {
					try {
						return Enum.valueOf(TeamUtils.TeamType.class, s);
					}
					catch (IllegalArgumentException e) {}

					return TeamUtils.TeamType.NO_OP;
				})
				.map(TeamUtils.TeamType::getTeamHandler)
				.filter(Objects::nonNull)
				.collect(Collectors.toList()));
		//@formatter:on

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)
			SecurityCraft.network.sendToAll(new UpdateTeamPrecedence(precedence));
	}
}
