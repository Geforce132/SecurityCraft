package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IModuleInventory;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blockentities.DisguisableBlockEntity;
import net.geforcemods.securitycraft.blocks.DisguisableBlock;
import net.geforcemods.securitycraft.inventory.CustomizeBlockMenu;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class UniversalBlockModifierItem extends Item
{
	public UniversalBlockModifierItem(Item.Properties properties)
	{
		super(properties);
	}

	@Override
	public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext ctx)
	{
		Level world = ctx.getLevel();
		BlockPos pos = ctx.getClickedPos();
		BlockEntity te = world.getBlockEntity(pos);
		Player player = ctx.getPlayer();

		if(te instanceof IModuleInventory)
		{
			if(te instanceof IOwnable ownable && !ownable.getOwner().isOwner(player))
			{
				if(!(te instanceof DisguisableBlockEntity) || (((BlockItem)((DisguisableBlock)((DisguisableBlockEntity)te).getBlockState().getBlock()).getDisguisedStack(world, pos).getItem()).getBlock() instanceof DisguisableBlock))
					PlayerUtils.sendMessageToPlayer(player, Utils.localize(SCContent.UNIVERSAL_BLOCK_MODIFIER.get().getDescriptionId()), Utils.localize("messages.securitycraft:notOwned", PlayerUtils.getOwnerComponent(ownable.getOwner().getName())), ChatFormatting.RED);

				return InteractionResult.FAIL;
			}
			else if(!ctx.getLevel().isClientSide)
			{
				NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
					@Override
					public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
					{
						return new CustomizeBlockMenu(windowId, world, pos, inv);
					}

					@Override
					public Component getDisplayName()
					{
						return new TranslatableComponent(te.getBlockState().getBlock().getDescriptionId());
					}
				}, pos);
			}

			return InteractionResult.SUCCESS;
		}

		return InteractionResult.PASS;
	}
}
