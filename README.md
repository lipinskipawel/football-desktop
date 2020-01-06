# LAN-Game
Desktop football 2D game.
This GUI implementation uses external [engine], were you can contribute.

[engine]: https://github.com/lipinskipawel/game-engine

## Building from source
 - JDK 13
 - fill `application.properties` with `token.name`, `token.value` and `storage.service` or provide environment variables
 - execute `mvn clean package`
 - run `java -jar --enable-preview football-gui-game/target/football-game.jar` and start playing
