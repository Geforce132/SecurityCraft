--------------------------Changelog for the next version of SecurityCraft--------------------------

- New: The Keycard Reader, Keypad, and Retinal Scanner can now be placed facing up or down (Thanks CYB3RCA4T!)
- New: The "/sc owner" subcommand now has an optional "resetSettings" mode to force affected blocks to reset sensible data like passcodes
- New: Configuration option to control whether to render the spinning disk of Secure Redstone Interfaces
- Change: Several entries within the SecurityCraft Manual have been updated to remove outdated descriptions
- Change: Ownable blocks that are targeted by the "/sc owner" subcommand will now keep all of their non-owner-related data by default
- API: New method Owner#isTreatedTheSameAs to check if everything owned by one owner is also owned by the other owner
- Fix: Some Briefcase and Keycard interactions do not respect whether the player is wearing an Incognito Mask
- Fix: The Block Change Detector cannot open Reinforced Doors/Trapdoors/etc. or interact with Secure Redstone Interfaces (Thanks CYB3RCA4T!)
- Fix: Server crash involving Display Cases
- Fix: Redstone Wire cannot be placed on top of Reinforced Hoppers
- Fix: Lecterns and Chiseled Bookshelves display their content incorrectly when they are reinforced using /sc convert
- Fix: There are two identical SecurityCraft Manual pages of the Projector
- Fix: Reinforced Lanterns and Reinforced Chains cannot be waterlogged by placing water into them using a Bucket
- Fix: Some messages that are sent from a Keycard Lock mention a Keycard Reader as the source block
- Fix: Triggered Bouncing Betties do not flash white and increase in size before exploding
- Fix: Severe lag when joining a superflat world with a layer of Secure Redstone Interfaces
- Fix: Placing a Secure Redstone Interface using /setblock places it in its waterlogged state

--------------------------Changelog for v1.10.1-beta3 of SecurityCraft--------------------------

- Fix: The Frame feed is sometimes rendered with partly transparent pixels

--------------------------Changelog for v1.10.1-beta2 of SecurityCraft--------------------------

- Fix: The Frame feed doesn't display correctly

--------------------------Changelog for v1.10.1-beta1 of SecurityCraft--------------------------

- New: Reinforced blocks: All types of Reinforced Lightning Rod, Reinforced Copper Chain, Reinforced Copper Bars, Reinforced Copper Lantern, Reinforced Shelf
- New: Incognito Mask
- New: In-game notification message that informs players that an error occurred while capturing a frame feed
- New: Debug feature to help identify other mods preventing players from viewing a Security Camera
- New: Configuration option for limiting the amount of chunks that may be forceloaded by frame feeds
- Change: The model and texture of the Motion-Activated Light have been updated
- Change: The Reinforced Chain has been renamed to Reinforced Iron Chain to be consistent with vanilla
- Change: The Trophy System now shows its targeting laser even when disguised
- Change: The signature of a Keycard Reader can now be changed through a text box
- API: New IOwnable#isOwnedBy overload taking a Player and a boolean for checking ownership respecting the new Incognito Mask
- API: New method Owner#isDefaultOwner to check if the Owner object has no player data associated with it
- API: New method IPasscodeConvertible#getRequiredKeyPanels to control how many key panel items are consumed when converting this block
- Fix: Several mod incompatibilities that caused frame feeds to not show up correctly
- Fix: Game performance does not improve when breaking frames with an active frame feed
- Fix: Clouds sometimes flicker rapidly between two positions when a Frame is displaying a camera feed
- Fix: The Nether fog sometimes switches rapidly between two colors when a Frame is displaying a camera feed
- Fix: Active camera feeds in multiple dimensions can interfere with each other's chunk loading
- Fix: The Level 3 Keycard cannot be crafted
- Fix: The powered Secure Redstone Interface model looks wrong when placed on the wall or ceiling
- Fix: Several dupes and exploits
- Fix: Inventory scanner crash
- Fix: Reinforced cobwebs cannot be mined faster with swords
- Fix: Placing a Secure Redstone Interface as a receiver does not correctly update itself and surrounding blocks
- Fix: Lenses placed inside a Claymore, Security Camera, or Trophy System sometimes disappear when the world is reloaded
- Fix: Blocks projected by a Projector are reset to their default state when opening the Projector's screen
- Fix: The owner of the topmost block of the cage spawned by a Cage Trap is not set correctly
- Fix: Ownable blocks can be mined by drills or similar blocks from certain other mods
- Fix: Blocks disguised as vanilla blocks can be mined by anyone
- Fix: The rotation of a Security Camera can become desynced between client and server
- Fix: Tricking Retinal Scanners, Scanner Doors and Scanner Trapdoors with player heads does not work
- Fix: The Mine and Sentry Remote Access Tool appear to be able to interact with mines/sentries that are not owned by the player, even though they can't
- Fix: Players viewing a Security Camera are invisible for players in spectator mode
- Fix: The Rift Stabilizer does not keep its custom name as an item when the bottom half is mined
- Fix: Crash when teleporting or respawning near Security Cameras
- Fix: Crash when trying to change the owner of a Reinforced Button or Reinforced Lever
- Fix: Bad client performance when looking at many reinforced blocks at once
- Misc.: The minimum required NeoForge version is 21.10.19-beta