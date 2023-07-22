package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.entity.sentry.Sentry;
import net.geforcemods.securitycraft.entity.sentry.Sentry.EnumSentryMode;
import net.geforcemods.securitycraft.util.LevelUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class SentryItem extends Item {
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		boolean replacesTargetedBlock = world.getBlockState(pos).getMaterial().isReplaceable();

		if (!replacesTargetedBlock)
			pos = pos.offset(facing); //if the block is not replaceable, place sentry next to targeted block

		if (!world.isAirBlock(pos) && !replacesTargetedBlock)
			return EnumActionResult.PASS;
		else {
			BlockPos downPos = pos.down();

			if (world.isAirBlock(downPos) || world.getCollisionBoxes(null, new AxisAlignedBB(downPos)).isEmpty()) {
				PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:sentry.name"), Utils.localize("messages.securitycraft:sentry.needsBlockBelow"), TextFormatting.DARK_RED);
				return EnumActionResult.SUCCESS;
			}
		}

		Entity entity = new Sentry(world, pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F, player);
		ItemStack stack = player.getHeldItem(hand);

		if (stack.hasDisplayName())
			entity.setCustomNameTag(stack.getDisplayName());

		if (replacesTargetedBlock)
			world.setBlockState(pos, Blocks.AIR.getDefaultState());

		if (!world.isRemote) {
			LevelUtils.addScheduledTask(world, () -> world.spawnEntity(entity));
			player.sendStatusMessage(Utils.localize(EnumSentryMode.CAMOUFLAGE_HP.getModeKey()).appendSibling(Utils.localize(EnumSentryMode.CAMOUFLAGE_HP.getDescriptionKey())), true);
		}

		if (!player.isCreative())
			player.getHeldItem(hand).shrink(1);

		return EnumActionResult.SUCCESS;
	}
}
