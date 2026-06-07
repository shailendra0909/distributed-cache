package sp.test.membership;

/**
 * This represent status of a node if it is alive, suspected dead or dead.
 */
public enum NodeState {
    ALIVE,
    SUSPECT,
    DEAD;

    public int precedence(){
       return switch (this){
            case DEAD -> 3;
            case SUSPECT -> 2;
            case ALIVE -> 1;
        };
    }
}
