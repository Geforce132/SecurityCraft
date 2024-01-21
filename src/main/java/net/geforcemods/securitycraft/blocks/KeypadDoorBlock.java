package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeypadDoorBlock extends SpecialDoorBlock {
	public KeypadDoorBlock(AbstractBlock.Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
		if (!level.isClientSide) {
			KeypadDoorBlockEntity be = (KeypadDoorBlockEntity) level.getBlockEntity(pos);

			if (be.isDisabled())
				player.displayClientMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(level, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, level, pos, be.getSignalLength());
				}
				else if (player.getItemInHand(hand).getItem() != SCContent.CODEBREAKER.get())
					be.openPasscodeGUI(level, pos, player);
			}
		}

		return ActionResultType.SUCCESS;
	}

	public void activate(BlockState state, World level, BlockPos pos, int signalLength) {
		boolean open = !state.getValue(OPEN);

		level.levelEvent(null, open ? 1005 : 1011, pos, 0);
		level.setBlockAndUpdate(pos, state.setValue(OPEN, open));
		level.updateNeighborsAt(pos, SCContent.KEYPAD_DOOR.get());

		if (open && signalLength > 0)
			level.getBlockTicks().scheduleTick(pos, SCContent.KEYPAD_DOOR.get(), signalLength);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader level) {
		return new KeypadDoorBlockEntity();
	}

	@Override
	public Item getDoorItem() {
		return SCContent.KEYPAD_DOOR_ITEM.get();
	}
}
