package top.snowphoenix.toolsetencodetransformer.manager;

import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        return Paths.get(fileConfig.getOriginDir(), String.valueOf(uid), "target");
    }

    public Path transformedFileFromDir(Path dir, int fid) {
        return dir.resolve(String.valueOf(fid));
    }

    public Path transformedFile(int uid, int fid) {
        return transformedFileFromDir(transformedFileDir(uid), fid);
    }
}
