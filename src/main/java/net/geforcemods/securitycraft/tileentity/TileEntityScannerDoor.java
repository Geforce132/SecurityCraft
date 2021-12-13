package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.ILockable;
import net.geforcemods.securitycraft.api.IViewActivated;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.text.TextFormatting;

public class TileEntityScannerDoor extends TileEntitySpecialDoor implements IViewActivated, ILockable
{
	private int viewCooldown = 0;

	@Override
	public void update() {
		super.update();
		checkView(world, pos);
	}

	@Override
	public void onEntityViewed(EntityLivingBase entity)
	{
		IBlockState upperState = world.getBlockState(pos);
		IBlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.getValue(BlockDoor.HALF) == BlockDoor.EnumDoorHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof EntityPlayer))
				return;

			EntityPlayer player = (EntityPlayer)entity;

			if (!isLocked()) {
				String name = entity.getName();

				if (ConfigHandler.trickScannersWithPlayerHeads && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.SKULL)
					name = PlayerUtils.getNameOfSkull(player);

				if (name == null || (!getOwner().getName().equals(name) && !ModuleUtils.isAllowed(this, name))) {
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.retinalScanner.getTranslationKey()), Utils.localize("messages.securitycraft:retinalScanner.notOwner", PlayerUtils.getOwnerComponent(getOwner().getName())), TextFormatting.RED);
					return;
				}

				boolean open = !lowerState.getValue(BlockDoor.OPEN);
				int length = getSignalLength();

				world.setBlockState(pos, upperState.withProperty(BlockDoor.OPEN, !upperState.getValue(BlockDoor.OPEN)), 3);
				world.setBlockState(pos.down(), lowerState.withProperty(BlockDoor.OPEN, !lowerState.getValue(BlockDoor.OPEN)), 3);
				world.markBlockRangeForRenderUpdate(pos.down(), pos);
				world.playEvent(null, open ? 1005 : 1011, pos, 0);

				if(open && length > 0)
					world.scheduleUpdate(pos, SCContent.scannerDoor, length);

				if(open && sendsMessages())
					PlayerUtils.sendMessageToPlayer(player, Utils.localize("item.securitycraft:scannerDoorItem.name"), Utils.localize("messages.securitycraft:retinalScanner.hello", name), TextFormatting.GREEN);
			}
			else if (entity instanceof EntityPlayer && sendsMessages())
				PlayerUtils.sendMessageToPlayer((EntityPlayer)entity, Utils.localize(SCContent.scannerDoor), Utils.localize("messages.securitycraft:sonic_security_system.locked", Utils.localize(SCContent.scannerDoor)), TextFormatting.DARK_RED, false);
		}
	}

	@Override
	public int getViewCooldown()
	{
		return viewCooldown;
	}

	@Override
	public void setViewCooldown(int viewCooldown) {
		this.viewCooldown = viewCooldown;
	}

	@Override
	public int defaultSignalLength()
	{
		return 0;
	}
}
