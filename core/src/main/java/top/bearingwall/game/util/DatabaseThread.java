package top.bearingwall.game.util;

import top.bearingwall.game.ClientMain;
import top.bearingwall.game.data.Grid;
import top.bearingwall.game.data.Tower;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseThread extends Thread {
    Connection con;
    String tableName = "game";

    @Override
    public void run() {
        tableName = "game_"+ClientMain.INSTANCE.getPlayerName();
        try {
            Class.forName("org.sqlite.JDBC");
            con = DriverManager.getConnection("jdbc:sqlite:generals.db");
            String createTableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (turn INTEGER PRIMARY KEY, tower INTEGER, power INTEGER, map BLOB)";
            con.createStatement().executeUpdate(createTableSql);
            String cleanSql = "TRUNCATE TABLE " + tableName;
            con.createStatement().executeUpdate(cleanSql);
            wait();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void writeData(int turn, Grid[][] grids) throws SQLException, IOException {
        //TODO: write turn data
        int tower = 0;
        int power = 0;
        String playerName = ClientMain.INSTANCE.getPlayerName();
        // calculate towers and power
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                var grid = grids[i][j];
                var gridPlayerName = grid.getPlayer().getName();
                if (gridPlayerName.equals(playerName)) {
                    if (grid instanceof Tower) tower++;
                    power+= grid.getPower();
                }
            }
        }
        String insertSql = "INSERT INTO " + tableName + " (turn, tower, power, map) VALUES(?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(insertSql);
        pstmt.setInt(1, turn);
        pstmt.setInt(2, tower);
        pstmt.setInt(3, power);
        byte[] serializedData = serialize(grids);
        pstmt.setBytes(4, serializedData);
        pstmt.executeUpdate();
    }

    private static byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private static Grid[][] deserialize(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais)) {
            return (Grid[][]) ois.readObject();
        } catch (ClassNotFoundException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }
}
