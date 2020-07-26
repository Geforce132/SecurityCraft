package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.List;
import java.util.function.Supplier;

import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.misc.OwnershipEvent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext.Builder;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

//field_235612_b_ = EAST
//field_235613_c_ = NORTH
//field_235614_d_ = SOUTH
//field_235615_e_ = WEST
//field_235616_f_ = WATERLOGGED
public class ReinforcedWallBlock extends WallBlock implements IReinforcedBlock
{
	private final Supplier<Block> vanillaBlockSupplier;

	public ReinforcedWallBlock(Block vanillaBlock)
	{
		this(Block.Properties.create(Material.ROCK).hardnessAndResistance(-1.0F, 6000000.0F), vanillaBlock);
	}

	public ReinforcedWallBlock(Block.Properties properties, Block vanillaBlock)
	{
		super(properties);

		this.vanillaBlockSupplier = () -> vanillaBlock;
	}

	@Override
	public Block getVanillaBlock()
	{
		return vanillaBlockSupplier.get();
	}

	@Override
	public BlockState getConvertedState(BlockState vanillaState)
	{
		return getDefaultState()
				.with(UP, vanillaState.get(UP))
				.with(WALL_HEIGHT_NORTH, vanillaState.get(WALL_HEIGHT_NORTH))
				.with(WALL_HEIGHT_EAST, vanillaState.get(WALL_HEIGHT_EAST))
				.with(WALL_HEIGHT_SOUTH, vanillaState.get(WALL_HEIGHT_SOUTH))
				.with(WALL_HEIGHT_WEST, vanillaState.get(WALL_HEIGHT_WEST))
				.with(WATERLOGGED, vanillaState.get(WATERLOGGED));
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, Builder builder)
	{
		return NonNullList.from(ItemStack.EMPTY, new ItemStack(this));
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack)
	{
		if(placer instanceof PlayerEntity)
			MinecraftForge.EVENT_BUS.post(new OwnershipEvent(world, pos, (PlayerEntity)placer));
	}

	@Override
	public boolean hasTileEntity(BlockState state)
	{
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new OwnableTileEntity();
	}
}
