package net.geforcemods.securitycraft;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigHandler {

	public static class CommonConfig {
		public static final ForgeConfigSpec CONFIG_SPEC;
		public static final CommonConfig CONFIG;

		static {
			Pair<CommonConfig,ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);

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
		public DoubleValue motionActivatedLightSearchRadius;
		public BooleanValue debug;
		public BooleanValue allowBlockClaim;
		public BooleanValue sayThanksMessage;
		public BooleanValue checkForUpdates;
		public DoubleValue alarmSoundVolume;
		public DoubleValue cameraSpeed;

		public CommonConfig(ForgeConfigSpec.Builder builder)
		{

			allowCodebreakerItem = builder
					.translation("config.securitycraft:isCodebreakerAllowed")
					.comment("config.securitycraft:isCodebreakerAllowed.tooltip")
					.define("allowCodebreakerItem", true);

			allowAdminTool = builder
					.translation("config.securitycraft:allowAdminTool")
					.comment("config.securitycraft:allowAdminTool.tooltip")
					.define("allowAdminTool", false);

			shouldSpawnFire = builder
					.translation("config.securitycraft:shouldSpawnFire")
					.comment("config.securitycraft:shouldSpawnFire.tooltip")
					.define("shouldSpawnFire", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToBreakMines = builder
					.translation("config.securitycraft:ableToBreakMines")
					.comment("config.securitycraft:ableToBreakMines.tooltip")
					.worldRestart()
					.define("ableToBreakMines", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftKeycard1 = builder
					.translation("config.securitycraft:ableToCraftKeycard1")
					.comment("config.securitycraft:ableToCraftKeycard1.tooltip")
					.worldRestart()
					.define("ableToCraftKeycard1", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftKeycard2 = builder
					.translation("config.securitycraft:ableToCraftKeycard2")
					.comment("config.securitycraft:ableToCraftKeycard2.tooltip")
					.worldRestart()
					.define("ableToCraftKeycard2", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftKeycard3 = builder
					.translation("config.securitycraft:ableToCraftKeycard3")
					.comment("config.securitycraft:ableToCraftKeycard3.tooltip")
					.worldRestart()
					.define("ableToCraftKeycard3", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftKeycard4 = builder
					.translation("config.securitycraft:ableToCraftKeycard4")
					.comment("config.securitycraft:ableToCraftKeycard4.tooltip")
					.worldRestart()
					.define("ableToCraftKeycard4", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftKeycard5 = builder
					.translation("config.securitycraft:ableToCraftKeycard5")
					.comment("config.securitycraft:ableToCraftKeycard5.tooltip")
					.worldRestart()
					.define("ableToCraftKeycard5", true);

			//TODO: worldRestart() -> mcRestart() once available
			ableToCraftLUKeycard = builder
					.translation("config.securitycraft:ableToCraftLUKeycard")
					.comment("config.securitycraft:ableToCraftLUKeycard.tooltip")
					.worldRestart()
					.define("ableToCraftLUKeycard", true);

			smallerMineExplosion = builder
					.translation("config.securitycraft:smallerMineExplosion")
					.comment("config.securitycraft:smallerMineExplosion.tooltip")
					.define("smallerMineExplosion", false);

			mineExplodesWhenInCreative = builder
					.translation("config.securitycraft:mineExplodesWhenInCreative")
					.comment("config.securitycraft:mineExplodesWhenInCreative.tooltip")
					.define("mineExplodesWhenInCreative", true);

			portableRadarSearchRadius = builder
					.translation("config.securitycraft:portableRadarSearchRadius")
					.comment("config.securitycraft:portableRadarSearchRadius.tooltip")
					.defineInRange("portableRadarSearchRadius", 25.0D, 0.0D, Double.MAX_VALUE);

			usernameLoggerSearchRadius = builder
					.translation("config.securitycraft:usernameLoggerSearchRadius")
					.comment("config.securitycraft:usernameLoggerSearchRadius.tooltip")
					.defineInRange("usernameLoggerSearchRadius", 3, 0, Integer.MAX_VALUE);

			laserBlockRange = builder
					.translation("config.securitycraft:laserBlockRange")
					.comment("config.securitycraft:laserBlockRange.tooltip")
					.defineInRange("laserBlockRange", 5, 0, Integer.MAX_VALUE);

			alarmTickDelay = builder
					.translation("config.securitycraft:alarmTickDelay")
					.comment("config.securitycraft:alarmTickDelay.tooltip")
					.defineInRange("alarmTickDelay", 2, 0, Integer.MAX_VALUE);

			portableRadarDelay = builder
					.translation("config.securitycraft:portableRadarDelay")
					.comment("config.securitycraft:portableRadarDelay.tooltip")
					.defineInRange("portableRadarDelay", 4, 0, Integer.MAX_VALUE);

			claymoreRange = builder
					.translation("config.securitycraft:claymoreRange")
					.comment("config.securitycraft:claymoreRange.tooltip")
					.defineInRange("claymoreRange", 5, 0, Integer.MAX_VALUE);

			imsRange = builder
					.translation("config.securitycraft:imsRange")
					.comment("config.securitycraft:imsRange.tooltip")
					.defineInRange("imsRange", 12, 0, Integer.MAX_VALUE);

			inventoryScannerRange = builder
					.translation("config.securitycraft:inventoryScannerRange")
					.comment("config.securitycraft:inventoryScannerRange.tooltip")
					.defineInRange("inventoryScannerRange", 2, 0, Integer.MAX_VALUE);

			motionActivatedLightSearchRadius = builder
					.translation("config.securitycraft:motionActivatedLightSearchRadius")
					.comment("config.securitycraft:motionActivatedLightSearchRadius.tooltip")
					.defineInRange("motionActivatedLightSearchRadius", 5.0D, 0.0D, Double.MAX_VALUE);

			debug = builder
					.translation("config.securitycraft:debuggingMode")
					.comment("config.securitycraft:debuggingMode.tooltip")
					.define("debuggingMode", false);

			allowBlockClaim = builder
					.translation("config.securitycraft:allowBlockClaim")
					.comment("config.securitycraft:allowBlockClaim.tooltip")
					.define("allowBlockClaim", false);

			sayThanksMessage = builder
					.translation("config.securitycraft:sayThanksMessage")
					.comment("config.securitycraft:sayThanksMessage.tooltip")
					.define("sayThanksMessage", true);

			checkForUpdates = builder
					.translation("config.securitycraft:checkForUpdates")
					.comment("config.securitycraft:checkForUpdates.tooltip")
					.define("checkForUpdates", true);

			alarmSoundVolume = builder
					.translation("config.securitycraft:alarmSoundVolume")
					.comment("config.securitycraft:alarmSoundVolume.tooltip")
					.defineInRange("alarmSoundVolume", 0.3D, 0.0D, Double.MAX_VALUE);

			cameraSpeed = builder
					.translation("config.securitycraft:cameraSpeed")
					.comment("config.securitycraft:cameraSpeed.tooltip")
					.defineInRange("cameraSpeed", 2.0D, 0.0D, Double.MAX_VALUE);
		}
	}
}
