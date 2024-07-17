package top.bearingwall.game.net;

public class ServerTurnCounter extends Thread  {
    int turn = 0;

    @Override
    public void run() {
        try {
            // TODO: handle movement and turn calculation
            while (true) {
                sleep(1000);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
