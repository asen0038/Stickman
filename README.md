##Stickman

##Run
Run the code with 'gradle run'

##New Features
* Level Transition: New levels can be reached once a level is completed by touching the flag.
* Score: There is a score system that will show the current level score and a combined total score from previous levels.
    Killing slime enemy with bullet = +100 points
    Touching a mushroom = +50 points
* Time: Each level will now have a time limit which will begin to count down to 0
    Every 1 second above the time limit = +1 point
    Every 1 second below the time limit = -1 point
* Player Lives: The player will now have 5 lives to complete the game. If lives are exhausted, the game will be over.
* Quicksave/Quickload: The current state of the game can be saved by pressing the key 'S' on the keyboard. The same
    state can be reloaded by pressing the 'L' key on the keyboard at anytime. Each save will overwrite the previous save.
    Once a state is loaded, it cannot be loaded again.
    The loaded state will load everything including the timestamp, score, total score and lives.


##Design Patterns used
* Prototype: Used in all Entity type objects. The prototype method copy() in Entity interface is implemented by all
    Entities. The prototype method copy() method is also implemented in LevelManager from Level.
* Memento: Used in GameMemento (Memento), GameEngine (Originator) and GameReloader (Caretaker).
    GameMemento is located in the model package.
    GameReloader is located in the view package.

##Controls
* Move left: Left Arrow Key
* Move right: Right Arrow Key
* Jump: Up Arrow Key
* Shoot: Space Key
* S: quicksave
* L: quickload
