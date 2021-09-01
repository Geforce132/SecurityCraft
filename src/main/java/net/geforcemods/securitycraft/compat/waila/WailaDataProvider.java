package net.geforcemods.securitycraft.compat.waila;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.EntityAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElement.Align;
import mcp.mobius.waila.impl.Tooltip;
import mcp.mobius.waila.impl.ui.ItemStackElement;
import mcp.mobius.waila.impl.ui.TextElement;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.blocks.OwnableBlock;
import net.geforcemods.securitycraft.blocks.mines.BaseFullMineBlock;
import net.geforcemods.securitycraft.blocks.mines.FurnaceMineBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedCauldronBlock;
import net.geforcemods.securitycraft.blocks.reinforced.ReinforcedPaneBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.entity.Sentry;
import net.geforcemods.securitycraft.entity.Sentry.SentryMode;
import net.geforcemods.securitycraft.misc.ModuleType;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fmllegacy.RegistryObject;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider, IEntityComponentProvider
{
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_PASSWORDS = new ResourceLocation(SecurityCraft.MODID, "showpasswords");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");
	private static final Style MOD_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.BLUE).withItalic(true);
	private static final Style ITEM_NAME_STYLE = Style.EMPTY.applyFormat(ChatFormatting.WHITE);

	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addConfig(SHOW_OWNER, true);
		registrar.addConfig(SHOW_MODULES, true);
		registrar.addConfig(SHOW_PASSWORDS, true);
		registrar.addConfig(SHOW_CUSTOM_NAME, true);

		for(RegistryObject<Block> registryObject : SCContent.BLOCKS.getEntries())
		{
			Block block = registryObject.get();

			if(!(block instanceof OwnableBlock) && !block.getRegistryName().getPath().matches("(?!(reinforced_)).*?crystal_.*") && !(block instanceof ReinforcedCauldronBlock) && !(block instanceof ReinforcedPaneBlock)) //don't register unreinforced Crystal Quartz, Reinforced Cauldrons and Reinforced Glass Panes here, this prevents our component provider from being registered multiple times for certain blocks
				registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, block.getClass());

			if (block instanceof IOverlayDisplay)
				registrar.usePickedResult(block);
		}

		registrar.registerComponentProvider(INSTANCE, TooltipPosition.HEAD, BaseFullMineBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.HEAD, FurnaceMineBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, OwnableBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, ReinforcedCauldronBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, ReinforcedPaneBlock.class);
		registrar.registerIconProvider(INSTANCE, DisguisableBlock.class);
		registrar.registerIconProvider(INSTANCE, BaseFullMineBlock.class);
		registrar.registerIconProvider(INSTANCE, FurnaceMineBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, Sentry.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.TAIL, BaseFullMineBlock.class);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.TAIL, FurnaceMineBlock.class);
	}

	@Override
	public IElement getIcon(BlockAccessor data, IPluginConfig config, IElement currentIcon) {
		if(data.getBlock() instanceof IOverlayDisplay display)
			return ItemStackElement.of(display.getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()));

		return ItemStackElement.EMPTY;
	}

	@Override
	public void appendTooltip(ITooltip tooltip, BlockAccessor data, IPluginConfig config) {
		switch(data.getTooltipPosition())
		{
			case HEAD: {
				if(tooltip instanceof Tooltip head)
					head.lines.get(0).getAlignedElements(Align.LEFT).set(0, new TextElement(new TranslatableComponent(((IOverlayDisplay)data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition()).getDescriptionId()).setStyle(ITEM_NAME_STYLE)));

				break;
			}
			case BODY: {
				Block block = data.getBlock();
				boolean disguised = false;

				if(block instanceof DisguisableBlock disguisedBlock)
				{
					BlockState disguisedBlockState = disguisedBlock.getDisguisedBlockState(data.getLevel(), data.getPosition());

					if(disguisedBlockState != null)
					{
						disguised = true;
						block = disguisedBlockState.getBlock();
					}
				}

				if(block instanceof IOverlayDisplay display && !display.shouldShowSCInfo(data.getLevel(), data.getBlockState(), data.getPosition()))
					return;

				BlockEntity te = data.getBlockEntity();

				if(te != null)
				{
					//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
					if(config.get(SHOW_OWNER) && te instanceof IOwnable ownable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
						tooltip.add(Utils.localize("waila.securitycraft:owner", ownable.getOwner().getName()));

					if(disguised)
						return;

					//if the te is ownable, show modules only when it's owned, otherwise always show
					if(config.get(SHOW_MODULES) && te instanceof IModuleInventory inv && (!(te instanceof IOwnable ownable) || ownable.getOwner().isOwner(data.getPlayer()))){
						if(!inv.getInsertedModules().isEmpty())
							tooltip.add(Utils.localize("waila.securitycraft:equipped"));

						for(ModuleType module : inv.getInsertedModules())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(module.getTranslationKey())));
					}

					if(config.get(SHOW_PASSWORDS) && te instanceof IPasswordProtected ipp && ((IOwnable) te).getOwner().isOwner(data.getPlayer())){
						String password = ipp.getPassword();

						tooltip.add(Utils.localize("waila.securitycraft:password", (password != null && !password.isEmpty() ? password : Utils.localize("waila.securitycraft:password.notSet"))));
					}

					if(config.get(SHOW_CUSTOM_NAME) && te instanceof INameable nameable && nameable.canBeNamed()){
						Component text = nameable.getCustomSCName();

						tooltip.add(Utils.localize("waila.securitycraft:customName", nameable.hasCustomSCName() ? text : Utils.localize("waila.securitycraft:customName.notSet")));
					}
				}

				break;
			}
			case TAIL: {
				if(tooltip instanceof Tooltip tail)
				{
					ItemStack disguisedAs = ((IOverlayDisplay)data.getBlock()).getDisplayStack(data.getLevel(), data.getBlockState(), data.getPosition());
					Component modName = new TextComponent(ModList.get().getModContainerById(disguisedAs.getItem().getRegistryName().getNamespace()).get().getModInfo().getDisplayName()).setStyle(MOD_NAME_STYLE);

					tail.lines.get(tail.lines.size() - 1).getAlignedElements(Align.LEFT).set(0, new TextElement(modName));
				}
			}
		}
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor data, IPluginConfig config) {
		if(data.getTooltipPosition() == TooltipPosition.BODY)
		{
			Entity entity = data.getEntity();

			if(entity instanceof Sentry sentry)
			{
				SentryMode mode = sentry.getMode();

				if(config.get(SHOW_OWNER))
					tooltip.add(Utils.localize("waila.securitycraft:owner", sentry.getOwner().getName()));

				if(config.get(SHOW_MODULES) && sentry.getOwner().isOwner(data.getPlayer())){

					if(!sentry.getAllowlistModule().isEmpty() || !sentry.getDisguiseModule().isEmpty() || sentry.hasSpeedModule())
					{
						tooltip.add(Utils.localize("waila.securitycraft:equipped"));

						if(!sentry.getAllowlistModule().isEmpty())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.ALLOWLIST.getTranslationKey())));

						if(!sentry.getDisguiseModule().isEmpty())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.DISGUISE.getTranslationKey())));

						if(sentry.hasSpeedModule())
							tooltip.add(new TextComponent("- ").append(new TranslatableComponent(ModuleType.SPEED.getTranslationKey())));
					}
				}

				MutableComponent modeDescription = Utils.localize(mode.getModeKey());

				if(mode != SentryMode.IDLE)
					modeDescription.append("- ").append(Utils.localize(mode.getTargetKey()));

				tooltip.add(modeDescription);
			}
		}
	}
}
