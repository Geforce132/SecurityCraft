package net.geforcemods.securitycraft.items;

import net.geforcemods.securitycraft.SecurityCraft;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends Item
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		setMaxDamage(damage);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			player.openGui(SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			Block block = world.getBlockState(pos).getBlock();

			for(Block rb : IReinforcedBlock.BLOCKS)
			{
				IReinforcedBlock reinforcedBlock = (IReinforcedBlock)rb;

				if(reinforcedBlock.getVanillaBlocks().contains(block))
				{
					if(reinforcedBlock.getVanillaBlocks().size() == reinforcedBlock.getAmount())
					{
						for(int i = 0; i < reinforcedBlock.getAmount(); i++)
						{
							if(block.equals(reinforcedBlock.getVanillaBlocks().get(i)))
								world.setBlockState(pos, rb.getStateFromMeta(i));
						}
					}
					else
						world.setBlockState(pos, rb.getStateFromMeta(block.getDamageValue(world, pos)), 2);

					((IOwnable)world.getTileEntity(pos)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					stack.damageItem(1, player);
					return true;
				}
			}
		}

		return false;
	}
}
