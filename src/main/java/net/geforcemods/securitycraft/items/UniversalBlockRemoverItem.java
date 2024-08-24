package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blockentities.LaserBlockBlockEntity;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.IBlockWithNoDrops;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.MutableBlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class UniversalBlockRemoverItem extends Item {
	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tileEntity = world.getTileEntity(pos);
		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if (tileEntity != null && isOwnableBlock(block, tileEntity)) {
			if (tileEntity instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) tileEntity).isOpen() && ((DisplayCaseBlockEntity) tileEntity).getDisplayedStack().isEmpty()))
				return EnumActionResult.PASS;

			if (!((IOwnable) tileEntity).isOwnedBy(player)) {
				if (!(block instanceof IBlockMine) && (!(tileEntity.getBlockType() instanceof IDisguisable) || (((ItemBlock) ((IDisguisable) tileEntity.getBlockType()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof IDisguisable))) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:universalBlockRemover.name"), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) tileEntity).getOwner())), TextFormatting.RED);
					return EnumActionResult.SUCCESS;
				}

				return EnumActionResult.PASS;
			}

			if (tileEntity instanceof IModuleInventory)
				((IModuleInventory) tileEntity).dropAllModules();

			if (block == SCContent.laserBlock) {
				LaserBlockBlockEntity te = (LaserBlockBlockEntity) world.getTileEntity(pos);

				for (ItemStack module : te.getInventory()) {
					if (!module.isEmpty())
						te.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) module.getItem()).getModuleType(), false), te);
				}

				if (!world.isRemote) {
					world.destroyBlock(pos, true);
					LaserBlock.destroyAdjacentLasers(world, pos);
					player.getHeldItem(hand).damageItem(1, player);
				}
			}
			else if (block == SCContent.cageTrap && world.getBlockState(pos).getValue(CageTrapBlock.DEACTIVATED)) {
				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.up(4);

				if (!world.isRemote) {
					Owner owner = ((IOwnable) tileEntity).getOwner();

					CageTrapBlock.loopIronBarPositions(new MutableBlockPos(originalPos), barPos -> {
						TileEntity barBe = world.getTileEntity(barPos);

						if (barBe instanceof IOwnable && owner.owns((IOwnable) barBe)) {
							Block barBlock = world.getBlockState(barPos).getBlock();

							if (barBlock == SCContent.reinforcedIronBars || (barPos.equals(middlePos) && barBlock == SCContent.horizontalReinforcedIronBars))
								world.destroyBlock(barPos, false);
						}
					});
					world.destroyBlock(originalPos, true);
					player.getHeldItem(hand).damageItem(1, player);
				}
			}
			else {
				if (block == SCContent.inventoryScanner) {
					InventoryScannerBlockEntity te = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

					if (te != null)
						te.getInventory().clear();
				}
				else if (block instanceof IBlockWithNoDrops)
					Block.spawnAsEntity(world, pos, ((IBlockWithNoDrops) block).getUniversalBlockRemoverDrop());

				if (!world.isRemote) {
					world.destroyBlock(pos, true); //this also removes the BlockEntity
					block.onPlayerDestroy(world, pos, state);
					player.getHeldItem(hand).damageItem(1, player);
				}
			}

			return EnumActionResult.SUCCESS;
		}

		return EnumActionResult.PASS;
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity) {
		return (tileEntity instanceof OwnableBlockEntity || tileEntity instanceof IOwnable || block instanceof OwnableBlock);
	}
}
