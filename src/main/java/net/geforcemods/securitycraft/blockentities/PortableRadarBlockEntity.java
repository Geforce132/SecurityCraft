package net.geforcemods.securitycraft.blockentities;

import java.util.List;

import net.geforcemods.securitycraft.api.CustomizableBlockEntity;
import net.geforcemods.securitycraft.api.Option;
import net.geforcemods.securitycraft.api.Option.DisabledOption;
import net.geforcemods.securitycraft.api.Option.IgnoreOwnerOption;
import net.geforcemods.securitycraft.api.Option.BooleanOption;
import net.geforcemods.securitycraft.api.Option.DoubleOption;
import net.geforcemods.securitycraft.api.Option.IntOption;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.PortableRadarBlock;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.EntityUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;

public class PortableRadarBlockEntity extends CustomizableBlockEntity implements ITickable {
	private DoubleOption searchRadiusOption = new DoubleOption(this::getPos, "searchRadius", 25.0D, 5.0D, 50.0D, 1.0D, true);
	private IntOption searchDelayOption = new IntOption(this::getPos, "searchDelay", 4, 4, 10, 1, true);
	private BooleanOption repeatMessageOption = new BooleanOption("repeatMessage", true);
	private DisabledOption disabled = new DisabledOption(false);
	private IgnoreOwnerOption ignoreOwner = new IgnoreOwnerOption(true);
	private boolean shouldSendNewMessage = true;
	private Owner lastPlayer = new Owner();
	private int ticksUntilNextSearch = getSearchDelay();

	@Override
	public void update() {
		if (!world.isRemote && !disabled.get() && ticksUntilNextSearch-- <= 0) {
			ticksUntilNextSearch = getSearchDelay();

			EntityPlayerMP owner = world.getMinecraftServer().getPlayerList().getPlayerByUsername(getOwner().getName());
			AxisAlignedBB area = new AxisAlignedBB(pos).grow(getSearchRadius(), getSearchRadius(), getSearchRadius());
			List<EntityPlayer> entities = world.getEntitiesWithinAABB(EntityPlayer.class, area, e -> !(isOwnedBy(e) && ignoresOwner()) && !isAllowed(e) && !e.isSpectator() && !EntityUtils.isInvisible(e));

			if (isModuleEnabled(ModuleType.REDSTONE)) {
				PortableRadarBlock.togglePowerOutput(world, pos, !entities.isEmpty());
			}

			if (owner != null) {
				for (EntityPlayer e : entities) {
					if (shouldSendMessage(e)) {
						PlayerUtils.sendMessageToPlayer(owner, Utils.localize("tile.securitycraft:portableRadar.name"), hasCustomName() ? (Utils.localize("messages.securitycraft:portableRadar.withName", TextFormatting.ITALIC + e.getName() + TextFormatting.RESET, TextFormatting.ITALIC + getName() + TextFormatting.RESET)) : (Utils.localize("messages.securitycraft:portableRadar.withoutName", TextFormatting.ITALIC + e.getName() + TextFormatting.RESET, pos)), TextFormatting.BLUE);
						setSentMessage();
					}
				}
			}
		}
	}

	@Override
	public void onModuleRemoved(ItemStack stack, ModuleType module, boolean toggled) {
		super.onModuleRemoved(stack, module, toggled);

		if (module == ModuleType.REDSTONE)
			PortableRadarBlock.togglePowerOutput(world, pos, false);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);

		NBTTagCompound lastPlayerTag = new NBTTagCompound();

		tag.setBoolean("shouldSendNewMessage", shouldSendNewMessage);
		lastPlayer.writeToNBT(lastPlayerTag, false);
		tag.setTag("lastPlayer", lastPlayerTag);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		shouldSendNewMessage = tag.getBoolean("shouldSendNewMessage");
		lastPlayer = Owner.fromCompound(tag.getCompoundTag("lastPlayer"));
	}

	@Override
	public void readOptions(NBTTagCompound tag) {
		if (tag.hasKey("enabled"))
			tag.setBoolean("disabled", !tag.getBoolean("enabled")); //legacy support

		for (Option<?> option : customOptions()) {
			option.readFromNBT(tag);
		}
	}

	public boolean shouldSendMessage(EntityPlayer player) {
		Owner currentPlayer = new Owner(player);

		if (!currentPlayer.equals(lastPlayer)) {
			shouldSendNewMessage = true;
			lastPlayer = currentPlayer;
		}

		return (shouldSendNewMessage || repeatMessageOption.get()) && !(lastPlayer.owns(this) && ignoresOwner());
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

	public boolean ignoresOwner() {
		return ignoreOwner.get();
	}

	@Override
	public ModuleType[] acceptedModules() {
		return new ModuleType[] {
				ModuleType.REDSTONE, ModuleType.ALLOWLIST
		};
	}

	@Override
	public Option<?>[] customOptions() {
		return new Option[] {
				searchRadiusOption, searchDelayOption, repeatMessageOption, disabled, ignoreOwner
		};
	}
}
