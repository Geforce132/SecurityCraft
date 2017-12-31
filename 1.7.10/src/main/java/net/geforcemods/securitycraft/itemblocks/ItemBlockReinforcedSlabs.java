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

	public ItemBlockReinforcedSlabs(Block par1Block, BlockReinforcedWoodSlabs par2Block, Boolean par3, ReinforcedSlabType slabType){
		super(par1Block);
		singleSlab = par2Block;
		isNotSlab = par3;
		this.slabType = slabType;
		setMaxDurability(0);
		setHasSubtypes(true);
	}

	public ItemBlockReinforcedSlabs(Block par1Block, BlockReinforcedSlabs par2Block, Boolean par3, ReinforcedSlabType slabType){
		super(par1Block);
		singleSlab = par2Block;
		isNotSlab = par3;
		this.slabType = slabType;
		setMaxDurability(0);
		setHasSubtypes(true);
	}

	/**
	 * Gets an icon index based on an item's damage value
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1){
		if(slabType == ReinforcedSlabType.OTHER)
			return SCContent.reinforcedStoneSlabs.getIcon(2, par1);
		else
			return SCContent.reinforcedWoodSlabs.getIcon(2, par1);
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
	public int getMetadata(int par1){
		return par1;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10){
		if(isNotSlab)
			return super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
		else if(par1ItemStack.stackSize == 0)
			return false;
		else if(!par2EntityPlayer.canPlayerEdit(par4, par5, par6, par7, par1ItemStack))
			return false;
		else{
			Block block = par3World.getBlock(par4, par5, par6);
			int i1 = par3World.getBlockMetadata(par4, par5, par6);
			int j1 = i1 & 7;
			boolean flag = (i1 & 8) != 0;

			Owner owner = null;

			if(par3World.getTileEntity(par4, par5, par6) instanceof IOwnable){
				owner = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner();

				if(!((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner().isOwner(par2EntityPlayer)){
					if(!par3World.isRemote)
						PlayerUtils.sendMessageToPlayer(par2EntityPlayer, StatCollector.translateToLocal("messages.reinforcedSlab"), StatCollector.translateToLocal("messages.reinforcedSlab.cannotDoubleSlab"), EnumChatFormatting.RED);

					return false;
				}
			}

			if((par7 == 1 && !flag || par7 == 0 && flag) && isBlock(block) && j1 == par1ItemStack.getMetadata()){
				if(par3World.checkNoEntityCollision(this.getBlockVariant(i1).getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.getBlockVariant(block, i1), (block == SCContent.reinforcedStoneSlabs && i1 == 2 ? 2 : j1), 3)){
					par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.getBlockVariant(block, i1).stepSound.getPlaceSound(), (this.getBlockVariant(block, i1).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(block, i1).stepSound.getFrequency() * 0.8F);
					--par1ItemStack.stackSize;

					if(owner != null)
						((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner().set(owner);
				}

				return true;
			}
			else
				return func_150946_a(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7) ? true : super.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean func_150936_a(World par1World, int par2, int par3, int par4, int par5, EntityPlayer par6EntityPlayer, ItemStack par7ItemStack){
		int i1 = par2;
		int j1 = par3;
		int k1 = par4;
		Block block = par1World.getBlock(par2, par3, par4);
		int l1 = par1World.getBlockMetadata(par2, par3, par4);
		int i2 = l1 & 7;
		boolean flag = (l1 & 8) != 0;

		if((par5 == 1 && !flag || par5 == 0 && flag) && block == singleSlab && i2 == par7ItemStack.getMetadata())
			return true;
		else{
			if(par5 == 0)
				--par3;

			if(par5 == 1)
				++par3;

			if(par5 == 2)
				--par4;

			if(par5 == 3)
				++par4;

			if(par5 == 4)
				--par2;

			if(par5 == 5)
				++par2;

			Block block1 = par1World.getBlock(par2, par3, par4);
			int j2 = par1World.getBlockMetadata(par2, par3, par4);
			i2 = j2 & 7;
			return block1 == singleSlab && i2 == par7ItemStack.getMetadata() ? true : super.func_150936_a(par1World, i1, j1, k1, par5, par6EntityPlayer, par7ItemStack);
		}
	}

	private boolean func_150946_a(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7){
		if(par7 == 0)
			--par5;

		if(par7 == 1)
			++par5;

		if(par7 == 2)
			--par6;

		if(par7 == 3)
			++par6;

		if(par7 == 4)
			--par4;

		if(par7 == 5)
			++par4;

		Block block = par3World.getBlock(par4, par5, par6);
		int i1 = par3World.getBlockMetadata(par4, par5, par6);
		int j1 = i1 & 7;

		Owner owner = null;

		if(par3World.getTileEntity(par4, par5, par6) instanceof IOwnable)
			owner = ((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner();

		if(block == singleSlab && j1 == par1ItemStack.getMetadata()){
			if(par3World.checkNoEntityCollision(this.getBlockVariant(i1).getCollisionBoundingBoxFromPool(par3World, par4, par5, par6)) && par3World.setBlock(par4, par5, par6, this.getBlockVariant(i1), j1, 3)){
				par3World.playSoundEffect(par4 + 0.5F, par5 + 0.5F, par6 + 0.5F, this.getBlockVariant(i1).stepSound.getPlaceSound(), (this.getBlockVariant(i1).stepSound.getVolume() + 1.0F) / 2.0F, this.getBlockVariant(i1).stepSound.getFrequency() * 0.8F);
				--par1ItemStack.stackSize;

				if(owner != null)
					((IOwnable) par3World.getTileEntity(par4, par5, par6)).getOwner().set(owner.getUUID(), owner.getName());
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
