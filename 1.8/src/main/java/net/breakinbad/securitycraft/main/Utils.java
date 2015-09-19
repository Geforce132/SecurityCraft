package net.breakinbad.securitycraft.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.breakinbad.securitycraft.api.CustomizableSCTE;
import net.breakinbad.securitycraft.api.IOwnable;
import net.breakinbad.securitycraft.blocks.BlockInventoryScanner;
import net.breakinbad.securitycraft.items.ItemModule;
import net.breakinbad.securitycraft.misc.EnumCustomModules;
import net.breakinbad.securitycraft.network.packets.PacketSSyncTENBTTag;
import net.breakinbad.securitycraft.tileentity.TileEntityInventoryScanner;
import net.breakinbad.securitycraft.tileentity.TileEntityKeycardReader;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypad;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadChest;
import net.breakinbad.securitycraft.tileentity.TileEntityKeypadFurnace;
import net.breakinbad.securitycraft.tileentity.TileEntityOwnable;
import net.breakinbad.securitycraft.tileentity.TileEntityPortableRadar;
import net.breakinbad.securitycraft.tileentity.TileEntityRetinalScanner;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Utils {

//TODO North: Z-  South: Z+  East: X+  West: X-  Up: Y+  Down: Y-

public static class PlayerUtils{
	
	/**
	 * Gets the EntityPlayer instance of a player (if they're online) using their name. <p>
	 * 
	 * Args: playerName.
	 */
	@SuppressWarnings("rawtypes")
	public static EntityPlayer getPlayerFromName(String par1){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			List players = Minecraft.getMinecraft().theWorld.playerEntities;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getName().matches(par1)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}else{
			List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getName().matches(par1)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}
    }
	
	public static EntityPlayer getPlayerByUUID(String uuid){
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			List players = Minecraft.getMinecraft().theWorld.playerEntities;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getGameProfile().getId().toString().matches(uuid)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}else{
			List players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
	    	Iterator iterator = players.iterator();
	    	
	    	while(iterator.hasNext()){
	    		EntityPlayer tempPlayer = (EntityPlayer) iterator.next();
	    		if(tempPlayer.getGameProfile().getId().toString().matches(uuid)){
	    			return tempPlayer;
	    		}
	    	}
	    	
	    	return null;
		}
	}
	
	/**
	 * Returns true if a player with the given name is in the world.
	 * 
	 * Args: playerName.
	 */
	public static boolean isPlayerOnline(String par1) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
			for(int i = 0; i < Minecraft.getMinecraft().theWorld.playerEntities.size(); i++){
	    		EntityPlayer player = (EntityPlayer) Minecraft.getMinecraft().theWorld.playerEntities.get(i);
	    		
	    		if(player != null && player.getName().matches(par1)){
	    			return true;
	    		}
	    	}
			
			return false;
		}else{
			return (MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(par1) != null);  	
		}
    }
	
	public static void sendMessage(ICommandSender par1ICommandSender, String par2, EnumChatFormatting par3){
		ChatComponentText chatcomponenttext = new ChatComponentText(par2);
		chatcomponenttext.getChatStyle().setColor(par3);
        par1ICommandSender.addChatMessage(chatcomponenttext);
	}
	
	public static void sendMessageToPlayer(EntityPlayer par1EntityPlayer, String par2, EnumChatFormatting par3){
		ChatComponentText chatcomponenttext = new ChatComponentText(par2);
    	
		if(par3 != null){
			chatcomponenttext.getChatStyle().setColor(par3);
    	}
    	
		par1EntityPlayer.addChatComponentMessage(chatcomponenttext);
	}

	/**
	 * Returns true if the player is holding the given item.
	 * 
	 * Args: player, item.
	 */
	public static boolean isHoldingItem(EntityPlayer player, Item item){
		if(item == null && player.getCurrentEquippedItem() == null){
			return true;
		}
		
		return (player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == item);
	}
	
}

public static class BlockUtils{
	
