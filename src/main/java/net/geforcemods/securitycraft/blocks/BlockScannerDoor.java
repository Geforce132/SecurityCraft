package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.TileEntityScannerDoor;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockScannerDoor extends BlockSpecialDoor
{
	public BlockScannerDoor(Material material)
	{
		super(material);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityScannerDoor();
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.scannerDoorItem;
	}
}
