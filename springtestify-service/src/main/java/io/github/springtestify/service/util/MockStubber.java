package io.github.springtestify.service.util;

import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * A fluent API for stubbing mock behavior.
 * <p>
 * This class provides a more readable way to set up mock behavior
 * than the standard Mockito API.
 *
 * @param <T> the type being mocked
 * @param <R> the return type of the method being stubbed
 */
public class MockStubber<T, R> {

    private final T mock;
    private final OngoingStubbing<R> stubbing;

    /**
     * Creates a new MockStubber for the specified mock and stubbing.
     *
     * @param mock the mock being stubbed
     * @param stubbing the ongoing stubbing
     */
    private MockStubber(T mock, OngoingStubbing<R> stubbing) {
        this.mock = mock;
        this.stubbing = stubbing;
    }

    /**
     * Sets up the mock to return the specified value.
     *
     * @param returnValue the value to return
     * @return the mock
     */
    public T returns(R returnValue) {
        stubbing.thenReturn(returnValue);
        return mock;
    }

    /**
     * Sets up the mock to return the specified values in sequence.
     *
     * @param returnValues the values to return in sequence
     * @return the mock
     */
    @SafeVarargs
    public final T returnsSequence(R... returnValues) {
        stubbing.thenReturn(returnValues[0], Arrays.copyOfRange(returnValues, 1, returnValues.length));
        return mock;
    }

    /**
     * Sets up the mock to return the values from the specified list in sequence.
     *
     * @param returnValues the list of values to return in sequence
     * @return the mock
     */
    public T returnsFromList(List<R> returnValues) {
        if (returnValues.isEmpty()) {
            throw new IllegalArgumentException("Return values list cannot be empty");
        }

        R firstValue = returnValues.get(0);
        if (returnValues.size() > 1) {
            @SuppressWarnings("unchecked")
            R[] remainingValues = (R[]) returnValues.subList(1, returnValues.size()).toArray();
            stubbing.thenReturn(firstValue, remainingValues);
        } else {
            stubbing.thenReturn(firstValue);
        }

        return mock;
    }

    /**
     * Sets up the mock to throw the specified exception.
     *
     * @param throwable the exception to throw
     * @return the mock
     */
    public T throws_(Throwable throwable) {
        stubbing.thenThrow(throwable);
        return mock;
    }

    /**
     * Sets up the mock to answer with the specified answer.
     *
     * @param answer the answer
     * @return the mock
     */
    public T answers(Answer<R> answer) {
        stubbing.thenAnswer(answer);
        return mock;
    }

    /**
     * Creates a new MockStubber for the specified method call.
     *
     * @param <T> the type being mocked
     * @param <R> the return type of the method being stubbed
     * @param mock the mock
     * @param methodCall a function that calls the method to stub
     * @return a new MockStubber
     */
    public static <T, R> MockStubber<T, R> when(T mock, Function<T, R> methodCall) {
        R result = methodCall.apply(mock);
        OngoingStubbing<R> stubbing = Mockito.when(result);
        return new MockStubber<>(mock, stubbing);
    }
}
