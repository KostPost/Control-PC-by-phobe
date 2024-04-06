package org.kostpost;

import com.sun.jna.Native;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;


import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinNT.HRESULT;

public class ServerPC {

    private static void stopService() {
        String serviceName = "ControlPC"; // Имя вашей службы
        try {
            System.out.println("Stopping the service " + serviceName + "...");
            Process process = Runtime.getRuntime().exec("net stop \"" + serviceName + "\"");
            process.waitFor();
            System.out.println("Service " + serviceName + " stopped successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to stop the service " + serviceName);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Service stopping process was interrupted for " + serviceName);
        }
    }


    public void startServer() {
        System.out.println("Server started, waiting for connections...");
        try (ServerSocket serverSocket = new ServerSocket(11)) {
            while (true) { // Infinite loop to wait for client connections
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("Error while connecting client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Failed to start server: " + e.getMessage());
        } finally {
            try {
                Thread.sleep(1000); // Give server time to wait for connections before shutting down
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split("\t");
                    if (parts.length >= 2) {
                        String command = parts[0];
                        String userName = parts[1];
                        System.out.println("Received command '" + command + "' from user " + userName);
                        executeCommand(command);
                    } else {
                        System.out.println("Incorrect data from client: " + inputLine);
                    }
                }
            } catch (IOException e) {
                System.out.println("Error while handling client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error while closing client socket: " + e.getMessage());
                }
            }
        }).start();
    }

    private void executeCommand(String command) {
        // System.out.println("\n\n" + command + "\n\n");

        switch (command) {
            case "SHUTDOWN":
                shutdownComputer();
                break;
            case "SLEEP":
                sleepComputer();
                break;
            case "REBOOT":
                rebootComputer();
                break;
            case "IDEA":
                launchIDEA();
                break;
            case "NEXT_SONG":
                nextSong();
                break;
            case "PREVIOUS_SONG":
                previousSong();
                break;
            case "PAUSE_PLAY":
                pausePlaySong();
                break;
            default:
                System.out.println("Unknown command: " + command);
                break;
        }
    }


    private void shutdownComputer() {
        try {
            stopService();
            System.out.println("Shutting down the computer...");
            Runtime.getRuntime().exec("shutdown.exe -s -t 0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to shut down the computer");
        }
    }

    private void sleepComputer() {
        try {
            stopService();
            System.out.println("Putting the computer to sleep...");
            // Rundll32 command just locks the computer, a different implementation may be needed for sleep mode
            Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to put the computer to sleep");
        }
    }

    private void rebootComputer() {
        try {
            stopService();
            System.out.println("Rebooting the computer...");
            Runtime.getRuntime().exec("shutdown.exe -r -t 0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to reboot the computer");
        }
    }

    private void launchIDEA() {
        try {
            System.out.println("Launching IntelliJ IDEA...");
            // Полный путь к исполняемому файлу IntelliJ IDEA
            String pathToIDEA = "P:\\Proga\\IDEs\\IntelliJ IDEA 2023.3.4\\bin\\idea64.exe";
            Runtime.getRuntime().exec(pathToIDEA);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to launch IntelliJ IDEA");
        }
    }

    private static void startSpotify() {
        try {
            System.out.println("Starting Spotify...");
            String pathToSpotify = "C:\\Users\\KostPost\\AppData\\Roaming\\Spotify\\Spotify.exe";
            Process process = Runtime.getRuntime().exec(pathToSpotify);
            Thread.sleep(1500);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("Failed to start Spotify");
        }
    }


    private static boolean isSpotifyActive() {
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        if (hwnd == null) {
            return false;
        }

        char[] windowText = new char[512];
        User32.INSTANCE.GetWindowText(hwnd, windowText, 512);
        String wText = Native.toString(windowText).trim();


        return wText.contains("Spotify");
    }

    public static void pausePlaySong() {
        try {
            Robot robot = new Robot();

            if (!isSpotifyActive()) {
                startSpotify();
            }

            Thread.sleep(1);
            // Нажатие клавиши Ctrl+Shift+Right
            robot.keyPress(KeyEvent.VK_SPACE);
            robot.keyRelease(KeyEvent.VK_SPACE);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void nextSong() {
        try {
            Robot robot = new Robot();

            if (!isSpotifyActive()) {
                startSpotify();
                // Подождите, пока Spotify не станет активным окном
            }

            // Симуляция нажатия Ctrl + → для переключения на следующий трек
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_RIGHT);
            robot.keyRelease(KeyEvent.VK_CONTROL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void previousSong() {
        try {
            Robot robot = new Robot();

            if (!isSpotifyActive()) {
                startSpotify();
            }

            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_LEFT);
            robot.keyRelease(KeyEvent.VK_CONTROL);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