	public static void setBlockInBox(World par1World, int par2, int par3, int par4, Block par5){
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 1, par4, par5); 
    	BlockUtils.setBlock(par1World, par2 + 1, par3 + 2, par4, par5);
    	BlockUtils.setBlock(par1World, par2 + 1, par3 + 3, par4, par5);
    	BlockUtils.setBlock(par1World, par2 + 1, par3 + 1, par4 + 1, par5);
    	BlockUtils.setBlock(par1World, par2 + 1, par3 + 2, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 3, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 1, par4, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 2, par4, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 3, par4, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 1, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 2, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 3, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2, par3 + 1, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2, par3 + 2, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2, par3 + 3, par4 + 1, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 1, par4, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 2, par4, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 3, par4, par5);

		BlockUtils.setBlock(par1World, par2, par3 + 1, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2, par3 + 2, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2, par3 + 3, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 1, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 2, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2 + 1, par3 + 3, par4 - 1, par5);
		
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 1, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 2, par4 - 1, par5);
		BlockUtils.setBlock(par1World, par2 - 1, par3 + 3, par4 - 1, par5);    
	}
	
	/**
	 * Updates a block and notify's neighboring blocks of a change.
	 * 
	 * Args: worldObj, pos, blockID, tickRate, shouldUpdate
	 * 
	 * 
	 */
	public static void updateAndNotify(World par1World, BlockPos pos, Block par5, int par6, boolean par7){
		if(par7){
			par1World.scheduleUpdate(pos, par5, par6);
		}
		par1World.notifyBlockOfStateChange(pos.east(), par1World.getBlockState(pos).getBlock());
		par1World.notifyBlockOfStateChange(pos.west(), par1World.getBlockState(pos).getBlock());
		par1World.notifyBlockOfStateChange(pos.south(), par1World.getBlockState(pos).getBlock());
		par1World.notifyBlockOfStateChange(pos.north(), par1World.getBlockState(pos).getBlock());
		par1World.notifyBlockOfStateChange(pos.up(), par1World.getBlockState(pos).getBlock());
		par1World.notifyBlockOfStateChange(pos.down(), par1World.getBlockState(pos).getBlock());
	}
	
	public static Item getItemFromBlock(Block par1){
		return Item.getItemFromBlock(par1);
	}
	
	public static void destroyBlock(World par1World, BlockPos pos, boolean par5){
		par1World.destroyBlock(pos, par5);
	}

	public static void destroyBlock(World par1World, int par2, int par3, int par4, boolean par5){
		par1World.destroyBlock(toPos(par2, par3, par4), par5);
	}
	
	public static boolean isAirBlock(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock() == Blocks.air;
	}

	public static boolean isAirBlock(World par1World, int par2, int par3, int par4){
		return par1World.getBlockState(toPos(par2, par3, par4)).getBlock() == Blocks.air;
	}
	
	public static int getBlockMeta(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock().getMetaFromState(par1World.getBlockState(pos));
	}

	public static int getBlockMeta(World par1World, int par2, int par3, int par4){
		return par1World.getBlockState(toPos(par2, par3, par4)).getBlock().getMetaFromState(par1World.getBlockState(toPos(par2, par3, par4)));
	}

	public static void setBlock(World par1World, BlockPos pos, Block block){
		par1World.setBlockState(pos, block.getDefaultState());
	}

	public static void setBlock(World par1World, int par2, int par3, int par4, Block block){
		setBlock(par1World, toPos(par2, par3, par4), block);
	}

	public static Block getBlock(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock();
	}

	public static Block getBlock(World par1World, int par2, int par3, int par4){
		return par1World.getBlockState(toPos(par2, par3, par4)).getBlock();
	}

	public static void setBlockProperty(World par1World, BlockPos pos, PropertyBool property, boolean value) {
		setBlockProperty(par1World, pos, property, value, false);
	}
	
	public static void setBlockProperty(World par1World, BlockPos pos, PropertyBool property, boolean value, boolean retainOldTileEntity) {
		if(retainOldTileEntity){
			ItemStack[] modules = null;
			ItemStack[] inventory = null;
			int[] times = new int[4];
			String password = "";
			String ownerUUID = "";
			String ownerName = "";
			int cooldown = -1;

			if(par1World.getTileEntity(pos) instanceof CustomizableSCTE){
				modules = ((CustomizableSCTE) par1World.getTileEntity(pos)).itemStacks;
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				inventory = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceItemStacks;
				times[0] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceBurnTime;
				times[1] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).currentItemBurnTime;
				times[2] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).cookTime;
				times[3] = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).totalCookTime;
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityOwnable && ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerUUID() != null){
				ownerUUID = ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerUUID();
				ownerName = ((TileEntityOwnable) par1World.getTileEntity(pos)).getOwnerName();
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityKeypad && ((TileEntityKeypad) par1World.getTileEntity(pos)).getPassword() != null){
				password = ((TileEntityKeypad) par1World.getTileEntity(pos)).getPassword();
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace && ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).getPassword() != null){
				password = ((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).getPassword();
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityKeypadChest && ((TileEntityKeypadChest) par1World.getTileEntity(pos)).getPassword() != null){
				password = ((TileEntityKeypadChest) par1World.getTileEntity(pos)).getPassword();
			}

			if(par1World.getTileEntity(pos) instanceof TileEntityPortableRadar && ((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown != 0){
				cooldown = ((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown;
			}

			TileEntity tileEntity = par1World.getTileEntity(pos);
			par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
			par1World.setTileEntity(pos, tileEntity);

			if(modules != null){
				((CustomizableSCTE) par1World.getTileEntity(pos)).itemStacks = modules;
			}

			if(inventory != null && par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceItemStacks = inventory;
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).furnaceBurnTime = times[0];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).currentItemBurnTime = times[1];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).cookTime = times[2];
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).totalCookTime = times[3];
			}

			if(!ownerUUID.isEmpty() && !ownerName.isEmpty()){
				((TileEntityOwnable) par1World.getTileEntity(pos)).setOwner(ownerUUID, ownerName);
			}

			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypad){
				((TileEntityKeypad) par1World.getTileEntity(pos)).setPassword(password);
			}

			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypadFurnace){
				((TileEntityKeypadFurnace) par1World.getTileEntity(pos)).setPassword(password);
			}

			if(!password.isEmpty() && par1World.getTileEntity(pos) instanceof TileEntityKeypadChest){
				((TileEntityKeypadChest) par1World.getTileEntity(pos)).setPassword(password);
			}

			if(cooldown != -1 && par1World.getTileEntity(pos) instanceof TileEntityPortableRadar){
				((TileEntityPortableRadar) par1World.getTileEntity(pos)).cooldown = cooldown;
			}
		}else{
			par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
		}
	}

	public static void setBlockProperty(World par1World, int par2, int par3, int par4, PropertyBool property, boolean value) {
		ItemStack[] modules = null;
		if(par1World.getTileEntity(new BlockPos(par2, par3, par4)) instanceof CustomizableSCTE){
			modules = ((CustomizableSCTE) par1World.getTileEntity(toPos(par2, par3, par4))).itemStacks;
		}

		TileEntity tileEntity = par1World.getTileEntity(toPos(par2, par3, par4));
		par1World.setBlockState(new BlockPos(par2, par3, par4), par1World.getBlockState(new BlockPos(par2, par3, par4)).withProperty(property, value));
		par1World.setTileEntity(new BlockPos(par2, par3, par4), tileEntity);

		if(modules != null){
			((CustomizableSCTE) par1World.getTileEntity(toPos(par2, par3, par4))).itemStacks = modules;
		}
	}
	
	public static void setBlockProperty(World par1World, BlockPos pos, PropertyInteger property, int value) {
		par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
	}
	
	public static void setBlockProperty(World par1World, BlockPos pos, PropertyEnum property, EnumFacing value) {
		par1World.setBlockState(pos, par1World.getBlockState(pos).withProperty(property, value));
	}

	public static boolean hasBlockProperty(World par1World, BlockPos pos, IProperty property){
		try{
			par1World.getBlockState(pos).getValue(property);
			return true;
		}catch(IllegalArgumentException e){
			return false;
		}
	}

	public static boolean hasBlockProperty(World par1World, int par2, int par3, int par4, IProperty property){
		return hasBlockProperty(par1World, toPos(par2, par3, par4), property);
	}

	public static boolean getBlockPropertyAsBoolean(World par1World, BlockPos pos, PropertyBool property){
		return ((Boolean) par1World.getBlockState(pos).getValue(property)).booleanValue();
	}

	public static boolean getBlockPropertyAsBoolean(World par1World, int par2, int par3, int par4, PropertyBool property){
		return ((Boolean) par1World.getBlockState(toPos(par2, par3, par4)).getValue(property)).booleanValue();
	}
	
	public static int getBlockPropertyAsInteger(World par1World, BlockPos pos, PropertyInteger property){
		return ((Integer) par1World.getBlockState(pos).getValue(property)).intValue();
	}

	public static int getBlockPropertyAsInteger(World par1World, int par2, int par3, int par4, PropertyInteger property){
		return ((Integer) par1World.getBlockState(toPos(par2, par3, par4)).getValue(property)).intValue();
	}
	
	public static EnumFacing getBlockPropertyAsEnum(World par1World, BlockPos pos, PropertyEnum property){
		return ((EnumFacing) par1World.getBlockState(pos).getValue(property));
	}

	public static EnumFacing getBlockPropertyAsEnum(World par1World, int par2, int par3, int par4, PropertyEnum property){
		return ((EnumFacing) par1World.getBlockState(toPos(par2, par3, par4)).getValue(property));
	}

	public static EnumFacing getBlockProperty(World par1World, BlockPos pos, PropertyDirection property) {
		return (EnumFacing) par1World.getBlockState(pos).getValue(property);
	}

	public static EnumFacing getBlockProperty(World par1World, int par2, int par3, int par4, PropertyDirection property) {
		return (EnumFacing) par1World.getBlockState(new BlockPos(par2, par3, par4)).getValue(property);
	}
	
	public static Material getBlockMaterial(World par1World, BlockPos pos){
		return par1World.getBlockState(pos).getBlock().getMaterial();
	}
	
	/**
	 * Checks if the block at x, y, z is touching the specified block on any side.
	 */
	public static boolean blockSurroundedBy(World world, BlockPos pos, Block blockToCheckFor, boolean checkYAxis) {
		if(!checkYAxis && (world.getBlockState(pos.east()).getBlock() == blockToCheckFor || world.getBlockState(pos.west()).getBlock() == blockToCheckFor || world.getBlockState(pos.south()).getBlock() == blockToCheckFor || world.getBlockState(pos.north()).getBlock() == blockToCheckFor)){
			return true;
		}else if(checkYAxis && (world.getBlockState(pos.east()).getBlock() == blockToCheckFor || world.getBlockState(pos.west()).getBlock() == blockToCheckFor || world.getBlockState(pos.south()).getBlock() == blockToCheckFor || world.getBlockState(pos.north()).getBlock() == blockToCheckFor || world.getBlockState(pos.up()).getBlock() == blockToCheckFor || world.getBlockState(pos.down()).getBlock() == blockToCheckFor)){
			return true;
		}else{
			return false;
		}
	}
	
	public static ItemStack getItemInTileEntity(IInventory inventory, ItemStack item){
		for(int i = 0; i < inventory.getSizeInventory(); i++){
			if(inventory.getStackInSlot(i) != null){
				if(inventory.getStackInSlot(i) == item){
					return inventory.getStackInSlot(i);
				}
			}
		}
		
		return null;
	}
	
	/**
	 * Returns true if the player is the owner of the given block.
	 * 
	 * Args: block's TileEntity, player.
	 */
	public static boolean isOwnerOfBlock(IOwnable block, EntityPlayer player){
		if(!(block instanceof IOwnable) || block == null){
			throw new ClassCastException("You must provide an instance of IOwnable when using Utils.isOwnerOfBlock!");
		}
		
		String uuid = block.getOwnerUUID();
		String owner = block.getOwnerName();
		
		if(uuid != null && !uuid.matches("ownerUUID")){
			return uuid.matches(player.getGameProfile().getId().toString());
		}
		
		if(owner != null && !owner.matches("owner")){
			return owner.matches(player.getName());
		}
		
		return false;
	}
	
	public static BlockPos toPos(int x, int y, int z){
		return new BlockPos(x, y, z);
	}
	
	public static int[] fromPos(BlockPos pos){
		return new int[]{pos.getX(), pos.getY(), pos.getZ()};
	}
	
}

