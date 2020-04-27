package lv.herbis.cnakes.status;

import lv.herbis.cnakes.entities.Timer;

public abstract class SinglePlayerGameStatus implements GameStatus {
    private boolean isPLAYED, isPAUSED, justSTARTED;
    private boolean hasENDED = false;

    private long SCORE = 0;
    private long SNAKE_LENGTH = 5;
    private long BUGS_COLLECTED = 0;
    private boolean IN_BONUS = false;
    private Timer GAME_TIMER;
    private long gameLength = 0;

    public SinglePlayerGameStatus(final long gameLength) {
        this.gameLength = gameLength;
    }

    @Override
    public void start() {
        reset();
        isPLAYED = true;
        justSTARTED = true;
        GAME_TIMER.start();
    }


    public long getScore() {
        return SCORE;
    }


    public long getSnakeLength() {
        return SNAKE_LENGTH;
    }


    public long getBugsCollected() {
        return BUGS_COLLECTED;
    }


    public void setBugsCollected(final long bugsCollected) {
        BUGS_COLLECTED = bugsCollected;
    }


    public void collectBug() {
        BUGS_COLLECTED++;
        SNAKE_LENGTH++;
    }

    public void setSnakeLength(final long length) {
        SNAKE_LENGTH = length;
    }

    public void setScore(final long score) {
        SCORE = score;
    }


    public long addScore(final long add) {
        return SCORE += add;
    }


    @Override
    public void pause() {
        /* Only attempt to pause if the game is being played. */
        if (isPlayed()) {
            isPAUSED = !isPAUSED;

            GAME_TIMER.pause();
        }
    }


    @Override
    public boolean isPaused() {
        return isPAUSED;
    }

    @Override
    public boolean isPlayed() {
        return isPLAYED;
    }


    public boolean inBonus() {
        return IN_BONUS;
    }


    public void setInBonus(final boolean inBonus) {
        IN_BONUS = inBonus;
    }

    @Override
    public void end() {
        hasENDED = true;
        isPLAYED = false;
        afterEnd();
    }

    public abstract void afterEnd();


    @Override
    public boolean hasEnded() {
        if (hasENDED) {
            return true;
        } else {
            if (GAME_TIMER != null) {
                if (GAME_TIMER.getTimeLeft() <= 0) {
                    end();
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public void reset() {
        SCORE = 0;
        SNAKE_LENGTH = 5;
        BUGS_COLLECTED = 0;
        IN_BONUS = false;
        hasENDED = false;
        isPAUSED = false;
        isPLAYED = false;
        justSTARTED = false;

        GAME_TIMER = new Timer(gameLength);
    }

    public Timer getTimer() {
        return GAME_TIMER;
    }

    /**
     * Will return true only once if the game has just been started.
     */
    @Override
    public boolean hasJustStarted() {
        if (justSTARTED) {
            justSTARTED = false;
            return true;
        } else {
            return false;
        }
    }

}
