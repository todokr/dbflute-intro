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

import org.dbflute.intro.app.logic.intro.IntroPhysicalLogic;

import javax.annotation.Resource;

/**
 * @author deco
 */
public class DfpropPhysicalLogic {

    private static final String DFPROP_DIR_PATH = "dfprop";

    @Resource
    private IntroPhysicalLogic introPhysicalLogic;

    public String buildDfpropDirPath(String project) {
        return introPhysicalLogic.toDBFluteClientResourcePath(project, DFPROP_DIR_PATH);
    }

    public String buildDfpropFilePath(String project, String fileName) {
        String dfpropDirPath = buildDfpropDirPath(project);
        return dfpropDirPath + "/" + fileName;
    }
}