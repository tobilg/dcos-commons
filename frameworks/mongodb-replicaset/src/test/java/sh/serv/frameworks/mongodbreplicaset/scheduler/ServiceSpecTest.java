package sh.serv.frameworks.mongodbreplicaset.scheduler;

import org.apache.curator.test.TestingServer;
import org.apache.mesos.Protos;
import org.apache.mesos.SchedulerDriver;
import com.mesosphere.sdk.config.ConfigStore;
import com.mesosphere.sdk.config.ConfigurationUpdater;
import com.mesosphere.sdk.offer.OfferRequirementProvider;
import com.mesosphere.sdk.scheduler.DefaultScheduler;
import com.mesosphere.sdk.specification.DefaultServiceSpec;
import com.mesosphere.sdk.specification.yaml.YAMLServiceSpecFactory;
import com.mesosphere.sdk.state.StateStore;
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
        environmentVariables.set("DCOS_SERVICE_NAME", "mongodb-replicaset");
        environmentVariables.set("EXECUTOR_URI", "");
        environmentVariables.set("PORT0", "8080");
        environmentVariables.set("MONGODB_COUNT", "3");
        environmentVariables.set("MONGODB_CPUS", "0.1");
        environmentVariables.set("MONGODB_MEM", "1024");
        environmentVariables.set("MONGODB_DISK", "1000");
        environmentVariables.set("INIT_COUNT", "1");
        environmentVariables.set("INIT_CPUS", "0.2");
        environmentVariables.set("INIT_MEM", "128");
        environmentVariables.set("INIT_DISK", "0");
        environmentVariables.set("INIT_APP_NAME", "blub");
        environmentVariables.set("INIT_ADD_DELAY", "10");
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
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("svc.yml").getFile());

        DefaultServiceSpec serviceSpec = YAMLServiceSpecFactory
                .generateServiceSpec(generateRawSpecFromYAML(file));
        Assert.assertNotNull(serviceSpec);
        Assert.assertEquals(8080, serviceSpec.getApiPort());
        DefaultServiceSpec.getFactory(serviceSpec, Collections.emptyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testServiceSpecValidation() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("svc.yml").getFile());
        DefaultServiceSpec serviceSpec = YAMLServiceSpecFactory
                .generateServiceSpec(generateRawSpecFromYAML(file));

        TestingServer testingServer = new TestingServer();
        StateStore stateStore = DefaultScheduler.createStateStore(
                serviceSpec,
                testingServer.getConnectString());
        ConfigStore configStore = DefaultScheduler.createConfigStore(
                serviceSpec,
                testingServer.getConnectString(),
                Collections.emptyList());

        Protos.FrameworkID FRAMEWORK_ID =
                Protos.FrameworkID.newBuilder()
                        .setValue("test-framework-id")
                        .build();

        Protos.MasterInfo MASTER_INFO =
                Protos.MasterInfo.newBuilder()
                        .setId("test-master-id")
                        .setIp(0)
                        .setPort(0)
                        .build();

        ConfigurationUpdater.UpdateResult configUpdateResult = DefaultScheduler
                .updateConfig(serviceSpec, stateStore, configStore);

        OfferRequirementProvider offerRequirementProvider = DefaultScheduler
                .createOfferRequirementProvider(stateStore, configUpdateResult.targetId);

        DefaultScheduler defaultScheduler = DefaultScheduler
                .create(serviceSpec, stateStore, configStore, offerRequirementProvider);
        defaultScheduler.registered(mockSchedulerDriver, FRAMEWORK_ID, MASTER_INFO);
    }
}
