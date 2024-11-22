package net.geforcemods.securitycraft.components;

import java.util.Optional;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.items.KeycardItem;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record KeycardData(int signature, boolean limited, int usesLeft, Optional<String> usableBy) implements TooltipProvider {

	public static final KeycardData DEFAULT = new KeycardData(0, false, 0, Optional.empty());
	//@formatter:off
	public static final Codec<KeycardData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.INT.fieldOf("signature").forGetter(KeycardData::signature),
					Codec.BOOL.fieldOf("limited").forGetter(KeycardData::limited),
					Codec.INT.fieldOf("uses_left").forGetter(KeycardData::usesLeft),
					Codec.STRING.optionalFieldOf("usable_by").forGetter(KeycardData::usableBy))
			.apply(instance, KeycardData::new));
	public static final StreamCodec<ByteBuf, KeycardData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, KeycardData::signature,
			ByteBufCodecs.BOOL, KeycardData::limited,
			ByteBufCodecs.VAR_INT, KeycardData::usesLeft,
			ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), KeycardData::usableBy,
			KeycardData::new);
	//@formatter:on
	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag) {
		lineAdder.accept(Component.translatable("tooltip.securitycraft:keycard.signature", StringUtils.leftPad("" + signature, 5, "0")).setStyle(Utils.GRAY_STYLE));
		lineAdder.accept(Component.translatable("tooltip.securitycraft:keycard.usable_by", usableBy.map(Component::literal).orElse(Component.translatable("tooltip.securitycraft:keycard.everyone"))).setStyle(Utils.GRAY_STYLE));

		if (limited)
			lineAdder.accept(Component.translatable("tooltip.securitycraft:keycard.uses", usesLeft).setStyle(Utils.GRAY_STYLE));
		else
			lineAdder.accept(KeycardItem.LIMITED_INFO);
	}

	public KeycardData setSignature(int signature) {
		return setSignatureAndUsableBy(signature, usableBy);
	}

	public KeycardData setSignatureAndUsableBy(int signature, Optional<String> usableBy) {
		return new KeycardData(signature, limited, usesLeft, usableBy);
	}

	public KeycardData setUsesLeft(int usesLeft) {
		return setLimitedAndUsesLeft(limited, usesLeft);
	}

	public KeycardData setLimitedAndUsesLeft(boolean limited, int usesLeft) {
		return new KeycardData(signature, limited, usesLeft, usableBy);
	}
}
