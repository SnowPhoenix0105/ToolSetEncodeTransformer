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

import java.io.BufferedOutputStream;
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

    /***
     * 将文件保存到文件系统中，并为每个文件生成唯一的fid来进行标识。
     *
     * @param workDirPath 工作路径
     * @param files 需要保存的文件
     * @return 文件名和文件fid的列表
     * @throws IOException 发生了io错误
     */
    private ArrayList<FileInfo> saveFiles(Path workDirPath, MultipartFile[] files) throws IOException {
        filePath.ensureAndClearDir(workDirPath);

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
            filePath.ensureAndClearDir(workDirPath);
            throw e;
        }
        return fileInfos;
    }

    /***
     * 为用户保存文件，并为每个文件创建唯一的fid用于表示。
     *
     * @param uid 用户ID
     * @param files 需要保存的文件
     * @return 文件名和文件fid的列表
     * @throws IOException 发生了io错误
     */
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

    /***
     * 获取转码后的文件的内容
     *
     * @param uid 用户ID
     * @param fid 文件ID
     * @return 文件的内容
     * @throws TimeoutException 会话超时，指缓存中的用户信息过期
     * @throws IOException 发生了io错误
     */
    public String getTransformedFileContent(int uid, int fid) throws TimeoutException, IOException {
        if (!cacheManager.refreshExpire(uid)) {
            throw new TimeoutException();
        }
        Path path = filePath.transformedFile(uid, fid);
        Encoding encoding = cacheManager.getTargetEncoding(uid);
        List<String> lines = Files.readAllLines(path, encoding.getCharset());

        return String.join("\n", lines);
    }

    /***
     * 将用户的所有转码后的文件通过单个压缩包的方式保存到指定stream中。
     *
     * @param uid 用户ID
     * @param outputStream 输出的流
     * @throws TimeoutException 会话超时，指缓存中的用户信息过期
     * @throws IOException 发生了io错误
     */
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

        ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));
        zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
        for (int fid : fids) {
            zipOutputStream.putNextEntry(new ZipEntry(fidToName.get(fid)));
            Path path = filePath.transformedFile(uid, fid);
            Files.copy(path, zipOutputStream);
            zipOutputStream.closeEntry();
        }
        zipOutputStream.flush();
        zipOutputStream.close();
    }
}
