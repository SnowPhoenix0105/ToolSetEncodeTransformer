package top.snowphoenix.toolsetencodetransformer.service;

import lombok.var;
import org.springframework.stereotype.Service;
import top.snowphoenix.toolsetencodetransformer.config.FileConfig;
import top.snowphoenix.toolsetencodetransformer.manager.CacheManager;
import top.snowphoenix.toolsetencodetransformer.model.Encoding;
import top.snowphoenix.toolsetencodetransformer.utils.charset.CharSetUtil;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@Service
public class EncodingJudgementService {
    // TODO Constructor

    private CharSetUtil charSetUtil;
    private FileConfig fileConfig;
    private CacheManager cacheManager;

    public Map<String, Map<Encoding, Double>> judge(int uid) {
        HashMap<String, Map<Encoding, Double>> ret = new HashMap<>();

        // TODO

        return ret;
    }
}
