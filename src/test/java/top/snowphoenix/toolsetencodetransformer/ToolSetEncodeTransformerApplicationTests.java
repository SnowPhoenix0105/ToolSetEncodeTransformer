package top.snowphoenix.toolsetencodetransformer;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;
import java.util.Map;

@SpringBootTest
@Slf4j
class ToolSetEncodeTransformerApplicationTests {
    @Autowired
    ApplicationContext applicationContext;

    @Test
    void contextLoads() {
    }

    private void printBeansWithAnnotation(Class<? extends Annotation> clz) {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(clz);
        log.info('@' + clz.getSimpleName() + ":\t" + beans.keySet());
    }

    @Test
    void printAllBeans() {
        printBeansWithAnnotation(Component.class);
        printBeansWithAnnotation(Repository.class);
        printBeansWithAnnotation(Controller.class);
        printBeansWithAnnotation(RestController.class);
        printBeansWithAnnotation(Service.class);
    }
}
