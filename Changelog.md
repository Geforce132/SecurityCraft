--------------------------Changelog for v1.9.7 of SecurityCraft--------------------------

- New: Reinforced Blocks:*
	- Block of Bamboo
	- Block of Stripped Bamboo
	- Bamboo Planks
	- Bamboo Mosaic
	- Bamboo Stairs
	- Bamboo Mosaic Stairs
	- Bamboo Slab
	- Bamboo Mosaic Slab
	- Bamboo Pressure Plate
	- Bamboo Button
	- Chiseled Bookshelf
- New: Reinforced Blocks:*
	- Cherry Log
	- Cherry Wood
	- Stripped Cherry Log
	- Stripped Cherry Wood
	- Cherry Planks
	- Cherry Stairs
	- Cherry Slab
	- Cherry Pressure Plate
	- Cherry Button
- New: Secret Bamboo Sign*
- New: Secret Cherry Sign*
- New: Secret Hanging Signs*
- New: Suspicious Sand Mine (can be defused to get the item without it exploding)*
- New: Sonic Security Systems and Portable Tune Players now support mob head and custom note block sounds*
- New: The Alarm, Motion Activated Light, and Portable Radar can now be waterlogged
- New: Laser and inventory scanner fields can now be waterlogged, which means the Laser and Inventory Scanner now properly work underwater without air pockets
- New: Randomize signature button for the Keycard Reader
- New: The pitch of the sound an alarm plays can now be changed
- New: The SecurityCraft Manual now shows default values and the range (if applicable) of a block's options
- Change: Several technical blocks' sounds have been adjusted to better match how they look
- Change: Improved visuals when holding a taser
- Change: Some alarm options have been moved to a separate screen
- Change: Passcodes of passcode-protected blocks are no longer directly accessible by clients, and are thus no longer displayable using mods like Jade/TOP or the admin tool
- Change: Passcodes are now stored and compared using secure hashing functions like PBKDF2, using a randomly generated salt that is stored on the server
- Change: All occurrences of "Password" in block or item names have been switched out with "Passcode" (for example, Password-protected Chest -> Passcode-protected Chest)
- Change: The storage location of passcodes in the NBT data of Display Cases has been changed from "Passcode" to "passcode"
- Change: The Universal Key Changer can now be used by players in creative mode on any passcode-protected block or briefcase, regardless of its owner
- API: IModuleInventory#getModuleDescriptionId to make it possible to have shared descriptions
- API: All occurrences of "password" in API class and method names have been switched out with "passcode" (for example, IPasswordProtected#getPassword -> IPasscodeProtected#getPasscode)
- API: IPasscodeProtected#setPasscode now returns a byte array, IPasscodeProtected#getPasscode now takes a byte array as its only parameter
- API: New methods IPasscodeProtected#getSalt and IPasscodeProtected#setSalt to get and set the block entity's "salt" field
- API: New methods IPasscodeProtected#hashAndSetPasscode, IPasscodeProtected#checkPasscode, IPasscodeProtected#loadPasscode for easier passcode management
- Fix: Jade does not properly hide blocks
- Fix: Shields do not take damage when blocking a taser that deals high enough damage
- Fix: A player shooting a guardian with a taser gets damaged by the guardian's thorns
- Fix: The death message of a player killed by a taser does not contain the player firing the taser
- Fix: Crashes when rendering some modded block entities in a Projector, or using them as a disguise
- Fix: The Sentry-/Mine Remote Access Tool screens do not show the item's custom name
- Fix: Reinforced Doors can be closed by giving them a block update
- Fix: Reinforced Doors that are next to, but don't face, each other can act as double doors
- Fix: Certain blocks don't update their indirect neighbors properly when getting destroyed while in a powered state
- Fix: The Reinforced Mangrove Button and Pressure Plate can't open reinforced doors
- Fix: Blocks cannot be placed on the side of any of SecurityCraft's doors
- Fix: Some reinforced blocks and mines can be destroyed by pistons or flowing fluids
- Fix: Reinforced Lever duplication exploit
- Fix: Several items like the Admin Tool or Universal Block Remover (and more) cannot be placed into a Display Case
- Fix: Briefcase inventory access exploit
- Misc.: The minimum required Forge version is 45.0.39
- Misc.: More texture updates

[*] only available if the update_1_20 datapack is enabled

--------------------------Changelog for v1.9.6.2 of SecurityCraft--------------------------

- Fix: JEI integration

--------------------------Changelog for v1.9.6.1 of SecurityCraft--------------------------

- Fix: Mounting two cameras after one another in different dimensions causes severe lag
- Misc.: The minimum required Forge version is 45.0.18

--------------------------Changelog for v1.9.6-beta1 of SecurityCraft--------------------------

- Misc.: The minimum required Forge version is 45.0.2