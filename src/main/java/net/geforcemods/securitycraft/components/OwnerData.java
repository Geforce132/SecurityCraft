package net.geforcemods.securitycraft.components;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record OwnerData(String name, String uuid) implements TooltipProvider {

	public static final OwnerData DEFAULT = new OwnerData("", "");
	//@formatter:off
	public static final Codec<OwnerData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(OwnerData::name),
					Codec.STRING.fieldOf("uuid").forGetter(OwnerData::uuid))
			.apply(instance, OwnerData::new));
	public static final StreamCodec<ByteBuf, OwnerData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, OwnerData::name,
			ByteBufCodecs.STRING_UTF8, OwnerData::uuid,
			OwnerData::new);
	//@formatter:on
	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag, DataComponentGetter componentGetter) {
		KeycardData keycardData = componentGetter.get(SCContent.KEYCARD_DATA);

		if (keycardData != null)
			lineAdder.accept(Component.translatable("tooltip.securitycraft:keycard.reader_owner", name).setStyle(Utils.GRAY_STYLE));
		else
			lineAdder.accept(Component.translatable("tooltip.securitycraft.component.owner", name).setStyle(Utils.GRAY_STYLE));
	}

	public OwnerData setOwnerName(String name) {
		return new OwnerData(name, uuid);
	}

	public OwnerData setOwnerUUID(String uuid) {
		return new OwnerData(name, uuid);
	}

	public Owner toOwner() {
		return new Owner(name, uuid);
	}

	public static OwnerData fromOwner(Owner owner) {
		return new OwnerData(owner.getName(), owner.getUUID());
	}

	public static OwnerData fromPlayer(Player player) {
		return new OwnerData(player.getName().getString(), player.getUUID().toString());
	}
}
