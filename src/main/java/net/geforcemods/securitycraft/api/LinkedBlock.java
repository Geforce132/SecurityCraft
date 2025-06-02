package net.geforcemods.securitycraft.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class LinkedBlock {
	//@formatter:off
	public static final Codec<LinkedBlock> CODEC = RecordCodecBuilder.create(i -> i.group(
					Codec.STRING.fieldOf("blockName").forGetter(LinkedBlock::getBlockName),
					Codec.INT.fieldOf("blockX").forGetter(LinkedBlock::getX),
					Codec.INT.fieldOf("blockY").forGetter(LinkedBlock::getY),
					Codec.INT.fieldOf("blockZ").forGetter(LinkedBlock::getZ))
			.apply(i, (name, x, y, z) -> new LinkedBlock(name, new BlockPos(x, y, z))));
	//@formatter:on
	private String blockName;
	private BlockPos blockPos = null;

	public LinkedBlock(String name, BlockPos pos) {
		blockName = name;
		blockPos = pos;
	}

	public LinkedBlock(LinkableBlockEntity blockEntity) {
		blockName = blockEntity.getBlockState().getBlock().getDescriptionId();
		blockPos = blockEntity.getBlockPos();
	}

	public boolean validate(Level level) {
		return !(level == null || (level.isEmptyBlock(blockPos) || !level.getBlockState(blockPos).getBlock().getDescriptionId().equals(blockName)));
	}

	public LinkableBlockEntity asBlockEntity(Level level) {
		if (!validate(level))
			return null;

		return (LinkableBlockEntity) level.getBlockEntity(blockPos);
	}

	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}

	public String getBlockName() {
		return blockName;
	}

	public void setPos(BlockPos pos) {
		blockPos = pos;
	}

	public BlockPos getPos() {
		return blockPos;
	}

	public int getX() {
		return blockPos.getX();
	}

	public int getY() {
		return blockPos.getY();
	}

	public int getZ() {
		return blockPos.getZ();
	}

	@Override
	public boolean equals(Object o) {
		return blockPos != null && o instanceof LinkedBlock block && blockPos.equals(block.getPos());
	}

	@Override
	public int hashCode() {
		return blockPos == null ? 0 : blockPos.hashCode();
	}

	@Override
	public String toString() {
		return blockName + " | " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ();
	}
}
