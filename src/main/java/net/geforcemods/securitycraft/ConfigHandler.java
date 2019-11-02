package net.geforcemods.securitycraft;

import cpw.mods.fml.common.Loader;
import net.minecraftforge.common.config.Property;

public class ConfigHandler
{
	public boolean allowCodebreakerItem;
	public boolean allowAdminTool;
	public boolean shouldSpawnFire;
	public boolean ableToBreakMines;
	public boolean ableToCraftKeycard1;
	public boolean ableToCraftKeycard2;
	public boolean ableToCraftKeycard3;
	public boolean ableToCraftKeycard4;
	public boolean ableToCraftKeycard5;
	public boolean ableToCraftLUKeycard;
	public boolean smallerMineExplosion;
	public boolean mineExplodesWhenInCreative;
	public boolean sayThanksMessage;
	public boolean fiveMinAutoShutoff;
	public boolean useLookingGlass;
	public boolean checkForUpdates;
	public double portableRadarSearchRadius;
	public int usernameLoggerSearchRadius;
	public int laserBlockRange;
	public int alarmTickDelay;
	public double alarmSoundVolume;
	public int portableRadarDelay;
	public int claymoreRange;
	public int imsRange;
	public float cameraSpeed;
	public int inventoryScannerRange;
	public double motionActivatedLightSearchRadius;
	public boolean allowBlockClaim;

	public void setupConfiguration()
	{
		SecurityCraft.configFile.load();

		Property dummyProp;

		dummyProp = SecurityCraft.configFile.get("options", "Is codebreaker allowed?", true);
		dummyProp.setLanguageKey("config.securitycraft:isCodebreakerAllowed");
		allowCodebreakerItem = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Is admin tool allowed?", false);
		dummyProp.setLanguageKey("config.securitycraft:allowAdminTool");
		allowAdminTool = dummyProp.getBoolean(false);

		dummyProp = SecurityCraft.configFile.get("options", "Mine(s) spawn fire when detonated?", true);
		dummyProp.setLanguageKey("config.securitycraft:shouldSpawnFire");
		shouldSpawnFire = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Are mines unbreakable?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToBreakMines");
		ableToBreakMines = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable level 1 keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftKeycard1");
		ableToCraftKeycard1 = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable level 2 keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftKeycard2");
		ableToCraftKeycard2 = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable level 3 keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftKeycard3");
		ableToCraftKeycard3 = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable level 4 keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftKeycard4");
		ableToCraftKeycard4 = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable level 5 keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftKeycard5");
		ableToCraftKeycard5 = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Craftable Limited Use keycard?", true);
		dummyProp.setLanguageKey("config.securitycraft:ableToCraftLUKeycard");
		ableToCraftLUKeycard = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Mines use a smaller explosion?", false);
		dummyProp.setLanguageKey("config.securitycraft:smallerMineExplosion");
		smallerMineExplosion = dummyProp.getBoolean(false);

		dummyProp = SecurityCraft.configFile.get("options", "Mines explode when broken in Creative?", true);
		dummyProp.setLanguageKey("config.securitycraft:mineExplodesWhenInCreative");
		mineExplodesWhenInCreative = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Monitors shutoff after 5 minutes?", true);
		dummyProp.setLanguageKey("config.securitycraft:fiveMinAutoShutoff");
		fiveMinAutoShutoff = dummyProp.getBoolean(true);

		if(!Loader.isModLoaded("LookingGlass"))
			useLookingGlass = false;
		else{
			dummyProp = SecurityCraft.configFile.get("options", "Use LookingGlass for viewing cameras?", true);
			dummyProp.setLanguageKey("config.securitycraft:useLookingGlass");
			useLookingGlass = dummyProp.getBoolean(true);
		}

		dummyProp = SecurityCraft.configFile.get("options", "Portable radar search radius:", 25);
		dummyProp.setLanguageKey("config.securitycraft:portableRadarSearchRadius");
		portableRadarSearchRadius = dummyProp.getDouble(25);

		dummyProp = SecurityCraft.configFile.get("options", "Username logger search radius:", 3);
		dummyProp.setLanguageKey("config.securitycraft:usernameLoggerSearchRadius");
		usernameLoggerSearchRadius = dummyProp.getInt(3);

		dummyProp = SecurityCraft.configFile.get("options", "Laser range:", 5);
		dummyProp.setLanguageKey("config.securitycraft:laserBlockRange");
		laserBlockRange = dummyProp.getInt(5);

		dummyProp = SecurityCraft.configFile.get("options", "Delay between alarm sounds (seconds):", 2);
		dummyProp.setLanguageKey("config.securitycraft:alarmTickDelay");
		alarmTickDelay = dummyProp.getInt(2);

		dummyProp = SecurityCraft.configFile.get("options", "Alarm sound volume:", 0.8D);
		dummyProp.setLanguageKey("config.securitycraft:alarmSoundVolume");
		alarmSoundVolume = dummyProp.getDouble(0.8D);

		dummyProp = SecurityCraft.configFile.get("options", "Portable radar delay (seconds):", 4);
		dummyProp.setLanguageKey("config.securitycraft:portableRadarDelay");
		portableRadarDelay = dummyProp.getInt(4);

		dummyProp = SecurityCraft.configFile.get("options", "Claymore range:", 5);
		dummyProp.setLanguageKey("config.securitycraft:claymoreRange");
		claymoreRange = dummyProp.getInt(5);

		dummyProp = SecurityCraft.configFile.get("options", "IMS range:", 12);
		dummyProp.setLanguageKey("config.securitycraft:imsRange");
		imsRange = dummyProp.getInt(12);

		dummyProp = SecurityCraft.configFile.get("options", "Display a 'tip' message at spawn?", true);
		dummyProp.setLanguageKey("config.securitycraft:sayThanksMessage");
		sayThanksMessage = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Is debug mode? (not recommended!)", false);
		dummyProp.setLanguageKey("config.securitycraft:debuggingMode");
		SecurityCraft.debug = dummyProp.getBoolean(false);

		dummyProp = SecurityCraft.configFile.get("options", "Camera Speed when not using LookingGlass:", 2);
		dummyProp.setLanguageKey("config.securitycraft:cameraSpeed");
		cameraSpeed = dummyProp.getInt(2);

		dummyProp = SecurityCraft.configFile.get("options", "Should check for updates on Github?", true);
		dummyProp.setLanguageKey("config.securitycraft:checkForUpdates");
		checkForUpdates = dummyProp.getBoolean(true);

		dummyProp = SecurityCraft.configFile.get("options", "Inventory Scanner range:", 2);
		dummyProp.setLanguageKey("config.securitycraft:inventoryScannerRange");
		inventoryScannerRange = dummyProp.getInt(2);

		dummyProp = SecurityCraft.configFile.get("options", "Motion-activated light range:", 5);
		dummyProp.setLanguageKey("config.securitycraft:motionLightSearchRadius");
		motionActivatedLightSearchRadius = dummyProp.getDouble(5.0D);

		dummyProp = SecurityCraft.configFile.get("options", "Allow claiming unowned blocks?", false);
		dummyProp.setLanguageKey("config.securitycraft:allowBlockClaim");
		allowBlockClaim = dummyProp.getBoolean(false);

		if(SecurityCraft.configFile.hasChanged())
			SecurityCraft.configFile.save();
	}
}
