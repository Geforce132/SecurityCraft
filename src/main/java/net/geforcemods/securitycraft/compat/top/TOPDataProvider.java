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
import net.geforcemods.securitycraft.entity.SentryEntity.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
				.text(new TextComponent("" + ChatFormatting.BLUE + ChatFormatting.ITALIC + ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()));
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
			public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, Player player, Level world, BlockState blockState, IProbeHitData data)
			{
				Block block = blockState.getBlock();

				if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(world, blockState, data.getPos()))
					return;

				BlockEntity te = world.getBlockEntity(data.getPos());

				if(te instanceof IOwnable)
					probeInfo.vertical().text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ((IOwnable) te).getOwner().getName()).getString()));

				//if the te is ownable, show modules only when it's owned, otherwise always show
				if(te instanceof IModuleInventory && (!(te instanceof IOwnable) || ((IOwnable)te).getOwner().isOwner(player)))
				{
					if(!((IModuleInventory)te).getInsertedModules().isEmpty())
					{
						probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:equipped").getString()));

						for(ModuleType module : ((IModuleInventory) te).getInsertedModules())
							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(module.getTranslationKey())));
					}
				}

				if(te instanceof IPasswordProtected && !(te instanceof KeycardReaderTileEntity) && ((IOwnable)te).getOwner().isOwner(player))
				{
					String password = ((IPasswordProtected) te).getPassword();

					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))).getString()));
				}

				if(te instanceof INameable && ((INameable) te).canBeNamed()){
					Component text = ((INameable) te).getCustomSCName();
					Component name = text == null ? TextComponent.EMPTY : text;

					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:customName", (((INameable) te).hasCustomSCName() ? name : Utils.localize("waila.securitycraft:customName.notSet"))).getString()));
				}
			}
		});
		theOneProbe.registerEntityProvider(new IProbeInfoEntityProvider() {
			@Override
			public String getID() {
				return SecurityCraft.MODID + ":" + SecurityCraft.MODID;
			}

			@Override
			public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo probeInfo, Player player, Level world, Entity entity, IProbeHitEntityData data) {
				if (entity instanceof SentryEntity sentry)
				{
					SentryMode mode = sentry.getMode();

					probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:owner", ((SentryEntity) entity).getOwner().getName()).getString()));

					if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
					{
						probeInfo.text(new TextComponent(ChatFormatting.GRAY + Utils.localize("waila.securitycraft:equipped").getString()));

						if(!sentry.getAllowlistModule().isEmpty())
							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())));

						if(!sentry.getDisguiseModule().isEmpty())
							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())));

						if(sentry.hasSpeedModule())
							probeInfo.text(new TextComponent(ChatFormatting.GRAY + "- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())));
					}

					MutableComponent modeDescription = Utils.localize(mode.getModeKey());

					if(mode != SentryMode.IDLE)
						modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

					probeInfo.text(new TextComponent(ChatFormatting.GRAY + modeDescription.getString()));
				}
			}
		});
		return null;
	}
}