package net.geforcemods.securitycraft.components;

import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record KeycardData(boolean linked, int signature, boolean limited, int usesLeft, String ownerName, String ownerUUID) implements TooltipProvider {

	public static final KeycardData DEFAULT = new KeycardData(false, 0, false, 0, "", "");
	//@formatter:off
	public static final Codec<KeycardData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.BOOL.fieldOf("linked").forGetter(KeycardData::linked),
					Codec.INT.fieldOf("signature").forGetter(KeycardData::signature),
					Codec.BOOL.fieldOf("limited").forGetter(KeycardData::limited),
					Codec.INT.fieldOf("uses_left").forGetter(KeycardData::usesLeft),
					Codec.STRING.fieldOf("owner_name").forGetter(KeycardData::ownerName),
					Codec.STRING.fieldOf("owner_uuid").forGetter(KeycardData::ownerUUID))
			.apply(instance, KeycardData::new));
	public static final StreamCodec<ByteBuf, KeycardData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, KeycardData::linked,
			ByteBufCodecs.VAR_INT, KeycardData::signature,
			ByteBufCodecs.BOOL, KeycardData::limited,
			ByteBufCodecs.VAR_INT, KeycardData::usesLeft,
			ByteBufCodecs.STRING_UTF8, KeycardData::ownerName,
			ByteBufCodecs.STRING_UTF8, KeycardData::ownerUUID,
			KeycardData::new);
	//@formatter:on
	private static final Component LINK_INFO = Component.translatable("tooltip.securitycraft:keycard.link_info").setStyle(Utils.GRAY_STYLE);
	public static final Component LIMITED_INFO = Component.translatable("tooltip.securitycraft:keycard.limited_info").setStyle(Utils.GRAY_STYLE);
	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> list, TooltipFlag flag) {
		if (linked) {
			list.accept(Component.translatable("tooltip.securitycraft:keycard.signature", StringUtils.leftPad("" + signature, 5, "0")).setStyle(Utils.GRAY_STYLE));
			list.accept(Component.translatable("tooltip.securitycraft:keycard.reader_owner", ownerName).setStyle(Utils.GRAY_STYLE));
		}
		else
			list.accept(LINK_INFO);

		if (limited)
			list.accept(Component.translatable("tooltip.securitycraft:keycard.uses", usesLeft).setStyle(Utils.GRAY_STYLE));
		else
			list.accept(LIMITED_INFO);
	}

	public KeycardData setUsesLeft(int usesLeft) {
		return new KeycardData(linked, signature, limited, usesLeft, ownerName, ownerUUID);
	}

	public KeycardData setLimitedAndUsesLeft(boolean limited, int usesLeft) {
		return new KeycardData(linked, signature, limited, usesLeft, ownerName, ownerUUID);
	}
}
