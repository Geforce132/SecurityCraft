package net.geforcemods.securitycraft.misc;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class SaltData extends WorldSavedData {
	private static SaltData instance;
	private final Map<UUID, byte[]> saltMap = new HashMap<>();

	private SaltData() {
		super("securitycraft-salts");
	}

	public static void refreshLevel(ServerWorld level) {
		instance = level.getDataStorage().computeIfAbsent(SaltData::new, "securitycraft-salts");
	}

	public static void invalidate() {
		instance = null;
	}

	public static boolean containsKey(UUID saltKey) {
		if (saltKey == null)
			return false;

		return instance.saltMap.containsKey(saltKey);
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
		instance.setDirty();
		return saltKey;
	}

	public static void removeSalt(UUID saltKey) {
		if (saltKey != null) {
			instance.saltMap.remove(saltKey);
			instance.setDirty();
		}
	}

	@Override
	public void load(CompoundNBT tag) {
		ListNBT listtag = tag.getList("Salts", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < listtag.size(); ++i) {
			CompoundNBT saltTag = listtag.getCompound(i);

			saltMap.put(UUID.fromString(saltTag.getString("key")), saltTag.getByteArray("salt"));
		}
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		ListNBT saltTable = new ListNBT();

		for (Map.Entry<UUID, byte[]> saltMapping : saltMap.entrySet()) {
			CompoundNBT saltTag = new CompoundNBT();

			saltTag.putString("key", saltMapping.getKey().toString());
			saltTag.putByteArray("salt", saltMapping.getValue());
			saltTable.add(saltTag);
		}

		tag.put("Salts", saltTable);
		return tag;
	}
}
