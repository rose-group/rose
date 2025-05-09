/*
 * Copyright © 2025 rose-group.github.io
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
package io.github.rose.core.groovy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.groovy.util.Maps;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This is {@link GroovyShellScriptTests}.
 */
@Tag("Groovy")
@Slf4j
@Execution(ExecutionMode.SAME_THREAD)
class GroovyShellScriptTests {

    @RequiredArgsConstructor
    private static final class RunnableScript implements Runnable {

        private final Map<String, Object> attributes;

        private final GroovyShellScript shellScript;

        private final Object expectedAttribute;

        @Override
        public void run() {
            try {
                shellScript.setBinding(Maps.of("attributes", attributes));
                List returnValue = shellScript.execute(ArrayUtils.EMPTY_OBJECT_ARRAY, List.class);
                assertEquals(1, returnValue.size());
                assertEquals(expectedAttribute, returnValue.get(0));
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Nested
    class StaticCompilationTests {

        @Test
        void verifyOperation() throws Exception {
            String script = "   def logger = (Logger) binding.getVariable('log') \n"
                + "   def attributes = (Map) binding.getVariable('attributes') \n"
                + "   logger.info('Attributes: {}', attributes) \n"
                + "   if ((attributes.get('entitlement') as List).contains('admin')){ \n"
                + "       return [(attributes['uid'] as List).get(0).toString().toUpperCase()]\n" + "   } else{ \n"
                + "       return attributes['identifier'] as List \n" + "   } \n";
            GroovyShellScript shellScript = new GroovyShellScript(script.trim());

            Map<String, Object> attributes1 = new HashMap();
            attributes1.put("entitlement", Collections.singletonList("admin"));
            attributes1.put("uid", Collections.singletonList("casadmin"));
            attributes1.put("identifier", Collections.singletonList("1984"));
            new RunnableScript(attributes1, shellScript, "CASADMIN").run();
        }
    }

    @Nested
    class ConcurrentTests {

        @Test
        void verifyOperation() {
            String script = "   def attributes = (Map) binding.getVariable('attributes') \n"
                + "   if ((attributes.get('entitlement') as List).contains('admin')){ \n"
                + "       return [(attributes['uid'] as List).get(0).toString().toUpperCase()]\n" + "   } else{ \n"
                + "       return attributes['identifier'] as List \n" + "   } \n";

            GroovyShellScript shellScript = new GroovyShellScript(script.trim());

            Map<String, Object> attributes1 = new HashMap();
            attributes1.put("entitlement", Collections.singletonList("admin"));
            attributes1.put("uid", Collections.singletonList("casadmin"));
            attributes1.put("identifier", Collections.singletonList("1984"));

            Map<String, Object> attributes2 = new HashMap();
            attributes2.put("entitlement", Collections.singletonList("user"));
            attributes2.put("uid", Collections.singletonList("casuser"));
            attributes2.put("identifier", Collections.singletonList("dev-pwd"));

            AtomicBoolean testHasFailed = new AtomicBoolean();
            List<Thread> threads = new ArrayList();
            for (int i = 1; i <= 10; i++) {
                RunnableScript runnable = i % 2 == 0
                    ? new RunnableScript(attributes1, shellScript, "CASADMIN")
                    : new RunnableScript(attributes2, shellScript, "dev-pwd");
                Thread thread = new Thread(runnable);
                thread.setName("Thread-" + i);
                thread.setUncaughtExceptionHandler((t, e) -> {
                    log.error(e.getMessage(), e);
                    testHasFailed.set(true);
                });
                threads.add(thread);
                thread.start();
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (final Throwable e) {
                    fail(e);
                }
            }
            if (testHasFailed.get()) {
                fail("Test failed");
            }
        }
    }

    @Nested
    class DefaultTests {

        @Test
        void verifyExec() {
            try (GroovyShellScript shell = new GroovyShellScript("println 'test'")) {
                assertNull(shell.getGroovyScript());
                assertNotNull(shell.getScript());

                assertDoesNotThrow(() -> shell.execute(ArrayUtils.EMPTY_OBJECT_ARRAY));
                assertNotNull(shell.toString());
            }
        }

        @Test
        void verifyUnknownBadScript() {
            try (GroovyShellScript shell = new GroovyShellScript("###$$@@@!!!***&&&")) {
                assertDoesNotThrow(() -> {
                    shell.execute(ArrayUtils.EMPTY_OBJECT_ARRAY);
                    shell.execute("run", Void.class, ArrayUtils.EMPTY_OBJECT_ARRAY);
                });
            }
        }
    }
}
