package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.TileEntityOwnable;
import net.geforcemods.securitycraft.blocks.BlockCageTrap;
import net.geforcemods.securitycraft.blocks.BlockLaserBlock;
import net.geforcemods.securitycraft.blocks.BlockOwnable;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
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
	public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX,
			float hitY, float hitZ, EnumHand hand) {

		TileEntity tileEntity = world.getTileEntity(pos);
		Block block = world.getBlockState(pos).getBlock();

		if (isOwnableBlock(block, tileEntity)) {

			if (!((IOwnable) tileEntity).getOwner().isOwner(player)) {
				PlayerUtils.sendMessageToPlayer(player,
						ClientUtils.localize("item.securitycraft:universalBlockRemover.name"),
						ClientUtils.localize("messages.securitycraft:notOwned").replace("#",
								((IOwnable) tileEntity).getOwner().getName()),
						TextFormatting.RED);
				return EnumActionResult.SUCCESS;
			}

			if (block == SCContent.laserBlock) {

				world.destroyBlock(pos, true);
				BlockLaserBlock.destroyAdjacentLasers(world, pos);
				player.inventory.getCurrentItem().damageItem(1, player);

			} else if (block == SCContent.cageTrap && world.getBlockState(pos).getValue(BlockCageTrap.DEACTIVATED)) {

				BlockPos originalPos = pos;
				BlockPos middlePos = originalPos.up(4);

				new BlockCageTrap.BlockModifier(world, new MutableBlockPos(originalPos),
						((IOwnable) tileEntity).getOwner()).loop((w, p, o) -> {
							TileEntity te = w.getTileEntity(p);

							if (te instanceof IOwnable && ((IOwnable) te).getOwner().equals(o)) {
								Block b = w.getBlockState(p).getBlock();

								if (b == SCContent.reinforcedIronBars
										|| (p.equals(middlePos) && b == SCContent.horizontalReinforcedIronBars))
									w.destroyBlock(p, false);
							}
						});

				world.destroyBlock(originalPos, false);
				return EnumActionResult.SUCCESS;

			}

			world.destroyBlock(pos, true);
			world.removeTileEntity(pos);
			player.inventory.getCurrentItem().damageItem(1, player);

			return EnumActionResult.SUCCESS;
		} else {
			return EnumActionResult.FAIL;
		}
	}

	private boolean isOwnableBlock(Block block, TileEntity tileEntity) {
		return (tileEntity instanceof TileEntityOwnable || tileEntity instanceof IOwnable
				|| block instanceof BlockOwnable);
	}

}
