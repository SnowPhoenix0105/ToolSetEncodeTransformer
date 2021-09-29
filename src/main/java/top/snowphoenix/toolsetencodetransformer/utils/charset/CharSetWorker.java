package top.snowphoenix.toolsetencodetransformer.utils.charset;

/***
 * 一个字符集进行判断的工作类接口
 */
public interface CharSetWorker {
    /***
     * 判断一个字符是否在本字符集内
     *
     * @param c 字符
     * @return true表示在字符集内
     */
    boolean contains(char c);

    /***
     * 将字符集中的所有字符打印到StringBuilder中。
     *
     * @param stringBuilder 字符集的字符将输出到此builder中。
     */
    void printAll(StringBuilder stringBuilder);
}
