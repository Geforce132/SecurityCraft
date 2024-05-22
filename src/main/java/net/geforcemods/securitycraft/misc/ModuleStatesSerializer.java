package net.geforcemods.securitycraft.misc;

import java.util.EnumMap;
import java.util.Map;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;

public class ModuleStatesSerializer implements EntityDataSerializer<Map<ModuleType, Boolean>> {
	@Override
	public void write(FriendlyByteBuf buf, Map<ModuleType, Boolean> value) {
		buf.writeVarInt(value.size());
		value.forEach((k, v) -> {
			buf.writeEnum(k);
			buf.writeBoolean(v);
		});
	}

	@Override
	public Map<ModuleType, Boolean> read(FriendlyByteBuf buf) {
		Map<ModuleType, Boolean> moduleStates = new EnumMap<>(ModuleType.class);
		int size = buf.readVarInt();

		for (int i = 0; i < size; i++) {
			moduleStates.put(buf.readEnum(ModuleType.class), buf.readBoolean());
		}

		return moduleStates;
	}

	@Override
	public EntityDataAccessor<Map<ModuleType, Boolean>> createAccessor(int id) {
		return new EntityDataAccessor<>(id, this);
	}

	@Override
	public Map<ModuleType, Boolean> copy(Map<ModuleType, Boolean> value) {
		return new EnumMap<>(value);
	}
}
