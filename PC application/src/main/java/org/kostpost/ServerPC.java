package org.kostpost;

import java.io.*;
import java.net.*;


public class ServerPC {

    public void startServer() {

        int i = 0;
        System.out.println("Сервер запущен, ожидание подключения...");
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            while (true) { // Бесконечный цикл для ожидания подключения клиентов
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Клиент подключен: " + clientSocket.getInetAddress());
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.out.println("Ошибка при подключении клиента: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }//qwe

    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    String[] parts = inputLine.split("\t");
                    if (parts.length >= 2) {
                        String command = parts[0];
                        String userName = parts[1];
                        System.out.println("Получено команда '" + command + "' от пользователя " + userName);
                        executeCommand(command);
                    } else {
                        System.out.println("Некорректные данные от клиента: " + inputLine);
                    }
                }
            } catch (IOException e) {
                System.out.println("Ошибка при обработке клиента: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Ошибка при закрытии сокета клиента: " + e.getMessage());
                }
            }
        }).start();
    }

    private void executeCommand(String command) {
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
            default:
                System.out.println("Неизвестная команда: " + command);
                break;
        }
    }

    private void shutdownComputer() {
        try {
            System.out.println("Выключение компьютера...");
            Runtime.getRuntime().exec("shutdown.exe -s -t 0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось выключить компьютер");
        }
    }

    private void sleepComputer() {
        try {
            System.out.println("Перевод компьютера в спящий режим...");
            // Команда Rundll32 только блокирует компьютер, для спящего режима может потребоваться другая реализация
            Runtime.getRuntime().exec("rundll32.exe powrprof.dll,SetSuspendState 0,1,0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось перевести компьютер в спящий режим");
        }
    }

    private void rebootComputer() {
        try {
            System.out.println("Перезагрузка компьютера...");
            Runtime.getRuntime().exec("shutdown.exe -r -t 0");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось перезагрузить компьютер");
        }
    }

    private void launchIDEA() {
        try {
            System.out.println("Запуск IntelliJ IDEA...");
            // Путь к исполняемому файлу IntelliJ IDEA может отличаться
            Runtime.getRuntime().exec("cmd /c start idea64.exe");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Не удалось запустить IntelliJ IDEA");
        }
    }


}
