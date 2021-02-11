package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption(this::getPos, "searchRadius", 25.0D, 5.0D, 50.0D, 1.0D, true);
	private IntOption searchDelayOption = new IntOption(this::getPos, "searchDelay", 4, 4, 10, 1, true);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private BooleanOption enabledOption = new BooleanOption("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";
	private int ticksUntilNextSearch = getSearchDelay();

	public PortableRadarTileEntity()
	{
		super(SCContent.teTypePortableRadar);
	}

	@Override
	public void tick()
	{
		super.tick();

		if(!world.isRemote && enabledOption.get() && ticksUntilNextSearch-- <= 0)
		{
			ticksUntilNextSearch = getSearchDelay();

			ServerPlayerEntity owner = world.getServer().getPlayerList().getPlayerByUsername(getOwner().getName());
			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<PlayerEntity> entities = world.getEntitiesWithinAABB(PlayerEntity.class, area, e -> {
				boolean isNotWhitelisted = true;

				if(hasModule(ModuleType.WHITELIST))
					isNotWhitelisted = !ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(e.getName().getString().toLowerCase());

				return e != owner && isNotWhitelisted && !e.isSpectator();
			});

			if(hasModule(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, !entities.isEmpty());

			if(owner != null)
			{
				for(PlayerEntity e : entities)
				{
					if(shouldSendMessage(e))
					{
						PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.PORTABLE_RADAR.get().getTranslationKey()), hasCustomSCName() ? (ClientUtils.localize("messages.securitycraft:portableRadar.withName", TextFormatting.ITALIC + e.getName().getFormattedText() + TextFormatting.RESET, TextFormatting.ITALIC + getCustomSCName().getFormattedText() + TextFormatting.RESET)) : (ClientUtils.localize("messages.securitycraft:portableRadar.withoutName", TextFormatting.ITALIC + e.getName().getFormattedText() + TextFormatting.RESET, pos)), TextFormatting.BLUE);
						setSentMessage();
					}
				}
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module)
	{
		super.onModuleRemoved(stack, module);

		if(module == ModuleType.REDSTONE)
			PortableRadarBlock.togglePowerOutput(world, pos, false);
	}

	@Override
	public CompoundNBT write(CompoundNBT tag)
	{
		super.write(tag);

		tag.putBoolean("shouldSendNewMessage", shouldSendNewMessage);
		tag.putString("lastPlayerName", lastPlayerName);
		return tag;
	}

	@Override
	public void read(CompoundNBT tag)
	{
		super.read(tag);

		shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");
		lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(PlayerEntity player) {
		if(!player.getName().getFormattedText().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName().getFormattedText();
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !player.getName().getFormattedText().equals(getOwner().getName());
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.REDSTONE, ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
