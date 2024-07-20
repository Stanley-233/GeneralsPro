package top.bearingwall.game.util;

import top.bearingwall.game.ClientMain;
import top.bearingwall.game.data.Grid;
import top.bearingwall.game.data.Tower;

import java.io.*;
import java.sql.*;

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
            String cleanSql = "DELETE FROM " + tableName;
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

    public Grid[][] readMap(int turn) throws SQLException, IOException {
        String sql = "SELECT map FROM " + tableName + " WHERE turn = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, turn);
        ResultSet rs = pstmt.executeQuery();
        byte[] mapBlob = rs.getBytes("map");
        return deserialize(mapBlob);
    }

    public int readTower(int turn) throws SQLException, IOException {
        String sql = "SELECT tower FROM " + tableName + " WHERE turn = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, turn);
        ResultSet rs = pstmt.executeQuery();
        return rs.getInt("tower");
    }

    public int readPower(int turn) throws SQLException, IOException {
        String sql = "SELECT power FROM " + tableName + " WHERE turn = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, turn);
        ResultSet rs = pstmt.executeQuery();
        return rs.getInt("power");
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
