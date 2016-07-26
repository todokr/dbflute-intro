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
package org.dbflute.intro.app.logic.client;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.FileUtils;
import org.dbflute.helper.mapstring.MapListString;
import org.dbflute.intro.app.logic.intro.IntroPhysicalLogic;
import org.dbflute.intro.mylasta.util.ZipUtil;

/**
 * @author p1us2er0
 * @author jflute
 */
public class ClientUpdateLogic {

    // ===================================================================================
    //                                                                Create/Update Client
    //                                                                ====================
    public void createClient(ClientParam clientParam) {
        _createClient(clientParam, false);
    }

    public void updateClient(ClientParam clientParam) {
        _createClient(clientParam, true);
    }

    private void _createClient(ClientParam clientParam, boolean update) {
        final File dbfluteClientDir = new File(IntroPhysicalLogic.BASE_DIR_PATH, "dbflute_" + clientParam.getProject());
        final String dbfluteVersionExpression = "dbflute-" + clientParam.getDbfluteVersion();
        final File mydbflutePureFile = new File(IntroPhysicalLogic.BASE_DIR_PATH, "/mydbflute");
        if (!dbfluteClientDir.exists()) {
            if (update) {
                throw new RuntimeException("already deleted.");
            }
            final String extractDirectoryBase = mydbflutePureFile.getAbsolutePath() + "/" + dbfluteVersionExpression;
            final String templateZipFileName = extractDirectoryBase + "/etc/client-template/dbflute_dfclient.zip";
            ZipUtil.decrypt(templateZipFileName, IntroPhysicalLogic.BASE_DIR_PATH);
            new File(IntroPhysicalLogic.BASE_DIR_PATH, "dbflute_dfclient").renameTo(dbfluteClientDir);
        } else {
            if (!update) {
                throw new RuntimeException("already exists.");
            }
        }

        try {
            List<String> dfpropFileList = new ArrayList<String>();
            dfpropFileList.add("basicInfoMap.dfprop");
            dfpropFileList.add("databaseInfoMap.dfprop");
            dfpropFileList.add("documentMap.dfprop");
            dfpropFileList.add("littleAdjustmentMap.dfprop");
            dfpropFileList.add("outsideSqlMap.dfprop");
            dfpropFileList.add("replaceSchemaMap.dfprop");

            try {
                for (String dfpropFile : dfpropFileList) {
                    File file = new File(dbfluteClientDir, "dfprop/" + dfpropFile);
                    if (!file.exists()) {
                        file = new File(dbfluteClientDir, "dfprop/" + dfpropFile.replace("Map.dfprop", "DefinitionMap.dfprop"));
                    }
                    URL url = ClassLoader.getSystemResource("dfprop/" + dfpropFile.replace(".dfprop", "+.dfprop"));
                    FileUtils.copyURLToFile(url, new File(dbfluteClientDir, "dfprop/" + file.getName().replace(".dfprop", "+.dfprop")));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<File, Map<String, Object>> fileMap = new LinkedHashMap<File, Map<String, Object>>();

            Map<String, Object> replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("MY_PROJECT_NAME=dfclient", "MY_PROJECT_NAME=" + clientParam.getProject());
            fileMap.put(new File(dbfluteClientDir, "/_project.bat"), replaceMap);
            fileMap.put(new File(dbfluteClientDir, "/_project.sh"), replaceMap);

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("torque.project = dfclient", "torque.project = " + clientParam.getProject());
            fileMap.put(new File(dbfluteClientDir, "/build.properties"), replaceMap);

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@database@", clientParam.getDatabase());
            replaceMap.put("@targetLanguage@", clientParam.getTargetLanguage());
            replaceMap.put("@targetContainer@", clientParam.getTargetContainer());
            replaceMap.put("@packageBase@", clientParam.getPackageBase());
            fileMap.put(new File(dbfluteClientDir, "/dfprop/basicInfoMap+.dfprop"), replaceMap);

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("{Please write your setting! at './dfprop/databaseInfoMap.dfprop'}", "");
            fileMap.put(new File(dbfluteClientDir, "/dfprop/databaseInfoMap.dfprop"), replaceMap);

            String schema = clientParam.getDatabaseParam().getSchema();
            schema = schema == null ? "" : schema;
            String[] schemaList = schema.split(",");
            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@driver@", escapeControlMark(clientParam.getJdbcDriver()));
            replaceMap.put("@url@", escapeControlMark(clientParam.getDatabaseParam().getUrl()));
            replaceMap.put("@schema@", escapeControlMark(schemaList[0].trim()));
            replaceMap.put("@user@", escapeControlMark(clientParam.getDatabaseParam().getUser()));
            replaceMap.put("@password@", escapeControlMark(clientParam.getDatabaseParam().getPassword()));
            StringBuilder builder = new StringBuilder();
            for (int i = 1; i < schemaList.length; i++) {
                builder.append("            ; " + escapeControlMark(schemaList[i].trim())
                        + " = map:{objectTypeTargetList=list:{TABLE; VIEW; SYNONYM}}\n");
            }

            replaceMap.put("@additionalSchema@", builder.toString().replaceAll("\n$", ""));
            fileMap.put(new File(dbfluteClientDir, "/dfprop/databaseInfoMap+.dfprop"), replaceMap);

            OptionParam optionParam = clientParam.getOptionParam();

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@isDbCommentOnAliasBasis@", optionParam.isDbCommentOnAliasBasis());
            replaceMap.put("@aliasDelimiterInDbComment@", optionParam.getAliasDelimiterInDbComment());
            replaceMap.put("@isCheckColumnDefOrderDiff@", optionParam.isCheckColumnDefOrderDiff());
            replaceMap.put("@isCheckDbCommentDiff@", optionParam.isCheckDbCommentDiff());
            replaceMap.put("@isCheckProcedureDiff@", optionParam.isCheckProcedureDiff());
            fileMap.put(new File(dbfluteClientDir, "/dfprop/documentMap+.dfprop"), replaceMap);

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@isGenerateProcedureParameterParam@", optionParam.isGenerateProcedureParameterBean());
            replaceMap.put("@procedureSynonymHandlingType@", optionParam.getProcedureSynonymHandlingType());
            fileMap.put(new File(dbfluteClientDir, "/dfprop/outsideSqlMap+.dfprop"), replaceMap);

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@driver@", escapeControlMark(clientParam.getJdbcDriver()));
            replaceMap.put("@url@", escapeControlMark(clientParam.getSystemUserDatabaseParam().getUrl()));
            replaceMap.put("@schema@", escapeControlMark(clientParam.getSystemUserDatabaseParam().getSchema()));
            replaceMap.put("@user@", escapeControlMark(clientParam.getSystemUserDatabaseParam().getUser()));
            replaceMap.put("@password@", escapeControlMark(clientParam.getSystemUserDatabaseParam().getPassword()));
            fileMap.put(new File(dbfluteClientDir, "/dfprop/replaceSchemaMap+.dfprop"), replaceMap);

            replaceFile(fileMap, false);

            fileMap = new LinkedHashMap<File, Map<String, Object>>();

            replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("((?:set|export) DBFLUTE_HOME=[^-]*-)(.*)", "$1" + clientParam.getDbfluteVersion());
            fileMap.put(new File(dbfluteClientDir, "/_project.bat"), replaceMap);
            fileMap.put(new File(dbfluteClientDir, "/_project.sh"), replaceMap);

            replaceFile(fileMap, true);

            if (clientParam.getJdbcDriverJarPath() != null && !clientParam.getJdbcDriverJarPath().equals("")) {
                File extLibDir = new File(dbfluteClientDir, "extlib");
                File jarFile = new File(clientParam.getJdbcDriverJarPath());
                File jarFileOld = new File(extLibDir, jarFile.getName());

                boolean flg = true;
                if (jarFileOld.exists()) {
                    if (jarFile.getCanonicalPath().equals(jarFileOld.getCanonicalPath())) {
                        flg = false;
                    }
                }

                if (flg) {
                    try {
                        for (File file : FileUtils.listFiles(extLibDir, new String[] { ".jar" }, false)) {
                            file.delete();
                        }

                        FileUtils.copyFileToDirectory(new File(clientParam.getJdbcDriverJarPath()), extLibDir);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            createSchemaSyncCheck(clientParam);
        } catch (Exception e) {
            try {
                FileUtils.deleteDirectory(dbfluteClientDir);
            } catch (IOException ignored) {}
            throw new RuntimeException(e);
        }
    }

    private void createSchemaSyncCheck(ClientParam clientParam) {
        final File dbfluteClientDir = new File(IntroPhysicalLogic.BASE_DIR_PATH, "dbflute_" + clientParam.getProject());
        URL schemaSyncCheckURL = ClassLoader.getSystemResource("dfprop/documentMap+schemaSyncCheck.dfprop");

        for (Entry<String, DatabaseParam> entry : clientParam.getSchemaSyncCheckMap().entrySet()) {
            final File dfpropEnvDir = new File(dbfluteClientDir, "dfprop/schemaSyncCheck_" + entry.getKey());
            dfpropEnvDir.mkdir();

            File documentMapFile = new File(dfpropEnvDir, "documentMap+.dfprop");
            try {
                FileUtils.copyURLToFile(schemaSyncCheckURL, documentMapFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Map<File, Map<String, Object>> fileMap = new LinkedHashMap<File, Map<String, Object>>();

            Map<String, Object> replaceMap = new LinkedHashMap<String, Object>();
            replaceMap.put("@url@", escapeControlMark(entry.getValue().getUrl()));
            replaceMap.put("@schema@", escapeControlMark(entry.getValue().getSchema()));
            replaceMap.put("@user@", escapeControlMark(entry.getValue().getUser()));
            replaceMap.put("@password@", escapeControlMark(entry.getValue().getPassword()));
            replaceMap.put("@env@", escapeControlMark(entry.getKey()));
            fileMap.put(documentMapFile, replaceMap);

            replaceFile(fileMap, false);
        }
    }

    private void replaceFile(Map<File, Map<String, Object>> fileMap, boolean regularExpression) {
        try {
            for (Entry<File, Map<String, Object>> entry : fileMap.entrySet()) {
                File file = entry.getKey();
                if (!file.exists()) {
                    file = new File(file.getParentFile(), file.getName().replace("Map+.dfprop", "DefinitionMap+.dfprop"));
                }
                String text = FileUtils.readFileToString(file, Charsets.UTF_8);
                for (Entry<String, Object> replaceEntry : entry.getValue().entrySet()) {
                    Object value = replaceEntry.getValue();
                    value = value == null ? "" : value;
                    if (regularExpression) {
                        text = text.replaceAll(replaceEntry.getKey(), String.valueOf(value));
                    } else {
                        text = text.replace(replaceEntry.getKey(), String.valueOf(value));
                    }
                }
                FileUtils.write(file, text, Charsets.UTF_8);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String escapeControlMark(Object value) {
        return new MapListString().escapeControlMark(value);
    }

    // ===================================================================================
    //                                                                       Delete Client
    //                                                                       =============
    public boolean deleteClient(String project) {
        if (project == null) {
            return false;
        }
        final File dbfluteClientDir = new File(IntroPhysicalLogic.BASE_DIR_PATH, "dbflute_" + project);
        try {
            FileUtils.deleteDirectory(dbfluteClientDir);
        } catch (IOException e) {
            return false;
        }
        return true;
    }
}