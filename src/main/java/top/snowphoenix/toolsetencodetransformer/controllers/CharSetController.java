package top.snowphoenix.toolsetencodetransformer.controllers;

import lombok.Builder;
import lombok.Data;
import lombok.var;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSet;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/charset")
public class CharSetController {
    public CharSetController(CharSetUtil charSetUtil) {
        this.charSetUtil = charSetUtil;
    }

    private final CharSetUtil charSetUtil;

    @Data
    @Builder
    public static class CharSetInfo {
        private int cid;
        private String name;
        private String desc;
    }

    @GetMapping("/list")
    public List<CharSetInfo> list() {
        return Arrays.stream(CharSet.values())
                .map(charSet -> CharSetInfo.builder()
                        .cid(charSet.getCid())
                        .name(charSet.getName())
                        .desc(charSet.getDesc())
                        .build())
                .collect(Collectors.toList())
                ;
    }

    @GetMapping("/info")
    public ResponseEntity<HashMap<String, String>> info(@RequestParam("cid") int cid) {
        var charSet = CharSet.ofCid(cid);
        if (charSet == null) {
            return ResponseEntity.notFound().build();
        }
        var ret = new HashMap<String, String>();
        ret.put("desc", charSet.getDesc());
        var sb = new StringBuilder();
        charSetUtil.getWorker(charSet).printAll(sb);
        ret.put("set", sb.toString());
        return ResponseEntity.ok(ret);
    }
}
