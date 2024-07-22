package top.bearingwall.game.util;

import top.bearingwall.game.ClientMain;
import top.bearingwall.game.data.Grid;

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
            String createTableSql = "CREATE TABLE IF NOT EXISTS " + tableName + " (turn INTEGER PRIMARY KEY, grid INTEGER, power INTEGER, map BLOB)";
            con.createStatement().executeUpdate(createTableSql);
            try {
                String cleanSql = "DELETE FROM " + tableName;
                con.createStatement().executeUpdate(cleanSql);
            } catch (SQLException e) {
                ClientMain.INSTANCE.getLogger().info(e.getMessage());
                ClientMain.INSTANCE.getLogger().info(tableName + "不存在");
            }
            wait();
        } catch (Exception e) {
            ClientMain.INSTANCE.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public void writeData(int turn, Grid[][] grids) throws SQLException, IOException {
        //write turn data
        int grid = 0;
        int power = 0;
        String playerName = ClientMain.INSTANCE.getPlayerName();
        // calculate grids and power
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                var currentGrid = grids[i][j];
                var gridPlayerName = currentGrid.getPlayer().getName();
                if (gridPlayerName.equals(playerName)) {
                    if (currentGrid.getPlayer().getName().equals(playerName)) grid++;
                    power+= currentGrid.getPower();
                }
            }
        }
        String insertSql = "INSERT INTO " + tableName + " (turn, grid, power, map) VALUES(?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(insertSql);
        pstmt.setInt(1, turn);
        pstmt.setInt(2, grid);
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

    public int readGrid(int turn) throws SQLException, IOException {
        String sql = "SELECT grid FROM " + tableName + " WHERE turn = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, turn);
        ResultSet rs = pstmt.executeQuery();
        return rs.getInt("grid");
    }

    public int readPower(int turn) throws SQLException, IOException {
        String sql = "SELECT power FROM " + tableName + " WHERE turn = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setInt(1, turn);
        ResultSet rs = pstmt.executeQuery();
        return rs.getInt("power");
    }

    public ResultSet readAll() throws SQLException, IOException {
        String sql = "SELECT * FROM " + tableName;
        Statement stmt = con.createStatement();
        return stmt.executeQuery(sql);
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
        } catch (ClassNotFoundException | NullPointerException e) {
            ClientMain.INSTANCE.getLogger().info(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }
}
