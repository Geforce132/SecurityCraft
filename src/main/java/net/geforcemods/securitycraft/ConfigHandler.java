package net.geforcemods.securitycraft;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Config(modid = SecurityCraft.MODID, category = "options")
@EventBusSubscriber(modid = SecurityCraft.MODID)
public class ConfigHandler {
	@Ignore
	private static final Logger LOGGER = LogManager.getLogger();

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

	@Name("Are mines breakable?")
	@LangKey("config.securitycraft:ableToBreakMines")
	@RequiresMcRestart
	public static boolean ableToBreakMines = true;

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
	public static int inventoryScannerRange = 2;

	@Name("Maximum Alarm range:")
	@LangKey("config.securitycraft:maxAlarmRange")
	@RangeInt(min = 1)
	public static int maxAlarmRange = 100;

	@Name("Allow claiming unowned blocks?")
	@LangKey("config.securitycraft:allowBlockClaim")
	public static boolean allowBlockClaim = false;

	@Name("Respect invisibility?")
	@LangKey("config.securitycraft:respectInvisibility")
	public static boolean respectInvisibility = false;

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
		if (SecurityCraft.MODID.equals(event.getModID()))
			loadEffects();
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
				LOGGER.warn("Not enough information provided for effect \"{}\", skipping", entry);
				continue;
			}

			int duration = Integer.parseInt(split[1]);
			int amplifier = Integer.parseInt(split[2]);

			if (!validateValue(duration, entry) || !validateValue(amplifier, entry))
				continue;

			ResourceLocation effectLocation = new ResourceLocation(split[0]);

			if (!ForgeRegistries.POTIONS.containsKey(effectLocation)) {
				LOGGER.warn("Effect \"{}\" does not exist, skipping", effectLocation);
				continue;
			}

			//the amplifier is actually 0-indexed, but 1-indexed in the config for ease of use
			effects.add(() -> new PotionEffect(ForgeRegistries.POTIONS.getValue(effectLocation), duration, amplifier - 1));
		}
	}

	private static boolean validateValue(int value, String entry) {
		if (value <= 0) {
			LOGGER.warn("Value \"{}\" cannot be less than or equal to zero for entry \"{}\", skipping", value, entry);
			return false;
		}

		return true;
	}
}
