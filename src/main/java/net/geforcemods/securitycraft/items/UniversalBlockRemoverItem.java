package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.blockentities.DisplayCaseBlockEntity;
import net.geforcemods.securitycraft.blockentities.InventoryScannerBlockEntity;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class UniversalBlockRemoverItem extends Item {
	public UniversalBlockRemoverItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		World level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		TileEntity be = level.getBlockEntity(pos);
		PlayerEntity player = ctx.getPlayer();

		if (be != null && isOwnableBlock(block, be)) {
			if (be instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) be).isOpen() && ((DisplayCaseBlockEntity) be).getDisplayedStack().isEmpty()))
				return ActionResultType.PASS;

			if (!((IOwnable) be).isOwnedBy(player)) {
				if (!(block instanceof IBlockMine) && (!(be.getBlockState().getBlock() instanceof IDisguisable) || (((BlockItem) ((IDisguisable) be.getBlockState().getBlock()).getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable)))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), TextFormatting.RED);

				return ActionResultType.FAIL;
			}

			if (be instanceof IModuleInventory)
				((IModuleInventory) be).dropAllModules();

			if (block == SCContent.LASER_BLOCK.get()) {
				LinkableBlockEntity laser = (LinkableBlockEntity) be;

				for (ItemStack module : laser.getInventory()) {
					if (!module.isEmpty())
						laser.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) module.getItem()).getModuleType(), false), laser);
				}

				if (!level.isClientSide) {
					level.destroyBlock(pos, true);
					LaserBlock.destroyAdjacentLasers(level, pos);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}
			else if (block == SCContent.CAGE_TRAP.get() && state.getValue(CageTrapBlock.DEACTIVATED)) {
				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.above(4);

				if (!level.isClientSide) {
					new CageTrapBlock.BlockModifier(level, new BlockPos.Mutable().set(originalPos), ((IOwnable) be).getOwner()).loop((w, p, o) -> {
						TileEntity otherBe = w.getBlockEntity(p);

						if (otherBe instanceof IOwnable && ((IOwnable) otherBe).getOwner().owns((IOwnable) otherBe)) {
							Block b = w.getBlockState(p).getBlock();

							if (b == SCContent.REINFORCED_IRON_BARS.get() || (p.equals(middlePos) && b == SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get()))
								w.destroyBlock(p, false);
						}
					});

					level.destroyBlock(originalPos, true);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}
			else {
				if ((block instanceof ReinforcedDoorBlock || block instanceof SpecialDoorBlock) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
					pos = pos.below();

				if (block == SCContent.INVENTORY_SCANNER.get()) {
					InventoryScannerBlockEntity inventoryScanner = InventoryScannerBlock.getConnectedInventoryScanner(level, pos);

					if (inventoryScanner != null)
						inventoryScanner.getInventory().clear();
				}

				if (!level.isClientSide) {
					level.destroyBlock(pos, true); //this also removes the BlockEntity
					block.destroy(level, pos, state);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}

	private static boolean isOwnableBlock(Block block, TileEntity be) {
		return (be instanceof OwnableBlockEntity || be instanceof IOwnable || block instanceof OwnableBlock);
	}
}
