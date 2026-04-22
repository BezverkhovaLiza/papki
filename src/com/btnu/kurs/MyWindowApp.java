package com.btnu.kurs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class MyWindowApp {

    private JFrame jFrame = new JFrame();
    private JPanel jPanel = new JPanel();
    private Logger logger = Logger.getLogger(MyWindowApp.class.getName());

    private final FolderMonitoring monitoring = new FolderMonitoring();
    private java.util.Timer timer;
    private TimerTask timerTask;

    public void openWindow() {
        jFrame.setVisible(true);
        jFrame.setTitle("Monitoring");
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension dimension = toolkit.getScreenSize();
        jFrame.setBounds(dimension.width / 2 - 300, dimension.height / 2 - 150, 600, 300);
        jPanel.setBounds(50, 50, 100, 50);
        jFrame.add(jPanel);
        addButton();
        addTextPanel();
    }

    private void addButton() {
        JButton button = new JButton("Следить");
        jPanel.setBounds(50, 50, 100, 50);
        jPanel.add(button);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (timer == null && timerTask == null) {
                    button.setText("Остановить");
                    timerTask = new TimerTask() {
                        @Override
                        public void run() {
                            try {
                                monitoring.folderWatch(logger);
                            } catch (IOException | InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    };
                    timer = new java.util.Timer();
                    timer.schedule(timerTask, 0);
                } else {
                    timerTask.cancel();
                    timer.cancel();
                    monitoring.cancel();
                    button.setText("Следить");
                    timer = null;
                    timerTask = null;
                }
            }
        });
    }

    private void addTextPanel() {
        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logger.setUseParentHandlers(false); // Отключаем стандартные обработчики
        logger.addHandler(new TextAreaHandler(logArea));
        jPanel.add(logArea);
        // Пример логгирования
        logger.info("Приложение запущено.");
        logger.warning("");
    }

    // Кастомный Handler для вывода в JTextArea
    private static class TextAreaHandler extends Handler {
        private JTextArea textArea;

        public TextAreaHandler(JTextArea textArea) {
            this.textArea = textArea;
            setFormatter(new SimpleFormatter());
        }


        @Override
        public void publish(LogRecord record) {
            if (isLoggable(record)) {
                // Выводим в EDT (Event Dispatch Thread)
                //SwingUtilities.invokeLater(() -> {
                textArea.append(getFormatter().format(record));
                //});
            }
        }

        @Override
        public void flush() {
            // Ничего не делаем, или можно очистить textArea
        }

        @Override
        public void close() throws SecurityException {
            // Ничего не делаем
        }
    }
}