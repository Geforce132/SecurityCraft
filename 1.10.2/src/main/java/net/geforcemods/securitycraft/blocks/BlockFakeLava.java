package net.geforcemods.securitycraft.blocks;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.TileEntitySCTE;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO: look at this class for changed method signatures
public class BlockFakeLava extends BlockDynamicLiquid implements IIntersectable {		
	
	int adjacentSourceBlocks;

    public BlockFakeLava(Material par1Material)
    {
        super(par1Material);
    }

    
    /**
     * Updates the flow for the BlockFlowing object.
     */
    private void placeStaticBlock(World par1World, BlockPos pos, IBlockState state)
    {
    	par1World.setBlockState(pos, getStaticBlock(this.blockMaterial).getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
    }

    public static BlockStaticLiquid getStaticBlock(Material materialIn)
    {
        if (materialIn == Material.WATER)
        {
            return mod_SecurityCraft.bogusWater;
        }
        else if (materialIn == Material.LAVA)
        {
            return mod_SecurityCraft.bogusLava;
        }
        else
        {
            throw new IllegalArgumentException("Invalid material");
        }
    }

    @Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        int i = state.getValue(LEVEL).intValue();
        byte b0 = 1;

        if (this.blockMaterial == Material.LAVA && !worldIn.provider.doesWaterVaporize())
        {
            b0 = 2;
        }

        int j = this.tickRate(worldIn);
        int i1;

        if (i > 0)
        {
            int k = -100;
            this.adjacentSourceBlocks = 0;
            EnumFacing enumfacing;

            for (Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator(); iterator.hasNext(); k = this.checkAdjacentBlock(worldIn, pos.offset(enumfacing), k))
            {
                enumfacing = (EnumFacing)iterator.next();
            }

            int l = k + b0;

            if (l >= 8 || k < 0)
            {
                l = -1;
            }

            if (this.getDepth(worldIn.getBlockState(pos.up())) >= 0)
            {
                i1 = this.getDepth(worldIn.getBlockState(pos.up()));

                if (i1 >= 8)
                {
                    l = i1;
                }
                else
                {
                    l = i1 + 8;
                }
            }

            if (this.adjacentSourceBlocks >= 2 && this.blockMaterial == Material.WATER)
            {
                IBlockState iblockstate2 = worldIn.getBlockState(pos.down());

                if (iblockstate2.getBlock().getMaterial(iblockstate2).isSolid())
                {
                    l = 0;
                }
                else if (iblockstate2.getBlock().getMaterial(iblockstate2) == this.blockMaterial && iblockstate2.getValue(LEVEL).intValue() == 0)
                {
                    l = 0;
                }
            }

            if (this.blockMaterial == Material.LAVA && i < 8 && l < 8 && l > i && rand.nextInt(4) != 0)
            {
                j *= 4;
            }

            if (l == i)
            {
                this.placeStaticBlock(worldIn, pos, state);
            }
            else
            {
                i = l;

                if (l < 0)
                {
                    worldIn.setBlockToAir(pos);
                }
                else
                {
                    state = state.withProperty(LEVEL, Integer.valueOf(l));
                    worldIn.setBlockState(pos, state, 2);
                    worldIn.scheduleUpdate(pos, this, j);
                    worldIn.notifyNeighborsOfStateChange(pos, this);
                }
            }
        }
        else
        {
            this.placeStaticBlock(worldIn, pos, state);
        }

        IBlockState iblockstate1 = worldIn.getBlockState(pos.down());

        if (this.canFlowInto(worldIn, pos.down(), iblockstate1))
        {
            if (this.blockMaterial == Material.LAVA && worldIn.getBlockState(pos.down()).getBlock().getMaterial(worldIn.getBlockState(pos.down())) == Material.WATER)
            {
                worldIn.setBlockState(pos.down(), Blocks.STONE.getDefaultState());
                this.triggerMixEffects(worldIn, pos.down());
                return;
            }

            if (i >= 8)
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate1, i);
            }
            else
            {
                this.tryFlowInto(worldIn, pos.down(), iblockstate1, i + 8);
            }
        }
        else if (i >= 0 && (i == 0 || this.isBlocked(worldIn, pos.down(), iblockstate1)))
        {
            Set<?> set = this.getPossibleFlowDirections(worldIn, pos);
            i1 = i + b0;

            if (i >= 8)
            {
                i1 = 1;
            }

            if (i1 >= 8)
            {
                return;
            }

            Iterator<?> iterator1 = set.iterator();

            while (iterator1.hasNext())
            {
                EnumFacing enumfacing1 = (EnumFacing)iterator1.next();
                this.tryFlowInto(worldIn, pos.offset(enumfacing1), worldIn.getBlockState(pos.offset(enumfacing1)), i1);
            }
        }
    }

    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level)
    {
        if (this.canFlowInto(worldIn, pos, state))
        {
            if (state.getBlock() != Blocks.AIR)
            {
                if (this.blockMaterial == Material.LAVA)
                {
                    this.triggerMixEffects(worldIn, pos);
                }
                else
                {
                    state.getBlock().dropBlockAsItem(worldIn, pos, state, 0);
                }
            }

            worldIn.setBlockState(pos, this.getDefaultState().withProperty(LEVEL, Integer.valueOf(level)), 3);
        }
    }

    private int func_176374_a(World worldIn, BlockPos pos, int distance, EnumFacing calculateFlowCost)
    {
        int j = 1000;
        Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

        while (iterator.hasNext())
        {
            EnumFacing enumfacing1 = (EnumFacing)iterator.next();

            if (enumfacing1 != calculateFlowCost)
            {
                BlockPos blockpos1 = pos.offset(enumfacing1);
                IBlockState iblockstate = worldIn.getBlockState(blockpos1);

                if (!this.isBlocked(worldIn, blockpos1, iblockstate) && (iblockstate.getBlock().getMaterial(iblockstate) != this.blockMaterial || iblockstate.getValue(LEVEL).intValue() > 0))
                {
                    if (!this.isBlocked(worldIn, blockpos1.down(), iblockstate))
                    {
                        return distance;
                    }

                    if (distance < 4)
                    {
                        int k = this.func_176374_a(worldIn, blockpos1, distance + 1, enumfacing1.getOpposite());

                        if (k < j)
                        {
                            j = k;
                        }
                    }
                }
            }
        }

        return j;
    }

    private Set<?> getPossibleFlowDirections(World worldIn, BlockPos pos)
    {
        int i = 1000;
        EnumSet<EnumFacing> enumset = EnumSet.noneOf(EnumFacing.class);
        Iterator<?> iterator = EnumFacing.Plane.HORIZONTAL.iterator();

        while (iterator.hasNext())
        {
            EnumFacing enumfacing = (EnumFacing)iterator.next();
            BlockPos blockpos1 = pos.offset(enumfacing);
            IBlockState iblockstate = worldIn.getBlockState(blockpos1);

            if (!this.isBlocked(worldIn, blockpos1, iblockstate) && (iblockstate.getBlock().getMaterial(iblockstate) != this.blockMaterial || iblockstate.getValue(LEVEL).intValue() > 0))
            {
                int j;

                if (this.isBlocked(worldIn, blockpos1.down(), worldIn.getBlockState(blockpos1.down())))
                {
                    j = this.func_176374_a(worldIn, blockpos1, 1, enumfacing.getOpposite());
                }
                else
                {
                    j = 0;
                }

                if (j < i)
                {
                    enumset.clear();
                }

                if (j <= i)
                {
                    enumset.add(enumfacing);
                    i = j;
                }
            }
        }

        return enumset;
    }

    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        return !(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER && block != Blocks.REEDS ? (block.getMaterial(worldIn.getBlockState(pos)) == Material.PORTAL ? true : block.getMaterial(worldIn.getBlockState(pos)).blocksMovement()) : true;
    }

    @Override
	protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel)
    {
        int j = this.getDepth(worldIn.getBlockState(pos));

        if (j < 0)
        {
            return currentMinLevel;
        }
        else
        {
            if (j == 0)
            {
                ++this.adjacentSourceBlocks;
            }

            if (j >= 8)
            {
                j = 0;
            }

            return currentMinLevel >= 0 && j >= currentMinLevel ? currentMinLevel : j;
        }
    }

    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state)
    {
        Material material = state.getBlock().getMaterial(state);
        return material != this.blockMaterial && material != Material.LAVA && !this.isBlocked(worldIn, pos, state);
    }

    @Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!this.checkForMixing(worldIn, pos, state))
        {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }
    
    @Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer){
				((EntityPlayer) entity).heal(4);
				((EntityPlayer) entity).extinguish();
			}
		}
	}
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, BlockPos pos)
    {
        return null;
    }
    
    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntitySCTE().intersectsEntities();
	}
}
