--------------------------Changelog for v1.8.13 of SecurityCraft--------------------------

- New: Italian translation (Thanks Chexet48!)
- New: Trophy System (Inspired from https://callofduty.fandom.com/wiki/Trophy_System)
- New: Block Pocket (Designed by Henzoid)
- New: The Whitelist Module can now be used with the Sentry
- New: Button to clear the Username Logger logged players list
- New: Reinforced Stone Pressure Plate (Only the owner and whitelisted users can press it, can be used to open Reinforced Iron (Trap-)Doors)
- Change: The Motion Activated Light is now triggered by mobs as well (This change includes tweaks to the attack logic that may impact other blocks, like the Portable Radar or the I.M.S., as well)
- Change: New Secret Sign textures
- Fix: Inventory Scanner ignores contents of Shulker Boxes
- Fix: Portable Radar does not update redstone signal correctly when Redstone Module is installed
- Fix: Installed modules do not drop when destroying block with Universal Block Remover
- Fix: SecurityCraft Manual does not correctly display items with metadata
- Removed: "/module" command. In order to interact with blacklist/whitelist modules, rightclick them
- Misc.: Backend improvements

--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Fix Sentry only attacking its owner (Thanks burtletoy!)

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
- Fix: Crash when rightclicking a Furnace Mine with an empty hand
- Misc.: Small adjustments in english translation

--------------------------Changelog for v1.8.10 of SecurityCraft--------------------------

- New: Config option to allow players to claim blocks that do not have an owner. Only blocks with the owner "owner" and uuid "ownerUUID" will work (check with the Admin Tool)
~~- Fix: SecurityCraft blocks can be destroyed by the Wither~~
- Fix: Typo in German localization
- Fix: Keybind localization does not work
- Fix: "Illegal extra prefix" errors

--------------------------Changelog for v1.8.9 of SecurityCraft--------------------------

- New: Whitelist and Blacklist Modules can now be rightclicked to manage the players
- New: The SC Manual now has a recipe (Book + Iron Bars)
- Misc.: Using the recommended Forge build at minimum is now required

--------------------------Changelog for v1.8.8.1 of SecurityCraft--------------------------

- Fix: Players can teleport to a Security Camera's position when logging out while viewing one
- Fix: Crash when reinforcing reinforced blocks added in v1.8.8 (Thanks AlexM-Dev!)

--------------------------Changelog for v1.8.8 of SecurityCraft--------------------------

- New: Reinforced Blocks
		- Carpet
		- Glowstone
- New: Reinforced Stained Glass and Reinforced Stained Glass Panes can now be created using the Universal Block Reinforcer (alongside the already existing recipes)
- New: The Track Mine can now be controlled with the Mine Remote Access Tool
- New: The Briefcase's code can now be reset by crafting it together with a Universal Key Changer
- New: The Admin Tool now shows the text on Secret Signs
- New: The I.M.S. can now be reloaded by rightclicking it with Bouncing Betties
- New: Reinforced Blocks of Iron, Gold, Diamond, and Emerald can now be used as a Beacon base
- New: Gravel Mine
- New: Sand and Gravel Mines now fall like normal sand/gravel
- Change: Item tooltips in the SecurityCraft Manual now show the complete tooltip instead of just the name
- Change: The Alarm recipe now needs Reinforced Glass instead of vanilla glass
- Change: The Universal Block Reinforcer will now reinforce placed down blocks instantly
- Change: When broken, the I.M.S. will now drop any charges left
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
- Fix: Smart Module does not work
- Fix: When mounted to a Security Camera, the view cannot be moved up
- Fix: Items shift-clicked in the Inventory Scanner end up in the blacklist slot if there was an item there already
- Removed: Configuration option to use the old Keypad recipe
- Misc.: Possibly improved render performance a bit
- Internal: Removed unused code
- Internal: Refactored to make code more readable

--------------------------Changelog for v1.8.7 of SecurityCraft--------------------------

**!!WARNING!! Upgrading to this version of SecurityCraft will deny access to any items that are placed in the blacklist slots of any Inventory Scanner (they will *not* disappear, but you won't be able to get them back)**

- New: The SC Manual can now be navigated using the mouse's scroll wheel
- New: The SC Manual now remembers the last page that was viewed and opens back up at that page
- Change: The Inventory Scanner's blacklist slots are now "ghost slots", which means that items can be put into and pulled out of them, but no physical item will actually be used
- Fix: Taser doesn't respect PVP settings
- Fix: Blocks in the Manual aren't shaded correctly
- Fix: Portable Radar does not work
- Fix: Portable Radar doesn't respect the whitelist module
- Fix: Players mounted on cameras trigger motion activated lights
- Fix: Various strings aren't translated
- Fix: Storage Module slots on the Inventory Scanner's GUI texture are flipped
- Fix: Reinforced Doors cannot be opened from the top or bottom
- Fix: Reinforced Doors cannot be opened by Inventory Scanners placed directly south of the door
- Fix: Torches, Doors, etc. cannot be placed on Keypads

--------------------------Changelog for v1.8.6.1 of SecurityCraft--------------------------

- Fix: Reinforced Glass Panes cannot be created
- Fix: The Universal Block Reinforcer doesn't take damage when blocks are reinforced with its slot

--------------------------Changelog for v1.8.6 of SecurityCraft--------------------------

- New: Rightclicking an activated Cage Trap with a Universal Block Remover will remove the complete cage, provided the trap and bars are owned by the same person
- Fix: Possible conflicting language strings with other mods

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
- Fix: Incorrect Alarm hitbox
- Fix: Security Cameras which emit a redstone signal don't break if the block they're placed on is broken
- Fix: Ownership data doesn't update correctly on the client side
- Fix: Password-protected Chest has incorrect breaking particles
- Potential Fix: StackOverflowException involving block mines
- Sponge Fix: Keycard Reader cannot be configured
- Sponge Fix: Server crashes when placing Reinforced Stained Glass over an active Beacon

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
- Removed: Tip for /sc connect
- Internal: Refactoring to make code a little more readable and cleaner
- Internal: Removed legacy code
  
--------------------------Changelog for v1.8.3 of SecurityCraft)--------------------------

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
- New: Keypad Gurnace
- New: Information about how to exit the Security Camera
- New: Cameras can now be unbound from within the Camera Monitors' GUI, even if they're no longer present in the world (press the X at the top right of the respective button)
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
- Removed: IRC support chat. Please refer to the #help channel on SecurityCraft's Discord! https://discord.gg/U8DvBAW
- Internal: Rewrote handling of reinforced blocks (Now much easier to add them)

--------------------------Changelog for v1.8.2.4 of SecurityCraft--------------------------

- New: Camera can be set to a fixed angle using the Universal Block Modifier
- New: When looking at a camera while holding a monitor, an overlay will be shown based on if the camera is added to the monitor or not
- Fix: Unintended behavior when using '/sc contact' on a multiplayer server
- Fix: Invalid Discord invite link
- Fix: IRC security issue
- Fix: Incorrect German language strings
- Fix: Retinal Scanner doesn't respect the whitelist module
- Fix: Adding/Removing an active Redstone Module does not update the state of connected redstone
- Fix: Inconsistent behavior when opening a keypad while being whitelisted
- Fix: Reinforced Door can be held open with a redstone input (thanks LeKoopa!)
- Fix: Security Camera cannot emit redstone signal
- Fix: Unable to exit out of password GUIs

--------------------------Changelog for v1.8.2.3 of SecurityCraft (v1.8.2.3-hotfix below)--------------------------

- New: Reinforced Blocks
  - Stone Bricks (normal, mossy, cracked, chiseled) incl. stairs and slabs
  - Mossy Cobblestone
  - Bricks incl. stairs and slabs
  - Nether Bricks incl. stairs and slabs
  - Hardened Clay
  - Stained Hardened Clay
- New: Official SecurityCraft server tip
- Change: Heavily nerfed Codebreaker. It now has 5 uses and a 1 in 3 chance of failing
- Fix: Recipe for Reinforced Glass does not show up in the SecurityCraft Manual
- Fix: WAILA does not update the new owner of a door when changed with a Universal Owner Changer
- Fix: Both halves of a Scanner Door can have different owners
- Fix: Cage Trap can be escaped
- Fix: Taser can tase the player who shot
- Fix: Reinforced Doors can be opened by any SC block, not only the ones with the same owner as the door
- Fix: Descriptions do not translate to different languages in the SecurityCraft Manual

**Hotfixes:**
- Fix: Codebreaker can be enchanted with books at the anvil

--------------------------Changelog for v1.8.2.2 of SecurityCraft--------------------------

- New: Reinforced textures now adapt to the resourcepack being used
- New: The Cage Trap can now be set to capture hostile mobs via the Universal Block Modifier
- New: Information about needing the Redstone Module for the Security Camera
- New: Discord tip
- Change: If you use LookingGlass, you now need at least version 0.2.0.01 of it to play
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

--------------------------Changelog for v1.8.2.1 of SecurityCraft--------------------------

- Fix: Rare crash when entering a world having used the IRC feature beforehand
- Fix: Alarm crashes the game
- Fix: Retinal Scanners and Scanner Doors can be activated while looking through a camera
- Fix: Wrong description of Smart/Storage Modules in Inventory Scanner
- Fix: Version gets added incorrectly to the welcome message
- Fix: WAILA shows that a Keycard Reader can have a password
- Fix: WAILA distinguishes between fake and real lava/water
- Fix: Specific crashes reported by OpenEye
- Fix: Defusing a mine removes owner
- Fix: Codebreaker does not work on Keypads

--------------------------Changelog for v1.8.2 of SecurityCraft--------------------------

- New: Added config option to disable SecurityCraft's built-in version checking feature
- New: The admin tool can automatically open keycard readers by rightclicking on it
- New: A GUI displaying IRC information opens after typing "/sc connect"
- New: Security cameras can now have a custom name which is displayed in the monitor GUI
- New: Laser blocks can now be enabled/disabled
- New: Links sent through IRC are now clickable
- New: Recipe tooltips in the SecurityCraft Manual
- New: Blocks without a recipe now have an explanation on how to create them on their SecurityCraft Manual page
- New: Scanner Door (Acts like a Retinal Scanner and Reinforced Door in one)
- New: Alongside the already existing process of creating reinforced blocks, you can now rightclick the Universal Block Reinforcer, insert an item into the slot and close the GUI to quickly reinforce stacks of blocks
- New: Option to disable the Portable Radar using the Universal Block Modifier
- New: Keypads can now be disguised as other blocks by inserting a Disguise Module into it
- New: Buttons at the beginning and end of the SecurityCraft Manual for easier navigation
- API: Added CustomizableSCTE.linkable() which allows you to "link" two blocks together, and run code between them
- API: Added CameraView, a wrapper class to handle different camera views
- Change: Bouncing betties can now be defused
- Change: /sc contact now doesn't require a message, instead it changes your normal chat to send to IRC instead of Minecraft chat. You can use /sc resume to go back to normal Minecraft chat
- Fix: Crash with username logger not checking if the name it saves is actually a player or not
- Fix: Rare crash with blocks implementing IIntersectable
- Fix: Incorrect password-protected chest recipe being shown in the SecurityCraft manual
- Fix: Retinal scanners can be activated by non-whitelisted players that are not the owner
- Fix: Camera monitor displaying "0/30 cameras" in the monitor's tooltip when 30 cameras are bound to it
- Fix: Crash which occurs when SecurityCraft's update .json file isn't downloaded properly at startup
- Fix: Crash when opening a monitor with more than 10 cameras bound to it
- Fix: Unbinding the first bound camera from a monitor restricts access to other cameras bound to the same monitor
- Fix: Portable Radar option to disable repeating message does not show
- Fix: Language strings regarding block options
- Fix: Portable Radar crash
- Fix: Resizing Minecraft while having the SecurityCraft Manual open doesn't update tooltips correctly
- Fix: Hostile mobs attack the player when he is viewing a camera
- Fix: Cage Trap can be activated by its owner
- Fix: Protecto attacks whitelisted players
- Fix: The Portable Radar sends a message when its owner is in its radius
- Fix: Translations don't work in the SC Manual under certain circumstances

--------------------------Changelog for v1.8.1 of SecurityCraft--------------------------

- New: [Protecto](http://megaman.wikia.com/wiki/Protecto)
- New: Briefcase
- New: Notification if player is banned from IRC
- New: Information on how IRC works
- New: Camera Monitor now shows how many cameras are bound to it
- New: Added config option to configure camera speed when not using LookingGlass
- New: All chat messages/item tooltips/GUI elements/config options etc. are now translateable
- New: Added descriptions to all SecurityCraft config options
- New: Updated to Forge v10.13.3.1420
- New: GoogleDocs form to report crashes/bugs (see /sc bug)
- New: Clickable links in the Trello and Patreon SecurityCraft tips, and for the new GoogleDocs form link in /sc bug
- New: The admin tool now shows a "no info" message when rightclicking a block with no owner, password, or module inserted
- New: Custom options for keypads, portable radars, and security cameras
- New: Spam detection while using /sc contact will not allow users to send the same message more than two times consecutively
- API: Added TileEntitySCTE.attacks(), which you can use to have an attack() method automatically called
- API: Added Owner class which allows for easy access to player's names and UUIDs, with a few helpful methods as well
- API: Added IPasswordProtected.onCodebreakerUsed() and IPasswordProtected.openPasswordGUI()
- API: Added option, which allows you to add custom, "per-block" configuration values
- API: Added INameable, which allows you to set a custom name for a specific TileEntity
- API: General improvements
- Change: Improved IRC messaging system
- Change: Improved cracked client detection to automatically kick them from IRC
- Change: Reinforced glass and reinforced glass panes drop after breaking again
- Change: Camera monitors can now store up to 30 cameras when not using LookingGlass
- Change: Camera selection GUI when not using LookingGlass
- Change: Changed the name of some SecurityCraft files
- Fix: Some messages and texts don't display correctly
- Fix: Everyone connected to IRC from Minecraft gets kicked if a kick in the channel occurs
- Fix: Crash occuring when mounted to a camera
- Fix: Crash when mounting a camera which is directly under a block
- Fix: Crash when trying to mount a non-existing camera
- Fix: Crash with Inventory Scanner
- Fix: Blocks can be broken when mounted to a camera
- Fix: Security Cameras break when a block is placed next to them if they face north or west
- Fix: Unbinding first bound Camera from Monitor denies access to all cameras bound afterwards
- Fix: Waila shows "\<ERROR\>" while looking at a newly placed password-protected chest
- Fix: Cameras don't emit a redstone signal
- Fix:  Monitors require a second rightclick to display a camera's view (when using LookingGlass)
- Removed: Some redundant/unused code
