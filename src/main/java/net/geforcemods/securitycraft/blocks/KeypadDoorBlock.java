package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.blockentities.KeypadDoorBlockEntity;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class KeypadDoorBlock extends SpecialDoorBlock {
	public KeypadDoorBlock(Material material) {
		super(material);
		setHardness(5.0F);
		setHarvestLevel("pickaxe", 0);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(POWERED))
			return false;
		else if (!world.isRemote) {
			KeypadDoorBlockEntity be = (KeypadDoorBlockEntity) world.getTileEntity(pos);

			if (be.isDisabled())
				player.sendStatusMessage(Utils.localize("gui.securitycraft:scManual.disabled"), true);
			else if (be.verifyPasscodeSet(world, pos, be, player)) {
				if (be.isDenied(player)) {
					if (be.sendsDenylistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onDenylist"), TextFormatting.RED);
				}
				else if (be.isAllowed(player)) {
					if (be.sendsAllowlistMessage())
						PlayerUtils.sendMessageToPlayer(player, Utils.localize(getTranslationKey() + ".name"), Utils.localize("messages.securitycraft:module.onAllowlist"), TextFormatting.GREEN);

					activate(state, world, pos, be.getSignalLength());
				}
				else if (player.getHeldItem(hand).getItem() != SCContent.codebreaker)
					be.openPasscodeGUI(world, pos, player);
			}
		}

		return true;
	}

	public void activate(IBlockState state, World world, BlockPos pos, int signalLength) {
		boolean open = !state.getValue(OPEN);
		EnumDoorHalf half = state.getValue(HALF);
		BlockPos otherHalfPos = pos.offset(half == EnumDoorHalf.UPPER ? EnumFacing.DOWN : EnumFacing.UP);

		world.playEvent(null, open ? 1005 : 1011, pos, 0);
		world.setBlockState(pos, state.withProperty(OPEN, open));
		world.setBlockState(otherHalfPos, world.getBlockState(otherHalfPos).withProperty(OPEN, open));
		world.markBlockRangeForRenderUpdate(pos, otherHalfPos);

		if (open && signalLength > 0)
			world.scheduleUpdate(pos, this, signalLength);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new KeypadDoorBlockEntity();
	}

	@Override
	public Item getDoorItem() {
		return SCContent.keypadDoorItem;
	}
}
