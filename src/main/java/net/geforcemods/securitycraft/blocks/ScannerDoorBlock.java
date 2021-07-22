package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.ScannerDoorTileEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ScannerDoorBlock extends SpecialDoorBlock
{
	public ScannerDoorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public BlockEntity createTileEntity(BlockState state, BlockGetter world)
	{
		return new ScannerDoorTileEntity().linkable().activatedByView();
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.SCANNER_DOOR_ITEM.get();
	}
}
