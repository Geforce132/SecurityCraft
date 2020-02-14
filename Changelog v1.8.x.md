--------------------------Changelog for v1.8.17 of SecurityCraft--------------------------

- New: The Universal Block Reinforcer can now "unreinforce" blocks. Place items in the top slot to reinforce them, use the bottom slot instead for "unreinforcing" them
- Change: The Username Logger now displays logged users in a scrollable list
- Change: The floor of Block Pockets is now solid (reactivate Block Pockets to fix this for already existing ones)
- Fix: Lasers don't get removed correctly (Thanks Redstone_Dubstep!)
- Fix: Motion Activated Light activates when a player is mounted to a camera in its range
- Fix: Being in fake lava creates a hurting sound
- Fix: Entities keep burning when exiting fake lava
- Fix: Some mines drop when exploding
- Fix: Camera overlay is incorrect when only night vision is activated
- Fix: Track Mines cannot be defused/armed with Wire Cutters/Flint and Steel
- Fix: Electrified Iron Fence Gates cannot be opened by reinforced pressure plates
- Fix: Feedback message is missing when changing the owner of a Sentry
- Fix: It's possible to reinforce more items than the universal block reinforcer has durability for
- Fix: Codebreaker loses damage even when rightclicking blocks that are not password-protected
- Fix: Reinforced (Stained) Glass sides are visible through the same type of Reinforced (Stained) Glass
- Fix: Reinforced (Stained) Glass throws a shadow
- Fix: Dropped items get destroyed in fake water
- Fix: Camera does not render in third person view when being mounted to one
- Fix: It's possible to remove or otherwise interact with a camera when being mounted to it
- Potential Fix: ConcurrentModificationException when saving a linkable tile entity

--------------------------Changelog for v1.8.16 of SecurityCraft--------------------------

- New: The Retinal Scanner and the Scanner Door now have an option to turn off the "Hello" message. Available by rightclicking them with a Universal Block Modifier
- New: The Alarm now has an option to set the range of blocks that it can be heard in (0-100)
- New: The Cage Trap can now be disguised
- New: The SC Manual now shows available options and modules for customizable blocks
- New: French translation (Thanks marminot!)
- Change: Additionally to sneak-rightclicking, sentries can now also be removed using the Universal Block Remover
- Change: The powered taser now inflicts one heart of damage instead of half a heart
- Fix: Players other than the one mounted to a camera can take screenshots or otherwhise interact with the camera
- Fix: Players that don't own an Inventory Scanner can still take out its items
- Fix: Inventory Scanner Field's selection box is slightly off
- Fix: Reinforced Doors and Scanner Doors can be broken using pistons
- Fix: Block Pocket Wall's sides are visible through other Block Pocket Walls
- Fix: Activating a Cage Trap can delete blocks that shouldn't be deleted
- Fix: Sentry Remote Access Tool tooltip does not show sentry names
- Fix: Portable Radar can duplicate modules
- Fix: Inventory Scanner removes its owner's items
- Fix: Laser harms its owner if the harming module is installed
- Fix: Trophy System does not target arrows shot from dispensers
- Fix: Copied module lists can only be pasted once
- Fix: Various language fixes and updates (Thanks supercat95 and Redstone_Dubstep!)
- Fix: Removing a laser (block) may sometimes break lasers that shouldn't be broken
- Fix: Various mines do not ignore their owner
- Fix: Sentry can be placed in the same block space as other blocks
- Fix: Breaking a Password-protected Chest does not drop its contents when using SpongeForge (Thanks gununakuna!)
- Fix: Breaking a Laser Block/Inventory Scanner keeps modules in connected Laser Blocks/Inventory Scanners
- Fix: Crash when summoning sentry using commands (Thanks LorenaGdL!)
- Potential Fix: Block Pocket still loses its owner when reloading world/server

--------------------------Changelog for v1.8.15 of SecurityCraft--------------------------

