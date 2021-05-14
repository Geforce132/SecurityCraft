package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableSCTE;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.OptionBoolean;
import net.geforcemods.securitycraft.api.Option.OptionDouble;
import net.geforcemods.securitycraft.api.Option.OptionInt;
import net.geforcemods.securitycraft.blocks.BlockPortableRadar;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

public class TileEntityPortableRadar extends CustomizableSCTE {

	private OptionDouble searchRadiusOption = new OptionDouble(this::getPos, "searchRadius", 25.0D, 5.0D, 50.0D, 1.0D, true);
	private OptionInt searchDelayOption = new OptionInt(this::getPos, "searchDelay", 4, 4, 10, 1, true);
	private OptionBoolean repeatMessageOption = new OptionBoolean("repeatMessage", true);
	private OptionBoolean enabledOption = new OptionBoolean("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";
	private int ticksUntilNextSearch = getSearchDelay();

	@Override
	public void update()
	{
		super.update();

		if(!world.isRemote && enabledOption.get() && ticksUntilNextSearch-- <= 0)
		{
			ticksUntilNextSearch = getSearchDelay();

			EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUsername(getOwner().getName());
			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, area, e -> {
				boolean isNotWhitelisted = true;

				if(hasModule(EnumModuleType.WHITELIST))
					isNotWhitelisted = !ModuleUtils.getPlayersFromModule(world, pos, EnumModuleType.WHITELIST).contains(e.getName().toLowerCase());

				return e != owner && isNotWhitelisted && !e.isSpectator() && !EntityUtils.isInvisible(e);
			});

			if(hasModule(EnumModuleType.REDSTONE))
				BlockPortableRadar.togglePowerOutput(world, pos, !entities.isEmpty());

			if(owner != null)
			{
				for(EntityPlayer e : entities)
				{
					if(shouldSendMessage(e))
					{
						PlayerUtils.sendMessageToPlayer(owner, Utils.localize("tile.securitycraft:portableRadar.name"), hasCustomName() ? (Utils.localize("messages.securitycraft:portableRadar.withName", TextFormatting.ITALIC + e.getName() + TextFormatting.RESET, TextFormatting.ITALIC + getCustomName() + TextFormatting.RESET)) : (Utils.localize("messages.securitycraft:portableRadar.withoutName", TextFormatting.ITALIC + e.getName() + TextFormatting.RESET, pos)), TextFormatting.BLUE);
						setSentMessage();
					}
				}
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, EnumModuleType module)
	{
		super.onModuleRemoved(stack, module);

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
		shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");
		lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(EntityPlayer player) {
		if(!player.getName().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName();
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !player.getName().equals(getOwner().getName());
	}

	public void setSentMessage() {
		shouldSendNewMessage = false;
	}

	public double getSearchRadius() {
		return searchRadiusOption.get();
	}

	public int getSearchDelay() {
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
