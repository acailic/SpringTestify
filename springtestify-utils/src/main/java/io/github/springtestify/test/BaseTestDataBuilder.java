package io.github.springtestify.test;

public abstract class BaseTestDataBuilder<T, SELF extends BaseTestDataBuilder<T, SELF>> {
    
    /**
     * Build the test object with the current builder state
     * @return Created test object
     */
    public abstract T build();
    
    /**
     * Get this builder as its concrete type
     * @return This builder cast to its concrete type
     */
    @SuppressWarnings("unchecked")
    protected SELF self() {
        return (SELF) this;
    }
}