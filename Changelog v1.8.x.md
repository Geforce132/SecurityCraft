--------------------------Changelog for v1.8.14.1 of SecurityCraft--------------------------

- Fix: Universal Block Reinforcer adds an unnecessary NBT to the reinforced block
- Fix: Reinforced Slabs aren't being placed properly when placing against the side of a block
- Fix: Crash when opening customizing GUI of blocks that can't take modules

--------------------------Changelog for v1.8.14 of SecurityCraft--------------------------

- New: Reinforced Glass Panes can now be crafted using six Reinforced Glass
- New: Reinforced Stained Glass Panes can now be crafted by surrounding a dye with eight Reinforced Glass Panes
- New: All Reinforced Terracotta blocks can now be crafted analogous to their vanilla counterparts
- New: Block/Item Tags
- New: Recipes now use block/item tags wherever possible
- New: Reinforced Red Sand
- New: Reinforced versions of all types of wooden pressure plates
- New: The Block Pocket now tells the player when it was successfully de-/activated
- Change: The Reinforced Iron Trapdoor is now created by putting a regular Iron Trapdoor into a Universal Block Reinforcer
- Change: The Frame recipe is slightly different, ingredients stay the same
- Change: The Sentry now uses Reinforced Blocks of Iron in its recipe
- Change: Fake Water/Lava Buckets now use the 1.14 texture
- Fix: Username Logger crash involving armor stands
- Fix: Reinforced Diorite and Reinforced Andesite use vanilla Cobblestone instead of Reinforced Cobblestone
- Fix: Using six Reinforced Stained Glass to craft Reinforced Stained Glass Panes yields 8 glass panes instead of 16
- Fix: Incorrect Taser is showing up in the creative tab (again)
- Fix: Fake Liquid Buckets can't be crafted using level two potions
- Fix: Chests and Furnaces cannot be converted to their password-protected variant when not sneak-rightclicking
- Fix: Reinforced Doors and Scanner Doors do not drop when breaking the top half or the block below them
- Fix: Disguised Keypad collision does not completely respect the collision of the block it's disguised as
- Fix: Disguised Keypad throws a shadow no matter what it's disguised as (ambient occlusion)
- Fix: Block Pocket Description incorrectly mentions Reinforced Quartz instead of Reinforced Crystal Quartz
- Fix: Cannot walk through non-see-through Block Pocket Walls
- Fix: Entities do not get healed when standing in fake lava
- Potential Fix: Block Pocket loses its owner when the player reloads the world

--------------------------Changelog for v1.8.13.1 of SecurityCraft--------------------------

- Fix: Disguising a Keypad does not synchronize to other players
- Fix: Sentry animation is not synchronized between players
- Fix: Disguised Keypad doesn't show reinforced blocks correctly
- Fix: Sentry whitelist does not work correctly

--------------------------Changelog for v1.8.13 of SecurityCraft--------------------------

