package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.KeypadDoorBlock;
import net.geforcemods.securitycraft.containers.GenericTEContainer;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkHooks;

public class KeypadDoorTileEntity extends SpecialDoorTileEntity implements IPasswordProtected
{
	private String passcode;

	public KeypadDoorTileEntity()
	{
		super(SCContent.teTypeKeypadDoor);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		return tag;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		passcode = tag.getString("passcode");
	}

	@Override
	public void activate(PlayerEntity player) {
		if(!world.isRemote && getBlockState().getBlock() instanceof KeypadDoorBlock)
			((KeypadDoorBlock)getBlockState().getBlock()).activate(getBlockState(), world, pos, getSignalLength());
	}

	@Override
	public void openPasswordGUI(PlayerEntity player) {
		if(getPassword() != null)
		{
			if(player instanceof ServerPlayerEntity)
			{
				NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new GenericTEContainer(SCContent.cTypeCheckPassword, windowId, world, pos);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return KeypadDoorTileEntity.super.getDisplayName();
					}
				}, pos);
			}
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof ServerPlayerEntity)
				{
					NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
						@Override
						public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
						{
							return new GenericTEContainer(SCContent.cTypeSetPassword, windowId, world, pos);
						}

						@Override
						public ITextComponent getDisplayName()
						{
							return KeypadDoorTileEntity.super.getDisplayName();
						}
					}, pos);
				}
			}
			else
				PlayerUtils.sendMessageToPlayer(player, new StringTextComponent("SecurityCraft"), Utils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(BlockState blockState, PlayerEntity player) {
		if(!blockState.get(DoorBlock.OPEN)) {
			activate(player);
			return true;
		}

		return false;
	}

	@Override
	public String getPassword() {
		return (passcode != null && !passcode.isEmpty()) ? passcode : null;
	}

	@Override
	public void setPassword(String password) {
		TileEntity te = null;

		passcode = password;

		if(getBlockState().get(DoorBlock.HALF) == DoubleBlockHalf.LOWER)
			te = world.getTileEntity(pos.up());
		else if(getBlockState().get(DoorBlock.HALF) == DoubleBlockHalf.UPPER)
			te = world.getTileEntity(pos.down());

		if(te instanceof KeypadDoorTileEntity)
			((KeypadDoorTileEntity)te).setPasswordExclusively(password);
	}

	//only set the password for this door half
	public void setPasswordExclusively(String password)
	{
		passcode = password;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.ALLOWLIST, ModuleType.DENYLIST};
	}

	@Override
	public int defaultSignalLength()
	{
		return 60;
	}
}
