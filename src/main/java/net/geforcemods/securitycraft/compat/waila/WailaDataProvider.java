package net.geforcemods.securitycraft.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.INameable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.compat.IOverlayDisplay;
import net.geforcemods.securitycraft.misc.CustomModules;
import net.geforcemods.securitycraft.tileentity.KeycardReaderTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

@WailaPlugin(SecurityCraft.MODID)
public class WailaDataProvider implements IWailaPlugin, IComponentProvider {
	public static final WailaDataProvider INSTANCE = new WailaDataProvider();
	public static final ResourceLocation SHOW_OWNER = new ResourceLocation(SecurityCraft.MODID, "showowner");
	public static final ResourceLocation SHOW_MODULES = new ResourceLocation(SecurityCraft.MODID, "showmodules");
	public static final ResourceLocation SHOW_PASSWORDS = new ResourceLocation(SecurityCraft.MODID, "showpasswords");
	public static final ResourceLocation SHOW_CUSTOM_NAME = new ResourceLocation(SecurityCraft.MODID, "showcustomname");

	@Override
	public void register(IRegistrar registrar)
	{
		registrar.addConfig(SHOW_OWNER, true);
		registrar.addConfig(SHOW_MODULES, true);
		registrar.addConfig(SHOW_PASSWORDS, true);
		registrar.addConfig(SHOW_CUSTOM_NAME, true);
		registrar.registerComponentProvider(INSTANCE, TooltipPosition.BODY, IOwnable.class);
		registrar.registerStackProvider(INSTANCE, IOverlayDisplay.class);
	}

	@Override
	public ItemStack getStack(IDataAccessor data, IPluginConfig config) {
		if(data.getBlock() instanceof IOverlayDisplay)
			return ((IOverlayDisplay) data.getBlock()).getDisplayStack(data.getWorld(), data.getBlockState(), data.getPosition());

		return ItemStack.EMPTY;
	}

	@Override
	public void appendBody(List<ITextComponent> body, IDataAccessor data, IPluginConfig config) {
		Block block = data.getBlock();
		boolean disguised = false;

		if(block instanceof DisguisableBlock)
		{
			BlockState disguisedBlockState = ((DisguisableBlock)block).getDisguisedBlockState(data.getWorld(), data.getPosition());

			if(disguisedBlockState != null)
			{
				disguised = true;
				block = disguisedBlockState.getBlock();
			}
		}

		if(block instanceof IOverlayDisplay && !((IOverlayDisplay) block).shouldShowSCInfo(data.getWorld(), data.getBlockState(), data.getPosition())) return;

		//last part is a little cheaty to prevent owner info from being displayed on non-sc blocks
		if(config.get(SHOW_OWNER) && data.getTileEntity() instanceof IOwnable && block.getRegistryName().getNamespace().equals(SecurityCraft.MODID))
			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:owner") + " " + ((IOwnable) data.getTileEntity()).getOwner().getName()));

		if(disguised)
			return;

		if(config.get(SHOW_MODULES) && data.getTileEntity() instanceof CustomizableTileEntity && ((CustomizableTileEntity) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			if(!((CustomizableTileEntity) data.getTileEntity()).getModules().isEmpty())
				body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:equipped")));

			for(CustomModules module : ((CustomizableTileEntity) data.getTileEntity()).getModules())
				body.add(new StringTextComponent("- " + module.getName()));
		}

		if(config.get(SHOW_PASSWORDS) && data.getTileEntity() instanceof IPasswordProtected && !(data.getTileEntity() instanceof KeycardReaderTileEntity) && ((IOwnable) data.getTileEntity()).getOwner().isOwner(data.getPlayer())){
			String password = ((IPasswordProtected) data.getTileEntity()).getPassword();

			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:password") + " " + (password != null && !password.isEmpty() ? password : ClientUtils.localize("waila.securitycraft:password.notSet"))));
		}

		if(config.get(SHOW_CUSTOM_NAME) && data.getTileEntity() instanceof INameable && ((INameable) data.getTileEntity()).canBeNamed()){
			String name = ((INameable) data.getTileEntity()).getCustomSCName().getFormattedText();

			body.add(new StringTextComponent(ClientUtils.localize("waila.securitycraft:customName") + " " + (((INameable) data.getTileEntity()).hasCustomSCName() ? name : ClientUtils.localize("waila.securitycraft:customName.notSet"))));
		}
	}
}
