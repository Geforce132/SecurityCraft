package net.geforcemods.securitycraft.itemblocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemBlockCrystalQuartzSlab extends ItemBlock
{

	private final boolean isNotSlab; // <--- Not really, I just don't know what the purpose of this boolean is yet.
	private final BlockSlab singleSlab;

	public ItemBlockCrystalQuartzSlab(Block blockType, Boolean notSlab)
	{
		super(blockType);
		singleSlab = (BlockSlab)SCContent.crystalQuartzSlab;
		isNotSlab = notSlab;
		setMaxDurability(0);
		setHasSubtypes(true);
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta)
	{
		return SCContent.crystalQuartz.getIcon(2, meta);
	}

	@Override
	public int getMetadata(int meta) //u wot
	{
		return meta;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
	{
		if(isNotSlab)
			return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		else if(stack.stackSize == 0)
			return false;
		else if(!player.canPlayerEdit(x, y, z, side, stack))
			return false;
		else
		{
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			int blockType = meta & 7;
			boolean flag = (meta & 8) != 0;

			if((side == 1 && !flag || side == 0 && flag) && isBlock(block) && blockType == stack.getMetadata())
			{
				if(world.checkNoEntityCollision(this.getBlockVariant(meta).getCollisionBoundingBoxFromPool(world, x, y, z)) && world.setBlock(x, y, z, this.getBlockVariant(block, meta), (block == SCContent.crystalQuartzSlab && meta == 2 ? 2 : blockType), 3))
				{
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.getBlockVariant(block, meta).stepSound.getPlaceSound(), (this.getBlockVariant(block, meta).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(block, meta).stepSound.getFrequency() * 0.8F);
					--stack.stackSize;
				}

				return true;
			}
			else
				return tryPlace(stack, player, world, x, y, z, side) ? true : super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack)
	{
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int blockType = meta & 7;
		boolean flag = (meta & 8) != 0;

		if((side == 1 && !flag || side == 0 && flag) && block == singleSlab && blockType == stack.getMetadata())
			return true;
		else
		{
			if(side == 0)
				--y;

			if(side == 1)
				++y;

			if(side == 2)
				--z;

			if(side == 3)
				++z;

			if(side == 4)
				--x;

			if(side == 5)
				++x;

			Block block1 = world.getBlock(x, y, z);
			int block1Meta = world.getBlockMetadata(x, y, z);
			blockType = block1Meta & 7;
			return block1 == singleSlab && blockType == stack.getMetadata() ? true : super.func_150936_a(world, x, y, z, side, player, stack);
		}
	}

	private boolean tryPlace(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta)
	{
		if(meta == 0)
			--y;

		if(meta == 1)
			++y;

		if(meta == 2)
			--z;

		if(meta == 3)
			++z;

		if(meta == 4)
			--x;

		if(meta == 5)
			++x;

		Block block = world.getBlock(x, y, z);
		int blockMeta = world.getBlockMetadata(x, y, z);
		int blockType = blockMeta & 7;

		if(block == singleSlab && blockType == stack.getMetadata())
		{
			if(world.checkNoEntityCollision(this.getBlockVariant(blockMeta).getCollisionBoundingBoxFromPool(world, x, y, z)) && world.setBlock(x, y, z, this.getBlockVariant(blockMeta), blockType, 3))
			{
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.getBlockVariant(blockMeta).stepSound.getPlaceSound(), (this.getBlockVariant(blockMeta).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(blockMeta).stepSound.getFrequency() * 0.8F);
				--stack.stackSize;
			}

			return true;
		}
		else
			return false;
	}

	public Block getBlockVariant(Block slab, int meta)
	{
		return SCContent.doubleCrystalQuartzSlab;
	}

	public Block getBlockVariant(int meta)
	{
		return Block.getBlockFromItem(new ItemStack(SCContent.crystalQuartzSlab, 1, meta).getItem());
	}

	public boolean isBlock(Block block)
	{
		return block == SCContent.crystalQuartzSlab;
	}
}
