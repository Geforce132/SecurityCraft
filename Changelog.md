--------------------------Changelog for the upcoming version of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- New: All blocks can now be broken only by the owner with normal tools (axe, shovel, hoe, ...) and the Universal Block Remover has been disabled by default
- New: Config to re-enable the Universal Block Remover and disable normal block breaking
- New: Config to define tool requirement behavior (e.g. does Reinforced Stone always drop, or just when breaking it using a pickaxe?)
- New: Config to allow other players to break anyone's blocks (disallowed by default)
- New: Config for defining how much longer it should take to break another player's block compared to breaking one's own
- New: Reinforced Blocks: Pale Oak Log, Stripped Pale Oak Log, Pale Oak Wood, Stripped Pale Oak Wood, Pale Oak Planks, Pale Oak Stairs, Pale Oak Slabs, Pale Oak Button, Pale Oake Pressure Plate, Pale Oak Fence, Pale Oak Fence Gate, Pale Moss Block, Pale Moss Carpet
- New: Secret Pale Oak (Hanging) Sign
- New: Pale Oak Security Sea Boat
- New: Creaking Heart Mine
- New: The cage trap iron bars now also break when breaking a cage trap in creative mode
- New: The Camera Monitor, Mine/Sentry Remote Access Tool, and Sonic Security System can now be copied in the crafting table, by combining two of the same item (one empty, another one with things bound to it) in a crafting table
- Change: The block mine overlay now shows in the first person hand models as well
- Change: Camera model animations are now synchronized between players
- Change: The Secure Redstone Interface has new visuals for when it receives/outputs a redstone signal
- Change: Owners of disguised blocks and players in creative mode now receive the actual block instead of the disguise when using Pick Block  
- API: IDisguisable has been simplified
- API: New IBlockMine interface for blocks that are block mines
- API: New method IExplosive#explodesWhenInteractedWith as well as two utility methods
- API: New method IPasscodeProtected#savePasscodeAndSalt for more conveniently saving passcode and salt key to NBT
- API: New methods IPasscodeProtected#setSaveSalt and IPasscodeProtected#shouldSaveSalt to control storing the object's salt into its data storage
- Fix: Module descriptions of security sea boats do not show up correctly
- Fix: Reinforced Mud Bricks and Blackstone Slab/Stairs don't look 100% like their vanilla counterparts
- Fix: Holding a camera monitor makes it possible to identify disguised cameras
- Fix: Items that can have linked positions (e.g. Camera Monitor) show the idle animation even when they have positions and are either dropped, or held by a non-player entity
- Fix: The mine remote access tool can identify block mines
- Fix: The display of items in the SC Manual can change too fast in certain situations
- Fix: Potential startup crash
- Fix: The map color, instrument, and more properties of many reinforced blocks don't match their vanilla counterparts
- Fix: Several reinforced blocks can be broken by pistons
- Fix: Wall hanging signs and horizontal reinforced iron bars have an incorrect translation key
- Fix: Security sea boats cannot be broken by players like normal boats
- Fix: The claymore does not ignore the owner if the "Ignore Owner" option is true, and vice versa
- Fix: Cage traps can be used to maliciously change ownership of blocks
- Fix: Limited use keycards can be used indefinitely in a keycard holder
- Fix: A portable radar chat message is broken in German
- Fix: Anything can pull books out of a Reinforced Chiseled Bookshelf
- Fix: Reinforced buttons and levers can be triggered by wind charges
- Fix: Reinforced grass blocks and water cauldrons are still tinted even if the reinforced tint is turned off
- Fix: Fake water does not flow the same way as regular water
- Fix: Laser and inventory scanner fields don't show up properly in the overlay of Jade/TOP/etc.
- Fix: The color chooser doesn't show color
- Fix: Loading a structure with passcode-protected objects onto itself using a structure block invalidates all passcodes
- Fix: A system of connected laser blocks does not emit redstone correctly when walking into two or more laser fields 
- Fix: Brushing suspicious sand mines does not respect the player's block_interaction_range attribute
- Fix: Reinforced Block recipes are grouped and categorized differently in the recipe book compared to vanilla block recipes
- Fix: Text above the Sonic Security System does not render correctly
- Fix: Some blocks are not tinted correctly when rendered in the disguise module or projector screen's state selector
- Removed: Configuration "ableToBreakMines"

[1] Note: Frame blocks that already existed in the world prior to this update will lose their owner. These frames can be broken by anyone so they can be placed down with the proper owner again.

--------------------------Changelog for v1.9.12 of SecurityCraft--------------------------

- New: The Panic Button now supports the Allowlist Module
- New: Config setting to disable the ability of the Universal Block Reinforcer to un-/reinforce blocks that are placed in the world
- New: Option for the Portable Radar to only send notification messages to the owner instead of all members of the team
- New: Server config setting to allow setting the precedence of team ownership checks (useful when FTB Teams is installed, and the mod should check for vanilla teams instead)
- New: When linking a keycard, a player can optionally be set who will be the only player able to use the keycard
- API: New method IPasscodeProtected#setPasscodeInAdjacentBlock for updating an adjacent block (e.g. the second half of a chest) with the passcode when setting it
- Fix: Placing a Passcode-protected Chest or Barrel leads to unnecessary data being saved
- Fix: Bouncing betties are rendered incorrectly
- Fix: The Universal Block Reinforcer's screen title does not display the item's actual name
- Fix: Reinforced tuff brick slabs cannot be crafted in the crafting table
- Fix: The operator items creative tab shows when it's disabled
- Fix: Changing the Keypad Trapdoor's signal length or disabled option plays the close sound even when already closed
- Fix: Pressing enter when setting up a passcode does not save the passcode
- Fix: The Reinforced Dispenser and Dropper don't have a reinforced tint when placed in the world
- Fix: The Rift Stabilizer cannot open reinforced doors/trapdoors/etc. or interact with secure redstone interfaces
- Fix: Right-clicking a passcode-protected block in spectator mode while holding a codebreaker does not work
- Fix: Jade/TOP/WTHIT show the owner of block mines
- Fix: Several instances of blocks disregarding the team ownership config setting being disabled when FTB Teams is installed
- Fix: Security Camera entities are sent to every player instead of only the player mounting the camera
- Fix: Entity translations of security sea boats do not exist
- Misc.: Salt keys are now stored as integer arrays instead of strings, reducing file size

--------------------------Changelog for v1.9.11-beta2 of SecurityCraft--------------------------

- Fix: Security sea boats are not invulnerable to damage types they should be invulnerable to
- Fix: Jade and WTHIT integration
- Fix: In certain scenarios, it's not possible to join any world or multiplayer server
- Fix: Some pages in the SecurityCraft Manual appear twice

--------------------------Changelog for v1.9.11-beta1 of SecurityCraft--------------------------

- Change: Item rarities of the Admin Tool and Universal Block Reinforcer (Lvl 3) have been adjusted
- Misc: Security sea boat entity types have been split up just like vanilla's boats