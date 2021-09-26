package top.snowphoenix.toolsetencodetransformer.controllers;

import lombok.Builder;
import lombok.Data;
import lombok.var;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.snowphoenix.toolsetencodetransformer.aop.CurrentUser;
import top.snowphoenix.toolsetencodetransformer.aop.RequireAuthWithLevel;
import top.snowphoenix.toolsetencodetransformer.exception.TimeoutException;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.service.EncodingService;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/encoding")
public class EncodingController {
    public EncodingController(EncodingService encodingService) {
        this.encodingService = encodingService;
    }

    private final EncodingService encodingService;

    @Data
    @Builder
    public static class TransformBaseInfo {
        private ArrayList<Integer> selected;
        // private ArrayList<Integer> unselected;
        private ArrayList<Integer> charset;
        private String target;
    }

    @PostMapping("/baseinfo")
    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    public Map<Integer, String> baseInfo(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestBody TransformBaseInfo transformBaseInfo) throws TimeoutException, IOException {
        int uid = currentUserInfo.getUid();
        Encoding targetEncoding = Encoding.parse(transformBaseInfo.getTarget());

        var fidEncodingMap = encodingService.judgeAndTransform(
                uid,
                transformBaseInfo.getCharset().stream()
                        .map(CharSet::ofCid)
                        .collect(Collectors.toSet()),
                transformBaseInfo.getSelected(),
                targetEncoding);

        return fidEncodingMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().getName()
                ));
    }

    @Data
    @Builder
    private static class ModifyInfo {
        private int fid;
        private String encoding;
    }

    @RequireAuthWithLevel(AuthLevel.PASSERBY)
    @PostMapping("/modify")
    public void modify(
            @CurrentUser CurrentUserInfo currentUserInfo,
            @RequestBody ModifyInfo modifyInfo
    ) throws TimeoutException, IOException {
        int uid = currentUserInfo.getUid();
        int fid = modifyInfo.getFid();
        Encoding encoding = Encoding.parse(modifyInfo.getEncoding());

        encodingService.modifyAndTransform(uid, fid, encoding);
    }
}
