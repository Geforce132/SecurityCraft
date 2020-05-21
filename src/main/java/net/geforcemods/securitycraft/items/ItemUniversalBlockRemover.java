package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.EnumLinkedAction;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockInventoryScanner;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.tileentity.TileEntityDisguisable;
import net.geforcemods.securitycraft.tileentity.TileEntityInventoryScanner;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.IBlockWithNoDrops;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
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

public class ItemUniversalBlockRemover extends Item {

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		TileEntity tileEntity = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();

		if(isOwnableBlock(block, tileEntity)) {
			if(!((IOwnable) tileEntity).getOwner().isOwner(player)) {
				if(world.isRemote && !(block instanceof IBlockMine) && (!(tileEntity instanceof TileEntityDisguisable) || (((ItemBlock)((BlockDisguisable)((TileEntityDisguisable)tileEntity).getBlockType()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof BlockDisguisable)))
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize("item.securitycraft:universalBlockRemover.name"), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) tileEntity).getOwner().getName()), TextFormatting.RED);

				return EnumActionResult.FAIL;
			}

			if(block == SCContent.laserBlock) {
				CustomizableSCTE te = (CustomizableSCTE)world.getTileEntity(pos);

				for(ItemStack module : te.modules)
				{
					if(!module.isEmpty())
						te.createLinkedBlockAction(EnumLinkedAction.MODULE_REMOVED, new Object[] {module, ((ItemModule)module.getItem()).getModule()}, te);
				}

				world.destroyBlock(pos, true);
				BlockLaserBlock.destroyAdjacentLasers(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player);
			} else if(block == SCContent.cageTrap && world.getBlockState(pos).getValue(BlockCageTrap.DEACTIVATED)) {
				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.up(4);

				new BlockCageTrap.BlockModifier(world, new MutableBlockPos(originalPos), ((IOwnable) tileEntity).getOwner()).loop((w, p, o) -> {
					TileEntity te = w.getTileEntity(p);

					if(te instanceof IOwnable && ((IOwnable) te).getOwner().equals(o)) {
						Block b = w.getBlockState(p).getBlock();

						if(b == SCContent.reinforcedIronBars || (p.equals(middlePos) && b == SCContent.horizontalReinforcedIronBars))
							w.destroyBlock(p, false);
					}
				});

				world.destroyBlock(originalPos, false);
			} else {
				if(block == SCContent.inventoryScanner)
				{
					TileEntityInventoryScanner te = BlockInventoryScanner.getConnectedInventoryScanner(world, pos);

					if(te != null)
						te.modules.clear();
				}
				else if(block instanceof IBlockWithNoDrops)
					Block.spawnAsEntity(world, pos, ((IBlockWithNoDrops)block).getUniversalBlockRemoverDrop());

				world.destroyBlock(pos, true);
				world.removeTileEntity(pos);
				player.inventory.getCurrentItem().damageItem(1, player);
			}

			return EnumActionResult.SUCCESS;
		}
		else return EnumActionResult.PASS;
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity) {
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable || block instanceof BlockOwnable);
	}
}
