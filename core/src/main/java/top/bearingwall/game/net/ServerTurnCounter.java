package top.bearingwall.game.net;

public class ServerTurnCounter extends Thread  {
    int turn = 0;

    @Override
    public void run() {
        try {
            sleep(1000);
            // TODO: handle movement and 
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
