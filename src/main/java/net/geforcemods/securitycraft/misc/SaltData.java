package net.geforcemods.securitycraft.misc;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public class SaltData extends SavedData {
	//@formatter:off
	private record SaltEntry(UUID key, byte[] salt) {
		private static final Codec<UUID> UUID_AS_STRING_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);
		private static final Codec<UUID> KEY_CODEC = Codec.withAlternative(UUIDUtil.CODEC, UUID_AS_STRING_CODEC);
		private static final Codec<byte[]> VALUE_CODEC = Codec.BYTE_BUFFER.xmap(ByteBuffer::array, ByteBuffer::wrap);
		public static final Codec<SaltEntry> CODEC = RecordCodecBuilder.create(i -> i.group(
				KEY_CODEC.fieldOf("key").forGetter(SaltEntry::key),
				VALUE_CODEC.fieldOf("salt").forGetter(SaltEntry::salt)
				).apply(i, SaltEntry::new));
	}

	public static final Codec<SaltData> CODEC = RecordCodecBuilder.create(i -> i.group(
			SaltEntry.CODEC.listOf().xmap(
					entryList -> new ConcurrentHashMap<>(entryList.stream().collect(Collectors.toMap(SaltEntry::key, SaltEntry::salt))),
					map -> map.entrySet().stream().map(entry -> new SaltEntry(entry.getKey(), entry.getValue())).toList())
			.fieldOf("Salts").forGetter(SaltData::saltMap)
			).apply(i, SaltData::new));
	//@formatter:on
	public static final SavedDataType<SaltData> TYPE = new SavedDataType<>("securitycraft-salts", SaltData::new, CODEC, DataFixTypes.SAVED_DATA_COMMAND_STORAGE);
	private static final Object DUMMY = new Object();
	private static SaltData instance;
	private final ConcurrentHashMap<UUID, byte[]> saltMap;
	private final Map<UUID, Object> saltKeysInUse = new ConcurrentHashMap<>();

	private SaltData() {
		this(new ConcurrentHashMap<>());
	}

	private SaltData(ConcurrentHashMap<UUID, byte[]> saltMap) {
		this.saltMap = saltMap;
	}

	public static void refreshLevel(ServerLevel level) {
		instance = level.getDataStorage().computeIfAbsent(TYPE);
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

	private ConcurrentHashMap<UUID, byte[]> saltMap() {
		return saltMap;
	}
}
