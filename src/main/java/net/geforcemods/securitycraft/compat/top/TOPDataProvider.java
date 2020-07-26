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
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.SentryEntity;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.ModList;

public class TOPDataProvider implements Function<ITheOneProbe, Void>
{
	@Nullable
	@Override
	public Void apply(ITheOneProbe theOneProbe)
	{
		theOneProbe.registerBlockDisplayOverride((mode, probeInfo, player, world, blockState, data) -> {
			ItemStack disguisedAs = ItemStack.EMPTY;

			if(blockState.getBlock() instanceof DisguisableBlock)
				disguisedAs = ((DisguisableBlock)blockState.getBlock()).getDisguisedStack(world, data.getPos());
			else if(blockState.getBlock() instanceof IOverlayDisplay)
				disguisedAs = ((IOverlayDisplay)blockState.getBlock()).getDisplayStack(world, blockState, data.getPos());

			if(!disguisedAs.isEmpty())
			{
				probeInfo.horizontal()
				.item(disguisedAs)
				.vertical()
				.itemLabel(disguisedAs)
				.text(new StringTextComponent("" + TextFormatting.BLUE + TextFormatting.ITALIC + ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()));
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
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, PlayerEntity player, World world, BlockState blockState, IProbeHitData data)
			{
				Block block = blockState.getBlock();

				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
					return;

				TileEntity te = world.getTileEntity(data.getPos());

				if(te instanceof IOwnable)
					probeInfo.vertical().text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:owner", ((IOwnable) te).getOwner().getName()).getString()));

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if(te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player)))
				{
					if(!((IModuleInventory)te).getInsertedModules().isEmpty())
					{
						probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped").getString()));

						for(ModuleType module : ((IModuleInventory) te).getInsertedModules())
							probeInfo.text(new StringTextComponent(TextFormatting.GRAY + "- ").append(new TranslationTextComponent(module.getTranslationKey())));
					}
				}

				if(te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable)te).getOwner().isOwner(player))
				{
					String password = ((IPasswordProtected) te).getPassword();

					probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet"))).getString()));
				}

				if(te instanceof INameable && ((INameable) te).canBeNamed()){
					ITextComponent text = ((INameable) te).getCustomSCName();
					ITextComponent name = text == null ? StringTextComponent.EMPTY : text;

					probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:customName", (((INameable) te).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet"))).getString()));
				}
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, PlayerEntity player, World world, Entity entity, IProbeHitEntityData data) {
				if (entity instanceof SentryEntity)
				{
					SentryEntity sentry = (SentryEntity)entity;
					SentryEntity.SentryMode mode = sentry.getMode();

					probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:owner", ((SentryEntity) entity).getOwner().getName()).getString()));

					if(!sentry.getWhitelistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty())
					{
						probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("waila.securitycraft:equipped").getString()));

						if (!sentry.getWhitelistModule().isEmpty())
							probeInfo.text(new StringTextComponent(TextFormatting.GRAY + "- ").append(new TranslationTextComponent(ModuleType.WHITELIST.getTranslationKey())));

						if (!sentry.getDisguiseModule().isEmpty())
							probeInfo.text(new StringTextComponent(TextFormatting.GRAY + "- ").append(new TranslationTextComponent(ModuleType.DISGUISE.getTranslationKey())));
					}

					if (mode == SentryEntity.SentryMode.AGGRESSIVE)
						probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("messages.securitycraft:sentry.mode1").getString()));
					else if (mode == SentryEntity.SentryMode.CAMOUFLAGE)
						probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("messages.securitycraft:sentry.mode2").getString()));
					else
						probeInfo.text(new StringTextComponent(TextFormatting.GRAY + ClientUtils.localize("messages.securitycraft:sentry.mode3").getString()));
				}
			}
		});
		return null;
	}
}