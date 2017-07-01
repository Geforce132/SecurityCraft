package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ClientUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCageTrap extends BlockOwnable implements IIntersectable {

	public static final PropertyBool DEACTIVATED = PropertyBool.create("deactivated");

	public BlockCageTrap(Material par2Material) {
		super(par2Material);
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state){
        return false;
    }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state){
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos){
		if(BlockUtils.getBlock(worldIn, pos) == mod_SecurityCraft.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(worldIn, pos, DEACTIVATED)){
			return null;
		}else{
			return blockState.getBoundingBox(worldIn, pos);
		}
	}
	
	@Override
	public void onEntityIntersected(World world, BlockPos pos, Entity entity) {
		if(!world.isRemote){
			TileEntityCageTrap tileEntity = (TileEntityCageTrap) world.getTileEntity(pos);
			boolean isPlayer = entity instanceof EntityPlayer;
			boolean shouldCaptureMobs = tileEntity.getOptionByName("captureMobs").asBoolean();
			
			if(isPlayer || (entity instanceof EntityMob && shouldCaptureMobs)){
				if((isPlayer && ((IOwnable)world.getTileEntity(pos)).getOwner().isOwner((EntityPlayer)entity)))
					return;
				
				if(BlockUtils.getBlockPropertyAsBoolean(world, pos, DEACTIVATED))
					return;
				
				BlockUtils.setBlockProperty(world, pos, DEACTIVATED, true);
				BlockUtils.setBlock(world, pos.up(4), mod_SecurityCraft.unbreakableIronBars);
				BlockUtils.setBlock(world, pos.getX() + 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX() - 1, pos.getY() + 4, pos.getZ(), mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() + 1, mod_SecurityCraft.unbreakableIronBars);	
				BlockUtils.setBlock(world, pos.getX(), pos.getY() + 4, pos.getZ() - 1, mod_SecurityCraft.unbreakableIronBars);	

				BlockUtils.setBlockInBox(world, pos.getX(), pos.getY(), pos.getZ(), mod_SecurityCraft.unbreakableIronBars);

				world.playSound(null, pos, SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 3.0F, 1.0F);
				
				if(isPlayer)
					world.getMinecraftServer().sendMessage(new TextComponentTranslation("["+ TextFormatting.BLACK + ClientUtils.localize("tile.cageTrap.name") + TextFormatting.RESET + "] " + ClientUtils.localize("messages.cageTrap.captured").replace("#player", ((EntityPlayer) entity).getName()).replace("#location", Utils.getFormattedCoordinates(pos))));
			}
		}
	}
	
	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand)
	{
        return this.getDefaultState().withProperty(DEACTIVATED, false);
	}
	
    @Override
	public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(DEACTIVATED, (meta == 1 ? true : false));
    }

    @Override
	public int getMetaFromState(IBlockState state)
    {
    	return state.getValue(DEACTIVATED).booleanValue() ? 1 : 0;
    }

    @Override
	protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {DEACTIVATED});
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCageTrap().intersectsEntities();
	}
    
}
