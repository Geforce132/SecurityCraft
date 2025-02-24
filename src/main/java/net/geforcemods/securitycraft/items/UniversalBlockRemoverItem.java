package net.geforcemods.securitycraft.items;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.util.ITooltipFlag;
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
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class UniversalBlockRemoverItem extends Item {
	private static final ITextComponent DISABLED_ITEM_TOOLTIP = new TranslationTextComponent("tooltip.securitycraft:universal_block_remover.disabled").withStyle(TextFormatting.RED);

	public UniversalBlockRemoverItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx) {
		if (!ConfigHandler.SERVER.vanillaToolBlockBreaking.get()) {
			World level = ctx.getLevel();
			BlockPos pos = ctx.getClickedPos();
			BlockState state = level.getBlockState(pos);
			Block block = state.getBlock();
			TileEntity be = level.getBlockEntity(pos);
			PlayerEntity player = ctx.getPlayer();

			if (be != null && isOwnableBlock(block, be)) {
				if (be instanceof DisplayCaseBlockEntity && (((DisplayCaseBlockEntity) be).isOpen() && ((DisplayCaseBlockEntity) be).getDisplayedStack().isEmpty()))
					return ActionResultType.PASS;

				IOwnable ownable = (IOwnable) be;
				Owner owner = ownable.getOwner();
				boolean isDefault = owner.getName().equals("owner") && owner.getUUID().equals("ownerUUID");

				if (!ConfigHandler.SERVER.allowBreakingNonOwnedBlocks.get() && !(isDefault && state.is(SCContent.FRAME.get())) && !ownable.isOwnedBy(player)) {
					if (!(block instanceof IBlockMine) && (!(be.getBlockState().getBlock() instanceof IDisguisable) || (((BlockItem) ((IDisguisable) be.getBlockState().getBlock()).getDisguisedStack(level, pos).getItem()).getBlock() instanceof IDisguisable)))
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(owner)), TextFormatting.RED);

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
				else if (block == SCContent.CAGE_TRAP.get()) {
					if (!level.isClientSide) {
						CageTrapBlock.disassembleIronBars(state, level, pos, owner);
						level.destroyBlock(pos, true);
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
		}

		return ActionResultType.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, World level, List<ITextComponent> tooltipComponents, ITooltipFlag tooltipFlag) {
		if (ConfigHandler.SERVER.vanillaToolBlockBreaking.get())
			tooltipComponents.add(DISABLED_ITEM_TOOLTIP);

		super.appendHoverText(stack, level, tooltipComponents, tooltipFlag);
	}

	private static boolean isOwnableBlock(Block block, TileEntity be) {
		return be instanceof OwnableBlockEntity || be instanceof IOwnable || block instanceof OwnableBlock;
	}
}