- New: Reinforced Bookshelf (Can also be used for an enchanting table)
- New: If a Briefcase has been renamed, it will now show that name in the inventory GUI
- New: Sentry Remote Access Tool to remotely control sentries, analogously to the Mine Remote Access Tool (Thanks LorenaGdL!)
- New: The following blocks can now be disguised with the Disguise Module, similar to the Keypad: Inventory Scanner, Keycard Reader, Laser Block, Retinal Scanner, Username Logger
- New: Sentries can now be named. Names show up in the Sentry Remote Access Tool
- New: The Universal Owner Changer now works for sentries
- New: I.M.S. can now target only hostile mobs
- New: Page indicators for SecurityCraft Manual
- New: Reinforced Redstone Block
- Change: Rightclicking a double chest with a Key Panel will now convert the double chest instead of just a single chest
- Change: Lasers now look more like lasers
- Change: Inventory Scanner Fields now look more like Inventory Scanner Fields (Thanks LorenaGdL!)
- Change: More recipes now use reinforced blocks instead of their vanilla equivalent
- Change: The Sentry no longer needs 4 blocks around it to be placeable
- Change: The Cage Trap now has a ceiling to properly prevent players from escaping (Thanks LorenaGdL!)
- Fix: Inventory Scanner does not check for prohibited items in armor and offhand slots
- Fix: Crash when the Trophy System tries to destroy a sentry's bullet
- Fix: Alarm does not turn off when it no longer receives a redstone signal
- Fix: Keypad blacklist does not work
- Fix: Trophy System crash
- Fix: Sentry head does not show when rejoining world/dimension if it was previously showing (Thanks LorenaGdL!)
- Fix: Cage Trap uses stone instead of metal sounds
- Fix: Disguised Keypad doesn't show up as disguised when switching worlds/dimensions
- Fix: Laser Block can duplicate modules
- Fix: I.M.S. does not emit a sound when launching one of its mines
- Fix: Username Logger logs its owner
- Fix: Laser Blocks can connect to other Laser Blocks that don't have the same owner
- Fix: I.M.S. particles don't work
- Fix: Sentry kick/crash
- Fix: Module duplication
- Fix: Message about cage trap having trapped someone gets sent to the one being trapped and not the cage trap owner (Thanks LorenaGdL!)
- Misc.: The minimum required Forge version is now 14.23.5.2826

--------------------------Changelog for v1.8.14.1 of SecurityCraft--------------------------

- Fix: Crash when opening customizing GUI of blocks that can't take modules
- Fix: In the customizing GUI, JEI moves items out of the way without needing to
- Fix: Keycard Reader GUI displays for a short amount of time when opening the customizing GUI
- Fix: Inventory Scanner does not check for prohibited items in armor and offhand slots
- Misc.: Added another safeguard against alarm crash

--------------------------Changelog for v1.8.14 of SecurityCraft--------------------------

- New: Reinforced Glass Panes can now be crafted using six Reinforced Glass
- New: Reinforced Stained Glass Panes can now be crafted by surrounding a dye with eight Reinforced Glass Panes
- New: All Reinforced Terracotta blocks can now be crafted analogous to their vanilla counterparts
- New: Reinforced Red Sand
- New: Reinforced Wooden Pressure Plate
- New: The Block Pocket now tells the player when it was successfully de-/activated
- Change: The Reinforced Iron Trapdoor is now created by putting a regular Iron Trapdoor into a Universal Block Reinforcer
- Change: The Frame recipe is slightly different, ingredients stay the same
- Change: The Sentry now uses Reinforced Blocks of Iron in its recipe
- Fix: Username Logger crash involving armor stands
- Fix: Reinforced Diorite and Reinforced Andesite use vanilla Cobblestone instead of Reinforced Cobblestone
- Fix: Using six Reinforced Stained Glass to craft Reinforced Stained Glass Panes yields 8 glass panes instead of 16
- Fix: Chests and Furnaces cannot be converted to their password-protected variant when not sneak-rightclicking
- Fix: Disguised Keypad collision and selection boxes do not respect the boxes of the block it's disguised as
- Fix: Block Pocket Description incorrectly mentions Reinforced Quartz instead of Reinforced Crystal Quartz
- Fix: Entities do not get healed when standing in fake lava
- Potential Fix: Block Pocket loses its owner when the player reloads the world

