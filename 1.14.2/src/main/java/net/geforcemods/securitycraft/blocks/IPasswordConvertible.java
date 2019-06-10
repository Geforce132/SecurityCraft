package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IPasswordConvertible
{
	public static final List<Block> BLOCKS = Arrays.asList(new Block[] {
			SCContent.keypad,
			SCContent.keypadChest,
			SCContent.keypadFurnace
	});

	public Block getOriginalBlock();

	public boolean convert(PlayerEntity player, World world, BlockPos pos);
}
