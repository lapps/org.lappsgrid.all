/*
 * Copyright (C) 2018 The Language Applications Grid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import groovy.xml.XmlUtil

/**
 * Use the UpdateVersions program to update module versions in the pom.xml file.
 * This program will:
 * 1. Iterate over each module listed in the <properties/> element
 * 2. Parse the pom for each module found and extract the version number
 * 3. Update the version of the module property
 * 4. Create a timestamped backup of the current pom.xml
 * 5. Write the updated pom.xml
 */
class UpdateVersions {

    private XmlParser parser = new XmlParser()

    void run() {
        File parent = new File ("../")
        if (parent == null || !parent.exists()) {
            println "Can not get parent directory."
            return
        }

        File pom = new File("pom.xml")
        Node project = parser.parse(pom);
        project.properties.each { prop ->
            println prop.name().localPart
            prop.each {
                String module = it.name().localPart
                File directory = new File(parent, "org.lappsgrid." + module)
                File modulePom = new File(directory, "pom.xml")
                if (!modulePom.exists()) {
                    println "Unable to find pom.xml for $module"
                }
                else {
                    String version = getVersion(modulePom)
                    println "Updating $module to $version"
                    it.value = version
                }
            }
        }
        // Create a backup of the current pom
        new File(timestamp() + "-pom.xml").text = pom.text

        // Write the new pom
        FileOutputStream out = new FileOutputStream(pom)
        XmlUtil.serialize(project, out)
        out.flush()
        out.close()

    }

    protected String timestamp() {
        return new Date().format("yyyy-MM-dd-HH-mm")
    }

    // Parse the version from a pom.xml file.
    protected String getVersion(File file) {
        Node project = parser.parse(file)
        return project.version.text()
    }

    static void main(String[] args) {
        new UpdateVersions().run();
    }
}