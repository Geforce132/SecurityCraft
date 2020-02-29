package net.geforcemods.securitycraft;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigHandler {

	public static final ForgeConfigSpec CONFIG_SPEC;
	public static final ConfigHandler CONFIG;

	static {
		Pair<ConfigHandler,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ConfigHandler::new);

		CONFIG_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public BooleanValue allowCodebreakerItem;
	public BooleanValue allowAdminTool;
	public BooleanValue shouldSpawnFire;
	public BooleanValue ableToBreakMines;
	public BooleanValue ableToCraftKeycard1;
	public BooleanValue ableToCraftKeycard2;
	public BooleanValue ableToCraftKeycard3;
	public BooleanValue ableToCraftKeycard4;
	public BooleanValue ableToCraftKeycard5;
	public BooleanValue ableToCraftLUKeycard;
	public BooleanValue smallerMineExplosion;
	public BooleanValue mineExplodesWhenInCreative;
	public DoubleValue portableRadarSearchRadius;
	public IntValue usernameLoggerSearchRadius;
	public IntValue laserBlockRange;
	public IntValue alarmTickDelay;
	public IntValue portableRadarDelay;
	public IntValue claymoreRange;
	public IntValue imsRange;
	public IntValue inventoryScannerRange;
	public IntValue maxAlarmRange;
	public DoubleValue motionActivatedLightSearchRadius;
	public BooleanValue allowBlockClaim;
	public BooleanValue sayThanksMessage;
	public DoubleValue alarmSoundVolume;
	public DoubleValue cameraSpeed;
	public BooleanValue respectInvisibility;

	ConfigHandler(ForgeConfigSpec.Builder builder)
	{
		allowCodebreakerItem = builder
				.translation("config.securitycraft:isCodebreakerAllowed")
				.comment("Can the codebreaker be used?")
				.define("allowCodebreakerItem", true);

		allowAdminTool = builder
				.translation("config.securitycraft:allowAdminTool")
				.comment("Can the admin tool be used?")
				.define("allowAdminTool", false);

		shouldSpawnFire = builder
				.translation("config.securitycraft:shouldSpawnFire")
				.comment("Should mines spawn fire after exploding?")
				.define("shouldSpawnFire", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToBreakMines = builder
				.translation("config.securitycraft:ableToBreakMines")
				.comment("Should players be able to break a mine without it exploding?")
				.worldRestart()
				.define("ableToBreakMines", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftKeycard1 = builder
				.translation("config.securitycraft:ableToCraftKeycard1")
				.comment("Is the level 1 keycard craftable?")
				.worldRestart()
				.define("ableToCraftKeycard1", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftKeycard2 = builder
				.translation("config.securitycraft:ableToCraftKeycard2")
				.comment("Is the level 2 keycard craftable?")
				.worldRestart()
				.define("ableToCraftKeycard2", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftKeycard3 = builder
				.translation("config.securitycraft:ableToCraftKeycard3")
				.comment("Is the level 3 keycard craftable?")
				.worldRestart()
				.define("ableToCraftKeycard3", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftKeycard4 = builder
				.translation("config.securitycraft:ableToCraftKeycard4")
				.comment("Is the level 4 keycard craftable?")
				.worldRestart()
				.define("ableToCraftKeycard4", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftKeycard5 = builder
				.translation("config.securitycraft:ableToCraftKeycard5")
				.comment("Is the level 5 keycard craftable?")
				.worldRestart()
				.define("ableToCraftKeycard5", true);

		//TODO: worldRestart() -> mcRestart() once available
		ableToCraftLUKeycard = builder
				.translation("config.securitycraft:ableToCraftLUKeycard")
				.comment("Is the limited use keycard craftable?")
				.worldRestart()
				.define("ableToCraftLUKeycard", true);

		smallerMineExplosion = builder
				.translation("config.securitycraft:smallerMineExplosion")
				.comment("Should mines' explosions be smaller than usual.")
				.define("smallerMineExplosion", false);

		mineExplodesWhenInCreative = builder
				.translation("config.securitycraft:mineExplodesWhenInCreative")
				.comment("Should mines explode if broken while in Creative mode?")
				.define("mineExplodesWhenInCreative", true);

		portableRadarSearchRadius = builder
				.translation("config.securitycraft:portableRadarSearchRadius")
				.comment("From how many blocks can the portable radar detect players?")
				.defineInRange("portableRadarSearchRadius", 25.0D, 0.0D, Double.MAX_VALUE);

		usernameLoggerSearchRadius = builder
				.translation("config.securitycraft:usernameLoggerSearchRadius")
				.comment("From how many blocks can the username logger detect players?")
				.defineInRange("usernameLoggerSearchRadius", 3, 0, Integer.MAX_VALUE);

		laserBlockRange = builder
				.translation("config.securitycraft:laserBlockRange")
				.comment("From how many blocks away can a laser block connect to another laser block?")
				.defineInRange("laserBlockRange", 5, 0, Integer.MAX_VALUE);

		alarmTickDelay = builder
				.translation("config.securitycraft:alarmTickDelay")
				.comment("If an alarm is activated, how many seconds in-between alarm sounds effects?")
				.defineInRange("alarmTickDelay", 2, 0, Integer.MAX_VALUE);

		portableRadarDelay = builder
				.translation("config.securitycraft:portableRadarDelay")
				.comment("If a portable radar is activated, how many seconds should pass before the radar searches again?")
				.defineInRange("portableRadarDelay", 4, 0, Integer.MAX_VALUE);

		claymoreRange = builder
				.translation("config.securitycraft:claymoreRange")
				.comment("From how many blocks can the claymore mine be tripped from?")
				.defineInRange("claymoreRange", 5, 0, Integer.MAX_VALUE);

		imsRange = builder
				.translation("config.securitycraft:imsRange")
				.comment("How many blocks away can the I.M.S. detect players from?")
				.defineInRange("imsRange", 12, 0, Integer.MAX_VALUE);

		inventoryScannerRange = builder
				.translation("config.securitycraft:inventoryScannerRange")
				.comment("From how many blocks away can an inventory scanner connect to another inventory scanner?")
				.defineInRange("inventoryScannerRange", 2, 0, Integer.MAX_VALUE);

		maxAlarmRange = builder
				.translation("config.securitycraft:maxAlarmRange")
				.comment("What is the maximum value that can be set for an alarm's range option? Do note, that this may be limited by chunk loading distance. Higher values may also not be finetuneable.")
				.defineInRange("maxAlarmRange", 100, 1, Integer.MAX_VALUE);

		motionActivatedLightSearchRadius = builder
				.translation("config.securitycraft:motionActivatedLightSearchRadius")
				.comment("How many blocks away can the Motion Activated Light detect entities from?")
				.defineInRange("motionActivatedLightSearchRadius", 5.0D, 0.0D, Double.MAX_VALUE);

		allowBlockClaim = builder
				.translation("config.securitycraft:allowBlockClaim")
				.comment("Allow claiming unowned blocks?")
				.define("allowBlockClaim", false);

		sayThanksMessage = builder
				.translation("config.securitycraft:sayThanksMessage")
				.comment("Display a 'tip' message at spawn?")
				.define("sayThanksMessage", true);

		alarmSoundVolume = builder
				.translation("config.securitycraft:alarmSoundVolume")
				.comment("What volume should the alarm sound effect be played at?")
				.defineInRange("alarmSoundVolume", 0.3D, 0.0D, Double.MAX_VALUE);

		cameraSpeed = builder
				.translation("config.securitycraft:cameraSpeed")
				.comment("How fast can you rotate when mounted to a camera and holding W-A-S-D?")
				.defineInRange("cameraSpeed", 2.0D, 0.0D, Double.MAX_VALUE);

		respectInvisibility = builder
				.translation("config.securitycraft:respectInvisibility")
				.comment("Should the sentry/inventory scanner/laser block/etc. ignore players and entities that are invisible?")
				.define("respect_invisibility", false);
	}
}
