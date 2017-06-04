package net.geforcemods.securitycraft.blocks;

import net.geforcemods.securitycraft.api.IIntersectable;
import net.geforcemods.securitycraft.api.IOwnable;
import net.geforcemods.securitycraft.main.mod_SecurityCraft;
import net.geforcemods.securitycraft.tileentity.TileEntityCageTrap;
import net.geforcemods.securitycraft.tileentity.TileEntityOwnable;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.Utils;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
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
    public EnumWorldBlockLayer getBlockLayer()
    {
        return EnumWorldBlockLayer.CUTOUT;
    }

	public AxisAlignedBB getCollisionBoundingBox(World par1World, BlockPos pos, IBlockState state){
		if(BlockUtils.getBlock(par1World, pos) == mod_SecurityCraft.cageTrap && !BlockUtils.getBlockPropertyAsBoolean(par1World, pos, DEACTIVATED)){
			return null;
		}else{
			return AxisAlignedBB.fromBounds(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ);
		}
	}
	
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

				world.playSoundEffect((double) pos.getX(),(double) pos.getY(),(double) pos.getZ(), "random.anvil_use", 3.0F, 1.0F);
				
				if(isPlayer)
					MinecraftServer.getServer().getConfigurationManager().sendChatMsg(new ChatComponentTranslation("["+ EnumChatFormatting.BLACK + StatCollector.translateToLocal("tile.cageTrap.name") + EnumChatFormatting.RESET + "] " + StatCollector.translateToLocal("messages.cageTrap.captured").replace("#player", ((EntityPlayer) entity).getCommandSenderName()).replace("#location", Utils.getFormattedCoordinates(pos))));
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
    	return state.getValue(DEACTIVATED).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {DEACTIVATED});
    }

	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCageTrap().intersectsEntities();
	}
    
}
