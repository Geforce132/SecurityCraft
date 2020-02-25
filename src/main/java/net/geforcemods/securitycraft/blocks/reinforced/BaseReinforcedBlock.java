package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.storage.loot.LootContext.Builder;

public class BaseReinforcedBlock extends OwnableBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public BaseReinforcedBlock(Material mat, Block vB)
	{
		this(mat, vB, 0);
	}

	public BaseReinforcedBlock(Material mat, Block vB, int lightValue)
	{
		this(SoundType.STONE, mat, vB, lightValue);
	}

	public BaseReinforcedBlock(SoundType soundType, Material mat, Block vB)
	{
		this(soundType, mat, vB, 0);
	}

	/**
	 * Only use for non-solid blocks
	 */
	public BaseReinforcedBlock(Block.Properties properties, SoundType soundType, Block vB)
	{
		super(soundType, properties.notSolid());

		vanillaBlockSupplier = () -> vB;
	}

	public BaseReinforcedBlock(SoundType soundType, Material mat, Block vB, int lightValue)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F).lightValue(lightValue));

		vanillaBlockSupplier = () -> vB;
	}

	public BaseReinforcedBlock(SoundType soundType, Material mat, Supplier<Block> vB, int lightValue)
	{
		super(soundType, Block.Properties.create(mat).hardnessAndResistance(-1.0F, 6000000.0F).lightValue(lightValue));

		vanillaBlockSupplier = vB;
	}

	@Override
	public boolean isFireSource(BlockState state, IBlockReader world, BlockPos pos, Direction side)
	{
		return this == SCContent.REINFORCED_NETHERRACK.get() && side == Direction.UP;
	}

	@Override
	public boolean isBeaconBase(BlockState state, IWorldReader world, BlockPos pos, BlockPos beacon)
	{
		return this == SCContent.REINFORCED_IRON_BLOCK.get() || this == SCContent.REINFORCED_GOLD_BLOCK.get() || this == SCContent.REINFORCED_DIAMOND_BLOCK.get() || this == SCContent.REINFORCED_EMERALD_BLOCK.get();
	}

	@Override
	public boolean isConduitFrame(BlockState state, IWorldReader world, BlockPos pos, BlockPos conduit)
	{
		return this == SCContent.REINFORCED_PRISMARINE.get() || this == SCContent.REINFORCED_PRISMARINE_BRICKS.get() || this == SCContent.REINFORCED_SEA_LANTERN.get() || this == SCContent.REINFORCED_DARK_PRISMARINE.get();
	}

	@Override
	public boolean isPortalFrame(BlockState state, IWorldReader world, BlockPos pos)
	{
		return this == SCContent.REINFORCED_OBSIDIAN.get();
	}

	@Override
	public float getEnchantPowerBonus(BlockState state, IWorldReader world, BlockPos pos)
	{
		return this == SCContent.REINFORCED_BOOKSHELF.get() ? 1.0F : 0.0F;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState();
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}
}
