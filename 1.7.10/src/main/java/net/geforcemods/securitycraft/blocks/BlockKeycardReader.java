package net.geforcemods.securitycraft.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.geforcemods.securitycraft.SCContent;
import net.geforcemods.securitycraft.api.IPasswordProtected;
import net.geforcemods.securitycraft.items.ItemKeycardBase;
import net.geforcemods.securitycraft.misc.EnumCustomModules;
import net.geforcemods.securitycraft.tileentity.TileEntityKeycardReader;
import net.geforcemods.securitycraft.util.BlockUtils;
import net.geforcemods.securitycraft.util.ModuleUtils;
import net.geforcemods.securitycraft.util.PlayerUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockKeycardReader extends BlockOwnable {

	@SideOnly(Side.CLIENT)
	private IIcon keypadIconTop;
	@SideOnly(Side.CLIENT)
	private IIcon keypadIconFront;

	public BlockKeycardReader(Material material) {
		super(material);
	}

	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}

	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack){
		super.onBlockPlacedBy(world, x, y, z, entity, stack);

		int entityRotation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;

		if (entityRotation == 0)
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);

		if (entityRotation == 1)
			world.setBlockMetadataWithNotify(x, y, z, 5, 2);

		if (entityRotation == 2)
			world.setBlockMetadataWithNotify(x, y, z, 3, 2);

		if (entityRotation == 3)
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
	}

	public void insertCard(World world, int x, int y, int z, ItemStack stack, EntityPlayer player) {
		if(ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.WHITELIST) || ModuleUtils.checkForModule(world, x, y, z, player, EnumCustomModules.BLACKLIST))
			return;

		int requiredLevel = -1;
		int cardLvl = ((ItemKeycardBase) stack.getItem()).getKeycardLvl(stack);

		if(((TileEntityKeycardReader)world.getTileEntity(x, y, z)).getPassword() != null)
			requiredLevel = Integer.parseInt(((TileEntityKeycardReader)world.getTileEntity(x, y, z)).getPassword());

		if((!((TileEntityKeycardReader)world.getTileEntity(x, y, z)).doesRequireExactKeycard() && requiredLevel <= cardLvl || ((TileEntityKeycardReader)world.getTileEntity(x, y, z)).doesRequireExactKeycard() && requiredLevel == cardLvl)){
			if(cardLvl == 6 && stack.getTagCompound() != null && !player.capabilities.isCreativeMode){
				stack.getTagCompound().setInteger("Uses", stack.getTagCompound().getInteger("Uses") - 1);

				if(stack.getTagCompound().getInteger("Uses") <= 0)
					stack.stackSize--;
			}

			BlockKeycardReader.activate(world, x, y, z);
		}

		if(!world.isRemote)
		{
			if(requiredLevel != -1 && ((TileEntityKeycardReader)world.getTileEntity(x, y, z)).doesRequireExactKeycard() && requiredLevel != cardLvl)
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:keycardReader.name"), StatCollector.translateToLocal("messages.securitycraft:keycardReader.required").replace("#r", ((IPasswordProtected) world.getTileEntity(x, y, z)).getPassword()).replace("#c", "" + cardLvl), EnumChatFormatting.RED);
			else if(requiredLevel == -1)
				PlayerUtils.sendMessageToPlayer(player, StatCollector.translateToLocal("tile.securitycraft:keycardReader.name"), StatCollector.translateToLocal("messages.securitycraft:keycardReader.notSet"), EnumChatFormatting.RED);
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		if(world.isRemote)
			return true;

		if(player.getCurrentEquippedItem() == null || (!(player.getCurrentEquippedItem().getItem() instanceof ItemKeycardBase) && player.getCurrentEquippedItem().getItem() != SCContent.adminTool))
			((TileEntityKeycardReader) world.getTileEntity(x, y, z)).openPasswordGUI(player);
		else if(player.getCurrentEquippedItem().getItem() == SCContent.adminTool)
			((BlockKeycardReader) world.getBlock(x, y, z)).insertCard(world, x, y, z, new ItemStack(SCContent.keycards, 1, 3), player);
		else if(BlockUtils.isMetadataBetween(world, x, y, z, 2, 5))
			((BlockKeycardReader) world.getBlock(x, y, z)).insertCard(world, x, y, z, player.getCurrentEquippedItem(), player);

		return true;
	}

	public static void activate(World world, int x, int y, int z){
		world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 5, 3);
		world.notifyBlocksOfNeighborChange(x, y, z, SCContent.keycardReader);
		world.scheduleBlockUpdate(x, y, z, SCContent.keycardReader, 60);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random){
		if(!world.isRemote && world.getBlockMetadata(x, y, z) > 6 && world.getBlockMetadata(x, y, z) < 11)
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) - 5, 3);
	}

	@Override
	public boolean canProvidePower(){
		return true;
	}

	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side){
		if(world.getBlockMetadata(x, y, z) > 6 && world.getBlockMetadata(x, y, z) < 11)
			return 15;
		else
			return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random){
		int meta = world.getBlockMetadata(x, y, z);

		if(meta > 6 && meta < 11){
			double spawnX = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double spawnY = y + 0.7F + (random.nextFloat() - 0.5F) * 0.2D;
			double spawnZ = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2D;
			double magicNumber1 = 0.2199999988079071D;
			double magicNumber2 = 0.27000001072883606D;

			world.spawnParticle("reddust", spawnX - magicNumber2, spawnY + magicNumber1, spawnZ, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", spawnX + magicNumber2, spawnY + magicNumber1, spawnZ, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", spawnX, spawnY + magicNumber1, spawnZ - magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", spawnX, spawnY + magicNumber1, spawnZ + magicNumber2, 0.0D, 0.0D, 0.0D);
			world.spawnParticle("reddust", spawnX, spawnY, spawnZ, 0.0D, 0.0D, 0.0D);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if(side == 3 && meta == 0)
			return keypadIconFront;

		if(meta == 7 || meta == 8 || meta == 9 || meta == 10)
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != (meta - 5) ? blockIcon : keypadIconFront));
		else
			return side == 1 ? keypadIconTop : (side == 0 ? keypadIconTop : (side != meta ? blockIcon : keypadIconFront));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register){
		blockIcon = register.registerIcon("furnace_side");
		keypadIconTop = register.registerIcon("furnace_top");
		keypadIconFront = register.registerIcon("securitycraft:keycardReaderFront");
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityKeycardReader();
	}

}
