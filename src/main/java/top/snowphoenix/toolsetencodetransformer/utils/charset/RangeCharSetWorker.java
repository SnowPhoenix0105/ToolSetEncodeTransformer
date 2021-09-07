package top.snowphoenix.toolsetencodetransformer.utils.charset;

import lombok.var;

public class RangeCharSetWorker implements CharSetWorker {
    public RangeCharSetWorker(long min, long max) {
        this.min = min;
        this.max = max;
    }

    public long min;
    public long max;
    public String all;

    @Override
    public boolean contains(char c) {
        return min <= c && c <= max;
    }

    @Override
    public void printAll(StringBuilder stringBuilder) {
        if (all == null) {
            var sb = new StringBuilder();
            for (long i = min; i <= max; i++) {
                sb.append((char) i);
            }
            all = sb.toString();
        }
        stringBuilder.append(all);
    }
}
