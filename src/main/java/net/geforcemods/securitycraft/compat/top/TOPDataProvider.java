package net.geforcemods.securitycraft.compat.top;

import java.util.function.Function;

import javax.annotation.Nullable;

import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ITheOneProbe;
import mcjty.theoneprobe.api.ProbeMode;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.BlockDisguisable;
import net.geforcemods.securitycraft.blocks.BlockFakeLavaBase;
import net.geforcemods.securitycraft.blocks.BlockFakeWaterBase;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.EntitySentry;
import net.geforcemods.securitycraft.entity.EntitySentry.EnumSentryMode;
import net.geforcemods.securitycraft.misc.EnumModuleType;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;

public class TOPDataProvider implements Function<ITheOneProbe, Void>
{
	private final String formatting = TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString();

	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe)
	{
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
			boolean edited = false;
			ItemStack item = ItemStack.EMPTY;
			ItemStack itemLabel = ItemStack.EMPTY;
			String labelText = "";
			String text = formatting + "Minecraft";

			//split up so the display override does not work for every block
			if(blockState.getBlock() instanceof BlockDisguisable)
			{
				item = ((BlockDisguisable)blockState.getBlock()).getDisguisedStack(world, data.getPos());
				itemLabel = item;
				text = formatting + Loader.instance().getIndexedModList().get(item.getItem().getRegistryName().getNamespace()).getName();
				edited = true;
			}
			else if(blockState.getBlock() instanceof BlockFakeLavaBase)
			{
				item = new ItemStack(Items.LAVA_BUCKET);
				labelText = ClientUtils.localize("tile.lava.name");
				edited = true;
			}
			else if(blockState.getBlock() instanceof BlockFakeWaterBase)
			{
				item = new ItemStack(Items.WATER_BUCKET);
				labelText = ClientUtils.localize("tile.water.name");
				edited = true;
			}
			else if(blockState.getBlock() instanceof IOverlayDisplay)
			{
				item = ((IOverlayDisplay)blockState.getBlock()).getDisplayStack(world, blockState, data.getPos());
				itemLabel = item;
				edited = true;
			}

			if(edited)
			{
				IProbeInfo info = probeInfo.horizontal().item(item).vertical();

				if(itemLabel.isEmpty())
					info.text(labelText);
				else
					info.itemLabel(itemLabel);

				info.text(text);
				return true;
			}

			return false;
		});
		theOneProbe.registerProvider(new IProbeInfoProvider() {
			@Override
			public String getID()
			{
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data)
			{
				Block block = blockState.getBlock();

				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
					return;

				TileEntity te = world.getTileEntity(data.getPos());

				if(te instanceof IOwnable)
					probeInfo.vertical().text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) te).getOwner().getName());

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if(te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player)))
				{
					if(!((IModuleInventory)te).getInsertedModules().isEmpty())
					{
						probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped"));

						for(EnumModuleType module : ((IModuleInventory) te).getInsertedModules())
							probeInfo.text(TextFormatting.GRAY + "- " + ClientUtils.localize(module.getTranslationKey()));
					}
				}

				if(te instanceof IPasswordProtected && !(te instanceof TileEntityKeycardReader) && ((IOwnable)te).getOwner().isOwner(player))
				{
					String password = ((IPasswordProtected) te).getPassword();

					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet")));
				}

				if(te instanceof INameable && ((INameable) te).canBeNamed()){
					String name = ((INameable) te).getCustomName();

					probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) te).hasCustomName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet")));
				}
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, EntityPlayer player, World world, Entity entity, IProbeHitEntityData data) {
				if (entity instanceof EntitySentry)
				{
					EntitySentry sentry = (EntitySentry)entity;
					EnumSentryMode mode = sentry.getMode();

					probeInfo.text(TextFormatting.GRAY + (ClientUtils.localize("waila.securitycraft:owner") + " " + ((EntitySentry) entity).getOwner().getName()));

					if(!sentry.getWhitelistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty())
					{
						probeInfo.text(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped"));

						if(!sentry.getWhitelistModule().isEmpty())
							probeInfo.text(TextFormatting.GRAY + "- " + ClientUtils.localize(EnumModuleType.WHITELIST.getTranslationKey()));

						if(!sentry.getDisguiseModule().isEmpty())
							probeInfo.text(TextFormatting.GRAY + "- " + ClientUtils.localize(EnumModuleType.DISGUISE.getTranslationKey()));
					}

					String modeDescription = ClientUtils.localize(mode.getModeKey());

					if(mode != EnumSentryMode.IDLE)
						modeDescription += " - " + ClientUtils.localize(mode.getTargetKey());

					probeInfo.text(TextFormatting.GRAY + modeDescription);
				}
			}
		});
		return null;
	}
}