package org.freeforums.geforce.securitycraft.blocks;

import java.util.Iterator;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.freeforums.geforce.securitycraft.interfaces.IHelpInfo;
import org.freeforums.geforce.securitycraft.main.Utils;
import org.freeforums.geforce.securitycraft.main.mod_SecurityCraft;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityAlarm;

public class BlockAlarm extends BlockOwnable implements IHelpInfo{
	
	private final boolean isLit;
    public static final PropertyEnum FACING = PropertyEnum.create("facing", BlockAlarm.EnumOrientation.class);
	
	public BlockAlarm(Material par1Material, boolean isLit) {
		super(par1Material);
		float f = 0.2F;
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, BlockAlarm.EnumOrientation.NORTH));
		this.isLit = isLit;
		
		if(isLit){
			this.setLightLevel(1.0F);
		}
	}
	
	 /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean isNormalCube()
    {
        return false;
    }
    
    public int getRenderType(){
    	return 3;
    }
	
    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.UP && World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? true : worldIn.isSideSolid(pos.offset(side.getOpposite()), side);
    }

    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.isSideSolid(pos.west(),  EnumFacing.EAST ) ||
               worldIn.isSideSolid(pos.east(),  EnumFacing.WEST ) ||
               worldIn.isSideSolid(pos.north(), EnumFacing.SOUTH) ||
               worldIn.isSideSolid(pos.south(), EnumFacing.NORTH) ||
               worldIn.isSideSolid(pos.down(),  EnumFacing.UP   ) ||
               worldIn.isSideSolid(pos.up(),    EnumFacing.DOWN );
    }
    
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
    	IBlockState iblockstate = this.getDefaultState();

        if (worldIn.isSideSolid(pos.offset(facing.getOpposite()), facing))
        {
            return iblockstate.withProperty(FACING, BlockAlarm.EnumOrientation.forFacings(facing, placer.getHorizontalFacing()));
        }
        else
        {
            Iterator iterator = EnumFacing.Plane.HORIZONTAL.iterator();
            EnumFacing enumfacing1;

            do
            {
                if (!iterator.hasNext())
                {
                    if (World.doesBlockHaveSolidTopSurface(worldIn, pos.down()))
                    {
                        return iblockstate.withProperty(FACING, BlockAlarm.EnumOrientation.forFacings(EnumFacing.UP, placer.getHorizontalFacing()));
                    }

                    return iblockstate;
                }

                enumfacing1 = (EnumFacing)iterator.next();
            }
            while (enumfacing1 == facing || !worldIn.isSideSolid(pos.offset(enumfacing1.getOpposite()), enumfacing1));

            return iblockstate.withProperty(FACING, BlockAlarm.EnumOrientation.forFacings(enumfacing1, placer.getHorizontalFacing()));
        }
    }
    
	/**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World par1World, BlockPos pos, IBlockState state) {
    	if(par1World.isRemote){
    		return;
    	}else{
    		par1World.scheduleUpdate(pos, state.getBlock(), 1);
    	}
    }
	
	/**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World par1World, BlockPos pos, IBlockState state, Random par5Random)
    {
        if(par1World.isRemote){
        	return;
        }else{
    		this.playSoundAndUpdate(par1World, pos);
    		
    		par1World.scheduleUpdate(pos, state.getBlock(), 5);
        }
    }
	
	/**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor Block
     */
    public void onNeighborBlockChange(World par1World, BlockPos pos, IBlockState state, Block par5Block)
    {
    	if(par1World.isRemote){
    		return;
    	}else{
    		this.playSoundAndUpdate(par1World, pos);
    	}
    	
        EnumFacing dir = ((BlockAlarm.EnumOrientation)state.getValue(FACING)).getFacing();
        if (this.checkForDrop(par1World, pos) && !par1World.isSideSolid(pos.offset(dir.getOpposite()), dir))
        {
            this.dropBlockAsItem(par1World, pos, state, 0);
            par1World.setBlockToAir(pos);
        }
    }
    
    private boolean checkForDrop(World worldIn, BlockPos pos)
    {
        if (this.canPlaceBlockAt(worldIn, pos))
        {
            return true;
        }
        else
        {
            this.dropBlockAsItem(worldIn, pos, worldIn.getBlockState(pos), 0);
            worldIn.setBlockToAir(pos);
            return false;
        }
    }
    
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos)
    {
        float f = 0.1875F;
		float ySideMin = 0.5F - f; //bottom of the alarm when placed on a block side
		float ySideMax = 0.5F + f; //top of the alarm when placed on a block side
		float hSideMin = 0.5F - f; //the left start for s/w and right start for n/e
		float hSideMax = 0.5F + f; //the left start for n/e and right start for s/w
        
        switch (BlockAlarm.SwitchEnumFacing.ORIENTATION_LOOKUP[((BlockAlarm.EnumOrientation)worldIn.getBlockState(pos).getValue(FACING)).ordinal()])
        {
            case 0: //down
                f = 0.25F;
    			this.setBlockBounds(0.5F - f, 0.5F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
                break;
            case 1: //east
    			this.setBlockBounds(0.0F, ySideMin, hSideMin, 0.5F, ySideMax, hSideMax);
                break;
            case 2: //west
    			this.setBlockBounds(0.5F, ySideMin, hSideMin, 1.0F, ySideMax, hSideMax);
                break;
            case 3: //north
    			this.setBlockBounds(hSideMin, ySideMin, 0.0F, hSideMax, ySideMax, 0.5F);
                break;
            case 4: //south
    			this.setBlockBounds(hSideMin, ySideMin, 0.5F, hSideMax, ySideMax, 1.0F);
                break;
            case 5: //up
                f = 0.25F;
    			this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.5F, 0.5F + f);
        }
    }
    
    private void playSoundAndUpdate(World par1World, BlockPos pos){
    	if(par1World.isBlockIndirectlyGettingPowered(pos) > 0){
        	Utils.checkIfRunning();
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

    		if(!isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(true);
			}
    		
		}else{
    		boolean isPowered = ((TileEntityAlarm) par1World.getTileEntity(pos)).isPowered();

			if(isPowered){
    			((TileEntityAlarm) par1World.getTileEntity(pos)).setPowered(false);
			}
		}
    }
    
    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, BlockPos pos)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    public Item getItemDropped(IBlockState state, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(mod_SecurityCraft.alarm);
    }
    
    @SideOnly(Side.CLIENT)
    public IBlockState getStateForEntityRender(IBlockState state)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
    }

    public IBlockState getStateFromMeta(int meta)
    {
    	return this.getDefaultState().withProperty(FACING, BlockAlarm.EnumOrientation.byMetadata(meta & 7));
    }

    public int getMetaFromState(IBlockState state)
    {   	
    	return ((BlockAlarm.EnumOrientation) state.getValue(FACING)).getMetadata(); 	
    }
    
    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {FACING});
    }
    
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityAlarm();
	}

	public String getHelpInfo() {
		return "The alarm will emit a siren sound effect whenever it is powered by redstone, and in 2-second intervals after that (modifiable in the config file).";
	}

	public String[] getRecipe() {
		return new String[]{"The alarm requires: 7 glass, 1 note block, 1 redstone", "XXX", "XYX", "XZX", "X = glass, Y = note block, Z = redstone"};
	}

    public static enum EnumOrientation implements IStringSerializable
    {
        DOWN(0, "down", EnumFacing.DOWN),
        EAST(1, "east", EnumFacing.EAST),
        WEST(2, "west", EnumFacing.WEST),
        SOUTH(3, "south", EnumFacing.SOUTH),
        NORTH(4, "north", EnumFacing.NORTH),
        UP(5, "up", EnumFacing.UP);
        private static final BlockAlarm.EnumOrientation[] META_LOOKUP = new BlockAlarm.EnumOrientation[values().length];
        private final int meta;
        private final String name;
        private final EnumFacing facing;

        private static final String __OBFID = "CL_00002102";

        private EnumOrientation(int meta, String name, EnumFacing facing)
        {
            this.meta = meta;
            this.name = name;
            this.facing = facing;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public EnumFacing getFacing()
        {
            return this.facing;
        }

        public String toString()
        {
            return this.name;
        }

        public static BlockAlarm.EnumOrientation byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public static BlockAlarm.EnumOrientation forFacings(EnumFacing clickedSide, EnumFacing entityFacing)
        {
            switch (BlockAlarm.SwitchEnumFacing.FACING_LOOKUP[clickedSide.ordinal()])
            {
                case 1:
                    return DOWN;
                case 2:
                	return UP;
                case 3:
                    return NORTH;
                case 4:
                    return SOUTH;
                case 5:
                    return WEST;
                case 6:
                    return EAST;
                default:
                    throw new IllegalArgumentException("Invalid facing: " + clickedSide);
            }
        }

        public String getName()
        {
            return this.name;
        }

        static
        {
        	BlockAlarm.EnumOrientation[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
            	BlockAlarm.EnumOrientation var3 = var0[var2];
                META_LOOKUP[var3.getMetadata()] = var3;
            }
        }
    }
	
	static final class SwitchEnumFacing
    {
        static final int[] FACING_LOOKUP;

        static final int[] ORIENTATION_LOOKUP;

        static final int[] AXIS_LOOKUP = new int[EnumFacing.Axis.values().length];
        private static final String __OBFID = "CL_00002103";

        static
        {
            try
            {
                AXIS_LOOKUP[EnumFacing.Axis.X.ordinal()] = 1;
            }
            catch (NoSuchFieldError var16)
            {
                ;
            }

            try
            {
                AXIS_LOOKUP[EnumFacing.Axis.Z.ordinal()] = 2;
            }
            catch (NoSuchFieldError var15)
            {
                ;
            }

            ORIENTATION_LOOKUP = new int[BlockAlarm.EnumOrientation.values().length];

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.EAST.ordinal()] = 1;
            }
            catch (NoSuchFieldError var14)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.WEST.ordinal()] = 2;
            }
            catch (NoSuchFieldError var13)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.SOUTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var12)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.NORTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var11)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.UP.ordinal()] = 5;
            }
            catch (NoSuchFieldError var10)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.UP.ordinal()] = 6;
            }
            catch (NoSuchFieldError var9)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.DOWN.ordinal()] = 7;
            }
            catch (NoSuchFieldError var8)
            {
                ;
            }

            try
            {
                ORIENTATION_LOOKUP[BlockAlarm.EnumOrientation.DOWN.ordinal()] = 8;
            }
            catch (NoSuchFieldError var7)
            {
                ;
            }

            FACING_LOOKUP = new int[EnumFacing.values().length];

            try
            {
                FACING_LOOKUP[EnumFacing.DOWN.ordinal()] = 1;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.UP.ordinal()] = 2;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 3;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 4;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 5;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 6;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
