package net.geforcemods.securitycraft;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

public class ConfigHandler {

	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final Client CLIENT;
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final Server SERVER;

	static {
		Pair<Client,ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Client::new);
		Pair<Server,ForgeConfigSpec> serverSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);

		CLIENT_SPEC = clientSpecPair.getRight();
		CLIENT = clientSpecPair.getLeft();
		SERVER_SPEC = serverSpecPair.getRight();
		SERVER = serverSpecPair.getLeft();
	}

	public static class Client
	{
		public BooleanValue sayThanksMessage;
		public DoubleValue cameraSpeed;
		public BooleanValue reinforcedBlockTint;

		Client(ForgeConfigSpec.Builder builder)
		{
			sayThanksMessage = builder
					.comment("Display a 'tip' message at spawn?")
					.define("sayThanksMessage", true);

			cameraSpeed = builder
					.comment("How fast can you rotate when mounted to a camera and holding W-A-S-D?")
					.defineInRange("cameraSpeed", 2.0D, 0.0D, Double.MAX_VALUE);

			reinforcedBlockTint = builder
					.comment("Should reinforced blocks' textures be slightly darker than their vanilla counterparts? This setting can be overriden by servers.")
					.define("reinforced_block_tint", true);
		}
	}

	public static class Server
	{
		public BooleanValue allowCodebreakerItem;
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

		Server(ForgeConfigSpec.Builder builder)
		{
			allowCodebreakerItem = builder
					.comment("Can the codebreaker be used?")
					.define("allowCodebreakerItem", true);

			allowAdminTool = builder
					.comment("Can the admin tool be used?")
					.define("allowAdminTool", false);

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
					.comment("Set this to false if you want mines to not break blocks when they explode.")
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
					.comment("Set this to true to enable every player on a scoreboard team to own the blocks of every other player on the same team.",
							"This enables players on the same team to break each other's reinforced blocks, change options, add/remove modules, and have access to all other owner-restricted things.")
					.define("enable_team_ownership", false);
		}
	}
}
