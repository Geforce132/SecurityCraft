package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;

public class TileEntityPortableRadar extends CustomizableSCTE {

	private OptionDouble searchRadiusOption = new OptionDouble("searchRadius", SecurityCraft.config.portableRadarSearchRadius, 5.0D, 50.0D, 5.0D);
	private OptionInt searchDelayOption = new OptionInt("searchDelay", SecurityCraft.config.portableRadarDelay, 4, 10, 1);
	private OptionBoolean repeatMessageOption = new OptionBoolean("repeatMessage", true);
	private OptionBoolean enabledOption = new OptionBoolean("enabled", true);

	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";

	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
	@Override
	public boolean attackEntity(Entity attacked) {
		if (attacked instanceof EntityPlayer)
		{
			AxisAlignedBB area = AxisAlignedBB.fromBounds(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).expand(getAttackRange(), getAttackRange(), getAttackRange());
			List<?> entities = worldObj.getEntitiesWithinAABB(entityTypeToAttack(), area);

			if(entities.isEmpty())
			{
				boolean redstoneModule = hasModule(EnumCustomModules.REDSTONE);

				if(!redstoneModule || (redstoneModule && worldObj.getBlockState(pos).getValue(BlockPortableRadar.POWERED).booleanValue()))
				{
					BlockPortableRadar.togglePowerOutput(worldObj, pos, false);
					return false;
				}
			}

			EntityPlayerMP owner = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(getOwner().getName());

			if(owner != null && hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(worldObj, pos, EnumCustomModules.WHITELIST).contains(attacked.getName().toLowerCase()))
				return false;

			if(PlayerUtils.isPlayerOnline(getOwner().getName()) && shouldSendMessage((EntityPlayer)attacked))
			{
				PlayerUtils.sendMessageToPlayer(owner, StatCollector.translateToLocal("tile.securitycraft:portableRadar.name"), hasCustomName() ? (StatCollector.translateToLocal("messages.securitycraft:portableRadar.withName").replace("#p", EnumChatFormatting.ITALIC + attacked.getName() + EnumChatFormatting.RESET).replace("#n", EnumChatFormatting.ITALIC + getCustomName() + EnumChatFormatting.RESET)) : (StatCollector.translateToLocal("messages.securitycraft:portableRadar.withoutName").replace("#p", EnumChatFormatting.ITALIC + attacked.getName() + EnumChatFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), EnumChatFormatting.BLUE);
				setSentMessage();
			}

			if(hasModule(EnumCustomModules.REDSTONE))
				BlockPortableRadar.togglePowerOutput(worldObj, pos, true);

			return true;
		}
		else return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound tag)
	{
		super.writeToNBT(tag);

		tag.setBoolean("shouldSendNewMessage", shouldSendNewMessage);
		tag.setString("lastPlayerName", lastPlayerName);
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

		return (shouldSendNewMessage || repeatMessageOption.asBoolean()) && enabledOption.asBoolean() && !player.getName().equals(getOwner().getName());
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
		return searchRadiusOption.asDouble();
	}

	@Override
	public int getTicksBetweenAttacks() {
		return searchDelayOption.asInteger() * 20;
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.REDSTONE, EnumCustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module)
	{
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module)
	{
		worldObj.notifyNeighborsOfStateChange(pos, blockType);
	}
}
