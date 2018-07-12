package net.geforcemods.securitycraft.itemblocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.api.Owner;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedSlabs;
import net.geforcemods.securitycraft.blocks.reinforced.BlockReinforcedWoodSlabs;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemBlockReinforcedSlabs extends ItemBlock {

	private final boolean isNotSlab; // <--- Not really, I just don't know what the purpose of this boolean is yet.
	private final BlockSlab singleSlab;
	private final ReinforcedSlabType slabType;

	public ItemBlockReinforcedSlabs(Block blockType, BlockReinforcedWoodSlabs slabBlock, Boolean notSlab, ReinforcedSlabType slabType){
		super(blockType);
		singleSlab = slabBlock;
		isNotSlab = notSlab;
		this.slabType = slabType;
		setMaxDurability(0);
		setHasSubtypes(true);
	}

	public ItemBlockReinforcedSlabs(Block blockType, BlockReinforcedSlabs slabBlock, Boolean notSlab, ReinforcedSlabType slabType){
		super(blockType);
		singleSlab = slabBlock;
		isNotSlab = notSlab;
		this.slabType = slabType;
		setMaxDurability(0);
		setHasSubtypes(true);
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int meta){
		if(slabType == ReinforcedSlabType.OTHER)
			return SCContent.reinforcedStoneSlabs.getIcon(2, meta);
		else
			return SCContent.reinforcedWoodSlabs.getIcon(2, meta);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack){
		if(slabType == ReinforcedSlabType.WOOD){
			if(stack.getMetadata() == 0)
				return this.getUnlocalizedName() + "_oak";
			else if(stack.getMetadata() == 1)
				return this.getUnlocalizedName() + "_spruce";
			else if(stack.getMetadata() == 2)
				return this.getUnlocalizedName() + "_birch";if(stack.getMetadata() == 3)
					return this.getUnlocalizedName() + "_jungle";
				else if(stack.getMetadata() == 4)
					return this.getUnlocalizedName() + "_acacia";
				else if(stack.getMetadata() == 5)
					return this.getUnlocalizedName() + "_darkoak";
				else
					return this.getUnlocalizedName();
		}
		else if(stack.getMetadata() == 0)
			return this.getUnlocalizedName() + "_stone";
		else if(stack.getMetadata() == 1)
			return this.getUnlocalizedName() + "_cobble";
		else if(stack.getMetadata() == 2)
			return this.getUnlocalizedName() + "_sandstone";
		else if(stack.getMetadata() == 4)
			return this.getUnlocalizedName() + "_stonebrick";
		else if(stack.getMetadata() == 5)
			return this.getUnlocalizedName() + "_brick";
		else if(stack.getMetadata() == 6)
			return this.getUnlocalizedName() + "_netherbrick";
		else if(stack.getMetadata() == 7)
			return this.getUnlocalizedName() + "_quartz";
		else
			return this.getUnlocalizedName();
	}

	@Override
	public int getMetadata(int meta){ //u wot
		return meta;
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if(isNotSlab)
			return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		else if(stack.stackSize == 0)
			return false;
		else if(!player.canPlayerEdit(x, y, z, side, stack))
			return false;
		else{
			Block block = world.getBlock(x, y, z);
			int meta = world.getBlockMetadata(x, y, z);
			int j1 = meta & 7;
			boolean flag = (meta & 8) != 0;

			Owner owner = null;

			if(world.getTileEntity(x, y, z) instanceof IOwnable){
				owner = ((IOwnable) world.getTileEntity(x, y, z)).getOwner();

				if(!((IOwnable) world.getTileEntity(x, y, z)).getOwner().isOwner(player)){
					if(!world.isRemote)
						PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("messages.securitycraft:reinforcedSlab"), StatCollector.translateToLocal("messages.securitycraft:reinforcedSlab.cannotDoubleSlab"), EnumChatFormatting.RED);

					return false;
				}
			}

			if((side == 1 && !flag || side == 0 && flag) && isBlock(block) && j1 == stack.getMetadata()){
				if(world.checkNoEntityCollision(this.getBlockVariant(meta).getCollisionBoundingBoxFromPool(world, x, y, z)) && world.setBlock(x, y, z, this.getBlockVariant(block, meta), (block == SCContent.reinforcedStoneSlabs && meta == 2 ? 2 : j1), 3)){
					world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.getBlockVariant(block, meta).stepSound.getPlaceSound(), (this.getBlockVariant(block, meta).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(block, meta).stepSound.getFrequency() * 0.8F);
					--stack.stackSize;

					if(owner != null)
						((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(owner);
				}

				return true;
			}
			else
				return tryPlace(stack, player, world, x, y, z, side) ? true : super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack){
		Block block = world.getBlock(x, y, z);
		int meta = world.getBlockMetadata(x, y, z);
		int i2 = meta & 7;
		boolean flag = (meta & 8) != 0;

		if((side == 1 && !flag || side == 0 && flag) && block == singleSlab && i2 == stack.getMetadata())
			return true;
		else{
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
			i2 = block1Meta & 7;
			return block1 == singleSlab && i2 == stack.getMetadata() ? true : super.func_150936_a(world, x, y, z, side, player, stack);
		}
	}

	private boolean tryPlace(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int meta){
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
		int j1 = blockMeta & 7;

		Owner owner = null;

		if(world.getTileEntity(x, y, z) instanceof IOwnable)
			owner = ((IOwnable) world.getTileEntity(x, y, z)).getOwner();

		if(block == singleSlab && j1 == stack.getMetadata()){
			if(world.checkNoEntityCollision(this.getBlockVariant(blockMeta).getCollisionBoundingBoxFromPool(world, x, y, z)) && world.setBlock(x, y, z, this.getBlockVariant(blockMeta), j1, 3)){
				world.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, this.getBlockVariant(blockMeta).stepSound.getPlaceSound(), (this.getBlockVariant(blockMeta).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(blockMeta).stepSound.getFrequency() * 0.8F);
				--stack.stackSize;

				if(owner != null)
					((IOwnable) world.getTileEntity(x, y, z)).getOwner().set(owner.getUUID(), owner.getName());
			}

			return true;
		}
		else
			return false;
	}

	public Block getBlockVariant(Block slab, int meta){
		if(slab == SCContent.reinforcedWoodSlabs)
			return SCContent.reinforcedDoubleWoodSlabs;

		if(slab == SCContent.reinforcedStoneSlabs)
			return SCContent.reinforcedDoubleStoneSlabs;

		if(slab == SCContent.reinforcedDirtSlab)
			return SCContent.reinforcedDoubleDirtSlab;

		return slab;
	}

	public Block getBlockVariant(int meta){
		if(slabType == ReinforcedSlabType.OTHER)
			return Block.getBlockFromItem(new ItemStack(SCContent.reinforcedStoneSlabs, 1, meta).getItem());
		else
			return Block.getBlockFromItem(new ItemStack(SCContent.reinforcedWoodSlabs, 1, meta).getItem());
	}

	public boolean isBlock(Block block){
		if(slabType == ReinforcedSlabType.OTHER)
			return block == SCContent.reinforcedStoneSlabs || block == SCContent.reinforcedDirtSlab;
		else
			return block == SCContent.reinforcedWoodSlabs;
	}

	public static enum ReinforcedSlabType {
		WOOD,
		OTHER;
	}

}
