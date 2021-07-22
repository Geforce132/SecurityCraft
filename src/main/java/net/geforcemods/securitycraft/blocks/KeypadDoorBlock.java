package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.tileentity.KeypadDoorTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
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

public class KeypadDoorBlock extends SpecialDoorBlock
{
	public KeypadDoorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(state.getValue(OPEN))
			return ActionResultType.PASS;
		else if(!world.isClientSide)
		{
			KeypadDoorTileEntity te = (KeypadDoorTileEntity)world.getBlockEntity(pos);

			if(ModuleUtils.isDenied(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);

				return ActionResultType.FAIL;
			}

			if(ModuleUtils.isAllowed(te, player))
			{
				if(te.sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(getDescriptionId()), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

				activate(world, pos, state, te.getSignalLength());
			}
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand) && !PlayerUtils.isHoldingItem(player, SCContent.KEY_PANEL, hand))
				te.openPasswordGUI(player);
		}

		return ActionResultType.SUCCESS;
	}

	public static void activate(World world, BlockPos pos, BlockState state, int signalLength){
		boolean open = !state.getValue(OPEN);

		world.levelEvent(null, open ? 1005 : 1011, pos, 0);
		world.setBlockAndUpdate(pos, state.setValue(OPEN, open));
		world.updateNeighborsAt(pos, SCContent.KEYPAD_DOOR.get());

		if(open && signalLength > 0)
			world.getBlockTicks().scheduleTick(pos, SCContent.KEYPAD_DOOR.get(), signalLength);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world)
	{
		return new KeypadDoorTileEntity().linkable();
	}

	@Override
	public Item getDoorItem()
	{
		return SCContent.KEYPAD_DOOR_ITEM.get();
	}
}
