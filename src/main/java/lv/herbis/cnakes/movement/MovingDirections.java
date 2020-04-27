package lv.herbis.cnakes.movement;

import java.util.LinkedList;
import java.util.Queue;

public class MovingDirections {

    public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
    public static final int PLAYER_1 = 1, PLAYER_2 = 2, PLAYER_3 = 3, PLAYER_4 = 4,
            PLAYER_5 = 5, PLAYER_6 = 6, PLAYER_7 = 7, PLAYER_8 = 8;


    private static Queue<Integer> p1DirectionQ;
    private static int p1Direction;
    private static int p1LastDirection;
    private static Queue<Integer> p2DirectionQ;
    private static int p2Direction;
    private static int p2LastDirection;
    private static Queue<Integer> p3DirectionQ;
    private static int p3Direction;
    private static int p3LastDirection;
    private static Queue<Integer> p4DirectionQ;
    private static int p4Direction;
    private static int p4LastDirection;
    private static Queue<Integer> p5DirectionQ;
    private static int p5Direction;
    private static int p5LastDirection;
    private static Queue<Integer> p6DirectionQ;
    private static int p6Direction;
    private static int p6LastDirection;
    private static Queue<Integer> p7DirectionQ;
    private static int p7Direction;
    private static int p7LastDirection;
    private static Queue<Integer> p8DirectionQ;
    private static int p8Direction;
    private static int p8LastDirection;


    /**
     * Sets Direction the player should go to (adds the direction to queue).
     *
     * @param player    Which player to set it for.
     * @param direction Which direction the player should go.
     */
    public static void setDirection(final int player, final int direction) {

        switch (player) {
            case PLAYER_1:
                MovingDirections.p1DirectionQ.add(direction);
                MovingDirections.p1Direction = direction;
                break;
            case PLAYER_2:
                MovingDirections.p2DirectionQ.add(direction);
                MovingDirections.p2Direction = direction;
                break;
            case PLAYER_3:
                MovingDirections.p3DirectionQ.add(direction);
                MovingDirections.p3Direction = direction;
                break;
            case PLAYER_4:
                MovingDirections.p4DirectionQ.add(direction);
                MovingDirections.p4Direction = direction;
                break;
            case PLAYER_5:
                MovingDirections.p5DirectionQ.add(direction);
                MovingDirections.p5Direction = direction;
                break;
            case PLAYER_6:
                MovingDirections.p6DirectionQ.add(direction);
                MovingDirections.p6Direction = direction;
                break;
            case PLAYER_7:
                MovingDirections.p7DirectionQ.add(direction);
                MovingDirections.p7Direction = direction;
                break;
            case PLAYER_8:
                MovingDirections.p8DirectionQ.add(direction);
                MovingDirections.p8Direction = direction;
                break;
        }
    }

    public static int getDirection(final int player) {
        int direction = -1;

        switch (player) {
            case PLAYER_1:
                if (MovingDirections.p1DirectionQ.isEmpty()) {
                    direction = MovingDirections.p1Direction;
                } else {
                    direction = MovingDirections.p1DirectionQ.poll();
                }

                MovingDirections.p1LastDirection = direction;

                break;
            case PLAYER_2:
                if (MovingDirections.p2DirectionQ.isEmpty()) {
                    direction = MovingDirections.p2Direction;
                } else {
                    direction = MovingDirections.p2DirectionQ.poll();
                }

                MovingDirections.p2LastDirection = direction;

                break;
            case PLAYER_3:
                if (MovingDirections.p3DirectionQ.isEmpty()) {
                    direction = MovingDirections.p3Direction;
                } else {
                    direction = MovingDirections.p3DirectionQ.poll();
                }

                MovingDirections.p3LastDirection = direction;

                break;
            case PLAYER_4:
                if (MovingDirections.p4DirectionQ.isEmpty()) {
                    direction = MovingDirections.p4Direction;
                } else {
                    direction = MovingDirections.p4DirectionQ.poll();
                }

                MovingDirections.p4LastDirection = direction;

                break;
            case PLAYER_5:
                if (MovingDirections.p5DirectionQ.isEmpty()) {
                    direction = MovingDirections.p5Direction;
                } else {
                    direction = MovingDirections.p5DirectionQ.poll();
                }

                MovingDirections.p5LastDirection = direction;

                break;
            case PLAYER_6:
                if (MovingDirections.p6DirectionQ.isEmpty()) {
                    direction = MovingDirections.p6Direction;
                } else {
                    direction = MovingDirections.p6DirectionQ.poll();
                }

                MovingDirections.p6LastDirection = direction;

                break;
            case PLAYER_7:
                if (MovingDirections.p7DirectionQ.isEmpty()) {
                    direction = MovingDirections.p7Direction;
                } else {
                    direction = MovingDirections.p7DirectionQ.poll();
                }

                MovingDirections.p7LastDirection = direction;

                break;
            case PLAYER_8:
                if (MovingDirections.p8DirectionQ.isEmpty()) {
                    direction = MovingDirections.p8Direction;
                } else {
                    direction = MovingDirections.p8DirectionQ.poll();
                }

                MovingDirections.p8LastDirection = direction;

                break;
        }

        return direction;
    }


