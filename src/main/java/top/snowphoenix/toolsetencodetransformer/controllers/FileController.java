package top.snowphoenix.toolsetencodetransformer.controllers;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.snowphoenix.toolsetencodetransformer.aop.CurrentUser;
import top.snowphoenix.toolsetencodetransformer.aop.RequireAuthWithLevel;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;
import top.snowphoenix.toolsetencodetransformer.model.SelectedFileInfo;
import top.snowphoenix.toolsetencodetransformer.service.FileService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    private final FileService fileService;

    @RequestMapping("/echo_info")
    public List<Map<String, String>> echoInfo(
            @RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        ArrayList<Map<String, String>> ret = new ArrayList<>();
        for (var file : multipartFiles) {
            HashMap<String, String> map = new HashMap<>();
            map.put("name", file.getName());
            map.put("originFileName", file.getOriginalFilename());
            ret.add(map);
        }
        log.info(ret.toString());
        return ret;
    }

    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    @RequestMapping("/upload")
    public SelectedFileInfo upload(
             @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestParam("file") MultipartFile[] multipartFiles) throws IOException {
        int uid = currentUserInfo.getUid();
        var allFiles = fileService.saveFilesForUser(uid, multipartFiles);

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
            HttpServletResponse response,
            HttpServletRequest request
    ) throws IOException, TimeoutException {
        // Content-Type, see
        // https://www.iana.org/assignments/media-types/application/octet-stream
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM.toString());
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        // Content-Disposition, see:
        // https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Content-Disposition
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        String filename = timestamp + ".zip";
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        fileService.packTransformedFiles(currentUserInfo.getUid(), response.getOutputStream());
    }
}
