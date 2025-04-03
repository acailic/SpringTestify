package io.github.springtestify.service.test;

import io.github.springtestify.core.annotation.ServiceTest;
import io.github.springtestify.service.mock.MockFactory;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base class for service tests using SpringTestify.
 * <p>
 * Provides convenient methods for verifying interactions with mocks
 * and accessing the service under test. Classes extending this class 
 * should be annotated with {@link ServiceTest}.
 * <p>
 * Example usage:
 * <pre>
 * &#064;ServiceTest(UserService.class)
 * public class UserServiceTest extends AbstractServiceTest {
 *     
 *     &#064;Autowired
 *     private UserService userService;
 *     
 *     &#064;Autowired
 *     private UserRepository userRepository; // This will be automatically mocked
 *     
 *     &#064;Test
 *     void shouldFindUserById() {
 *         // Given
 *         User expectedUser = new User("1", "John");
 *         when(userRepository.findById("1")).thenReturn(Optional.of(expectedUser));
 *         
 *         // When
 *         User user = userService.getUserById("1");
 *         
 *         // Then
 *         assertThat(user).isEqualTo(expectedUser);
 *         verify(userRepository).findById("1");
 *         
 *         // Or using the helper methods:
 *         verifyMock(UserRepository.class).findById("1");
 *     }
 * }
 * </pre>
 */
public abstract class AbstractServiceTest {

    @Autowired
    protected MockFactory mockFactory;

    /**
     * Gets a mock for the specified class.
     *
     * @param <T> the mock type
     * @param mockClass the class of the mock
     * @return the mock
     */
    protected <T> T getMock(Class<T> mockClass) {
        T mock = mockFactory.getMock(mockClass);
        if (mock == null) {
            throw new IllegalStateException("No mock found for " + mockClass.getName());
        }
        return mock;
    }

    /**
     * Verifies interactions with the mock of the specified class.
     *
     * @param <T> the mock type
     * @param mockClass the class of the mock
     * @return the mock for verification
     */
    protected <T> T verifyMock(Class<T> mockClass) {
        return Mockito.verify(getMock(mockClass));
    }

    /**
     * Verifies interactions with the mock of the specified class using the specified verification mode.
     *
     * @param <T> the mock type
     * @param mockClass the class of the mock
     * @param mode the verification mode
     * @return the mock for verification
     */
    protected <T> T verifyMock(Class<T> mockClass, VerificationMode mode) {
        return Mockito.verify(getMock(mockClass), mode);
    }

    /**
     * Resets all mocks after each test.
     * <p>
     * This ensures that interactions with mocks don't carry over between tests.
     */
    @AfterEach
    void resetMocks() {
        mockFactory.reset();
    }
}