package net.geforcemods.securitycraft.containers;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blocks.AbstractKeypadFurnaceBlock;
import net.geforcemods.securitycraft.tileentity.AbstractKeypadFurnaceTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.AbstractFurnaceContainer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class KeypadSmokerContainer extends AbstractFurnaceContainer {
	public AbstractKeypadFurnaceTileEntity te;
	private IWorldPosCallable worldPosCallable;

	public KeypadSmokerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory) {
		this(windowId, world, pos, inventory, (AbstractKeypadFurnaceTileEntity) world.getTileEntity(pos), ((AbstractKeypadFurnaceTileEntity) world.getTileEntity(pos)).getFurnaceData());
	}

	public KeypadSmokerContainer(int windowId, World world, BlockPos pos, PlayerInventory inventory, IInventory furnaceInv, IIntArray furnaceData) {
		super(SCContent.cTypeKeypadSmoker, IRecipeType.SMOKING, windowId, inventory, furnaceInv, furnaceData);
		this.te = (AbstractKeypadFurnaceTileEntity) world.getTileEntity(pos);
		worldPosCallable = IWorldPosCallable.of(world, pos);
	}

	@Override
	public boolean canInteractWith(PlayerEntity player) {
		return isWithinUsableDistance(worldPosCallable, player, SCContent.KEYPAD_SMOKER.get());
	}

	@Override
	public void onContainerClosed(PlayerEntity player) {
		te.getWorld().setBlockState(te.getPos(), te.getBlockState().with(AbstractKeypadFurnaceBlock.OPEN, false));
	}
}