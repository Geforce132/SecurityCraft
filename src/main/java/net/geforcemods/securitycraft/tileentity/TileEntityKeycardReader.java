package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockKeycardReader;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

public class TileEntityKeycardReader extends TileEntityDisguisable implements IPasswordProtected {

	private int passLV = 0;
	private boolean requiresExactKeycard = false;
	private OptionBoolean sendMessage = new OptionBoolean("sendMessage", true);

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag){
		super.writeToNBT(tag);
		tag.setInteger("passLV", passLV);
		tag.setBoolean("requiresExactKeycard", requiresExactKeycard);
		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void readFromNBT(NBTTagCompound tag){
		super.readFromNBT(tag);

		if (tag.hasKey("passLV"))
			passLV = tag.getInteger("passLV");

		if (tag.hasKey("requiresExactKeycard"))
			requiresExactKeycard = tag.getBoolean("requiresExactKeycard");

	}

	public void setRequiresExactKeycard(boolean par1) {
		requiresExactKeycard = par1;
	}

	public boolean doesRequireExactKeycard() {
		return requiresExactKeycard;
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeycardReader)
			BlockKeycardReader.activate(world, getPos());
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() == null)
		{
			if(getOwner().isOwner(player))
				player.openGui(SecurityCraft.instance, GuiHandler.SETUP_KEYCARD_READER_ID, world, pos.getX(), pos.getY(), pos.getZ());
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		return false;
	}

	@Override
	public String getPassword() {
		return passLV == 0 ? null : String.valueOf(passLV);
	}

	@Override
	public void setPassword(String password) {
		passLV = Integer.parseInt(password);
	}

	@Override
	public EnumModuleType[] acceptedModules() {
		return new EnumModuleType[]{EnumModuleType.WHITELIST, EnumModuleType.BLACKLIST, EnumModuleType.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ sendMessage };
	}

	public boolean sendsMessages()
	{
		return sendMessage.asBoolean();
	}
}
