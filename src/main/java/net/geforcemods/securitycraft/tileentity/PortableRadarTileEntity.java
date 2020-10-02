package net.geforcemods.securitycraft.tileentity;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
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
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", ConfigHandler.CONFIG.portableRadarSearchRadius.get(), 5.0D, 50.0D, 5.0D);
	private IntOption searchDelayOption = new IntOption("searchDelay", ConfigHandler.CONFIG.portableRadarDelay.get(), 4, 10, 1);
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

				return e != owner && isNotWhitelisted;
			});

			if(hasModule(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, !entities.isEmpty());

			if(owner != null)
			{
				for(PlayerEntity e : entities)
				{
					if(shouldSendMessage(e))
					{
						IFormattableTextComponent attackedName = e.getName().copyRaw().mergeStyle(TextFormatting.ITALIC);
						IFormattableTextComponent text;

						if(hasCustomSCName())
							text = ClientUtils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomSCName().copyRaw().mergeStyle(TextFormatting.ITALIC));
						else
							text = ClientUtils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, Utils.getFormattedCoordinates(pos));

						PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.PORTABLE_RADAR.get().getTranslationKey()), text, TextFormatting.BLUE);
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
	public void read(BlockState state, CompoundNBT tag)
	{
		super.read(state, tag);

		if (tag.contains("shouldSendNewMessage"))
			shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");

		if (tag.contains("lastPlayerName"))
			lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(PlayerEntity player) {
		if(!player.getName().getString().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName().getString();
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !player.getName().getString().equals(getOwner().getName());
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
