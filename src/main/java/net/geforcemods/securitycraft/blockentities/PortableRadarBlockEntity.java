package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.ConfigHandler;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PortableRadarBlockEntity extends CustomizableBlockEntity {

	private DoubleOption searchRadiusOption = new DoubleOption(this::getBlockPos, "searchRadius", 25.0D, 5.0D, 50.0D, 1.0D, true);
	private IntOption searchDelayOption = new IntOption(this::getBlockPos, "searchDelay", 4, 4, 10, 1, true);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private BooleanOption enabledOption = new BooleanOption("enabled", true);
	private boolean shouldSendNewMessage = true;
	private String lastPlayerName = "";
	private int ticksUntilNextSearch = getSearchDelay();

	public PortableRadarBlockEntity(BlockPos pos, BlockState state)
	{
		super(SCContent.beTypePortableRadar, pos, state);
	}

	@Override
	public void tick(Level world, BlockPos pos, BlockState state)
	{
		super.tick(world, pos, state);

		if(!world.isClientSide && enabledOption.get() && ticksUntilNextSearch-- <= 0)
		{
			ticksUntilNextSearch = getSearchDelay();

			ServerPlayer owner = world.getServer().getPlayerList().getPlayerByName(getOwner().getName());
			AABB area = new AABB(pos).inflate(getSearchRadius());
			List<Player> entities = world.getEntitiesOfClass(Player.class, area, e -> {
				boolean isNotAllowed = true;

				if(hasModule(ModuleType.ALLOWLIST))
					isNotAllowed = !ModuleUtils.isAllowed(this, e);

				return e != owner && isNotAllowed && !e.isSpectator() && !EntityUtils.isInvisible(e);
			});

			if(hasModule(ModuleType.REDSTONE))
				PortableRadarBlock.togglePowerOutput(world, pos, !entities.isEmpty());

			if(owner != null)
			{
				for(Player e : entities)
				{
					if(shouldSendMessage(e))
					{
						MutableComponent attackedName = e.getName().plainCopy().withStyle(ChatFormatting.ITALIC);
						MutableComponent text;

						if(hasCustomSCName())
							text = Utils.localize("messages.securitycraft:portableRadar.withName", attackedName, getCustomSCName().plainCopy().withStyle(ChatFormatting.ITALIC));
						else
							text = Utils.localize("messages.securitycraft:portableRadar.withoutName", attackedName, Utils.getFormattedCoordinates(pos));

						PlayerUtils.sendMessageToPlayer(owner, Utils.localize(SCContent.PORTABLE_RADAR.get().getDescriptionId()), text, ChatFormatting.BLUE);
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
			PortableRadarBlock.togglePowerOutput(level, worldPosition, false);
	}

	@Override
	public CompoundTag save(CompoundTag tag)
	{
		super.save(tag);

		tag.putBoolean("shouldSendNewMessage", shouldSendNewMessage);
		tag.putString("lastPlayerName", lastPlayerName);
		return tag;
	}

	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);

		shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");
		lastPlayerName = tag.getString("lastPlayerName");
	}

	public boolean shouldSendMessage(Player player) {
		if(!player.getName().getString().equals(lastPlayerName)) {
			shouldSendNewMessage = true;
			lastPlayerName = player.getName().getString();
		}

		boolean lastPlayerOwns = ConfigHandler.SERVER.enableTeamOwnership.get() ? PlayerUtils.areOnSameTeam(lastPlayerName, getOwner().getName()) : lastPlayerName.equals(getOwner().getName());

		return (shouldSendNewMessage || repeatMessageOption.get()) && !lastPlayerOwns;
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
		return new ModuleType[]{ModuleType.REDSTONE, ModuleType.ALLOWLIST};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[]{ searchRadiusOption, searchDelayOption, repeatMessageOption, enabledOption };
	}

}