public static class ModuleUtils{
	
	public static void insertModule(World par1World, BlockPos pos, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(pos)).insertModule(module);
	}
	
	public static void removeModule(World par1World, BlockPos pos, EnumCustomModules module){
		((CustomizableSCTE) par1World.getTileEntity(pos)).removeModule(module);
	}
	
	public static void checkForBlockAndInsertModule(World par1World, BlockPos pos, String dir, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.east(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.west(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.up(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).insertModule(module);
					if(updateAdjecentBlocks){ 
						checkInAllDirsAndInsertModule(par1World, pos.down(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.south(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(CustomizableSCTE.getTypeFromModule(module))){
					((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.north(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndInsertModule(World par1World, BlockPos pos, Block blockToCheckFor, int range, ItemStack module, boolean updateAdjecentBlocks){
		checkForBlockAndInsertModule(par1World, pos, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	public static void checkForBlockAndRemoveModule(World par1World, BlockPos pos, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.east(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.west(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.up(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).removeModule(module);
					if(updateAdjecentBlocks){ 
						checkInAllDirsAndRemoveModule(par1World, pos.down(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.south(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && ((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).removeModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndRemoveModule(par1World, pos.north(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndRemoveModule(World par1World, BlockPos pos, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		checkForBlockAndRemoveModule(par1World, pos, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndRemoveModule(par1World, pos, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
	}
	
	public static List<String> getPlayersFromModule(World par1World, BlockPos pos, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();
		
		CustomizableSCTE te = (CustomizableSCTE) par1World.getTileEntity(pos);
		
		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);
						
			for(int i = 1; i <= 10; i++){
				if(item.getTagCompound() != null && item.getTagCompound().getString("Player" + i) != null && !item.getTagCompound().getString("Player" + i).isEmpty()){
					list.add(item.getTagCompound().getString("Player" + i).toLowerCase());
				}
			}
		}
		
		return list;
	}

	public static ItemModule getItemFromModule(EnumCustomModules module) { //TODO Add any new modules to this list!
		if(module == EnumCustomModules.REDSTONE){
			return mod_SecurityCraft.redstoneModule;
		}else if(module == EnumCustomModules.WHITELIST){
			return mod_SecurityCraft.whitelistModule;
		}else if(module == EnumCustomModules.BLACKLIST){
			return mod_SecurityCraft.blacklistModule;
		}else if(module == EnumCustomModules.HARMING){
			return mod_SecurityCraft.harmingModule;
		}else if(module == EnumCustomModules.SMART){
			return mod_SecurityCraft.smartModule;
		}else{
			return null;
		}
	}

	
	public static boolean checkForModule(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = par1World.getTileEntity(pos);
		
		if(te == null || !(te instanceof CustomizableSCTE)){ return false; }
		
		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "You have been whitelisted on this keypad.", EnumChatFormatting.GREEN);
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "You have been blacklisted on this keypad.", EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "You have been whitelisted on this reader.", EnumChatFormatting.GREEN);
				par1World.notifyNeighborsOfStateChange(pos, par1World.getBlockState(pos).getBlock());
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName().toLowerCase())){
				PlayerUtils.sendMessageToPlayer(par5EntityPlayer, "You have been blacklisted on this reader.", EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				return true;
			}
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && ModuleUtils.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName().toLowerCase())){
				return true;
			}
		}
		
		return false;
	}

}

public static class EntityUtils{
	
	@SuppressWarnings("rawtypes")
	public static boolean doesMobHavePotionEffect(EntityLivingBase mob, Potion potion){
		Iterator iterator = mob.getActivePotionEffects().iterator();

		while(iterator.hasNext()){
			PotionEffect effect = (PotionEffect) iterator.next();
			String eName = effect.getEffectName();
			
			if(eName.matches(potion.getName())){
				return true;
			}else{
				continue;
			}
		}
		
		return false;
	}

}

public static class ClientUtils{
	
	@SideOnly(Side.CLIENT)
	public static void closePlayerScreen(){
		Minecraft.getMinecraft().thePlayer.closeScreen();
	}
	
	/**
	 * Sets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void setCameraZoom(double zoom){
		if(zoom == 0){
			ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 1.0D, 48);
			return;
		}
		
		double tempZoom = ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 48);
		ObfuscationReflectionHelper.setPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, tempZoom + zoom, 48);
	}
	
	/**
	 * Gets the "zoom" of the client's view.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static double getCameraZoom(){
		return ObfuscationReflectionHelper.getPrivateValue(EntityRenderer.class, Minecraft.getMinecraft().entityRenderer, 48);
	}
	
	/**
	 * Takes a screenshot, and sends the player a notification. <p>
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void takeScreenshot() {
        if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT){
        	Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(ScreenShotHelper.saveScreenshot(Minecraft.getMinecraft().mcDataDir, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, Minecraft.getMinecraft().getFramebuffer()));	
        }
	}
	
	/**
	 * Returns the current Minecraft in-game time, in a 12-hour AM/PM format.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static String getFormattedMinecraftTime(){
		Long time = Long.valueOf(Minecraft.getMinecraft().theWorld.provider.getWorldTime());
		
		int hours24 = (int) ((float) time.longValue() / 1000L + 6L) % 24;
		int hours = hours24 % 12;
		int minutes = (int) ((float) time.longValue() / 16.666666F % 60.0F);
		
		return String.format("%02d:%02d %s", new Object[]{Integer.valueOf(hours < 1 ? 12 : hours), Integer.valueOf(minutes), hours24 < 12 ? "AM" : "PM"});
	}
	
	/**
	 * Sends the client-side NBTTagCompound of a block's TileEntity to the server.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static void syncTileEntity(TileEntity tileEntity){
		NBTTagCompound tag = new NBTTagCompound();                
		tileEntity.writeToNBT(tag);
		mod_SecurityCraft.network.sendToServer(new PacketSSyncTENBTTag(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ(), tag));
	}
	
	/**
	 * Returns true if the client is hosting a LAN world.
	 * 
	 * Only works on the CLIENT side. 
	 */
	@SideOnly(Side.CLIENT)
	public static boolean isInLANWorld(){
		return (Minecraft.getMinecraft().getIntegratedServer() != null && Minecraft.getMinecraft().getIntegratedServer().getPublic());
	}
}

public static class WorldUtils{
	
	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, int x1, int y1, int z1, int x2, int y2, int z2){
		return isPathObstructed(world, (double) x1, (double) y1, (double) z1, (double) x2, (double) y2, (double) z2);
	}

	/**
	 * Performs a ray trace against all blocks (except liquids) from the starting X, Y, and Z
	 * to the end point, and returns true if a block is within that path.
	 * 
	 * Args: Starting X, Y, Z, ending X, Y, Z.
	 */
	public static boolean isPathObstructed(World world, double x1, double y1, double z1, double x2, double y2, double z2) {
		return world.rayTraceBlocks(new Vec3(x1, y1, z1), new Vec3(x2, y2, z2)) != null;
	}
	
}

	/**
	 * Removes the last character in the given String. <p>
	 */
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty()){ return ""; }
		
		return par1.substring(0, par1.length() - 1);
	}

	public static String getFormattedCoordinates(BlockPos pos){
		return "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
	}

	public static boolean toggleBoolean(boolean par1) {
		return !par1;
	}
	
	//----------------------//
	//    Random methods    //
	//----------------------//
	
	public static void setISinTEAppropriately(World par1World, BlockPos pos, ItemStack[] contents, String type) {
		if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.west(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.east(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.north(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south(2)) == mod_SecurityCraft.inventoryScanner && BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.south(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setType(type);
		}
	}
    
	public static boolean hasInventoryScannerFacingBlock(World par1World, BlockPos pos) {
		if(BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST){
			return true;
		}
		else if(BlockUtils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && BlockUtils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST){
			return true;
		}
		else if(BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH){
			return true;
		}
		else if(BlockUtils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && BlockUtils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH){
			return true;
		}else{
			return false;
		}
	}
    
}
