package top.snowphoenix.toolsetencodetransformer.controllers;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.snowphoenix.toolsetencodetransformer.aop.CurrentUser;
import top.snowphoenix.toolsetencodetransformer.aop.RequireAuthWithLevel;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;
import top.snowphoenix.toolsetencodetransformer.model.SelectedFileInfo;
import top.snowphoenix.toolsetencodetransformer.service.FileService;

import java.io.IOException;
import java.util.ArrayList;


@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    // TODO Constructor

    private FileService fileService;

    @RequireAuthWithLevel(AuthLevel.USER)
    @RequestMapping("/upload")
    public SelectedFileInfo upload(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestParam("files") MultipartFile[] multipartFiles) throws IOException {
        var allFiles = fileService.save(currentUserInfo.getUid(), multipartFiles);

        return SelectedFileInfo.builder().selected(allFiles).unselected(new ArrayList<>()).build();
    }
}
