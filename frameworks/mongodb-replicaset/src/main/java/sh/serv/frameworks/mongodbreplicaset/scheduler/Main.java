package sh.serv.frameworks.mongodbreplicaset.scheduler;

import com.mesosphere.sdk.specification.*;

import java.io.File;

/**
 * Main.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        new DefaultService(new File(args[0]));
    }
}
