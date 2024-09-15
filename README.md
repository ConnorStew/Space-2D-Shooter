# Space Defence

I created this game for my second year graded unit at Fourth Valley College, it's a top-down shooter.

## Build
Debian/WSL prerequisites:
```
sudo apt-get install x11-xserver-utils maven
```

Build with Maven:
```
mvn clean package
java -jar target/SpaceShooter-1.jar
```

## Libraries
* [libGdx](https://libgdx.com/)