package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCageTrap extends BlockOwnable implements IIntersectable {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockCageTrap(Material par2Material) {
		super(par2Material);
	}
	
	public boolean isOpaqueCube(){
        return false;
    }
	
	public int getRenderType(){
		return 3;
	}
	
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos){
		if(BlockUtils.getBlock(worldIn, pos) == mod_SecurityCraft.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(worldIn, pos, DEACTIVATED)){
			return null;
		}else{
			return super.getCollisionBoundingBox(blockState, worldIn, pos);
		}
	}
	
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			if(entity instanceof EntityPlayer && !BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED)){
				if(((IOwnable)world.getTileEntity(pos)).getOwner().isOwner((EntityPlayer)entity))
					return;
				
				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				BlockUtils.setBlock(world, pos.up(4), mod_SecurityCraft.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, mod_SecurityCraft.unbreakableIronBars);	

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), mod_SecurityCraft.unbreakableIronBars);

				world.playSoundAtEntity(entity, "random.anvil_use", 3.0F, 1.0F);
				world.getMinecraftServer().addChatMessage(new TextComponentTranslation("["+ TextFormatting.BLACK + I18n.translateToLocal("tile.cageTrap.name") + TextFormatting.RESET + "] " + I18n.translateToLocal("messages.cageTrap.captured").replace("#player", ((EntityPlayer) entity).getName()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}
	
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(DEACTIVATED, false);
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DEACTIVATED, (meta == 1 ? true : false));
    }

    public int getMetaFromState(IBlockState state)
    {
    	return ((Boolean) state.getValue(DEACTIVATED)).booleanValue() ? 1 : 0;
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {DEACTIVATED});
    }

	public TileEntity createTileEntity(World worldIn, int meta) {
		return new TileEntityOwnable().intersectsEntities();
	}
    
}
