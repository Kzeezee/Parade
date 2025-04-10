# Parade
SMU CS102 Java Project: Group G2T2

# Overview
Parade is a console-based Java card game inspired by the Parade game rulebook. The game challenges players to strategically play cards to control the parade, using specific removal rules and scoring mechanics. This project is designed primarily for developers, showcasing object-oriented principles and clean coding practices with a modular design.

## Prerequisite 
Java Version: Java 8 (or later)
Build Tool: Gradle
Terminal: xterm-256color

## Building Files
Gradle to clean previous build files
- `./gradlew clean or Gradle clean`
    
Gradle to build the project
- `./gradlew build or Gradle build`

Gradle to package the jar file (Optional)
- `./gradlew jar or Gradle jar`

## Running the Client and Server
Gradle to run the application client
- `./gradlew run or Gradle run`

For the server, ensure the port (default 1234) and address (default localhost) is configured properly with the Parade.config file at project root.

Address should follow the machine running the server's host ipv4 address. Other clients can connect to it provided they are on the same network with the proper network configuration. Opening online games to players outside the network is out-of-scope can be done but is out-of-scope.
  
Gradle to run the application server
- `./gradlew run -PmainClass="com.g2t2.network.server.Server"`

# Design Decisions
1. Modularization:
The code is organized into multiple packages (game, app, enum, etc.) to separate concerns and follow the Single Responsibility Principle (SRP). Each class is focused on a single function or role within the game.

2. Configuration:
All configuration parameters are externalized where possible, allowing for future modifications without changing the source code directly.

3. Maintainability:
Emphasis has been placed on clean, readable code following Java coding conventions. The structure is intended to support both development and potential future enhancements, such as integrating GUI in the future.

4. Gradle Integration:
Gradle is used to manage builds and dependencies, streamlining the development and deployment process.

# SourceCode Overview

📁 app/ 
Contains the application entry point and high-level flow control.
- `App.java` - Main class that initializes the player database and launches the start menu.

  
📁 db/ 
Handles all database-related functionality, including initialization and queries.
- 📁 dao/
  Implements the Data Access Object (DAO) pattern for database interactions.
    - 📁 impl/
      - `PlayerProfileDaoImpl.java` - Concrete implementation of the DAO interface for accessing player profiles.
      - `PlayerProfileDao.java` - Interface defining the methods for accessing player profile data.
- `DBController.java` - Manages database connection and initialization of the player profile table
- `Query.java` - Stores SQL query strings as constants.


📁 enums/
    Defines enumerated types used throughout the project.
- `CardColour.java` - Enum for the six card colors with corresponding ANSI codes for display.
- `PlayerProfileOptionInput.java` - Enum for user command options related to player profile actions.


📁 game/
    Core game logic and rule enforcement.
- 📁 gameinit/
    Handles game setup and configuration.
    - `GameInit.java` - Manages configuration parameters such as number of players and CPU difficulty.
    - `GameInitPrompt.java` - Prompts user input for game initialization options.

- 📁 gameutility/
    Manages runtime logic and rendering during gameplay.
    - `CPUGameAction.java` - Encapsulates CPU behavior for different difficulty levels (Easy, Medium, Hard).
    - `GameFlow.java` - Controls turn-based game progression.
    - `GamePlayPrompt.java` - Manages user prompts during gameplay.
     - `GameProcessor.java` - Coordinates game actions as dictated by `GameFlow`.
    - `GameRenderer.java` - Renders visual elements such as the game board and ASCII art.
      
- `GamePlay.java` - Orchestrates the game rounds based on predefined win/loss conditions.
- `GameState.java` - Represents the full game state (players, game ID, game master, etc.).
- `MenuRender.java` - Renders UI for player profiles.
- `StartMenu.java` - Presents the initial game menu (e.g., Local/Online mode).
- `StateManager.java` - Maintains the current player profile.


📁 network/
Implements support for online multiplayer functionality.
- 📁 client/
    Client-side components for online play.
    - `Client.java` - Tracks the player’s state during an online game.
    - `OnlineGameProcessor.java` - Handles the flow of gameplay in an online session.
- 📁 networkutility/
    Utility classes for online communication and lobby management.
    - `Message.java` - Defines the structure of messages sent over the network.
    - `NetworkUtility.java` - Provides methods for broadcasting, sending, and receiving messages.
    - `OnlineLobby.java` - Manages online lobby creation, player joining, CPU addition, and game start.
- 📁 server/
    Server-side components for hosting multiplayer sessions.
    - `ClientHandler.java` - Handles client-side actions like joining or leaving a game.
    - `Server.java` -  Hosts and manages the Parade online game server.

📁 types/
Defines core domain models and helper classes.
- `Card.java` - Represents a card with a color and a value.
- `CardDeck.java` – Manages card deck creation, shuffling, and drawing.
- `ParadeBoard.java` – Represents the central parade line where cards are played.
- `Player.java` – Represents a player (human or CPU), including their hand, score pile, and actions.
- `PlayerComparator.java` – Compares players for sorting based on specific criteria (e.g., score).
- `PlayerProfile.java` – Stores data about a player, such as name, win rate, and profile stats.
- `ScoreCalculation.java` – Implements the rules for scoring at the end of the game.

📁 util/
Utility classes that support the rest of the codebase.
- `Constants.java` – Defines fixed values for game configuration and display.
- `Display.java` – Handles ASCII rendering of cards and other UI elements, including ANSI color.
- `Utility.java` – Provides shared utility methods such as `clearConsoleScreen()`.

# Credits & Acknowledgements
### Development Team
G2T2
- CHUE MYAT SANDY
- HTET SHWE WIN THAN
- NATHAN JEFFERSON HART
- EASAN SARAVAN
- YEO BEN SHIN
- GOH XUAN YU OLIVER

Project Inspired By: Parade Rulebook and associated video resources

## OpenSource Libraries Used
### Gradle
- Grade is used to build automation tool for easier development experience and for build-in dependency management
### SQLite
- SQLite is used for local database storage of player profiles and win records.