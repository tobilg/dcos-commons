# mongodb-replicaset

A framework to establish a MongoDB ReplicaSet in a Mesos or DC/OS cluster.

## Start with Marathon

Example app definition (as long as the package is not yet in the Universe):

```javascript
{
    "id": "/mongodb-replicaset",
    "cmd": "export LD_LIBRARY_PATH=$MESOS_SANDBOX/libmesos-bundle/lib && export MESOS_NATIVE_JAVA_LIBRARY=$(ls $MESOS_SANDBOX/libmesos-bundle/lib/libmesos-*.so) && export PATH=$(ls -d $MESOS_SANDBOX/jre*/bin):$PATH && env && ./scheduler/bin/mongodb-replicaset ./scheduler/svc.yml",
    "env": {
        "MONGODB_MEM": "2048",
        "MONGODB_CPUS": "0.5",
        "MONGODB_DISK": "1000",
        "MONGODB_COUNT": "3",
        "MONGODB_REPLSET": "rs",
        "MONGODB_CONSTRAINT": "[[&#39;hostname&#39;, &#39;UNIQUE&#39;]]",
        "INIT_MEM": "64",
        "INIT_CPUS": "0.1",
        "INIT_DISK": "0",
        "INIT_COUNT": "1",
        "INIT_ADD_DELAY": "10",
        "INIT_APP_NAME": "mongodb-replicaset",
        "CONFIG_TEMPLATE_PATH": "scheduler",
        "LD_LIBRARY_PATH": "/opt/mesosphere/lib",
        "EXECUTOR_URI": "https://files.serv.sh/frameworks/mongodb-replicaset/0.1.0-3.4.1/executor.zip",
        "LIBMESOS_URI": "http://downloads.mesosphere.com/libmesos-bundle/libmesos-bundle-1.8.7-1.0.2.tar.gz"
    },
    "instances": 1,
    "cpus": 0.25,
    "mem": 1230,
    "disk": 0,
    "gpus": 0,
    "fetch": [
        {
            "uri": "https://downloads.mesosphere.com/java/jre-8u112-linux-x64.tar.gz"
        },
        {
            "uri": "https://files.serv.sh/frameworks/mongodb-replicaset/0.1.0-3.4.1/scheduler.zip"
        },
        {
            "uri": "http://downloads.mesosphere.com/libmesos-bundle/libmesos-bundle-1.8.7-1.0.2.tar.gz"
        }
    ],
    "backoffSeconds": 1,
    "backoffFactor": 1.15,
    "maxLaunchDelaySeconds": 3600,
    "healthChecks": [
        {
            "protocol": "HTTP",
            "path": "/v1/tasks",
            "gracePeriodSeconds": 120,
            "intervalSeconds": 30,
            "timeoutSeconds": 5,
            "ignoreHttp1xx": false
        }
    ],
    "upgradeStrategy": {
        "minimumHealthCapacity": 0,
        "maximumOverCapacity": 0
    },
    "labels": {
        "DCOS_SERVICE_SCHEME": "http",
        "DCOS_SERVICE_NAME": "mongodb-replicaset",
        "DCOS_SERVICE_PORT_INDEX": "0",
        "DCOS_MIGRATION_API_PATH": "/v1/plan",
        "MARATHON_SINGLE_INSTANCE_APP": "true",
        "DCOS_MIGRATION_API_VERSION": "v1"
    },
    "portDefinitions": [
        {
            "protocol": "tcp",
            "port": 10000,
            "name": "api"
        }
    ],
    "requirePorts": false
}

```