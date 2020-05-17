**Battleship - Android Game**

`d282d6c9adebdbe3add88b3b806a77853151208e` is the last commit with done within the specified timeline, rest of the commits are only to add samples to the README.md

**App**

- MVVM Architecture is strictly followed. This can be visibly seen where just changing the Model layer makes it work for both Bot and Online Game

- Model will be abstracted using an interface `Game`, This will help us use different mode of communication without affecting other layers

- View Model will use an abstracted Model and work consistently with no coupling to model layer

- View layer observes the `gameState` (using LiveData)  maintained in the View Model layer and uses this to change the view represented

- All Views use constraint layout to scale in different device sizes, currently tested in 4 - 10 inch

**Core**

- The core library is written as a separate library, This handles maintaining the game state and game logic. Since it is a separate library with no Android dependency, It can also be used cross platform

- Core library will contain 

    1. Cell (Most basic unit of the game) - Maintains the cell level state

    2. Ship - The data structure to maintain the direction and position of the ship

    3. Grid - Collection of cells and ships. 

**WebSocket Client**

- Implemented as a separate module. It is implemented in such a way that underlying client library can be removed without affecting other parts

- Abstraction `GameSocket` is used to provide zero coupling with underlying websocket library

**WebSocket Server**

- WebSocket Server dependency can be found under websocket -> javanetlib package

- It is abstracted using `GameConnection` and `GameServer`, underlying websocket library can be migrated without affecting other parts

- Game logic can be later moved to a proper web server

- Logic is maintained under package controller

- `GameController` handles communication between players

- `GameCoordinator` communicates with `GameController` to handle logic between two players

- `Protocol` structure is used to communicate between server and client. All messages will contain `type` member. We will use `gson` to parse

--------

Project uses mono repo style - server is found under folder `server` and android app under the folder `android-app`

--------

**Sample**

![Sample](sample/battleship-1.gif)
