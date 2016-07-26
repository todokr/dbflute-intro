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
package org.dbflute.intro.app.logic.dfprop;

import java.io.File;
import java.net.MalformedURLException;
import java.net.ProxySelector;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.dbflute.intro.app.logic.client.DatabaseParam;
import org.dbflute.intro.app.logic.engine.EngineDownloadLogic;
import org.dbflute.intro.mylasta.exception.DatabaseConnectionException;
import org.dbflute.util.DfStringUtil;

/**
 * @author p1us2er0
 * @author jflute
 */
public class TestConnectionLogic {

    // ===================================================================================
    //                                                                     Test Connection
    //                                                                     ===============
    public void testConnection(String jdbcDriverJarPath, String dbfluteVersion, String jdbcDriver, DatabaseParam databaseParam) {
        ProxySelector proxySelector = ProxySelector.getDefault();
        ProxySelector.setDefault(null);
        Connection connection = null;

        try {
            List<URL> urls = new ArrayList<URL>();
            if (DfStringUtil.is_Null_or_Empty(jdbcDriverJarPath)) {
                File mydbfluteDir = new File(String.format(EngineDownloadLogic.MY_DBFLUTE_PATH, dbfluteVersion), "lib");
                if (mydbfluteDir.isDirectory()) {
                    for (File file : FileUtils.listFiles(mydbfluteDir, FileFilterUtils.suffixFileFilter(".jar"), null)) {
                        urls.add(file.toURI().toURL());
                    }
                }
            } else {
                URL fileUrl = new File(jdbcDriverJarPath).toURI().toURL();
                urls.add(fileUrl);
            }

            URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[urls.size()]));

            @SuppressWarnings("unchecked")
            Class<Driver> driverClass = (Class<Driver>) loader.loadClass(jdbcDriver);
            Driver driver = driverClass.newInstance();

            Properties info = new Properties();
            String user = databaseParam.getUser();
            if (DfStringUtil.is_NotNull_and_NotEmpty(user)) {
                info.put("user", user);
            }
            String password = databaseParam.getPassword();
            if (DfStringUtil.is_NotNull_and_NotEmpty(password)) {
                info.put("password", password);
            }
            connection = driver.connect(databaseParam.getUrl(), info);
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException | SQLException e) {
            throw new DatabaseConnectionException(e.getMessage(), e);
        } finally {
            ProxySelector.setDefault(proxySelector);

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ignored) {}
            }
        }
    }
}