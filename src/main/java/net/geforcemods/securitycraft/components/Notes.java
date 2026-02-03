package net.geforcemods.securitycraft.components;

import java.util.List;
import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public record Notes(List<NoteWrapper> notes) implements TooltipProvider {
	public static final Notes EMPTY = new Notes(List.of());
	//@formatter:off
	public static final Codec<Notes> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(NoteWrapper.CODEC.listOf().fieldOf("notes").forGetter(Notes::notes))
			.apply(instance, Notes::new));
	public static final StreamCodec<ByteBuf, Notes> STREAM_CODEC = StreamCodec.composite(
			NoteWrapper.STREAM_CODEC.apply(ByteBufCodecs.list()), Notes::notes,
			Notes::new);
	//@formatter:on

	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag, DataComponentGetter componentGetter) {
		lineAdder.accept(Utils.localize("tooltip.securitycraft.component.notes", notes.size()).withStyle(Utils.GRAY_STYLE));
	}

	/**
	 * A simple wrapper that makes it slightly easier to store and compare notes with
	 */
	public static record NoteWrapper(int id, String instrumentName, String customSound) {

		//@formatter:off
		public static final Codec<NoteWrapper> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
						Codec.INT.fieldOf("id").forGetter(NoteWrapper::id),
						Codec.STRING.fieldOf("instrument").forGetter(NoteWrapper::instrumentName),
						Codec.STRING.fieldOf("custom_sound").forGetter(NoteWrapper::customSound))
				.apply(instance, NoteWrapper::new));
		public static final StreamCodec<ByteBuf, NoteWrapper> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, NoteWrapper::id,
				ByteBufCodecs.STRING_UTF8, NoteWrapper::instrumentName,
				ByteBufCodecs.STRING_UTF8, NoteWrapper::customSound,
				NoteWrapper::new);
		//@formatter:on
		/**
		 * Checks to see if a passed note ID and instrument matches the info of this note
		 *
		 * @param note the note ID to check
		 * @param instrument the instrument to check
		 * @param customSoundId the id of a potentially played custom sound
		 */
		public boolean isSameNote(int note, NoteBlockInstrument instrument, String customSoundId) {
			return instrumentName.equals(instrument.getSerializedName()) && (!instrument.isTunable() || id == note) && (customSound.isEmpty() || customSound.equals(customSoundId));
		}
	}
}
