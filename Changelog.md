--------------------------Changelog for the upcoming version of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- New: All blocks can now be broken only by the owner with normal tools (axe, shovel, hoe, ...) and the Universal Block Remover has been disabled by default
- New: Config to re-enable the Universal Block Remover and disable normal block breaking
- New: Config to define tool requirement behavior (e.g. does Reinforced Stone always drop, or just when breaking it using a pickaxe?)
- New: Config to allow other players to break anyone's blocks (disallowed by default)
- New: Config for defining how much longer it should take to break another player's block compared to breaking one's own
- New: Reinforced Blocks: Pale Oak Log, Stripped Pale Oak Log, Pale Oak Wood, Stripped Pale Oak Wood, Pale Oak Planks, Pale Oak Stairs, Pale Oak Slabs, Pale Oak Button, Pale Oake Pressure Plate, Pale Oak Fence, Pale Oak Fence Gate, Pale Moss Block, Pale Moss Carpet
- New: Reinforced Blocks: Resin Block, Resin Bricks, Chiseled Resin Bricks, Resin Brick Slab, Resin Brick Stairs, Resin Brick Wall
- New: Secret Pale Oak (Hanging) Sign
- New: Pale Oak Security Sea Boat
- New: Creaking Heart Mine
- New: The cage trap iron bars now also break when breaking a cage trap in creative mode
- Change: Camera model animations are now synchronized between players
- Change: The Secure Redstone Interface has new visuals for when it receives/outputs a redstone signal
- API: New IBlockMine interface for blocks that are block mines
- API: New method IExplosive#explodesWhenInteractedWith as well as two utility methods
- API: New method IPasscodeProtected#savePasscodeAndSalt for more conveniently saving passcode and salt key to NBT
- API: New methods IPasscodeProtected#setSaveSalt and IPasscodeProtected#shouldSaveSalt to control storing the object's salt into its data storage
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
- Removed: Configuration "ableToBreakMines"

[1] Note: Frame blocks that already existed in the world prior to this update will lose their owner. These frames can be broken by anyone so they can be placed down with the proper owner again.

--------------------------Changelog for v1.9.12-beta1 of SecurityCraft--------------------------

- Misc.: The minimum required NeoForge version is 21.5.11-beta
