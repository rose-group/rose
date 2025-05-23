/*
 * Copyright © 2025 rosestack.github.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rose.core.function;

import java.util.function.Consumer;

/**
 * A {@link Consumer} that allows for checked exceptions.
 *
 * @author Lukas Eder
 */
@FunctionalInterface
public interface CheckedConsumer<T> {
    /**
     * @see {@link Unchecked#consumer(CheckedConsumer)}
     */
    static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer) {
        return Unchecked.consumer(consumer);
    }

    /**
     * @see {@link Unchecked#consumer(CheckedConsumer, Consumer)}
     */
    static <T> Consumer<T> unchecked(CheckedConsumer<T> consumer, Consumer<Throwable> handler) {
        return Unchecked.consumer(consumer, handler);
    }

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     */
    void accept(T t) throws Exception;
}
