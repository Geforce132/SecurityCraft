package net.geforcemods.securitycraft.tileentity;

import java.util.ArrayList;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.LinkedAction;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

public class ScannerDoorTileEntity extends CustomizableTileEntity
{
	private BooleanOption sendMessage = new BooleanOption("sendMessage", true);

	public ScannerDoorTileEntity()
	{
		super(SCContent.teTypeScannerDoor);
	}

	@Override
	public void entityViewed(LivingEntity entity)
	{
		BlockState upperState = world.getBlockState(pos);
		BlockState lowerState = world.getBlockState(pos.down());

		if(!world.isRemote && upperState.get(DoorBlock.HALF) == DoubleBlockHalf.UPPER && !EntityUtils.isInvisible(entity))
		{
			if(!(entity instanceof PlayerEntity))
				return;

			PlayerEntity player = (PlayerEntity)entity;

			if(PlayerUtils.isPlayerMountedOnCamera(player))
				return;

			if(!getOwner().isOwner(player) && (!hasModule(ModuleType.WHITELIST) || !ModuleUtils.getPlayersFromModule(getModule(ModuleType.WHITELIST)).contains(player.getName().getFormattedText().toLowerCase())))
			{
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SCANNER_DOOR_ITEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.notOwner").replace("#", getOwner().getName()), TextFormatting.RED);
				return;
			}

			boolean open = !BlockUtils.getBlockProperty(world, pos.down(), DoorBlock.OPEN);

			world.setBlockState(pos, upperState.with(DoorBlock.OPEN, !upperState.get(DoorBlock.OPEN)), 3);
			world.setBlockState(pos.down(), lowerState.with(DoorBlock.OPEN, !lowerState.get(DoorBlock.OPEN)), 3);
			world.playEvent(null, open ? 1005 : 1011, pos, 0);

			if(open && sendMessage.get())
				PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.SCANNER_DOOR_ITEM.get().getTranslationKey()), ClientUtils.localize("messages.securitycraft:retinalScanner.hello").replace("#", player.getName().getFormattedText()), TextFormatting.GREEN);
		}
	}

	@Override
	public void onModuleInserted(ItemStack stack, ModuleType module)
	{
		super.onModuleInserted(stack, module);
		handleModule(stack, module, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);
		handleModule(stack, module, true);
	}

	private void handleModule(ItemStack stack, ModuleType module, boolean removed)
	{
		DoubleBlockHalf myHalf = getBlockState().get(DoorBlock.HALF);
		BlockPos otherPos;

		if(myHalf == DoubleBlockHalf.UPPER)
			otherPos = getPos().down();
		else
			otherPos = getPos().up();

		BlockState other = world.getBlockState(otherPos);

		if(other.get(DoorBlock.HALF) != myHalf)
		{
			TileEntity otherTe = world.getTileEntity(otherPos);

			if(otherTe instanceof ScannerDoorTileEntity)
			{
				ScannerDoorTileEntity otherDoorTe = (ScannerDoorTileEntity)otherTe;

				if(!removed && !otherDoorTe.hasModule(module))
					otherDoorTe.insertModule(stack);
				else if(removed && otherDoorTe.hasModule(module))
					otherDoorTe.removeModule(module);
			}
		}
	}

	@Override
	protected void onLinkedBlockAction(LinkedAction action, Object[] parameters, ArrayList<CustomizableTileEntity> excludedTEs)
	{
		if(action == LinkedAction.OPTION_CHANGED)
			sendMessage.copy((Option<?>)parameters[0]);
	}

	@Override
	public int getViewCooldown()
	{
		return 30;
	}

	@Override
	public ModuleType[] acceptedModules()
	{
		return new ModuleType[]{ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions()
	{
		return new Option[]{ sendMessage };
	}
}
