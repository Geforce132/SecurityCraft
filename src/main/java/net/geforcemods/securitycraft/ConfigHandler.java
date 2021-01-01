package net.geforcemods.securitycraft;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

@Config(modid=SecurityCraft.MODID, category="options")
public class ConfigHandler
{
	@Name("Is codebreaker allowed?")
	@LangKey("config.securitycraft:isCodebreakerAllowed")
	public static boolean allowCodebreakerItem = true;

	@Name("Is admin tool allowed?")
	@LangKey("config.securitycraft:allowAdminTool")
	public static boolean allowAdminTool = false;

	@Name("Mine(s) spawn fire when detonated?")
	@LangKey("config.securitycraft:shouldSpawnFire")
	public static boolean shouldSpawnFire = true;

	@Name("Are mines unbreakable?")
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

	@Name("Display a 'tip' message at spawn?")
	@LangKey("config.securitycraft:sayThanksMessage")
	public static boolean sayThanksMessage = true;

	@Name("Should check for updates on Github?")
	@LangKey("config.securitycraft:checkForUpdates")
	public static boolean checkForUpdates =true;

	@Name("Laser range:")
	@LangKey("config.securitycraft:laserBlockRange")
	public static int laserBlockRange = 5;

	@Name("Camera Speed when not using LookingGlass:")
	@LangKey("config.securitycraft:cameraSpeed")
	@RequiresMcRestart
	public static float cameraSpeed = 2.0F;

	@Name("Inventory Scanner range:")
	@LangKey("config.securitycraft:inventoryScannerRange")
	public static int inventoryScannerRange = 2;

	@Name("Maximum Alarm range:")
	@LangKey("config.securitycraft:maxAlarmRange")
	@RangeInt(min=1)
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
}
