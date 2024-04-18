package net.geforcemods.securitycraft;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.GameData;

public class SCStreamCodecs {
	public static final StreamCodec<FriendlyByteBuf, BlockState> BLOCK_STATE = new StreamCodec<>() {
		@Override
		public BlockState decode(FriendlyByteBuf buf) {
			return GameData.getBlockStateIDMap().byId(buf.readInt());
		}

		@Override
		public void encode(FriendlyByteBuf buf, BlockState state) {
			buf.writeInt(GameData.getBlockStateIDMap().getId(state));
		}
	};
	public static final StreamCodec<FriendlyByteBuf, ResourceLocation> RESOURCE_LOCATION = new StreamCodec<>() {
		@Override
		public ResourceLocation decode(FriendlyByteBuf buf) {
			return buf.readResourceLocation();
		}

		@Override
		public void encode(FriendlyByteBuf buf, ResourceLocation resourceLocation) {
			buf.writeResourceLocation(resourceLocation);
		}
	};
	public static final StreamCodec<FriendlyByteBuf, boolean[]> BOOLEAN_ARRAY = new StreamCodec<>() {
		@Override
		public boolean[] decode(FriendlyByteBuf buf) {
			int size = buf.readVarInt();
			boolean[] array = new boolean[size];

			for (int i = 0; i < size; i++) {
				array[i] = buf.readBoolean();
			}

			return array;
		}

		@Override
		public void encode(FriendlyByteBuf buf, boolean[] array) {
			buf.writeVarInt(array.length);

			for (int i = 0; i < array.length; i++) {
				buf.writeBoolean(array[i]);
			}
		}
	};

	private SCStreamCodecs() {}
}
