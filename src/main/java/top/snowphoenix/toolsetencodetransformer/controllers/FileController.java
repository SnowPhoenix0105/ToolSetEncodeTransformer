package top.snowphoenix.toolsetencodetransformer.controllers;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.snowphoenix.toolsetencodetransformer.aop.CurrentUser;
import top.snowphoenix.toolsetencodetransformer.aop.RequireAuthWithLevel;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;
import top.snowphoenix.toolsetencodetransformer.model.SelectedFileInfo;
import top.snowphoenix.toolsetencodetransformer.service.FileService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;


@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private final FileService fileService;

    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    @RequestMapping("/upload")
    public SelectedFileInfo upload(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        var allFiles = fileService.saveFilesForUser(currentUserInfo.getUid(), multipartFiles);

        return SelectedFileInfo.builder().selected(allFiles).unselected(new ArrayList<>()).build();
    }

    @GetMapping("/preview")
    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    public String preview(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestParam("fid") int fid
    ) throws TimeoutException, IOException {
        return fileService.getTransformedFileContent(currentUserInfo.getUid(), fid);
    }

    @GetMapping("/download")
    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    public void download(
            @CurrentUser CurrentUserInfo currentUserInfo,
            HttpServletResponse response

    ) throws IOException, TimeoutException {
        fileService.packTransformedFiles(currentUserInfo.getUid(), response.getOutputStream());

        // Content-Type, see
        // https://www.iana.org/assignments/media-types/application/octet-stream
        response.setHeader("Content-Type", "application/octet-stream");

        // Content-Disposition, see:
        // https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Disposition
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        String filename = timestamp + ".zip";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);
    }
}
