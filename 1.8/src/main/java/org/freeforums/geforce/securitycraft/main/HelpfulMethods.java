package org.freeforums.geforce.securitycraft.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import org.freeforums.geforce.securitycraft.blocks.BlockInventoryScanner;
import org.freeforums.geforce.securitycraft.items.ItemModule;
import org.freeforums.geforce.securitycraft.misc.EnumCustomModules;
import org.freeforums.geforce.securitycraft.tileentity.CustomizableSCTE;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityInventoryScanner;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeycardReader;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityKeypad;
import org.freeforums.geforce.securitycraft.tileentity.TileEntityRetinalScanner;
import org.freeforums.geforce.securitycraft.timers.ScheduleKeycardUpdate;
import org.freeforums.geforce.securitycraft.timers.ScheduleUpdate;

public class HelpfulMethods {
	
	//TODO North: Z-  South: Z+  East: X+  West: X-  Up: Y+  Down: Y-
	
	public static void setBlockInBox(World par1World, int par2, int par3, int par4, Block par5){
		Utils.setBlock(par1World, par2 + 1, par3 + 1, par4, par5); 
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 1, par4), par5, 1200);

    	Utils.setBlock(par1World, par2 + 1, par3 + 2, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 2, par4), par5, 1200);

    	Utils.setBlock(par1World, par2 + 1, par3 + 3, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 3, par4), par5, 1200);

    	Utils.setBlock(par1World, par2 + 1, par3 + 1, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 1, par4 + 1), par5, 1200);

    	Utils.setBlock(par1World, par2 + 1, par3 + 2, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 2, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 3, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 3, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 1, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 1, par4), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 2, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 2, par4), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 3, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 3, par4), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 1, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 1, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 2, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 2, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 3, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 3, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2, par3 + 1, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 1, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2, par3 + 2, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 2, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2, par3 + 3, par4 + 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 3, par4 + 1), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 1, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 1, par4), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 2, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 2, par4), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 3, par4, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 3, par4), par5, 1200);

		
		Utils.setBlock(par1World, par2, par3 + 1, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 1, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2, par3 + 2, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 2, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2, par3 + 3, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2, par3 + 3, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 1, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 1, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 2, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 2, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2 + 1, par3 + 3, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 + 1, par3 + 3, par4 - 1), par5, 1200);

		
		Utils.setBlock(par1World, par2 - 1, par3 + 1, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 1, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 2, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 2, par4 - 1), par5, 1200);

		Utils.setBlock(par1World, par2 - 1, par3 + 3, par4 - 1, par5);
    	par1World.scheduleUpdate(new BlockPos(par2 - 1, par3 + 3, par4 - 1), par5, 1200);

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

	public static int getNumberOfUsernames(String usernames) {
		Scanner scanner = new Scanner(usernames);
		scanner.useDelimiter(",");
		
		int i = 0;
		while(scanner.hasNext()){
			scanner.next();
			i++;
		}
		
		return i;
	}
	
	public static Item getItemFromBlock(Block par1){
		return Item.getItemFromBlock(par1);
	}
	
	public static void sendMessage(ICommandSender par1ICommandSender, String par2, EnumChatFormatting par3){
        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(par2, new Object[0]);
        chatcomponenttranslation.getChatStyle().setColor(par3);
        par1ICommandSender.addChatMessage(chatcomponenttranslation);
	}
	
	public static void sendMessageToPlayer(EntityPlayer par1EntityPlayer, String par2, EnumChatFormatting par3){
		ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation(par2, new Object[0]);
    	
		if(par3 != null){
    		chatcomponenttranslation.getChatStyle().setColor(par3);
    	}
    	
		par1EntityPlayer.addChatComponentMessage(chatcomponenttranslation);
	}
	
	
	
	public static String removeLastChar(String par1){
		if(par1 == null || par1.isEmpty()){ return ""; }
		
		return par1.substring(0, par1.length() - 1);
	}
	
	public static void checkForBlockAndInsertModule(World par1World, BlockPos pos, String dir, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		for(int i = 1; i <= range; i++){
			if(dir.equalsIgnoreCase("x+")){
				if(par1World.getBlockState(pos.east(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.east(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.east(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("x-")){
				if(par1World.getBlockState(pos.west(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.west(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.west(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y+")){
				if(par1World.getBlockState(pos.up(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.up(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.up(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("y-")){
				if(par1World.getBlockState(pos.down(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.down(i))).insertModule(module);
					if(updateAdjecentBlocks){ 
						checkInAllDirsAndInsertModule(par1World, pos.down(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z+")){
				if(par1World.getBlockState(pos.south(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.south(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.south(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}else if(dir.equalsIgnoreCase("z-")){
				if(par1World.getBlockState(pos.north(i)).getBlock() == blockToCheckFor && !((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).hasModule(module)){
					((CustomizableSCTE) par1World.getTileEntity(pos.north(i))).insertModule(module);
					if(updateAdjecentBlocks){
						checkInAllDirsAndInsertModule(par1World, pos.north(i), blockToCheckFor, range, module, updateAdjecentBlocks);
					}
				}
			}
		}
	}
	
	public static void checkInAllDirsAndInsertModule(World par1World, BlockPos pos, Block blockToCheckFor, int range, EnumCustomModules module, boolean updateAdjecentBlocks){
		checkForBlockAndInsertModule(par1World, pos, "x+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "x-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "y-", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z+", blockToCheckFor, range, module, updateAdjecentBlocks);
		checkForBlockAndInsertModule(par1World, pos, "z-", blockToCheckFor, range, module, updateAdjecentBlocks);
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
	
	public static void setISinTEAppropriately(World par1World, BlockPos pos, ItemStack[] contents, String type) {
		if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && Utils.getBlock(par1World, pos.west(2)) == mod_SecurityCraft.inventoryScanner && Utils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.west(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.west(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && Utils.getBlock(par1World, pos.east(2)) == mod_SecurityCraft.inventoryScanner && Utils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.east(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.east(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && Utils.getBlock(par1World, pos.north(2)) == mod_SecurityCraft.inventoryScanner && Utils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.north(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.north(2))).setType(type);
		}
		else if((EnumFacing) par1World.getBlockState(pos).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && Utils.getBlock(par1World, pos.south(2)) == mod_SecurityCraft.inventoryScanner && Utils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScannerField && (EnumFacing) par1World.getBlockState(pos.south(2)).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH){
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setContents(contents);
			((TileEntityInventoryScanner) par1World.getTileEntity(pos.south(2))).setType(type);
		}
	}
    
	public static boolean hasInventoryScannerFacingBlock(World par1World, BlockPos pos) {
		if(Utils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST && Utils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST){
			return true;
		}
		else if(Utils.getBlock(par1World, pos.west()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.west()).getValue(BlockInventoryScanner.FACING) == EnumFacing.EAST && Utils.getBlock(par1World, pos.east()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.east()).getValue(BlockInventoryScanner.FACING) == EnumFacing.WEST){
			return true;
		}
		else if(Utils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH && Utils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH){
			return true;
		}
		else if(Utils.getBlock(par1World, pos.north()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.north()).getValue(BlockInventoryScanner.FACING) == EnumFacing.SOUTH && Utils.getBlock(par1World, pos.south()) == mod_SecurityCraft.inventoryScanner && (EnumFacing) par1World.getBlockState(pos.south()).getValue(BlockInventoryScanner.FACING) == EnumFacing.NORTH){
			return true;
		}else{
			return false;
		}
	}
	
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
	
	public static EntityPlayer getPlayerFromName(String par1){
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

	public static List<String> getPlayersFromModule(World par1World, BlockPos pos, EnumCustomModules module) {
		List<String> list = new ArrayList<String>();
		
		CustomizableSCTE te = (CustomizableSCTE) par1World.getTileEntity(pos);
		
		if(te.hasModule(module)){
			ItemStack item = te.getModule(module);
						
			for(int i = 1; i <= 10; i++){
				if(item.getTagCompound() != null && item.getTagCompound().getString("Player" + i) != null && !item.getTagCompound().getString("Player" + i).isEmpty()){
					list.add(item.getTagCompound().getString("Player" + i));
				}
			}
		}
		
		return list;
	}
	
	public static boolean checkForModule(World par1World, BlockPos pos, EntityPlayer par5EntityPlayer, EnumCustomModules module){
		TileEntity te = par1World.getTileEntity(pos);
		
		if(te == null || !(te instanceof CustomizableSCTE)){ return false; }
		
		if(te instanceof TileEntityKeypad){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName())){
				HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "You have been whitelisted on this keypad.", EnumChatFormatting.GREEN);
				new ScheduleUpdate(par1World, 3, pos.getX(), pos.getY(), pos.getZ());
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName())){
				HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "You have been blacklisted on this keypad.", EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityKeycardReader){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName())){
				HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "You have been whitelisted on this reader.", EnumChatFormatting.GREEN);
				((TileEntityKeycardReader) te).setIsProvidingPower(true);
				new ScheduleKeycardUpdate(3, par1World, pos.getX(), pos.getY(), pos.getZ());
				par1World.notifyNeighborsOfStateChange(pos, par1World.getBlockState(pos).getBlock());
				return true;
			}
			
			if(module == EnumCustomModules.BLACKLIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.BLACKLIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.BLACKLIST).contains(par5EntityPlayer.getName())){
				HelpfulMethods.sendMessageToPlayer(par5EntityPlayer, "You have been blacklisted on this reader.", EnumChatFormatting.RED);
				return true;
			}
		}else if(te instanceof TileEntityRetinalScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName())){
				return true;
			}
		}else if(te instanceof TileEntityInventoryScanner){
			if(module == EnumCustomModules.WHITELIST && ((CustomizableSCTE) te).hasModule(EnumCustomModules.WHITELIST) && HelpfulMethods.getPlayersFromModule(par1World, pos, EnumCustomModules.WHITELIST).contains(par5EntityPlayer.getName())){
				return true;
			}
		}
		
		return false;
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

	public static boolean toggleBoolean(boolean par1) {
		boolean tempBoolean = par1;
		
		if(tempBoolean){
			tempBoolean = false;
		}else{
			tempBoolean = true;
		}
		
		return tempBoolean;
	}
	
//	private static void bookCode(){
//		ItemStack book = new ItemStack(Items.written_book);
//		
//		NBTTagCompound tag = new NBTTagCompound();
//		NBTTagList bookPages = new NBTTagList();
//		bookPages.appendTag(new NBTTagString("SecurityCraft " + mod_SecurityCraft.getVersion() + " info book."));
//		bookPages.appendTag(new NBTTagString("Keypad: \n \nThe keypad is used by placing the keypad, right-clicking it, and setting a numerical passcode. Once the keycode is set, right-clicking the keypad will allow you to enter the code. If it's correct, the keypad will emit redstone power for three seconds."));
//		bookPages.appendTag(new NBTTagString("Laser block: The laser block is used by putting two of them within five blocks of each other. When the blocks are placed correctly, a laser should form between them. Whenever a player walks through the laser, both the laser blocks will emit a 15-block redstone signal."));
//		
//		book.setTagInfo("pages", bookPages);
//		book.setTagInfo("author", new NBTTagString("Geforce"));
//		book.setTagInfo("title", new NBTTagString("SecurityCraft"));
//		
//		player.inventory.addItemStackToInventory(book);
//	}
	
	
}
