package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.BlockKeypadFurnace;
import net.geforcemods.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerKeypadFurnace extends AbstractFurnaceContainer{

	public TileEntityKeypadFurnace te;

	public ContainerKeypadFurnace(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		super(SCContent.cTypeKeypadFurnace, IRecipeType.SMELTING, windowId, inventory);
		this.te = (TileEntityKeypadFurnace)world.getTileEntity(pos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player){
		return true;
	}

	@Override
	public void onContainerClosed(PlayerEntity player)
	{
		te.getWorld().setBlockState(te.getPos(), te.getBlockState().with(BlockKeypadFurnace.OPEN, false));
	}
}