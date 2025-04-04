package io.github.springtestify.annotation;

import io.github.springtestify.config.SpringTestifyAutoConfiguration;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.test.context.TestExecutionListeners;
import com.github.seregamorph.springtest.SmartDirtiesContextTestExecutionListener;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Import(SpringTestifyAutoConfiguration.class)
@TestExecutionListeners(
    listeners = SmartDirtiesContextTestExecutionListener.class,
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public @interface CrudControllerTest {

    @AliasFor(annotation = SpringBootTest.class, attribute = "properties")
    String[] properties() default {};

    /**
     * Base path for the controller endpoints
     */
    String path();

    /**
     * Whether to enable method ordering (default: true)
     */
    boolean enableMethodOrdering() default true;

    /**
     * Whether to enable smart context caching (default: true)
     */
    boolean enableSmartContextCaching() default true;

    /**
     * Whether to auto-configure MockMvc (default: true)
     */
    boolean enableAutoMockMvc() default true;

    /**
     * Whether to auto-configure ObjectMapper (default: true)
     */
    boolean enableAutoObjectMapper() default true;
}
