package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.KeypadDoorTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class KeypadDoorBlock extends SpecialDoorBlock
{
	public KeypadDoorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(state.getValue(OPEN))
			return InteractionResult.PASS;
		else if(!world.isClientSide)
		{
			KeypadDoorTileEntity te = (KeypadDoorTileEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}

			if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), ChatFormatting.GREEN);

				activate(world, pos, state, te.getSignalLength());
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand) && !PlayerUtils.isHoldingItem(player, SCContent.KEY_PANEL, hand))
				te.openPasswordGUI(player);
		}

		return InteractionResult.SUCCESS;
	}

	public static void activate(Level world, BlockPos pos, BlockState state, int signalLength){
		boolean open = !state.getValue(OPEN);

		world.levelEvent(null, open ? 1005 : 1011, pos, 0);
		world.setBlockAndUpdate(pos, state.setValue(OPEN, open));
		world.updateNeighborsAt(pos, SCContent.KEYPAD_DOOR.get());

		if(open && signalLength > 0)
			world.getBlockTicks().scheduleTick(pos, SCContent.KEYPAD_DOOR.get(), signalLength);
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new KeypadDoorTileEntity(pos, state).linkable();
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.KEYPAD_DOOR_ITEM.get();
	}
}
