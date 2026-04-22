package com.btnu.kurs;

import java.nio.file.*;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class FolderMonitoring {

    private boolean terminate = false;

    public void folderWatch(Logger logger) throws IOException, InterruptedException {
        terminate = false;
        Path folderToWatch = Paths.get("D:\\Test"); // Замените на свой путь

        // Получаем WatchService
        WatchService watchService = FileSystems.getDefault().newWatchService();

        // Регистрируем папку для отслеживания событий
        folderToWatch.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        logger.info("Начинаем отслеживание папки: " + folderToWatch.toAbsolutePath());

        // Основной цикл ожидания и обработки событий
        while (true) {
            // Блокирующий вызов: ждет, пока не появится событие
            WatchKey key = watchService.take();

            if (terminate) {
                break;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind(); // Тип события
                Path name = (Path) event.context(); // Имя файла/папки, где произошло событие

                String pathName = name.toString();
                // Можно добавить дополнительную логику для каждого типа события
                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    logger.info("Создан новый файл/папка: " + pathName);
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    logger.info("Удален файл/папка: " + pathName);
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    logger.info("Изменен файл/папка: " + pathName);
                }
            }

            // Сбрасываем ключ, чтобы продолжить получение событий
            boolean valid = key.reset();
            if (!valid) {
                System.out.println("Ключ больше не действителен. Выход из цикла.");
                break;
            }
        }
    }

    public void cancel() {
        terminate = true;
    }
}
