package sh.serv.frameworks.mongodbreplicaset.scheduler;

import org.apache.curator.test.TestingServer;
import org.apache.mesos.Protos;
import org.apache.mesos.Scheduler;
import org.apache.mesos.SchedulerDriver;
import com.mesosphere.sdk.scheduler.DefaultScheduler;
import com.mesosphere.sdk.specification.DefaultServiceSpec;
import com.mesosphere.sdk.specification.yaml.YAMLServiceSpecFactory;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.net.URL;
import java.util.Collections;

import static com.mesosphere.sdk.specification.yaml.YAMLServiceSpecFactory.generateRawSpecFromYAML;

public class ServiceSpecTest {
    @ClassRule
    public static final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Mock
    private SchedulerDriver mockSchedulerDriver;

    @BeforeClass
    public static void beforeAll() {
        environmentVariables.set("SERVICE_NAME", "inf-mongodb-replicaset");
        environmentVariables.set("EXECUTOR_URI", "");
        environmentVariables.set("PORT0", "8080");
        environmentVariables.set("MONGODB_PORT", "27017");
        environmentVariables.set("MONGODB_COUNT", "3");
        environmentVariables.set("MONGODB_CPUS", "0.1");
        environmentVariables.set("MONGODB_MEM", "1024");
        environmentVariables.set("MONGODB_DISK", "1000");
        environmentVariables.set("INIT_COUNT", "1");
        environmentVariables.set("INIT_CPUS", "0.2");
        environmentVariables.set("INIT_MEM", "128");
        environmentVariables.set("INIT_DISK", "0");
        environmentVariables.set("INIT_INITIATE_DELAY", "10");
        environmentVariables.set("INIT_ADD_DELAY", "5");
        URL resource = ServiceSpecTest.class.getClassLoader().getResource("init.sh.mustache");
        environmentVariables.set("CONFIG_TEMPLATE_PATH", new File(resource.getPath()).getParent());
        environmentVariables.set("LIBMESOS_URI", "");
    }

    @Before
    public void beforeEach() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testServiceSpecDeserialization() throws Exception {
        ClassLoader classLoader = ServiceSpecTest.class.getClassLoader();
        File file = new File(classLoader.getResource("svc.yml").getFile());

        DefaultServiceSpec serviceSpec = YAMLServiceSpecFactory
                .generateServiceSpec(generateRawSpecFromYAML(file));
        Assert.assertNotNull(serviceSpec);
        Assert.assertEquals(8080, serviceSpec.getApiPort());
        DefaultServiceSpec.getFactory(serviceSpec, Collections.emptyList());
    }

    @Test
    public void testServiceSpecValidation() throws Exception {
        File file = new File(getClass().getClassLoader().getResource("svc.yml").getFile());
        DefaultServiceSpec serviceSpec = YAMLServiceSpecFactory.generateServiceSpec(generateRawSpecFromYAML(file));

        TestingServer testingServer = new TestingServer();

        Protos.FrameworkID FRAMEWORK_ID = Protos.FrameworkID.newBuilder()
                .setValue("test-framework-id")
                .build();

        Protos.MasterInfo MASTER_INFO = Protos.MasterInfo.newBuilder()
                .setId("test-master-id")
                .setIp(0)
                .setPort(0)
                .build();

        Scheduler scheduler = DefaultScheduler.newBuilder(serviceSpec)
                .setStateStore(DefaultScheduler.createStateStore(serviceSpec, testingServer.getConnectString()))
                .setConfigStore(DefaultScheduler.createConfigStore(serviceSpec, testingServer.getConnectString()))
                .build();
        scheduler.registered(mockSchedulerDriver, FRAMEWORK_ID, MASTER_INFO);
        testingServer.close();
    }
}
