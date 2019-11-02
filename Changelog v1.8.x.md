--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Crash when rightclicking the Keycard Reader with a Keycard without having it set up first

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
- Fix: Rightclicking another player's chest with a Universal Block Remover/Modifier does not show a message

--------------------------Changelog for v1.8.9 of SecurityCraft--------------------------

- New: Whitelist and Blacklist Modules can now be rightclicked to manage the players
- New: The SC Manual now has a recipe (Book + Iron Bars)
- Misc.: The minimum required Forge build is now 12.17.0.2051

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
- Fix: Items shift-clicked in the Inventory Scanner end up in the blacklist slot if there was an item there already
- Fix: Taser reequips all the time when loading after a shot (Note that this cannot be implemented in 1.7.10 due to limitations in Forge)
- Fix: Password-protected Furnace does not give off light when open and burning
- Fix: The Inventory Scanner Field is missing its top and bottom texture
- Fix: Storage Module does not work
- Fix: Model loading errors
- Fix: Security Camera gets rendered incorrectly when placed on the ceiling
- Fix: SC Manual buttons are switched around
- Fix: Reinforced Iron Bars model is not the same as the vanilla Iron Bars model
- Fix: Password-protected Chest does not synchronize owner correctly when loading world
- Removed: Configuration option to use the old Keypad recipe
- Misc.: Possibly improved render performance a bit
- Internal: Removed unused code
- Internal: Refactored to make code more readable

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

--------------------------Changelog for v1.8.6 of SecurityCraft--------------------------

- New: Rightclicking an activated Cage Trap with a Universal Block Remover will remove the complete cage, provided the trap and bars are owned by the same person
- Fix: Possible conflicting language strings with other mods
- Fix: Reinforced Stained Hardened Clay doesn't show up/shows up incorrectly in SecurityCraft Manual
- Fix: Information about the Admin Tool and SecurityCraft Manual don't show up in JEI
- Fix: Crashes involving the Username Logger or Inventory Scanner Field (Thanks Kreezxil!)
- Fix: Fix incorrect lighting in some GUIs

--------------------------Changelog for v1.8.5 of SecurityCraft--------------------------

- New: 1.9.4 support
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
- Potential Fix: StackOverflowException involving block mines
- Sponge Fix: Keycard Reader cannot be configured
- Sponge Fix: Server crashes when placing Reinforced Stained Glass over an active Beacon