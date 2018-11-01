package nodemanager;

/*
Modes are used to tell the program how to act
for example, when the program is in MOVE mode, 
it will reposition a node until the user clicks.
This enumeration is used in the Session class

Implemented thus far:
NONE                                        - done
ADD               - in MapImage             - done
MOVE              - in MapImage             - done, but not super good. 
ADD_CONNECTION    - in NodeIcon             - done
REMOVE_CONNECTION - in NodeIcon             - done
RESCALE_UL        - in MapImage             - done
RESCALE_LR        - in MapImage             - done
*/
public enum Mode {
    NONE,
    ADD,
    MOVE,
    ADD_CONNECTION,
    REMOVE_CONNECTION,
    RESCALE_UL,
    RESCALE_LR
}
