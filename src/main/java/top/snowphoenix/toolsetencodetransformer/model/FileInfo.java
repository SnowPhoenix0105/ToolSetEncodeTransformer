package top.snowphoenix.toolsetencodetransformer.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileInfo {
    private String name;
    private int fid;
}
