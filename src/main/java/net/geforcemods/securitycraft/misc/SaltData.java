package net.geforcemods.securitycraft.misc;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

public class SaltData extends SavedData {
	private static final Object DUMMY = new Object();
	private static SaltData instance;
	private final Map<UUID, byte[]> saltMap = new ConcurrentHashMap<>();
	private final Map<UUID, Object> saltKeysInUse = new ConcurrentHashMap<>();

	private SaltData() {}

	public static void refreshLevel(ServerLevel level) {
		instance = level.getDataStorage().computeIfAbsent(new SavedData.Factory<>(SaltData::new, SaltData::load, null), "securitycraft-salts");
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
		instance.setDirty();
		return saltKey;
	}

	public static void removeSalt(UUID saltKey) {
		if (saltKey != null) {
			instance.saltMap.remove(saltKey);
			instance.saltKeysInUse.remove(saltKey);
			instance.setDirty();
		}
	}

	public static UUID copySaltToNewKey(UUID oldKey) {
		if (oldKey != null)
			return putSalt(getSalt(oldKey));

		return null;
	}

	public static SaltData load(CompoundTag tag) {
		SaltData saltData = new SaltData();
		ListTag listtag = tag.getList("Salts", Tag.TAG_COMPOUND);

		for (int i = 0; i < listtag.size(); ++i) {
			CompoundTag saltTag = listtag.getCompound(i);

			saltData.saltMap.put(UUID.fromString(saltTag.getString("key")), saltTag.getByteArray("salt"));
		}

		return saltData;
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag saltTable = new ListTag();

		for (Map.Entry<UUID, byte[]> saltMapping : saltMap.entrySet()) {
			CompoundTag saltTag = new CompoundTag();

			saltTag.putString("key", saltMapping.getKey().toString());
			saltTag.putByteArray("salt", saltMapping.getValue());
			saltTable.add(saltTag);
		}

		tag.put("Salts", saltTable);
		return tag;
	}
}
