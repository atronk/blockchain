package blockchain;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    public static boolean isExistingFile(String fileName) {
        File f = new File(fileName);
        return f.exists() && !f.isDirectory();
    }

    public static boolean createFile(String fileName) {
        String path = fileName.substring(0, fileName.lastIndexOf("/"));
        String name = fileName.substring(fileName.lastIndexOf("/"));
        try {
            File dir = new File(path);
            if (!dir.exists() && !dir.mkdirs()) {
                throw new IOException("Directory does not exist, and cannot be created: " + dir);
            }
            return new File(dir, name).createNewFile();
        } catch (Exception e) {
            System.out.printf("Could not create file %s: %s\n", fileName, e.getMessage());
            return false;
        }
    }
}
