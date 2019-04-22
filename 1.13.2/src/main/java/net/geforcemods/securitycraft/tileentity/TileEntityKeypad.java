package net.geforcemods.securitycraft.tileentity;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.blocks.BlockKeypad;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.misc.BaseInteractionObject;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.NetworkHooks;

public class TileEntityKeypad extends CustomizableSCTE implements IPasswordProtected {

	private String passcode;

	private OptionBoolean isAlwaysActive = new OptionBoolean("isAlwaysActive", false) {
		@Override
		public void toggle() {
			super.toggle();

			if(getValue()) {
				BlockUtils.setBlockProperty(world, pos, BlockKeypad.POWERED, true);
				world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
			}
			else {
				BlockUtils.setBlockProperty(world, pos, BlockKeypad.POWERED, false);
				world.notifyNeighborsOfStateChange(pos, SCContent.keypad);
			}
		}
	};

	public TileEntityKeypad()
	{
		super(SCContent.teTypeKeypad);
	}

	@Override
	public void onModuleInserted(ItemStack stack, EnumCustomModules module) {
		if(module == EnumCustomModules.DISGUISE)
			world.markBlockRangeForRenderUpdate(pos, pos);
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumCustomModules module) {
		if(module == EnumCustomModules.DISGUISE)
			world.markBlockRangeForRenderUpdate(pos, pos);
	}

	/**
	 * Writes a tile entity to NBT.
	 * @return
	 */
	@Override
	public NBTTagCompound write(NBTTagCompound tag)
	{
		super.write(tag);

		if(passcode != null && !passcode.isEmpty())
			tag.putString("passcode", passcode);

		return tag;
	}

	/**
	 * Reads a tile entity from NBT.
	 */
	@Override
	public void read(NBTTagCompound tag)
	{
		super.read(tag);

		if (tag.contains("passcode"))
			if(tag.getInt("passcode") != 0)
				passcode = String.valueOf(tag.getInt("passcode"));
			else
				passcode = tag.getString("passcode");
	}

	@Override
	public void activate(EntityPlayer player) {
		if(!world.isRemote && BlockUtils.getBlock(getWorld(), getPos()) instanceof BlockKeypad)
			BlockKeypad.activate(world, pos);
	}

	@Override
	public void openPasswordGUI(EntityPlayer player) {
		if(getPassword() != null)
		{
			if(player instanceof EntityPlayerMP)
				NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.INSERT_PASSWORD), pos);
		}
		else
		{
			if(getOwner().isOwner(player))
			{
				if(player instanceof EntityPlayerMP)
					NetworkHooks.openGui((EntityPlayerMP)player, new BaseInteractionObject(GuiHandler.SETUP_PASSWORD), pos);
			}
			else
				PlayerUtils.sendMessageToPlayer(player, "SecurityCraft", ClientUtils.localize("messages.securitycraft:passwordProtected.notSetUp"), TextFormatting.DARK_RED);
		}
	}

	@Override
	public boolean onCodebreakerUsed(IBlockState blockState, EntityPlayer player, boolean isCodebreakerDisabled) {
		if(isCodebreakerDisabled)
			PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.keypad.getTranslationKey()), ClientUtils.localize("messages.securitycraft:codebreakerDisabled"), TextFormatting.RED);
		else if(!BlockUtils.getBlockPropertyAsBoolean(world, pos, BlockKeypad.POWERED)) {
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
		passcode = password;
	}

	@Override
	public EnumCustomModules[] acceptedModules() {
		return new EnumCustomModules[]{EnumCustomModules.WHITELIST, EnumCustomModules.BLACKLIST, EnumCustomModules.DISGUISE};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ isAlwaysActive };
	}

	@Override
	public ITextComponent getCustomName()
	{
		return getCustomSCName();
	}

	@Override
	public boolean hasCustomName()
	{
		return hasCustomSCName();
	}
}
