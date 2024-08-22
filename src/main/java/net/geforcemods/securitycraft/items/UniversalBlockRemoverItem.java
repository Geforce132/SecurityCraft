package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IDisguisable;
import net.geforcemods.securitycraft.api.ILinkedAction;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkableBlockEntity;
import net.geforcemods.securitycraft.api.OwnableBlockEntity;
import net.geforcemods.securitycraft.api.Owner;
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
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class UniversalBlockRemoverItem extends Item {
	public UniversalBlockRemoverItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx) {
		Level level = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = level.getBlockState(pos);
		Block block = state.getBlock();
		BlockEntity be = level.getBlockEntity(pos);
		Player player = ctx.getPlayer();

		if (be != null && isOwnableBlock(block, be)) {
			if (be instanceof DisplayCaseBlockEntity displayCase && (displayCase.isOpen() && displayCase.getDisplayedStack().isEmpty()))
				return InteractionResult.PASS;

			IOwnable ownable = (IOwnable) be;
			Owner owner = ownable.getOwner();
			boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

			if (!(isDefault && state.is(SCContent.FRAME.get())) && !((IOwnable) be).isOwnedBy(player)) {
				if (!(block instanceof IBlockMine) && (!(be.getBlockState().getBlock() instanceof IDisguisable db) || (((BlockItem) db.getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable)))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(((IOwnable) be).getOwner())), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}

			if (be instanceof IModuleInventory inv)
				inv.dropAllModules();

			if (block == SCContent.LASER_BLOCK.get()) {
				LinkableBlockEntity laser = (LinkableBlockEntity) be;

				for (ItemStack module : laser.getInventory()) {
					if (!module.isEmpty())
						laser.propagate(new ILinkedAction.ModuleRemoved(((ModuleItem) module.getItem()).getModuleType(), false), laser);
				}

				if (!level.isClientSide) {
					level.destroyBlock(pos, true);
					LaserBlock.destroyAdjacentLasers(level, pos);
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(ctx.getHand()));
				}
			}
			else if (block == SCContent.CAGE_TRAP.get() && state.getValue(CageTrapBlock.DEACTIVATED)) {
				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.above(4);

				if (!level.isClientSide) {
					new CageTrapBlock.BlockModifier(level, new BlockPos.MutableBlockPos().set(originalPos), ((IOwnable) be).getOwner()).loop((w, p, o) -> {
						BlockEntity otherBe = w.getBlockEntity(p);

						if (otherBe instanceof IOwnable otherOwnable && o.owns(otherOwnable)) {
							Block b = w.getBlockState(p).getBlock();

							if (b == SCContent.REINFORCED_IRON_BARS.get() || (p.equals(middlePos) && b == SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get()))
								w.destroyBlock(p, false);
						}
					});

					level.destroyBlock(originalPos, true);
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(ctx.getHand()));
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
					stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(ctx.getHand()));
				}
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private static boolean isOwnableBlock(Block block, BlockEntity be) {
		return be instanceof OwnableBlockEntity || be instanceof IOwnable || block instanceof OwnableBlock;
	}
}