--------------------------Changelog for v1.8.13.1 of SecurityCraft--------------------------

- Fix: Disguising a Keypad does not synchronize to other players
- Fix: Sentry animation is not synchronized between players

--------------------------Changelog for v1.8.13 of SecurityCraft--------------------------

- New: Italian translation (Thanks Chexet48!)
- New: Trophy System (Inspired from https://callofduty.fandom.com/wiki/Trophy_System)
- New: Block Pocket (Designed by Henzoid)
- New: The Whitelist Module can now be used with the Sentry
- New: Button to clear the Username Logger logged players list
- New: Reinforced Stone Pressure Plate (Only the owner and whitelisted users can press it, can be used to open Reinforced Iron (Trap-)Doors)
- New: The Smart Module now works for Security Cameras, enabling others to view cameras at a fixed angle
- Change: The Motion Activated Light is now triggered by mobs as well (This change includes tweaks to the attack logic that may impact other blocks, like the Portable Radar or the I.M.S., as well)
- Change: New Secret Sign textures
- Fix: Inventory Scanner ignores contents of Shulker Boxes
- Fix: Portable Radar does not update redstone signal correctly when Redstone Module is installed
- Fix: Installed modules do not drop when destroying block with Universal Block Remover
- Fix: SecurityCraft Manual does not correctly display items with metadata
- Fix: Misc. crashes
- Fix: Taser can be duplicated using offhand slot (Thanks Boreaus!)
- Fix: Incorrect version of the taser shows up in the creative tab and JEI
- Removed: "/module" command. In order to interact with blacklist/whitelist modules, rightclick them
- Misc.: Backend improvements

--------------------------Changelog for v1.8.12.1 of SecurityCraft--------------------------

- Fix: Crash involving the Security Camera

--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- New: The One Probe support (https://minecraft.curseforge.com/projects/the-one-probe)
- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Crash when rightclicking the Keycard Reader with a Keycard without having it set up first
- Fix: French translation does not work
- Fix: Password-protected Chest doesn't show in the inventory under certain circumstances

--------------------------Changelog for v1.8.11 of SecurityCraft--------------------------

- New: Reinforced Gravel
- New: Reinforced Sand
- New: Sentry
	- This feature was designed by Henzoid, who won our New Year's Eve giveaway! If you want to get notified of	future giveaways, join our Discord server: https://discord.gg/U8DvBAW
	- If you want to completely design a feature of your choice, consider becoming a patron! https://www.patreon.com/Geforce
- New: The SC Manual now shows text, that is too long to properly display, on multiple subpages
- Change: Using the left and right arrow keys in the SC Manual will now change subpages
- Change: The viewing range of cameras placed on the ceiling has been increased drastically
- Fix for real: Reinforced blocks can be destroyed by the Wither
- Fix: The Mine Remote Access Tool's GUI displays incorrectly for mines that cannot be defused
- Fix: Two messages appear instead of one when using the Keycard Reader with an insufficient security level
- Fix: Disguise Module tooltip shows unlocalized block name
- Fix: Miscellaneous crashes
- Misc.: Small adjustments in english translation

--------------------------Changelog for v1.8.10 of SecurityCraft--------------------------

- New: Config option to allow players to claim blocks that do not have an owner. Only blocks with the owner "owner" and uuid "ownerUUID" will work (check with the Admin Tool)
~~- Fix: SecurityCraft blocks can be destroyed by the Wither~~
- Fix: Typo in German localization
- Fix: Keybind localization does not work
- Fix: Rightclicking another player's chest with a Universal Block Remover/Modifier does not show a message

--------------------------Changelog for v1.8.9 of SecurityCraft--------------------------

**This version no longer supports 1.12 and 1.12.1**

- New: Whitelist and Blacklist Modules can now be rightclicked to manage the players
- New: The SC Manual now has a recipe (Book + Iron Bars)
- Fix: Crash when using a Forge version that is too old. Instead, the game will now notify the user to update their Forge version
- Fix: Blocks do not show up correctly in the SC Manual (Thanks supercat95!)
- Misc.: The minimum required Forge build is now 14.23.3.2694

--------------------------Changelog for v1.8.8.1 of SecurityCraft--------------------------

- Fix: Players can teleport to a Security Camera's position when logging out while viewing one
- Fix: Crash when reinforcing reinforced blocks added in v1.8.8 (Thanks AlexM-Dev!)

--------------------------Changelog for v1.8.8 of SecurityCraft--------------------------

- New: Reinforced Blocks
		- Carpet
		- Glowstone
		- Stained Glass Panes are back!
- New: Reinforced Stained Glass and Reinforced Stained Glass Panes can now be created using the Universal Block Reinforcer (alongside the already existing recipes)
- New: The Track Mine can now be controlled with the Mine Remote Access Tool
- New: The Briefcase's code can now be reset by crafting it together with a Universal Key Changer
- New: The Admin Tool now shows the text on Secret Signs
- New: The I.M.S. can now be reloaded by rightclicking it with Bouncing Betties
- New: Reinforced Blocks of Iron, Gold, Diamond, and Emerald can now be used as a Beacon base
- New: Gravel Mine
- New: Sand and Gravel Mines now fall like normal sand/gravel
- New: Reinforced Moss Stone can now be crafted with a Reinforced Cobblestone and a Vine
- New: The several Reinforced Stone variants (Reinforced Granite, Reinforced Diorite, Reinforced Andesite) can now be crafted analogous to their vanilla counterparts
- New: Security Cameras can now be switched when already viewing a camera
- Change: Item tooltips in the SecurityCraft Manual now show the complete tooltip instead of just the name
- Change: The Alarm recipe now needs Reinforced Glass instead of vanilla glass
- Change: The Universal Block Reinforcer will now reinforce placed down blocks instantly
- Change: When broken, the I.M.S. will now drop any charges left
- API: Added a data serializer to the Owner class
- Fix: Torch/door/etc. placement on several SecurityCraft blocks does not work as expected
- Fix: Mines that don't exist in the world anymore do not disappear from a Mine Remote Access Tool, if bound
- Fix: Item duplication with Inventory Scanner and Storage Module
- Fix: Inventory Scanner's redstone mode doesn't respect Smart Module
- Fix: Items thrown through an Inventory Scanner Field don't respect the Smart and Storage Module
- Fix: Inventory Scanner does not trigger on item stacks that have a size greater than one
- Fix: When breaking an Inventory Scanner, items placed in the blacklist slots will drop
- Fix: Iron/Reinforced Trapdoor can be opened by redstone
- Fix: Placing/breaking Inventory Scanners can break other Inventory Scanner's scanner fields
- Fix: Name inconsistencies with vanilla
- Fix: Admin Tool doesn't get properly disabled when it is disabled in the config
- Fix: Existing translations do not work
- Fix: Taser reequips all the time when loading after a shot (Note that this cannot be implemented in 1.7.10 due to limitations in Forge)
- Fix: Password-protected Furnace does not give off light when open and burning
- Fix: The Inventory Scanner Field is missing its top and bottom texture
- Fix: Storage Module does not work
- Fix: Model loading errors
- Fix: Reinforced Iron Bars model is not the same as the vanilla Iron Bars model
- Fix: Password-protected Chest does not synchronize owner correctly when loading world
- Fix: Item Stack tooltips don't get rendered in SecurityCraft inventories
- Fix: Beacon color with Reinforced Stained Glass is slightly incorrect
- Fix: TileEntity IDs are not registered to SecurityCraft's domain
- Misc.: Possibly improved render performance a bit
- Internal: Removed unused code
- Internal: Refactored to make code more readable
- Internal: Use recommended way of registering content

--------------------------Changelog for v1.8.7 of SecurityCraft--------------------------

**!!WARNING!! Upgrading to this version of SecurityCraft will deny access to any items that are placed in the blacklist slots of any Inventory Scanner (they will *not* disappear, but you won't be able to get them back)**

- New: The SC Manual can now be navigated using the mouse's scroll wheel
- New: The SC Manual now remembers the last page that was viewed and opens back up at that page
- New: Reinforced Obsidian can now be used for nether portals (Note that this cannot be implemented in 1.7.10 due to limitations in Forge)
- Change: The Inventory Scanner's blacklist slots are now "ghost slots", which means that items can be put into and pulled out of them, but no physical item will actually be used
- Fix: Taser doesn't respect PVP settings
- Fix: Blocks in the Manual aren't shaded correctly
- Fix: Portable Radar does not work
- Fix: Portable Radar doesn't respect the whitelist module
- Fix: Players mounted on cameras trigger motion activated lights
- Fix: Various strings aren't translated
- Fix: Storage Module slots on the Inventory Scanner's GUI texture are flipped
- Fix: Reinforced Doors cannot be opened from the top or bottom
- Fix: Grass can grow under Fake Water (Note that this fix cannot be applied to 1.8.9 and below due to limitations in Forge)
- Fix: Fake liquids turn back into their vanilla forms (Note that this fix cannot be applied to 1.8.9 and below due to limitations in Forge)
- Fix: Dupe bug involving Reinforced (Scanner) Doors (Thanks InsertCheerios!)

--------------------------Changelog for v1.8.6 of SecurityCraft--------------------------

- New: Rightclicking an activated Cage Trap with a Universal Block Remover will remove the complete cage, provided the trap and bars are owned by the same person
- Fix: Possible conflicting language strings with other mods
- Fix: Crashes involving the Username Logger or Inventory Scanner Field (Thanks Kreezxil!)
- Fix: Fix incorrect lighting in some GUIs
- Fix: Fix GUI tooltips rendering under JEI interface

--------------------------Changelog for v1.8.5 of SecurityCraft--------------------------

- New: Two Reinforced Doors placed next to each other will open together when one of them is opened
- New: Completely overhauled the GUI of the Mine Remote Access Tool
- New: The Security Camera can now be placed on the ceiling
- New: Sneak-rightclicking the Taser with redstone in the inventory will double the Taser's power for one shot
- New: Secret Sign which can only be read by its owner
- New: Reinforced Blocks
	- Obsidian
	- End Stone
	- Netherrack
	- Sea Lantern
	- Bone Block
- New: Motion Activated Light
- Change: When viewing a camera, the view now more closely represents what the camera would see
- Change: The Briefcase recipe now requires a Password-protected Chest instead of a regular one
- Change: The Taser now only applies level 2 potion effects for 10 seconds
- Fix: Language key for Codebreaker doesn't inform the user about its limited usability
- Fix: Welcome message shows when connecting to a server, even when disabling it on clientside
- Fix: Name Tag gets deleted when renaming cameras in creative mode
- Fix: Incorrect model for double Password-protected Chest
- Fix: Rare crash involving Inventory Scanner Fields
- Fix: Fake Liquids sometimes don't work correctly
- Fix: Furnace Mine doesn't explode when an item is held
- Fix: Password-protected blocks can be set-up by players other than the owner
- Fix: Inconsistent Alarm model
- Fix: Protecto does not work
- Fix: Some trigger ranges for SecurityCraft blocks were not calculated correctly
- Fix: Under certain circumstances, the Alarm doesn't break when the block it's placed on is removed
- Potential Fix: StackOverflowException involving block mines
- Sponge Fix: Keycard Reader cannot be configured
- Sponge Fix: Server crashes when placing Reinforced Stained Glass over an active Beacon

--------------------------Changelog for v1.8.4.1 of SecurityCraft--------------------------

- Fix:Crash when inserting a module into a Laser Block (Thanks Shrimplet596!)

--------------------------Changelog for v1.8.4 of SecurityCraft--------------------------

- New: Inventory Scanners can now be configured to have a higher range, similar to Laser Blocks
- Change: Password-protected blocks no longer have a crafting recipe. Instead, rightclick a Frame/Chest/Furnace with a Key Panel to create them (any contents are safe!)
- Fix: Sounds don't respect their correct categories
- Fix: Laser fields break when breaking other Laser Blocks placed orthogonal to the fields
- Fix: Items can be duplicated within the Module GUI
- Fix: Reinforced Iron Trapdoor can be broken by any player (Thanks shaiapouf!)
- Fix: Portable Radar can be broken by any player
- Fix: Username Logger can be broken by any player
- Fix: Alarm sound pitch is incorrect
- Fix: Password-protected Furnace doesn't drop items when being broken
- Fix: Security Camera doesn't update Redstone correctly when the Redstone Module is removed/added/turned on/turned off
- Fix: SecurityCraft Tile Entity data doesn't get synchronized correctly with clients on world load [1]
- Fix: Module GUI cannot be accessed
- Fix: Crashes
- Fix: Alarm sound volume config option does not affect the ingame sound
- Fix: Incorrect string in Password-protected Furnace GUI
- Fix: Some SecurityCraft sounds don't play at all (Taser/Camera)
- Removed: Tip for /sc connect
- Internal: Refactoring to make code a little more readable and cleaner
- Internal: Removed legacy code

[1] This fixes:
  1. The camera not rotating when reloading the world
  2. The Redstone Module not working when trying to change the camera's redstone output right after reloading the world
  3. The Keypad not being disguised after reloading the world
  4. Probably some other things
  
--------------------------Changelog for v1.8.3 of SecurityCraft--------------------------

- New: Reinforced Blocks
  - Logs
  - Lapis Lazuli Block
  - Block of Coal
  - Block of Gold
  - Block of Iron
  - Block of Diamond
  - Block of Emerald
  - Wool
  - Quartz incl. slabs and stairs
  - Prismarine/Prismarine Bricks/Dark Prismarine
  - Red Sandstone incl. slabs and stairs
  - (Smooth) Granite/Andesite/Diorite
  - End Stone Bricks
  - Red Nether Brick
  - Purpur incl. slabs and stairs
  - Concrete
- New: Keypad Gurnace
- New: Information about how to exit the Security Camera
- New: Cameras can now be unbound from within the Camera Monitors' GUI, even if they're no longer present in the world (press the X at the top right of the respective button)
- New: JEI (JustEnoughItems) now shows information about blocks and items without a recipe
- Change: The SecurityCraft Manual now only displays one general page about reinforced blocks as not to clutter the book
- Fix: Reinforced Stained Hardened Clay (1.12.2: Terracotta) isn't craftable
- Fix: Several incorrect language strings
- Fix: Mines can be activated when viewing a camera (thanks LeKoopa!)
- Fix: Incorrect string in Password-protected Furnace GUI
- Fix: Crash involving the new Camera Monitor indicator (overlay in inventory when looking at a camera)
- Fix: The Camera Monitor's inventory overlay sometimes shows incorrect information
- Fix: Players get kicked sometimes when using the Codebreaker on a server
- Fix: Issue when rightclicking a block with a module
- Fix: The '/sc' command doesn't always show help when executing it incorrectly
- Fix: Crash when trying to open a blocked Password-protected Chest
- Fix: Unlocalized string in I.M.S. settings menu
- Fix: Incorrect rendering of the Camera Monitor's inventory overlay
- Fix: Buttons overlapping with the JEI interface are not accessible
- Fix: Sponge incompatibilities (~~untested on 1.10.2 and 1.11.2, please report any issues you find to our Discord's #bugreport channel.~~ As of 1. January 2018 Sponge no longer supports 1.10/1.11 versions, SecurityCraft will do the same)
- Fix: I.M.S. settings menu is not accessible
- Removed: IRC support chat. Please refer to the #help channel on SecurityCraft's Discord! https://discord.gg/U8DvBAW
- Internal: Rewrote handling of reinforced blocks (Now much easier to add them)

--------------------------Changelog for v1.8.2.4 of SecurityCraft--------------------------

- New: Camera can be set to a fixed angle using the Universal Block Modifier
- New: When looking at a camera while holding a monitor, an overlay will be shown based on if the camera is added to the monitor or not
- New: Disabled recipes now show an empty grid in the SecurityCraft Manual
- Fix: Unintended behavior when using '/sc contact' on a multiplayer server
- Fix: Invalid Discord invite link
- Fix: IRC security issue
- Fix: Incorrect German language strings
- Fix: Retinal Scanner doesn't respect the whitelist module
- Fix: Adding/Removing an active Redstone Module does not update the state of connected redstone
- Fix: Installed modules disappear in certain situations
- Fix: Keycard recipes cannot be disabled via config
- Fix: WAILA can now be used again (Use Hwyla https://minecraft.curseforge.com/projects/hwyla)

--------------------------Changelog for v1.8.2.3 of SecurityCraft (v1.8.2.3-hotfix below)--------------------------

- New: Reinforced Blocks
  - Stone Bricks (normal, mossy, cracked, chiseled) incl. stairs and slabs
  - Mossy Cobblestone
  - Bricks incl. stairs and slabs
  - Nether Bricks incl. stairs and slabs
  - Hardened Clay
  - Terracotta
- New: Official SecurityCraft server tip
- Change: Heavily nerfed Codebreaker. It now has 5 uses and a 1 in 3 chance of failing
- Fix: Recipe for Reinforced Glass does not show up in the SecurityCraft Manual
- Fix: WAILA does not update the new owner of a door when changed with a Universal Owner Changer
- Fix: Both halves of a Scanner Door can have different owners
- Fix: Cage Trap can be escaped
- Fix: Taser can tase the player who shot
- Fix: Reinforced Doors can be opened by any SC block, not only the ones with the same owner as the door
- Fix: Descriptions do not translate to different languages in the SecurityCraft Manual
- Fix: Reinforced Iron Bars placed by Cage Traps have no owner
- Fix: Reinforced Iron Fence does not damage players
- Fix: Reinforcing Andesite/Granite/Diorite gives back a glitched block
- Fix: Players get kicked sometimes when using the Password-protected Furnace
- Fix: Username Logger cannot be opened
- Fix: Reinforced Stairs are turned incorrectly when placed upside down
- Fix: Upside down Reinforced Slabs show up incorrectly in WAILA
- Fix: Wrong Fake Liquids recipes showing up in the SecurityCraft Manual
- Fix: Glass Panes can be put into Universal Block Reinforcers
- Fix: Crash when breaking planks/sandstone with a Universal Block Reinforcer
- Fix: Universal Block Reinforcer does not show up when being held
- Fix: Reinforced Doors don't have a placing sound
- Fix: Server error when using a Redstone Module
- Fix: Blocks scanning for players/mobs do not work correctly
- Fix: Recipes are not grouped in Recipe Book
- Removed: Config option to enable the old Keypad recipe

**Hotfixes:**
- Fix: Codebreaker can be enchanted with books at the anvil
- Fix: Issue with the recent Security Camera animation fix
- Fix: Reinforced stairs placed a certain way are still being displayed incorrectly
- Fix: New top slabs are displayed incorrectly

--------------------------Changelog for v1.8.2.2 of SecurityCraft--------------------------

- Ported to 1.12
- New: Reinforced textures now adapt to the resourcepack being used
- New: The Cage Trap can now be set to capture hostile mobs via the Universal Block Modifier
- New: Information about needing the Redstone Module for the Security Camera
- New: Discord tip
- New: Stained Reinforced Glass now colors beacon beams
- Change: Recipes with Reinforced Glass Panes now use normal Reinforced Glass
- Fix: Crash when adding an empty Disguise Module to a Keypad
- Fix: Missing German language strings
- Fix: Some GUIs cannot be closed
- Fix: The Universal Key Changer allows non digit characters
- Fix: Wrong texture for Reinforced Stone Slabs
- Fix: Crash when shift-clicking an item out of the Disguise Module's slot
- Fix: Security Camera can be broken without a Universal Block Remover
- Fix: Alarm bounding box is too large for top/bottom alarm
- Fix: Missing language strings for SecurityCraft Manual and Reinforced Planks within the SecurityCraft Manual
- Fix: Pick block does not work on Keypads
- Removed: Reinforced Glass Pane (updating this would have required a complete rewrite and a big chunk of time. Our priorities are sadly not positioned here)
- Removed: Reinforced Dirt Slab (The slab was not working at all and despite tons of debugging and checking, we could not iron out the issue)
