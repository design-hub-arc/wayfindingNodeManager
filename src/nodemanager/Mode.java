package nodemanager;

/*
Implemented thus far:
NONE                                        - done
ADD               - in MapImage             - done
MOVE              - in MapImage             - done, but not super good. 
ADD_CONNECTION    - in NodeIcon             - done
REMOVE_CONNECTION - in NodeIcon             - done
RESCALE_UL        - in MapImage             - need to redo
RESCALE_LR        - in MapImage             - not done
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
