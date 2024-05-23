package net.geforcemods.securitycraft.misc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class SaltData extends WorldSavedData {
	private static final Object DUMMY = new Object();
	private static SaltData instance;
	private final Map<UUID, byte[]> saltMap = new ConcurrentHashMap<>();
	private final Map<UUID, Object> saltKeysInUse = new ConcurrentHashMap<>();

	public SaltData(String name) {
		super(name);
	}

	public static void refreshLevel(WorldServer level) {
		WorldSavedData data = level.loadData(SaltData.class, "securitycraft-salts");

		if (data == null) {
			data = new SaltData("securitycraft-salts");
			level.setData("securitycraft-salts", data);
		}

		if (data instanceof SaltData)
			instance = (SaltData) data;
	}

	public static void invalidate() {
		instance = null;
	}

	public static boolean containsKey(UUID saltKey) {
		if (saltKey == null)
			return false;

		return instance.saltMap.containsKey(saltKey);
	}

	public static void setKeyInUse(UUID saltKey) {
		if (saltKey != null)
			instance.saltKeysInUse.put(saltKey, DUMMY);
	}

	public static boolean isKeyInUse(UUID saltKey) {
		if (saltKey == null)
			return false;

		return instance.saltKeysInUse.containsKey(saltKey);
	}

	public static byte[] getSalt(UUID saltKey) {
		if (saltKey == null)
			return null;

		byte[] salt = instance.saltMap.get(saltKey);

		return salt == null || salt.length == 0 ? null : salt;
	}

	public static UUID putSalt(byte[] salt) {
		UUID saltKey = UUID.randomUUID();

		instance.saltMap.put(saltKey, salt);
		setKeyInUse(saltKey);
		instance.markDirty();
		return saltKey;
	}

	public static void removeSalt(UUID saltKey) {
		if (saltKey != null) {
			instance.saltMap.remove(saltKey);
			instance.saltKeysInUse.remove(saltKey);
			instance.markDirty();
		}
	}

	public static UUID copySaltToNewKey(UUID oldKey) {
		if (oldKey != null)
			return putSalt(getSalt(oldKey));

		return null;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		NBTTagList listtag = tag.getTagList("Salts", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < listtag.tagCount(); ++i) {
			NBTTagCompound saltTag = listtag.getCompoundTagAt(i);

			saltMap.put(UUID.fromString(saltTag.getString("key")), saltTag.getByteArray("salt"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		NBTTagList saltTable = new NBTTagList();

		for (Map.Entry<UUID, byte[]> saltMapping : saltMap.entrySet()) {
			NBTTagCompound saltTag = new NBTTagCompound();

			saltTag.setString("key", saltMapping.getKey().toString());
			saltTag.setByteArray("salt", saltMapping.getValue());
			saltTable.appendTag(saltTag);
		}

		tag.setTag("Salts", saltTable);
		return tag;
	}
}
