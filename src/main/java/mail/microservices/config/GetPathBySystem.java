package mail.microservices.config;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetPathBySystem {

    @SneakyThrows
    public String getPathBySystem(String directoryName) {

        String system = System.getProperty("os.name").toLowerCase();
        String localDir = System.getProperty("user.dir");
        String path = "/opt/tomcat/webapps/?/" + directoryName;

        if (system.contains("windows")) {
            path = localDir + File.separator + "?" + File.separator + directoryName;
        }
        File file = new File(path);

        if (!file.exists()) {
            try {
                Files.createDirectories(Paths.get(path));
                System.out.println(String.format("Directory %s is created!", path));
            }catch (Exception e) {
                System.out.println(String.format("Directory %s is NOT created!", path));
            }

        }

        return path;
    }

}
