package com.deploy;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

public class Deploy {
  private static final Logger logger = Logger.getLogger(Deploy.class.getName());


  public static void main(String[] args) {
    logger.info("Tag version");
    Date currentDate = new Date();
    DateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");

    Path currentRelativePath = Paths.get("");
    String currentRelativePathSt = currentRelativePath.toAbsolutePath().toString();
    logger.info("currentPath[" + currentRelativePathSt + "]");


    // Create a Properties file for Spring
    String fileName = currentRelativePathSt + "/src/main/resources/version.properties";
    StringBuilder contentJava = new StringBuilder();

    contentJava.append("# this file is generated by com.deploy.Deploy\n");
    contentJava.append("togh.version=" + sdf.format(currentDate));
    writeFile(contentJava.toString(), fileName);

    // create a Spring component
    fileName = currentRelativePathSt + "/npm/src/component/ToghVersion.jsx";
    StringBuilder contentJsx = new StringBuilder();
    contentJsx.append("/* this file is generated by com.deploy.Deploy */\n");
    contentJsx.append("import React from 'react';\n");
    contentJsx.append("class ToghVersion extends React.Component {\n");
    contentJsx.append("  render() {\n");
    contentJsx.append("    return (\"" + sdf.format(currentDate) + "\")\n");
    contentJsx.append("  }");
    contentJsx.append("}");
    contentJsx.append("export default ToghVersion;");

    writeFile(contentJsx.toString(), fileName);
    logger.info("End Tag Version");


  }

  private static void writeFile(String content, String fileName) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, false))) {
      writer.append(content);
    } catch (Exception e) {
      logger.severe("Can't write file[" + fileName + "] error " + e);
    }
  }
}
