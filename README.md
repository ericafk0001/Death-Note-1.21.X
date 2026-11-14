# Death Note Mod :skull:

A Fabric Minecraft mod for Minecraft 1.20.5 through 1.21.11 that adds a few new items to the game from the death note anime! Craft the deadly book and go on a killing spree! Made by 8percent and x1yl.

--> Download the latest version in the "Releases" tab. Put the jar file into your minecraft mods folder to get started!

## Items

- **Death Note**: Write an Player's name or Entity's name to kill them
- **Shinigami Apple**: Eating this will both curse you and bless you. Who knows?
- **Potato Chips**: Eating this light snack will give you a slight speed boost. Might help your writing speed in the Death Note.

## Demo

### [Modrinth Page](https://modrinth.com/mod/mc-death-note-mod)

### [Trailer](https://youtu.be/mRzcB2U3BUk)

### [Demo](https://youtu.be/WkHIZz7zfR4)

## Screenshot

![Screenshot](https://cloud-r3kt02ah6-hack-club-bot.vercel.app/02025-01-19_17.08.46.png)

## Compiling the mod yourself

This project uses [Stonecutter](https://stonecutter.kikugie.dev/) to build one codebase for Minecraft 1.20.5 through 1.21.11. All version definitions (including which Minecraft releases each build targets) live in `stonecutter.properties.toml` — there is no per-version `gradle.properties` folder to maintain.

1. **Clone the Repository**  
   Clone the repository from GitHub:

   ```bash
   git clone https://github.com/ericafk0001/Death-Note-1.21.X.git
   ```

2. **Navigate to the Project Directory**  
   Move into the cloned repository's directory:

   ```bash
   cd Death-Note-1.21.X
   ```

3. **Ensure You Have Required Tools Installed**  
   Make sure you have the following installed:

   - [Java Development Kit (JDK) 25](https://adoptium.net/) — required to run Gradle and Stonecutter. Gradle provisions a Java 21 toolchain automatically for compiling the mod.
   - [Gradle](https://gradle.org/install/) (optional, as the wrapper is included)

4. **Build the Mod**  
   Run the Gradle wrapper to build every supported Minecraft version:

   ```bash
   ./gradlew buildAll
   ```

   On Windows, use:

   ```cmd
   gradlew buildAll
   ```

   To build a single version instead, use `./gradlew :1.21.1:build` or `./gradlew :1.21.11:build`.

   The jars are written to:

   - `versions/1.21.11/build/libs/deathnote-1.0.0+1.21.11.jar`
   - `versions/1.21.1/build/libs/deathnote-1.0.0+1.21.1.jar`
   - `versions/1.20.5/build/libs/deathnote-1.0.0+1.20.5.jar`

   Each jar declares the full range of Minecraft versions it supports in `fabric.mod.json`, so one build can cover several game releases:

   | Build node | Works on Minecraft |
   | --- | --- |
   | `1.21.11` | 1.21.2 – 1.21.11 |
   | `1.21.1` | 1.20.6 – 1.21.1 |
   | `1.20.5` | 1.20.5 |

   To add or split a version, edit `stonecutter.properties.toml`: add a `[[versions]]` block with `minecraft_version` plus its `mc_compat` / `recipe_folder` / `recipe_format` / mappings / loader entries. The Minecraft version doubles as the node name.

   Recipe JSON changed shape in 1.21.2, so they live in two small trees: `src/main/recipes/modern` (1.21.2 and later) and `src/main/recipes/legacy` (older releases). Each node ships the matching set.

5. **Install the Mod**  
   Copy the `.jar` file matching your Minecraft version to your Minecraft `mods` folder:

   - Locate your `.minecraft` directory (typically in `%appdata%/.minecraft` on Windows or `~/.minecraft` on Linux/Mac).
   - Paste the `.jar` file into the `mods` folder.

6. **Run the Mod**  
   Launch Minecraft with Fabric Loader for any Minecraft version listed in the table above.

   - Ensure Fabric Loader is installed via [Fabric Installer](https://fabricmc.net/use).
   - Check that your mod appears in the `Mods` section of the Minecraft main menu.

7. **Optional: Run the Mod in Development Environment**  
   To test the mod directly in the development environment:

   ```bash
   ./gradlew :1.21.1:runClient
   ```

   Replace `1.21.1` with any build node from the table above. This will start a Minecraft client with your mod preloaded for testing.

## How It Works

It uses the Fabric API and sends packets to the server to execute commands server side such as the killing mechanics. It uses fabric's item and food components to make the custom items/food.

## Features to Add

- Better Death Note functionality handling server side to handle random warnings
- More items from the Anime

## License

This project is licensed under Eric's Protective License - see the [LICENSE](LICENSE) file for details.

---

Thank you to .7410 (on discord) for helping!

