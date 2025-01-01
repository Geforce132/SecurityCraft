--------------------------Changelog for the upcoming version of SecurityCraft--------------------------

- New: Cameras can now be viewed on Frames within the world (live camera feeds)[1]
- New: Several server and client config settings to control chunk loading in camera feeds
- Change: The block mine overlay now shows in the first person hand models as well
- Change: Camera model animations are now synchronized between players
- API: IDisguisable has been simplified
- Fix: Module descriptions of security sea boats do not show up correctly
- Fix: Reinforced Mud Bricks and Blackstone Slab/Stairs don't look 100% like their vanilla counterparts
- Fix: Holding a camera monitor makes it possible to identify disguised cameras
- Fix: Items that can have linked positions (e.g. Camera Monitor) show the idle animation even when they have positions and are either dropped, or held by a non-player entity
- Fix: The display of items in the SC Manual can change too fast in certain situations

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