import groovy.xml.XmlUtil

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


class UpdateVersions {

    private XmlParser parser = new XmlParser()

    void run() {
        File cwd = new File(".")
        File parent = new File ("../")
        if (parent == null) {
            println "Can not get parent directory."
            return
        }
        XmlParser parser = new XmlParser();
        File pom = new File("pom.xml")
        Node project = parser.parse(pom);
        project.properties.each { prop ->
            println prop.name().localPart
            prop.each {
                //println "${it.name().localPart} ${it.text()}"
                String module = it.name().localPart
                File directory = new File(parent, "org.lappsgrid." + module)
                File modulePom = new File(directory, "pom.xml")
                if (!modulePom.exists()) {
                    println "Unable to find pom.xml for $module"
                }
                else {
                    String version = getVersion(modulePom)
                    println "$module $version"
                    it.value = version
                }
            }
        }
        XmlUtil.serialize(project, System.out)
        //QName name = (QName) props.name()
//        println name.localName()
//        project.properties { Node prop ->
//            println prop.name().toString()
//            println prop.value().toString()
//            println()
//        }
//        File parent = new File().getParentFile();
    }

    protected String getVersion(File file) {
        Node project = parser.parse(file)
        return project.version.text()
    }

    static void main(String[] args) {
        new UpdateVersions().run();
    }
}