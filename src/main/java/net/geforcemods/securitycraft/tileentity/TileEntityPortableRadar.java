package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

public class TileEntityPortableRadar extends CustomizableSCTE {

	private OptionDouble searchRadiusOption = new OptionDouble("searchRadius", ConfigHandler.portableRadarSearchRadius, 5.0D, 50.0D, 5.0D);
	private OptionInt searchDelayOption = new OptionInt("searchDelay", ConfigHandler.portableRadarDelay, 4, 10, 1);
	private OptionBoolean repeatMessageOption = new OptionBoolean("repeatMessage", true);
	private OptionBoolean enabledOption = new OptionBoolean("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";

	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
	@Override
	public boolean attackEntity(Entity attacked) {
		if (attacked instanceof EntityPlayer && !EntityUtils.isInvisible((EntityPlayer)attacked))
		{
			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getAttackRange(), getAttackRange(), getAttackRange());
			List<?> entities = world.getEntitiesWithinAABB(entityTypeToAttack(), area);

			if(entities.isEmpty())
			{
				boolean redstoneModule = hasModule(EnumModuleType.REDSTONE);

				if(!redstoneModule || world.getBlockState(pos).getValue(BlockPortableRadar.POWERED))
				{
					BlockPortableRadar.togglePowerOutput(world, pos, false);
					return false;
				}
			}

			EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUsername(getOwner().getName());

			if(owner != null && hasModule(EnumModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(attacked.getName().toLowerCase()))
				return false;


			if(PlayerUtils.isPlayerOnline(getOwner().getName()) && shouldSendMessage((EntityPlayer)attacked))
			{
				PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize("tile.securitycraft:portableRadar.name"), hasCustomName() ? (ClientUtils.localize("messages.securitycraft:portableRadar.withName").replace("#p", TextFormatting.ITALIC + attacked.getName() + TextFormatting.RESET).replace("#n", TextFormatting.ITALIC + getCustomName() + TextFormatting.RESET)) : (ClientUtils.localize("messages.securitycraft:portableRadar.withoutName").replace("#p", TextFormatting.ITALIC + attacked.getName() + TextFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), TextFormatting.BLUE);
				setSentMessage();
			}

			if(hasModule(EnumModuleType.REDSTONE))
				BlockPortableRadar.togglePowerOutput(world, pos, true);

			return true;
		}
		else return false;
	}

	@Override
	public void attackFailed()
	{
		if(hasModule(EnumModuleType.REDSTONE))
			BlockPortableRadar.togglePowerOutput(world, pos, false);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		if(module == EnumModuleType.REDSTONE)
			BlockPortableRadar.togglePowerOutput(world, pos, false);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("shouldSendNewMessage", shouldSendNewMessage);
		tag.setString("lastPlayerName", lastPlayerName);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);

		if (tag.hasKey("shouldSendNewMessage"))
			shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");

		if (tag.hasKey("lastPlayerName"))
			lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(EntityPlayer player) {
		if(!player.getName().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName();
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && enabledOption.get() && !player.getName().equals(getOwner().getName());
	}

	public void setSentMessage() {
		shouldSendNewMessage = false;
	}

	@Override
	public boolean canAttack() {
		return true;
	}

	@Override
	public boolean shouldSyncToClient() {
		return false;
	}

	@Override
	public double getAttackRange() {
		return searchRadiusOption.get();
	}

	@Override
	public int getTicksBetweenAttacks() {
		return searchDelayOption.get() * 20;
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.REDSTONE, EnumModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
