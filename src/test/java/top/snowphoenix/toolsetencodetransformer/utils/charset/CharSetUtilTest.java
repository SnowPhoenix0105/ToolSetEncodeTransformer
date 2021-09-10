package top.snowphoenix.toolsetencodetransformer.utils.charset;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class CharSetUtilTest {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void latinContainsSubSets() {
        CharSetUtil charSetUtil = applicationContext.getBean(CharSetUtil.class);
        CharSetWorker top = charSetUtil.getWorker(CharSet.COMMON_LATIN);
        var commonLatinStringBuilder = new StringBuilder();
        top.printAll(commonLatinStringBuilder);

        var subsetStringBuilder = new StringBuilder();
        for (CharSet charSet: CharSetUtil.COMMON_LATIN_CONTAINS) {
            charSetUtil.getWorker(charSet).printAll(subsetStringBuilder);
        }

        Set<Integer> latinSet = commonLatinStringBuilder
                .toString()
                .chars()
                .boxed()
                .collect(Collectors.toSet());
        Set<Integer> subSet = subsetStringBuilder
                .toString()
                .chars()
                .boxed()
                .collect(Collectors.toSet());

        log.info("in latin-set not in subs-set: " + latinSet
                .stream()
                .filter(c -> !subSet.contains(c))
                .collect(Collectors.toList()));

        log.info("in subs-set not in latin-set: " + subSet
                .stream()
                .filter(c -> !latinSet.contains(c))
                .collect(Collectors.toList()));
        assertEquals(latinSet, subSet);
    }
}