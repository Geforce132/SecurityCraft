package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.KeypadFurnaceBlock;
import net.geforcemods.securitycraft.tileentity.KeypadFurnaceTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeBookCategory;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadFurnaceContainer extends AbstractFurnaceContainer{

	public KeypadFurnaceTileEntity te;

	public KeypadFurnaceContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, world, pos, inventory, (KeypadFurnaceTileEntity)world.getTileEntity(pos), ((KeypadFurnaceTileEntity)world.getTileEntity(pos)).getFurnaceData());
	}

	public KeypadFurnaceContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory, IInventory furnaceInv, IIntArray furnaceData) {
		super(SCContent.cTypeKeypadFurnace, IRecipeType.SMELTING, RecipeBookCategory.FURNACE, windowId, inventory, furnaceInv, furnaceData);
		this.te = (KeypadFurnaceTileEntity)world.getTileEntity(pos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player){
		return true;
	}

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getBlockState().with(KeypadFurnaceBlock.OPEN, false));
	}
}