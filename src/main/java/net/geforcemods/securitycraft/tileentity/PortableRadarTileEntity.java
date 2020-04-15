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
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarTileEntity extends CustomizableTileEntity {

	private DoubleOption searchRadiusOption = new DoubleOption("searchRadius", ConfigHandler.CONFIG.portableRadarSearchRadius.get(), 5.0D, 50.0D, 5.0D);
	private IntOption searchDelayOption = new IntOption("searchDelay", ConfigHandler.CONFIG.portableRadarDelay.get(), 4, 10, 1);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private BooleanOption enabledOption = new BooleanOption("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";

	public PortableRadarTileEntity()
	{
		super(SCContent.teTypePortableRadar);
	}

	//Using TileEntitySCTE.attacks() and the attackEntity() method to check for players. :3
	@Override
	public boolean attackEntity(Entity attacked) {
		if (attacked instanceof PlayerEntity && !EntityUtils.isInvisible((PlayerEntity)attacked))
		{
			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getAttackRange(), getAttackRange(), getAttackRange());
			List<?> entities = world.getEntitiesWithinAABB(entityTypeToAttack(), area);

			if(entities.isEmpty())
			{
				boolean redstoneModule = hasModule(CustomModules.REDSTONE);

				if(!redstoneModule || world.getBlockState(pos).get(PortableRadarBlock.POWERED))
				{
					PortableRadarBlock.togglePowerOutput(world, pos, false);
					return false;
				}
			}

			ServerPlayerEntity owner = world.getServer().getPlayerList().getPlayerByUsername(getOwner().getName());

			if(owner != null && hasModule(CustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, CustomModules.WHITELIST).contains(attacked.getName().getFormattedText().toLowerCase()))
				return false;


			if(PlayerUtils.isPlayerOnline(getOwner().getName()) && shouldSendMessage((PlayerEntity)attacked))
			{
				PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.PORTABLE_RADAR.get().getTranslationKey()), hasCustomSCName() ? (ClientUtils.localize("messages.securitycraft:portableRadar.withName").replace("#p", TextFormatting.ITALIC + attacked.getName().getFormattedText() + TextFormatting.RESET).replace("#n", TextFormatting.ITALIC + getCustomSCName().getFormattedText() + TextFormatting.RESET)) : (ClientUtils.localize("messages.securitycraft:portableRadar.withoutName").replace("#p", TextFormatting.ITALIC + attacked.getName().getFormattedText() + TextFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), TextFormatting.BLUE);
				setSentMessage();
			}

			if(hasModule(CustomModules.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, true);

			return true;
		}
		else return false;
	}

	@Override
	public void attackFailed()
	{
		if(hasModule(CustomModules.REDSTONE))
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

		if (tag.contains("shouldSendNewMessage"))
			shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");

		if (tag.contains("lastPlayerName"))
			lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(PlayerEntity player) {
		if(!player.getName().getFormattedText().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName().getFormattedText();
		}

		return (shouldSendNewMessage || repeatMessageOption.asBoolean()) && enabledOption.asBoolean() && !player.getName().getFormattedText().equals(getOwner().getName());
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
	public CustomModules[] acceptedModules() {
		return new CustomModules[]{CustomModules.REDSTONE, CustomModules.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
