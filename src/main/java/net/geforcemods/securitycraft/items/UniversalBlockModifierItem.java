package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.CustomizableTileEntity;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.containers.CustomizeBlockContainer;
import net.geforcemods.securitycraft.tileentity.DisguisableTileEntity;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class UniversalBlockModifierItem extends Item
{
	public UniversalBlockModifierItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext ctx)
	{
		if(ctx.getWorld().isRemote)
			return ActionResultType.FAIL;

		World world = ctx.getWorld();
		BlockPos pos = ctx.getPos();
		TileEntity te = world.getTileEntity(pos);
		PlayerEntity player = ctx.getPlayer();

		if(te instanceof CustomizableTileEntity)
		{
			if(!((IOwnable) te).getOwner().isOwner(player))
			{
				if(!(te instanceof DisguisableTileEntity) || (((BlockItem)((DisguisableBlock)((DisguisableTileEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))
					PlayerUtils.sendMessageToPlayer(player, ClientUtils.localize(SCContent.universalBlockModifier.getTranslationKey()), ClientUtils.localize("messages.securitycraft:notOwned").replace("#", ((IOwnable) te).getOwner().getName()), TextFormatting.RED);

				return ActionResultType.FAIL;
			}
			else if(player instanceof ServerPlayerEntity)
			{
				NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
					@Override
					public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
					{
						return new CustomizeBlockContainer(windowId, world, pos, inv);
					}

					@Override
					public ITextComponent getDisplayName()
					{
						return new TranslationTextComponent(te.getBlockState().getBlock().getTranslationKey());
					}
				}, pos);
			}

			return ActionResultType.SUCCESS;
		}

		return ActionResultType.PASS;
	}
}
