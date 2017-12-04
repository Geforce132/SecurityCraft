package net.geforcemods.securitycraft.items;

import java.util.HashSet;

import com.google.common.collect.Sets;

import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.blocks.reinforced.IReinforcedBlock;
import net.geforcemods.securitycraft.gui.GuiHandler;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.world.World;

public class ItemUniversalBlockReinforcer extends ItemTool
{
	public ItemUniversalBlockReinforcer(int damage)
	{
		super(2.0F, ToolMaterial.GOLD, getBreakableBlocks());

		setMaxDamage(damage);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(!world.isRemote)
			player.openGui(mod_SecurityCraft.MODID, GuiHandler.BLOCK_REINFORCER, world, (int)player.posX, (int)player.posY, (int)player.posZ);
		return super.onItemRightClick(stack, world, player);
	}

	@Override
	public boolean onBlockStartBreak(ItemStack stack, int x, int y, int z, EntityPlayer player)
	{
		if(!player.capabilities.isCreativeMode)
		{
			World world = player.getEntityWorld();
			Block block = world.getBlock(x, y, z);

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
								world.setBlock(x, y, z, rb, i, 3);
						}
					}
					else
						world.setBlock(x, y, z, rb, block.getDamageValue(world, x, y, z), 2);

					((IOwnable)world.getTileEntity(x, y, z)).getOwner().set(player.getGameProfile().getId().toString(), player.getCommandSenderName());
					stack.damageItem(1, player);
					return true;
				}
			}
		}

		return false;
	}

	private static HashSet<Block> getBreakableBlocks()
	{
		HashSet<Block> set = Sets.newHashSet();

		IReinforcedBlock.BLOCKS.forEach((reinforcedBlock) -> {
			System.out.println(reinforcedBlock.getUnlocalizedName());
			set.addAll(((IReinforcedBlock)reinforcedBlock).getVanillaBlocks());
		});

		return set;
	}
}