    public static int getPreviousDirection(final int player) {

        switch (player) {
            case PLAYER_1:
                return MovingDirections.p1LastDirection;

            case PLAYER_2:
                return MovingDirections.p2LastDirection;

            case PLAYER_3:
                return MovingDirections.p3LastDirection;

            case PLAYER_4:
                return MovingDirections.p4LastDirection;

            case PLAYER_5:
                return MovingDirections.p5LastDirection;

            case PLAYER_6:
                return MovingDirections.p6LastDirection;

            case PLAYER_7:
                return MovingDirections.p7LastDirection;

            case PLAYER_8:
                return MovingDirections.p8LastDirection;
        }

        return -1;
    }

    public static void resetDirection(final int player) {
        switch (player) {
            case PLAYER_1:
                if (MovingDirections.p1DirectionQ == null) {
                    MovingDirections.p1DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p1DirectionQ.clear();
                }
                MovingDirections.p1Direction = MovingDirections.UP;
                MovingDirections.p1LastDirection = MovingDirections.UP;

                break;
            case PLAYER_2:
                if (MovingDirections.p2DirectionQ == null) {
                    MovingDirections.p2DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p2DirectionQ.clear();
                }
                MovingDirections.p2Direction = MovingDirections.UP;
                MovingDirections.p2LastDirection = MovingDirections.UP;

                break;
            case PLAYER_3:
                if (MovingDirections.p3DirectionQ == null) {
                    MovingDirections.p3DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p3DirectionQ.clear();
                }
                MovingDirections.p3Direction = MovingDirections.UP;
                MovingDirections.p3LastDirection = MovingDirections.UP;

                break;
            case PLAYER_4:
                if (MovingDirections.p4DirectionQ == null) {
                    MovingDirections.p4DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p4DirectionQ.clear();
                }
                MovingDirections.p4Direction = MovingDirections.UP;
                MovingDirections.p4LastDirection = MovingDirections.UP;

                break;
            case PLAYER_5:
                if (MovingDirections.p5DirectionQ == null) {
                    MovingDirections.p5DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p5DirectionQ.clear();
                }
                MovingDirections.p5Direction = MovingDirections.UP;
                MovingDirections.p5LastDirection = MovingDirections.UP;

                break;
            case PLAYER_6:
                if (MovingDirections.p6DirectionQ == null) {
                    MovingDirections.p6DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p6DirectionQ.clear();
                }
                MovingDirections.p6Direction = MovingDirections.UP;
                MovingDirections.p6LastDirection = MovingDirections.UP;

                break;
            case PLAYER_7:
                if (MovingDirections.p7DirectionQ == null) {
                    MovingDirections.p7DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p7DirectionQ.clear();
                }
                MovingDirections.p7Direction = MovingDirections.UP;
                MovingDirections.p7LastDirection = MovingDirections.UP;


                break;
            case PLAYER_8:
                if (MovingDirections.p8DirectionQ == null) {
                    MovingDirections.p8DirectionQ = new LinkedList<>();
                } else {
                    MovingDirections.p8DirectionQ.clear();
                }
                MovingDirections.p8Direction = MovingDirections.UP;
                MovingDirections.p8LastDirection = MovingDirections.UP;

                break;
        }
    }

    public static Queue<Integer> getP1DirectionQ() {
        return p1DirectionQ;
    }

    public static int getP1Direction() {
        return p1Direction;
    }

    public static int getP1LastDirection() {
        return p1LastDirection;
    }

    public static Queue<Integer> getP2DirectionQ() {
        return p2DirectionQ;
    }

    public static int getP2Direction() {
        return p2Direction;
    }

    public static int getP2LastDirection() {
        return p2LastDirection;
    }

    public static Queue<Integer> getP3DirectionQ() {
        return p3DirectionQ;
    }

    public static int getP3Direction() {
        return p3Direction;
    }

    public static int getP3LastDirection() {
        return p3LastDirection;
    }

    public static Queue<Integer> getP4DirectionQ() {
        return p4DirectionQ;
    }

    public static int getP4Direction() {
        return p4Direction;
    }

    public static int getP4LastDirection() {
        return p4LastDirection;
    }

    public static Queue<Integer> getP5DirectionQ() {
        return p5DirectionQ;
    }

    public static int getP5Direction() {
        return p5Direction;
    }

    public static int getP5LastDirection() {
        return p5LastDirection;
    }

    public static Queue<Integer> getP6DirectionQ() {
        return p6DirectionQ;
    }

    public static int getP6Direction() {
        return p6Direction;
    }

    public static int getP6LastDirection() {
        return p6LastDirection;
    }

    public static Queue<Integer> getP7DirectionQ() {
        return p7DirectionQ;
    }

    public static int getP7Direction() {
        return p7Direction;
    }

    public static int getP7LastDirection() {
        return p7LastDirection;
    }

    public static Queue<Integer> getP8DirectionQ() {
        return p8DirectionQ;
    }

    public static int getP8Direction() {
        return p8Direction;
    }

    public static int getP8LastDirection() {
        return p8LastDirection;
    }
}
