package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.ScannerDoorBlockEntity;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ScannerDoorBlock extends SpecialDoorBlock {
	public ScannerDoorBlock(Material material) {
		super(material);
		destroyTimeForOwner = 5.0F;
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new ScannerDoorBlockEntity();
	}

	@Override
	public Item getDoorItem() {
		return SCContent.scannerDoorItem;
	}

	public static EnumFacing.Axis getFacingAxis(IBlockState state) {
		EnumFacing facing = state.getValue(BlockDoor.FACING);

		return state.getValue(BlockDoor.OPEN) ? facing.rotateY().getAxis() : facing.getAxis();
	}
}
