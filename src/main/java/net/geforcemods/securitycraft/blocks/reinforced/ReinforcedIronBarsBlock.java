package net.geforcemods.securitycraft.blocks.reinforced;

import java.util.Collections;
import java.util.List;

import net.geforcemods.securitycraft.tileentity.ReinforcedIronBarsTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class ReinforcedIronBarsBlock extends ReinforcedPaneBlock
{
	public ReinforcedIronBarsBlock(Block.Properties properties, Block vB)
	{
		super(properties, vB);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
	{
		ResourceLocation resourcelocation = getLootTable();

		if(resourcelocation == LootTables.EMPTY)
			return Collections.emptyList();
		else
		{
			LootContext lootContext = builder.withParameter(LootParameters.BLOCK_STATE, state).build(LootParameterSets.BLOCK);
			ServerWorld world = lootContext.getWorld();
			LootTable lootTable = world.getServer().getLootTableManager().getLootTableFromLocation(resourcelocation);

			return lootTable.generate(lootContext);
		}
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new ReinforcedIronBarsTileEntity();
	}
}
