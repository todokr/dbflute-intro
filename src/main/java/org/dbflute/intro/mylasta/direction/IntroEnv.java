/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.dbflute.intro.mylasta.direction;

import org.lastaflute.core.direction.ObjectiveConfig;
import org.lastaflute.core.direction.exception.ConfigPropertyNotFoundException;

/**
 * @author FreeGen
 */
public interface IntroEnv {

    /** The key of the configuration. e.g. hot */
    String lasta_di_SMART_DEPLOY_MODE = "lasta_di.smart.deploy.mode";

    /** The key of the configuration. e.g. true */
    String DEVELOPMENT_HERE = "development.here";

    /** The key of the configuration. e.g. Local Development */
    String ENVIRONMENT_TITLE = "environment.title";

    /** The key of the configuration. e.g. false */
    String FRAMEWORK_DEBUG = "framework.debug";

    /** The key of the configuration. e.g. 0 */
    String TIME_ADJUST_TIME_MILLIS = "time.adjust.time.millis";

    /** The key of the configuration. e.g. debug */
    String LOG_LEVEL = "log.level";

    /** The key of the configuration. e.g. debug */
    String LOG_CONSOLE_LEVEL = "log.console.level";

    /** The key of the configuration. e.g. /tmp/lastaflute/dbfluteintro */
    String LOG_FILE_BASEDIR = "log.file.basedir";

    /**
     * Get the value of property as {@link String}.
     * @param propertyKey The key of the property. (NotNull)
     * @return The value of found property. (NotNull: if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    String get(String propertyKey);

    /**
     * Is the property true?
     * @param propertyKey The key of the property which is boolean type. (NotNull)
     * @return The determination, true or false. (if not found, exception)
     * @throws ConfigPropertyNotFoundException When the property is not found.
     */
    boolean is(String propertyKey);

    /**
     * Get the value for the key 'lasta_di.smart.deploy.mode'. <br>
     * The value is, e.g. hot <br>
     * comment: The mode of Lasta Di's smart-deploy, should be cool in production (e.g. hot, cool, warm)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLastaDiSmartDeployMode();

    /**
     * Get the value for the key 'development.here'. <br>
     * The value is, e.g. true <br>
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getDevelopmentHere();

    /**
     * Is the property for the key 'development.here' true? <br>
     * The value is, e.g. true <br>
     * comment: Is development environment here? (used for various purpose, you should set false if unknown)
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isDevelopmentHere();

    /**
     * Get the value for the key 'environment.title'. <br>
     * The value is, e.g. Local Development <br>
     * comment: The title of environment (e.g. local or integartion or production)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getEnvironmentTitle();

    /**
     * Get the value for the key 'framework.debug'. <br>
     * The value is, e.g. false <br>
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getFrameworkDebug();

    /**
     * Is the property for the key 'framework.debug' true? <br>
     * The value is, e.g. false <br>
     * comment: Does it enable the Framework internal debug? (true only when emergency)
     * @return The determination, true or false. (if not found, exception but basically no way)
     */
    boolean isFrameworkDebug();

    /**
     * Get the value for the key 'time.adjust.time.millis'. <br>
     * The value is, e.g. 0 <br>
     * comment: The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getTimeAdjustTimeMillis();

    /**
     * Get the value for the key 'time.adjust.time.millis' as {@link Long}. <br>
     * The value is, e.g. 0 <br>
     * comment: The milliseconds for (relative or absolute) adjust time (set only when test) @LongType *dynamic in development
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     * @throws NumberFormatException When the property is not long.
     */
    Long getTimeAdjustTimeMillisAsLong();

    /**
     * Get the value for the key 'log.level'. <br>
     * The value is, e.g. debug <br>
     * comment: The log level for logback
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLogLevel();

    /**
     * Get the value for the key 'log.console.level'. <br>
     * The value is, e.g. debug <br>
     * comment: The log console level for logback
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLogConsoleLevel();

    /**
     * Get the value for the key 'log.file.basedir'. <br>
     * The value is, e.g. /tmp/lastaflute/dbfluteintro <br>
     * comment: The log file basedir
     * @return The value of found property. (NotNull: if not found, exception but basically no way)
     */
    String getLogFileBasedir();

    /**
     * The simple implementation for configuration.
     * @author FreeGen
     */
    public static class SimpleImpl extends ObjectiveConfig implements IntroEnv {

        /** The serial version UID for object serialization. (Default) */
        private static final long serialVersionUID = 1L;

        public String getLastaDiSmartDeployMode() {
            return get(IntroEnv.lasta_di_SMART_DEPLOY_MODE);
        }

        public String getDevelopmentHere() {
            return get(IntroEnv.DEVELOPMENT_HERE);
        }

        public boolean isDevelopmentHere() {
            return is(IntroEnv.DEVELOPMENT_HERE);
        }

        public String getEnvironmentTitle() {
            return get(IntroEnv.ENVIRONMENT_TITLE);
        }

        public String getFrameworkDebug() {
            return get(IntroEnv.FRAMEWORK_DEBUG);
        }

        public boolean isFrameworkDebug() {
            return is(IntroEnv.FRAMEWORK_DEBUG);
        }

        public String getTimeAdjustTimeMillis() {
            return get(IntroEnv.TIME_ADJUST_TIME_MILLIS);
        }

        public Long getTimeAdjustTimeMillisAsLong() {
            return getAsLong(IntroEnv.TIME_ADJUST_TIME_MILLIS);
        }

        public String getLogLevel() {
            return get(IntroEnv.LOG_LEVEL);
        }

        public String getLogConsoleLevel() {
            return get(IntroEnv.LOG_CONSOLE_LEVEL);
        }

        public String getLogFileBasedir() {
            return get(IntroEnv.LOG_FILE_BASEDIR);
        }
    }
}