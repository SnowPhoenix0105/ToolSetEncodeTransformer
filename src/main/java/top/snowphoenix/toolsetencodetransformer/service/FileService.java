package top.snowphoenix.toolsetencodetransformer.service;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;
import top.snowphoenix.toolsetencodetransformer.manager.CacheManager;
import top.snowphoenix.toolsetencodetransformer.manager.FilePathManager;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.model.FileInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class FileService {
    public FileService(FilePathManager filePath, CacheManager cacheManager) {
        this.filePath = filePath;
        this.cacheManager = cacheManager;
    }

    private final FilePathManager filePath;
    private final CacheManager cacheManager;

    private ArrayList<FileInfo> saveFiles(Path workDirPath, MultipartFile[] files) throws IOException {
        Files.deleteIfExists(workDirPath);
        Files.createDirectories(workDirPath);

        ArrayList<FileInfo> fileInfos = new ArrayList<>();
        try {
            for (int i = 0; i < files.length; i++) {
                var file = files[i];
                String name = file.getOriginalFilename();
                if (name == null) {
                    name = file.getName();
                }
                Path target = filePath.transformedFileFromDir(workDirPath, i);
                Files.copy(file.getInputStream(), target);
                fileInfos.add(FileInfo.builder()
                        .fid(i)
                        .name(name)
                        .build());
            }
        }
        catch (Exception e) {
            log.warn("save(int, MultipartFile[]) wrong with file operations", e);
            Files.deleteIfExists(workDirPath);
            throw e;
        }
        return fileInfos;
    }

    public ArrayList<FileInfo> saveFilesForUser(int uid, MultipartFile[] files) throws IOException {
        Path workDirPath = filePath.originFileDir(uid);

        var fileInfos = saveFiles(workDirPath, files);

        try {
            cacheManager.setFileInfos(uid, fileInfos);
        }
        catch (Exception e) {
            log.warn("save(int, MultipartFile[]) wrong with redis operations", e);
            Files.deleteIfExists(workDirPath);
            throw e;
        }

        return fileInfos;
    }

    public String getTransformedFileContent(int uid, int fid) throws TimeoutException, IOException {
        if (!cacheManager.refreshExpire(uid)) {
            throw new TimeoutException();
        }
        Path path = filePath.transformedFile(uid, fid);
        Encoding encoding = cacheManager.getTargetEncoding(uid);
        List<String> lines = Files.readAllLines(path, encoding.getCharset());

        return String.join("\n", lines);
    }

    public void packTransformedFiles(int uid, OutputStream outputStream) throws TimeoutException, IOException {
        if (!cacheManager.refreshExpire(uid)) {
            throw new TimeoutException();
        }
        Collection<Integer> fids = cacheManager.getFileEncodings(uid).keySet();
        Map<Integer, String> fidToName = cacheManager
                .getFileInfos(uid)
                .stream()
                .collect(Collectors.toMap(
                        FileInfo::getFid,
                        FileInfo::getName));

        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        for (int fid : fids) {
            zipOutputStream.putNextEntry(new ZipEntry(fidToName.get(fid)));
            Path path = filePath.transformedFile(uid, fid);
            Files.copy(path, zipOutputStream);
            zipOutputStream.closeEntry();
        }
    }
}
