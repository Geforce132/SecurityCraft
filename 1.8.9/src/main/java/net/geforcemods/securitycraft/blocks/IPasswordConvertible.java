package net.geforcemods.securitycraft.blocks;

import java.util.Arrays;
import java.util.List;

import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public interface IPasswordConvertible
{
	public static final List<Block> BLOCKS = Arrays.asList(new Block[] {
			mod_SecurityCraft.keypad,
			mod_SecurityCraft.keypadChest,
			mod_SecurityCraft.keypadFurnace
	});

	public Block getOriginalBlock();

	public boolean convert(EntityPlayer player, World world, BlockPos pos);
}
