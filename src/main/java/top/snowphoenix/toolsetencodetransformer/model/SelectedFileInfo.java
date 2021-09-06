package top.snowphoenix.toolsetencodetransformer.model;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;

@Data
@Builder
public class SelectedFileInfo {
    private ArrayList<FileInfo> selected;
    private ArrayList<FileInfo> unselected;
}
