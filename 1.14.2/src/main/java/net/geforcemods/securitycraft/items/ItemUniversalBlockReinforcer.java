package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.containers.ContainerBlockReinforcer;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ItemUniversalBlockReinforcer extends Item
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		super(new Item.Properties().maxStackSize(1).defaultMaxDamage(damage).group(SecurityCraft.groupSCTechnical));
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity player, Hand hand)
	{
		if(!world.isRemote && player instanceof ServerPlayerEntity)
		{
			NetworkHooks.openGui((ServerPlayerEntity)player, new INamedContainerProvider() {
				@Override
				public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player)
				{
					return new ContainerBlockReinforcer(windowId, inv);
				}

				@Override
				public ITextComponent getDisplayName()
				{
					return new TranslationTextComponent(getTranslationKey());
				}
			});
		}
		return super.onItemRightClick( world, player, hand);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player)
	{
		if(!player.isCreative())
		{
			World world = player.getEntityWorld();
			Block block = world.getBlockState(pos).getBlock();

			for(Block rb : IReinforcedBlock.BLOCKS)
			{
				if(((IReinforcedBlock)rb).getVanillaBlock() == block)
				{
					world.setBlockState(pos, rb.getDefaultState());
					((IOwnable)world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getName());
					stack.damageItem(1, player, p -> {});
					return true;
				}
			}
		}

		return false;
	}
}
