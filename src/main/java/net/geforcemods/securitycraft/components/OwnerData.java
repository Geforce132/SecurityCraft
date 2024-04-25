package net.geforcemods.securitycraft.components;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import io.netty.buffer.ByteBuf;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record OwnerData(String name, String uuid, boolean showInTooltip) implements TooltipProvider {

	public static final OwnerData DEFAULT = new OwnerData("", "", false);
	//@formatter:off
	public static final Codec<OwnerData> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
					Codec.STRING.fieldOf("name").forGetter(OwnerData::name),
					Codec.STRING.fieldOf("uuid").forGetter(OwnerData::uuid),
					Codec.BOOL.fieldOf("show_in_tooltip").forGetter(OwnerData::showInTooltip))
			.apply(instance, OwnerData::new));
	public static final StreamCodec<ByteBuf, OwnerData> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, OwnerData::name,
			ByteBufCodecs.STRING_UTF8, OwnerData::uuid,
			ByteBufCodecs.BOOL, OwnerData::showInTooltip,
			OwnerData::new);
	//@formatter:on
	@Override
	public void addToTooltip(TooltipContext ctx, Consumer<Component> lineAdder, TooltipFlag flag) {
		if (showInTooltip)
			lineAdder.accept(Component.translatable("tooltip.securitycraft.component.owner", name).setStyle(Utils.GRAY_STYLE));
	}

	public OwnerData setOwnerName(String name) {
		return new OwnerData(name, uuid, showInTooltip);
	}

	public OwnerData setOwnerUUID(String uuid) {
		return new OwnerData(name, uuid, showInTooltip);
	}

	public Owner toOwner() {
		return new Owner(name, uuid);
	}

	public static OwnerData fromOwner(Owner owner, boolean showInTooltip) {
		return new OwnerData(owner.getName(), owner.getUUID(), showInTooltip);
	}

	public static OwnerData fromPlayer(Player player, boolean showInTooltip) {
		return new OwnerData(player.getName().getString(), player.getUUID().toString(), showInTooltip);
	}
}
