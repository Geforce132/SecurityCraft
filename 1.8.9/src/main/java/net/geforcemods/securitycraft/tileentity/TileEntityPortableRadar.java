package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityPortableRadar extends CustomizableSCTE {

	private OptionDouble searchRadiusOption = new OptionDouble("searchRadius", mod_SecurityCraft.configHandler.portableRadarSearchRadius, 5.0D, 50.0D, 5.0D);
	private OptionInt searchDelayOption = new OptionInt("searchDelay", mod_SecurityCraft.configHandler.portableRadarDelay, 4, 10, 1);
	private OptionBoolean repeatMessageOption = new OptionBoolean("repeatMessage", true);
	private OptionBoolean enabledOption = new OptionBoolean("enabled", true);

	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";

	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
	@Override
	public boolean attackEntity(Entity entity) {
		if (entity instanceof EntityPlayer) {
			BlockPortableRadar.searchForPlayers(worldObj, pos, worldObj.getBlockState(pos));
			return false;
		}
		else
			return false;
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);

		par1NBTTagCompound.setBoolean("shouldSendNewMessage", shouldSendNewMessage);
		par1NBTTagCompound.setString("lastPlayerName", lastPlayerName);
	}

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);

		if (par1NBTTagCompound.hasKey("shouldSendNewMessage"))
			shouldSendNewMessage = par1NBTTagCompound.getBoolean("shouldSendNewMessage");

		if (par1NBTTagCompound.hasKey("lastPlayerName"))
			lastPlayerName = par1NBTTagCompound.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(EntityPlayer player) {
		if(!player.getName().matches(lastPlayerName)) {
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
