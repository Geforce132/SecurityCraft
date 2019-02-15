package net.geforcemods.securitycraft;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

public class TileEntityIDDataFixer implements IFixableData
{
	public static final int VERSION = 1;
	private final Map<String, String> idsToFix;

	public TileEntityIDDataFixer()
	{
		idsToFix = ImmutableMap.<String, String>builder().put("minecraft:abstractownable", "securitycraft:ownable")
				.put("minecraft:abstractsc", "securitycraft:abstract")
				.put("minecraft:keypad", "securitycraft:keypad")
				.put("minecraft:laserblock", "securitycraft:laser_block")
				.put("minecraft:cagetrap", "securitycraft:cage_trap")
				.put("minecraft:keycardreader", "securitycraft:keycard_reader")
				.put("minecraft:inventoryscanner", "securitycraft:inventory_scanner")
				.put("minecraft:portableradar", "securitycraft:portable_radar")
				.put("minecraft:securitycamera", "securitycraft:security_camera")
				.put("minecraft:usernamelogger", "securitycraft:username_logger")
				.put("minecraft:retinalscanner", "securitycraft:retinal_scanner")
				.put("minecraft:keypadchest", "securitycraft:keypad_chest")
				.put("minecraft:alarm", "securitycraft:alarm")
				.put("minecraft:claymore", "securitycraft:claymore")
				.put("minecraft:keypadfurnace", "securitycraft:keypad_furnace")
				.put("minecraft:ims", "securitycraft:ims")
				.put("minecraft:protecto", "securitycraft:protecto")
				.put("minecraft:customizablescte", "securitycraft:customizable")
				.put("minecraft:scannerdoor", "securitycraft:scanner_door")
				.put("minecraft:secretsign", "securitycraft:secret_sign")
				.put("minecraft:motionlight", "securitycraft:motion_light").build();
	}

	@Override
	public NBTTagCompound fixTagCompound(NBTTagCompound tag)
	{
		String teID = tag.getString("id");

		tag.setString("id", idsToFix.getOrDefault(teID, teID)); //only change value if teID is in the map
		return tag;
	}

	@Override
	public int getFixVersion()
	{
		return VERSION;
	}
}
