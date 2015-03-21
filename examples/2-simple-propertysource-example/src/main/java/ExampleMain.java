import org.apache.tamaya.Configuration;
import org.apache.tamaya.ConfigurationProvider;

import java.util.Map;
import java.util.TreeMap;

public class ExampleMain {

    public static void main(String... args){
        System.out.println("****************************************************");
        System.out.println("Minimal Example");
        System.out.println("****************************************************");
        System.out.println();
        Configuration cfg = ConfigurationProvider.getConfiguration();
        System.out.println("Example Metadata:");
        System.out.println("  Type        :  " + cfg.get("example.type"));
        System.out.println("  Name        :  " + cfg.get("example.name"));
        System.out.println("  Description :  " + cfg.get("example.description"));
        System.out.println("  Version     :  " + cfg.get("example.version"));
        System.out.println("  Author      :  " + cfg.get("example.author"));
        System.out.println();
        System.out.println("  Path        :  " + cfg.get("Path"));
        System.out.println("  aProp       :  " + cfg.get("aProp"));
        System.out.println();
        System.out.println("FULL DUMP:\n\n" + dump(cfg.getProperties()));
    }

    private static String dump(Map<String, String> properties) {
        StringBuilder b = new StringBuilder();
        new TreeMap<>(properties).forEach((k,v)->b.append("  " + k + " = " + v + '\n'));
        return b.toString();
    }
}
