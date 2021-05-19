package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeypadDoorTileEntity;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class KeypadDoorBlock extends SpecialDoorBlock
{
	public KeypadDoorBlock(Properties properties)
	{
		super(properties);
	}

	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
	{
		if(state.get(OPEN))
			return false;
		else
		{
			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.DENYLIST))
				return true;

			if(ModuleUtils.checkForModule(world, pos, player, ModuleType.ALLOWLIST))
				activate(world, pos, state, ((KeypadDoorTileEntity)world.getTileEntity(pos)).getSignalLength());
			else if(!PlayerUtils.isHoldingItem(player, SCContent.CODEBREAKER, hand) && !PlayerUtils.isHoldingItem(player, SCContent.KEY_PANEL, hand))
				((IPasswordProtected) world.getTileEntity(pos)).openPasswordGUI(player);
		}

		return true;
	}

	public static void activate(World world, BlockPos pos, BlockState state, int signalLength){
		boolean open = !state.get(OPEN);

		world.playEvent(null, open ? 1005 : 1011, pos, 0);
		world.setBlockState(pos, state.with(OPEN, open));
		world.notifyNeighborsOfStateChange(pos, SCContent.KEYPAD_DOOR.get());

		if(open && signalLength > 0)
			world.getPendingBlockTicks().scheduleTick(pos, SCContent.KEYPAD_DOOR.get(), signalLength);
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
