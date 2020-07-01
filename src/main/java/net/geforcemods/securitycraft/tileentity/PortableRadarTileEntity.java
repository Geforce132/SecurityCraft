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
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
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
				boolean redstoneModule = hasModule(ModuleType.REDSTONE);

				if(!redstoneModule || world.getBlockState(pos).get(PortableRadarBlock.POWERED))
				{
					PortableRadarBlock.togglePowerOutput(world, pos, false);
					return false;
				}
			}

			ServerPlayerEntity owner = world.getServer().getPlayerList().getPlayerByUsername(getOwner().getName());

			if(owner != null && hasModule(ModuleType.WHITELIST) && ModuleUtils.getPlayersFromModule(world, pos, ModuleType.WHITELIST).contains(attacked.getName().getString().toLowerCase()))
				return false;


			if(PlayerUtils.isPlayerOnline(getOwner().getName()) && shouldSendMessage((PlayerEntity)attacked))
			{
				PlayerUtils.sendMessageToPlayer(owner, ClientUtils.localize(SCContent.PORTABLE_RADAR.get().getTranslationKey()), hasCustomSCName() ? (ClientUtils.localize("messages.securitycraft:portableRadar.withName").replace("#p", TextFormatting.ITALIC + attacked.getName().getString() + TextFormatting.RESET).replace("#n", TextFormatting.ITALIC + getCustomSCName().getString() + TextFormatting.RESET)) : (ClientUtils.localize("messages.securitycraft:portableRadar.withoutName").replace("#p", TextFormatting.ITALIC + attacked.getName().getString() + TextFormatting.RESET).replace("#l", Utils.getFormattedCoordinates(pos))), TextFormatting.BLUE);
				setSentMessage();
			}

			if(hasModule(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, true);

			return true;
		}
		else return false;
	}

	@Override
	public void attackFailed()
	{
		if(hasModule(ModuleType.REDSTONE))
			PortableRadarBlock.togglePowerOutput(world, pos, false);
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
	public void func_230337_a_(BlockState state, CompoundNBT tag)
	{
		super.func_230337_a_(state, tag);

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

		return (shouldSendNewMessage || repeatMessageOption.get()) && enabledOption.get() && !player.getName().getString().equals(getOwner().getName());
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
	public ModuleType[] acceptedModules() {
		return new ModuleType[]{ModuleType.REDSTONE, ModuleType.WHITELIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
