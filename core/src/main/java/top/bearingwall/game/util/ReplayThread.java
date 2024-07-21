package top.bearingwall.game.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import top.bearingwall.game.ClientMain;
import top.bearingwall.game.data.GameMap;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class ReplayThread extends Thread {
    private int currentReplayTurn = 1;
    private final Map<Integer, Integer> grid = new HashMap<>();
    private final Map<Integer, Integer> power = new HashMap<>();

    @Override
    public void run() {
        try {
            sleep(500);
            ClientMain.INSTANCE.getReplaySound().play();
            while (true) {
                // finish read grid[][] from JDBC
                // 循环调用calculateReplayMap
                GameMap.setGrids(ClientDataHandler.databaseThread.readMap(currentReplayTurn));
                int currentGrid = ClientDataHandler.databaseThread.readGrid(currentReplayTurn);
                if (currentGrid == 0) {
                    // 读完了
                    // gird
                    DefaultCategoryDataset gridData = new DefaultCategoryDataset();
                    for (int i = 1; i < currentReplayTurn; i++) {
                        gridData.addValue(grid.get(i), "grid", String.valueOf(i));
                    }
                    JFreeChart gridChart = ChartFactory.createLineChart(
                        "Occupied Grid Count",
                        "Turn",
                        "Grid Count",
                        gridData,
                        PlotOrientation.VERTICAL,
                        false,true,false);
                    ChartFrame towerChartFrame = new ChartFrame("Occupied Grid Count",gridChart);
                    towerChartFrame.pack();
                    towerChartFrame.setVisible(true);
                    try (var fos = new FileOutputStream("grid.png")) {
                        ChartUtils.writeChartAsPNG(fos, gridChart, 800, 600);
                    } catch (IOException e) {
                        ClientMain.INSTANCE.getLogger().error(e.getMessage());
                    }
                    // power
                    DefaultCategoryDataset powerData = new DefaultCategoryDataset();
                    for (int i = 1; i < currentReplayTurn; i++) {
                        powerData.addValue(power.get(i), "power", String.valueOf(i));
                    }
                    JFreeChart powerChart = ChartFactory.createLineChart(
                        "Total Power Count",
                        "Turn",
                        "Power Count",
                        powerData,
                        PlotOrientation.VERTICAL,
                        false,true,false);
                    ChartFrame powerChartFrame = new ChartFrame("Total Power Count",powerChart);
                    powerChartFrame.pack();
                    powerChartFrame.setVisible(true);
                    try (var fos = new FileOutputStream("power.png")) {
                        ChartUtils.writeChartAsPNG(fos, powerChart, 800, 600);
                    } catch (IOException e) {
                        ClientMain.INSTANCE.getLogger().error(e.getMessage());
                    }
                    ReportPDF.INSTANCE.writePDF();
                    this.interrupt();
                }
                grid.put(currentReplayTurn,currentGrid);
                int currentPower = ClientDataHandler.databaseThread.readPower(currentReplayTurn);
                power.put(currentReplayTurn,currentPower);
                ClientMain.INSTANCE.getLogger().info("回合数：" + currentReplayTurn + "占领格子数：" + currentGrid + "总兵力：" + currentPower);
                ClientDataHandler.INSTANCE.calculateReplayMap();
                currentReplayTurn++;
                sleep(500);
            }
        } catch (InterruptedException | SQLException | IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
