import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {

    static StringBuilder logBuilder = new StringBuilder();

    static List<String> listSavedFiles = new ArrayList<>();

    public static void main(String[] args) {

        createFolder("G://Netology/Games");
        createFolder("G://Netology/Games/src");
        createFolder("G://Netology/Games/res");
        createFolder("G://Netology/Games/savegames");
        createFolder("G://Netology/Games/temp");
        createFolder("G://Netology/Games/src/main");
        createFolder("G://Netology/Games/src/test");
        createFolder("G://Netology/Games/res/drawables");
        createFolder("G://Netology/Games/res/vecrors");
        createFolder("G://Netology/Games/res/icons");

        createFile("G://Netology/Games/src/main", "Main.java");
        createFile("G://Netology/Games/src/main", "Utils.java");
        createFile("G://Netology/Games/temp", "temp.txt");

        GameProgress gameProgress1 = new GameProgress(100, 20, 11, 2500);
        GameProgress gameProgress2 = new GameProgress(80, 40, 21, 4800);
        GameProgress gameProgress3 = new GameProgress(45, 10, 5, 1200);

        saveGame("G://Netology/Games/savegames/save1.dat", gameProgress1);
        saveGame("G://Netology/Games/savegames/save2.dat", gameProgress2);
        saveGame("G://Netology/Games/savegames/save3.dat", gameProgress3);

        zipFiles("G://Netology/Games/savegames/zipSaveGames.zip", listSavedFiles);

        openZip("G://Netology/Games/savegames/zipSaveGames.zip", "G://Netology/Games/savegames/");

        openProgress("G://Netology/Games/savegames/save1.dat");

        try (FileWriter fileWriter = new FileWriter("G://Netology/Games/temp/temp.txt")) {
            fileWriter.write(logBuilder.toString());
            System.out.println("\n" + "Вывод файла temp.txt для наглядности: " + "\n" + "\n" + logBuilder.toString());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void createFolder(String path) {
        File file = new File(path);
        if (file.mkdir()) {
            logBuilder.append("Каталог ").append(path).append(" создан успешно.").append("\n");

        } else {
            logBuilder.append("Ошибка. Каталог ").append(path).append(" не был создан.").append("\n");
        }
    }

    static void createFile(String pathFolder, String fileName) {
        File myFile = new File(pathFolder, fileName);
        try {
            if (myFile.createNewFile()) {
                logBuilder.append("Файл ").append(fileName).append(" создан успешно в каталоге ").append(pathFolder).append("\n");
            } else {
                logBuilder.append("Ошибка. Файл ").append(fileName).append(" не был создан в каталоге ").append(pathFolder).append("\n");
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void saveGame(String fullPathSaveFile, GameProgress gameProgress) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fullPathSaveFile); ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(gameProgress);
            listSavedFiles.add(fullPathSaveFile);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void zipFiles(String fullPathToArchive, List<String> listSavedFiles) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(fullPathToArchive))) {
            for (String pathSaveGame : listSavedFiles) {
                String[] parts = pathSaveGame.split("/");
                FileInputStream fis = new FileInputStream(pathSaveGame);
                ZipEntry entry = new ZipEntry(parts[parts.length - 1]);
                zout.putNextEntry(entry);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                zout.write(buffer);
                zout.closeEntry();
                logBuilder.append("Архив с сохранениями создан успешно: ").append(fullPathToArchive).append("\n");
                fis.close();


                File fileForDelete = new File(pathSaveGame);
                if (fileForDelete.delete()) {
                    logBuilder.append("Файл ").append(fileForDelete).append(" успешно удален из папки с сохранениями при архивации.").append("\n");
                } else {
                    System.out.println("Ошибка. Файл не удален");
                }
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    static void openZip(String fullPathToArchive, String pathToUnpackArchive) {
        try (ZipInputStream zin = new ZipInputStream(new FileInputStream(fullPathToArchive))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(pathToUnpackArchive + name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
                logBuilder.append("Архив с сохранениями распакован успешно в папку ").append(pathToUnpackArchive).append("\n");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    static void openProgress(String pathToSaveFile) {
        try (FileInputStream fis = new FileInputStream(pathToSaveFile);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            GameProgress gameProgress = (GameProgress) ois.readObject();
            System.out.println("Прогресс загружен: ");
            System.out.println(gameProgress);
            logBuilder.append("Прогресс сохранения ").append(pathToSaveFile).append(" загружен.").append("\n");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
