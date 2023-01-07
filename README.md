# ROBOTRACK

Below are the description of the task and more details on how the bullets points are fulfilled.

- [] MAIN
- [] Bonus 1
- [] Bonus 2
- [] Bonus 3

## TRIAL TASK

Make an app robot-tracker. (It can be backend or frontend or both, it can be in any tech stack.). The application is
covered by automated tests.

### MAIN

The robot is at certain coordinates, (e.g. 5km North, 3km East). We can tell the robot to move, e.g. move 1km West, 4km
South, 2km North, 7km North, etc, and we get the robot's new coordinates. At any point, we can ask the system to give us
coordinates for any robot.

### BONUS 1

Some regions on the ground have "holes", and when the robot walks over those regions then the game has ended.

### BONUS 2

Whenever the robot moves in any direction, the system publishes an event to a messaging bus (can be any).

### BONUS 3

Run Mutation Testing and attach the results.

Mutation testing is enabled in the project. Simply run

```
./gradlew pitest
```

