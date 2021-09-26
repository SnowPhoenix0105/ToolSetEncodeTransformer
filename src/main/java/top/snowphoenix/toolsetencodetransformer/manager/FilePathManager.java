package top.snowphoenix.toolsetencodetransformer.manager;

import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Component
public class FilePathManager {
    public FilePathManager(FileConfig fileConfig) {
        this.fileConfig = fileConfig;
    }

    private final FileConfig fileConfig;

    public Path originFileDir(int uid) {
        return Paths.get(fileConfig.getOriginDir(), String.valueOf(uid));
    }

    public Path originFileFromDir(Path dir, int fid) {
        return dir.resolve(String.valueOf(fid));
    }

    public Path originFile(int uid, int fid) {
        return originFileFromDir(originFileDir(uid), fid);
    }

    public Path transformedFileDir(int uid) {
        return Paths.get(fileConfig.getTransformedDir(), String.valueOf(uid));
    }

    public Path transformedFileFromDir(Path dir, int fid) {
        return dir.resolve(String.valueOf(fid));
    }

    public Path transformedFile(int uid, int fid) {
        return transformedFileFromDir(transformedFileDir(uid), fid);
    }


    public void ensureAndClearDir(Path dir) throws IOException {
        if (Files.exists(dir)) {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>(){
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        Files.createDirectories(dir);
    }
}
