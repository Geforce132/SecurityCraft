package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
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
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class UniversalBlockRemoverItem extends Item {
	private static final ITextComponent DISABLED_ITEM_TOOLTIP = new TextComponentTranslation("tooltip.securitycraft:universal_block_remover.disabled").setStyle(new Style().setColor(TextFormatting.RED));

	@Override
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
		if (!ConfigHandler.vanillaToolBlockBreaking) {
			TileEntity tileEntity = world.getTileEntity(pos);
			IBlockState state = world.getBlockState(pos);
			Block block = state.getBlock();

			if (tileEntity != null && isOwnableBlock(block, tileEntity)) {
				if (tileEntity instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) tileEntity).isOpen() && ((DisplayCaseBlockEntity) tileEntity).getDisplayedStack().isEmpty()))
					return EnumActionResult.PASS;

				if (!ConfigHandler.allowBreakingNonOwnedBlocks) {
					if (!((IOwnable) tileEntity).isOwnedBy(player)) {
						if (!(block instanceof IBlockMine) && (!(tileEntity.getBlockType() instanceof IDisguisable) || (((IDisguisable) tileEntity.getBlockType()).getDisguisedBlockState(tileEntity).getBlock() instanceof IDisguisable))) {
							PlayerUtils.sendMessageToPlayer(player, Utils.localize(this), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) tileEntity).getOwner())), TextFormatting.RED);
							return EnumActionResult.SUCCESS;
						}

						return EnumActionResult.PASS;
					}
				}

				if (tileEntity instanceof IModuleInventory)
					((IModuleInventory) tileEntity).dropAllModules();

				if (block == SCContent.laserBlock) {
					LaserBlockBlockEntity te = (LaserBlockBlockEntity) tileEntity;

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
				else if (block == SCContent.cageTrap) {
					if (!world.isRemote) {
						CageTrapBlock.disassembleIronBars(state, world, pos, ((IOwnable) tileEntity).getOwner());
						world.destroyBlock(pos, true);
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
		}
		return EnumActionResult.PASS;
	}

	@Override
	public void addInformation(ItemStack stack, World level, List<String> tooltipComponents, ITooltipFlag tooltipFlag) {
		if (ConfigHandler.vanillaToolBlockBreaking)
			tooltipComponents.add(DISABLED_ITEM_TOOLTIP.getFormattedText());

		super.addInformation(stack, level, tooltipComponents, tooltipFlag);
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity) {
		return (tileEntity instanceof OwnableBlockEntity || tileEntity instanceof IOwnable || block instanceof OwnableBlock);
	}
}
