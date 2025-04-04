package io.github.springtestify.test;

import io.github.springtestify.annotation.ScenarioAction;
import io.github.springtestify.annotation.TestScenario;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Method;

/**
 * JUnit extension that handles scenario test execution.
 * Automatically executes tests based on annotations.
 */
public class ScenarioTestExecutionListener implements TestInstancePostProcessor, InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation,
                                  ReflectiveInvocationContext<Method> invocationContext,
                                  ExtensionContext extensionContext) throws Throwable {
        Method method = invocationContext.getExecutable();
        Object testInstance = extensionContext.getRequiredTestInstance();

        if (method.isAnnotationPresent(TestScenario.class) && method.isAnnotationPresent(ScenarioAction.class)) {
            if (testInstance instanceof AbstractScenarioTest) {
                ((AbstractScenarioTest<?>) testInstance).executeScenario(method);
            } else {
                throw new IllegalStateException("Test class must extend AbstractScenarioTest");
            }
        } else {
            invocation.proceed();
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
        // No additional processing needed
    }
}
