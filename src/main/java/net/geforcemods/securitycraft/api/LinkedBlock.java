package net.geforcemods.securitycraft.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public record LinkedBlock(String blockName, BlockPos pos) {
	//@formatter:off
	public static final Codec<LinkedBlock> CODEC = RecordCodecBuilder.create(i -> i.group(
					Codec.STRING.fieldOf("block_name").forGetter(LinkedBlock::blockName),
					BlockPos.CODEC.fieldOf("pos").forGetter(LinkedBlock::pos))
			.apply(i, LinkedBlock::new));
	public static final Codec<LinkedBlock> LEGACY_CODEC = RecordCodecBuilder.create(i -> i.group(
					Codec.STRING.fieldOf("blockName").forGetter(LinkedBlock::blockName),
					Codec.INT.fieldOf("blockX").forGetter(lb -> lb.pos().getX()),
					Codec.INT.fieldOf("blockY").forGetter(lb -> lb.pos().getY()),
					Codec.INT.fieldOf("blockZ").forGetter(lb -> lb.pos().getZ()))
			.apply(i, (name, x, y, z) -> new LinkedBlock(name, new BlockPos(x, y, z))));
	public static final Codec<LinkedBlock> NEW_OR_LEGACY_CODEC = Codec.withAlternative(CODEC, LEGACY_CODEC);
	//@formatter:on

	public LinkedBlock(LinkableBlockEntity blockEntity) {
		this(blockEntity.getBlockState().getBlock().getDescriptionId(), blockEntity.getBlockPos());
	}

	public boolean validate(Level level) {
		return level != null && level.getBlockState(pos).getBlock().getDescriptionId().equals(blockName);
	}

	public LinkableBlockEntity asBlockEntity(Level level) {
		if (!validate(level))
			return null;

		return (LinkableBlockEntity) level.getBlockEntity(pos);
	}

	@Override
	public String toString() {
		return blockName + " | " + pos.getX() + " " + pos.getY() + " " + pos.getZ();
	}
}
