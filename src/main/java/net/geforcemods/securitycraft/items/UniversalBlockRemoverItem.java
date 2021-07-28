package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.OwnableTileEntity;
import net.geforcemods.securitycraft.blocks.CageTrapBlock;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.InventoryScannerBlock;
import net.geforcemods.securitycraft.blocks.LaserBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.SpecialDoorBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedDoorBlock;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.tileentity.InventoryScannerTileEntity;
import net.geforcemods.securitycraft.tileentity.KeypadChestTileEntity;
import net.geforcemods.securitycraft.util.IBlockMine;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
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

public class UniversalBlockRemoverItem extends Item
{
	public UniversalBlockRemoverItem(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
	{
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		BlockEntity tile = world.getBlockEntity(pos);
		Player player = ctx.getPlayer();

		if(tile != null && isOwnableBlock(block, tile))
		{
			if(!((IOwnable) tile).getOwner().isOwner(player))
			{
				if(!(block instanceof IBlockMine) && (!(tile instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)tile).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock)))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_REMOVER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", ((IOwnable) tile).getOwner().getName()), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}

			if(tile instanceof IModuleInventory inv)
			{
				boolean isChest = tile instanceof KeypadChestTileEntity;

				for(ItemStack module : inv.getInventory())
				{
					if(isChest)
						((KeypadChestTileEntity)tile).addOrRemoveModuleFromAttached(module, true);

					Block.popResource(world, pos, module);
				}
			}

			if(block == SCContent.LASER_BLOCK.get())
			{
				CustomizableTileEntity te = (CustomizableTileEntity)world.getBlockEntity(pos);

				for(ItemStack module : te.getInventory())
				{
					if(!module.isEmpty())
						te.createLinkedBlockAction(LinkedAction.MODULE_REMOVED, new Object[] {module, ((ModuleItem)module.getItem()).getModuleType()}, te);
				}

				if (!world.isClientSide) {
					world.destroyBlock(pos, true);
					LaserBlock.destroyAdjacentLasers(world, pos);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}
			else if(block == SCContent.CAGE_TRAP.get() && world.getBlockState(pos).getValue(CageTrapBlock.DEACTIVATED))
			{
				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.above(4);

				if (!world.isClientSide) {
					new CageTrapBlock.BlockModifier(world, new BlockPos.MutableBlockPos().set(originalPos), ((IOwnable)tile).getOwner()).loop((w, p, o) -> {
						BlockEntity te = w.getBlockEntity(p);

						if(te instanceof IOwnable ownable && ownable.getOwner().equals(o))
						{
							Block b = w.getBlockState(p).getBlock();

							if(b == SCContent.REINFORCED_IRON_BARS.get() || (p.equals(middlePos) && b == SCContent.HORIZONTAL_REINFORCED_IRON_BARS.get()))
								w.destroyBlock(p, false);
						}
					});

					world.destroyBlock(originalPos, false);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}
			else
			{
				if((block instanceof ReinforcedDoorBlock || block instanceof SpecialDoorBlock) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
					pos = pos.below();

				if(block == SCContent.INVENTORY_SCANNER.get())
				{
					InventoryScannerTileEntity te = InventoryScannerBlock.getConnectedInventoryScanner(world, pos);

					if(te != null)
						te.getInventory().clear();
				}

				if (!world.isClientSide) {
					world.destroyBlock(pos, true);
					world.removeBlockEntity(pos);
					stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(ctx.getHand()));
				}
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}

	private static boolean isOwnableBlock(Block block, BlockEntity te)
	{
		return (te instanceof OwnableTileEntity || te instanceof IOwnable || block instanceof OwnableBlock);
	}
}
