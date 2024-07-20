package top.bearingwall.game.net;

public class ReplayThread extends Thread {
    @Override
    public void run() {
        try {
            sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