- New: Italian translation (Thanks Chexet48!)
- New: Trophy System (Inspired from https://callofduty.fandom.com/wiki/Trophy_System)
- New: Block Pocket (Designed by Henzoid)
- New: The Whitelist Module can now be used with the Sentry
- New: Button to clear the Username Logger logged players list
- New: Reinforced Stone Pressure Plate (Only the owner and whitelisted users can press it, can be used to open Reinforced Iron (Trap-)Doors)
- New: The Smart Module now works for Security Cameras, enabling others to view cameras at a fixed angle
- New: Finnish translation (Thanks erland!)
- New: Reinforced blocks
	- Stripped Oak Log, Stripped Spruce Log, Stripped Birch Log, Stripped Jungle Log, Stripped Acacia Log, Stripped Dark Oak Log, Stripped Oak Wood, Stripped Spruce Wood, Stripped Birch Wood, Stripped Jungle Wood, Stripped Acacia Wood, Stripped Dark Oak Wood, Oak Wood, Spruce Wood, Birch Wood, Jungle Wood, Acacia Wood, Dark Oak Wood, Prismarine Slab, Prismarine Brick Slab, Dark Prismarine Slab, Smooth Quartz, Smooth Red Sandstone, Smooth Sandstone, Smooth Stone, Prismarine Stairs, Prismarine Brick Stairs, Dark Prismarine Stairs, Polished Granite Stairs, Smooth Red Sandstone Stairs, Mossy Stone Brick Stairs, Polished Diorite Stairs, Mossy Cobblestone Stairs, End Stone Brick Stairs, Smooth Sandstone Stairs, Smooth Quartz Stairs, Granite Stairs, Andesite Stairs, Red Nether Brick Stairs, Polished Andesite Stairs, Diorite Stairs, Polished Granite Slab, Smooth Red Sandstone Slab, Mossy Stone Brick Slab, Polished Diorite Slab, Mossy Cobblestone Slab, End Stone Brick Slab, Smooth Sandstone Slab, Smooth Quartz Slab, Granite Slab, Andesite Slab, Red Nether Brick Slab, Polished Andesite Slab, Diorite Slab
- New: New Secret Sign types (spruce, birch, jungle, acacia, dark oak)
- New: Secret Sign text can now be dyed
- New: Support for Public Gui Announcement
- Change: The Motion Activated Light is now triggered by mobs as well (This change includes tweaks to the attack logic that may impact other blocks, like the Portable Radar or the I.M.S., as well)
- Change: New Secret Sign textures
- Fix: Inventory Scanner ignores contents of Shulker Boxes
- Fix: Portable Radar does not update redstone signal correctly when Redstone Module is installed
- Fix: Installed modules do not drop when destroying block with Universal Block Remover
- Fix: Misc. crashes
- Fix: Taser can be duplicated using offhand slot (Thanks Boreaus!)
- Fix: Incorrect version of the taser shows up in the creative tab and JEI
- Fix: Admin Tool recipe description does not show in JEI
- Fix: Cannot interact with UI elements when mounted to a camera
- Fix: Pressing the inventory key while editing a whitelist/blacklist module closes the GUI
- Fix: Disguising a keypad does not work with Forge 25.0.193+
- Fix: Password-protected Chest is not resistant to explosions and can be broken instantly
- Fix: The Admin Tool's description in JEI is not working
- Fix: Model of activated alarm is incorrect
- Fix: Electrified Iron Fence loses its owner when placing specific blocks next to it
- Fix: Reinforced Door does not behave correctly
- Fix: Briefcase does not save items correctly
- Fix: Some sounds play server-wide instead of just for the players who should hear them
- Fix: Secret Sign does not drop
- Removed: "/module" command. In order to interact with blacklist/whitelist modules, rightclick them
- Misc.: Backend improvements

--------------------------Changelog for v1.8.12.3 of SecurityCraft--------------------------

- Fix: Mod is not compatible with newer Forge versions (The minimum required Forge version is now 28.1.0)

--------------------------Changelog for v1.8.12.2 of SecurityCraft--------------------------

- Fix: Mod is not compatible with Forge 28.0.62+

--------------------------Changelog for v1.8.12.1 of SecurityCraft--------------------------

- Fix: Mod is not compatible with Forge 28.0.45+

--------------------------Changelog for v1.8.12 of SecurityCraft--------------------------

- New: The One Probe support (https://minecraft.curseforge.com/projects/the-one-probe)
- Fix: Ownership does not get set correctly when creating Password-protected Chests/Furnaces
- Fix: Update some manual entries to reflect changes in functionality
- Fix: Fix Sentry only attacking its owner (Thanks burtletoy!)
- Fix: Crash when rightclicking the Keycard Reader with a Keycard without having it set up first

--------------------------Changelog for v1.8.12-beta2 of SecurityCraft--------------------------

- Fix: Crash on server load
- Fix: Inserting a module into a linked Laser/Inventory Scanner kicks the player

--------------------------Changelog for v1.8.12-beta1 of SecurityCraft--------------------------

- Change: Fake Water/Lava recipes are back to how they were before 1.13.2
