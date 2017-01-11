# mongodb-replicaset

A framework to establish a MongoDB Replica Set in a Mesos or DC/OS cluster. It is based on [dcos-commons](https://github.com/mesosphere/dcos-commons). 

At the current state, it launches a Mesos framework scheduler, which then coordinates the launch of the actual tasks containing the `mongod` processes. Once those tasks have been launched and are running, it will launch an initialization task, which initializes the Replica Set and then adds the remaining `mongod` instances to the Replica Set. Once this is done, you can use the Replica Set for your client applications.

Currently, it is not possible to scale the Replica Set to more instances than defined when installing the package, due to how the YAML plans work in [dcos-commons](https://github.com/mesosphere/dcos-commons). In the future, it's very likely that this will be possible.

## Installation via Universe

### CLI install

To install the `mongodb-replicaset` package with default settings, use the following:

```bash
$ dcos package install --yes mongodb-replicaset
```

If you want to customize the package, create an `options.json` file and add your custom configuration

```javascript
{
  "service": {
    "name": "my-mongodb-replicaset"
  },
  "mongodb": {
    "cpus": 1,
    "mem": 4096,
    "disk": 10000,
    "replicaSetName": "my-rs"
  }
}
```

This would then launch a service called `my-mongodb-replicaset`, which uses `1` cpu, `4096` megabytes of memory, and `10000` megabytes of disk space (local persistent volume). Furthermore, the Replica Set name would be `my-rs`.

### Via the DC/OS UI

In the first tab, you can define your DC/OS service name:

![Screenshot 1 - service definition](https://files.serv.sh/frameworks/mongodb-replicaset/images/screenshots/mongodb-screenshot_1.png)

In the second tab, you can set everything related to the actual `mongod` instances, namingly the resource requirements, the Replica Set name the MongoDB version, among other things:
![Screenshot 2 - mongodb definition](https://files.serv.sh/frameworks/mongodb-replicaset/images/screenshots/mongodb-screenshot_2.png)

In the third tab you can configure the resources and delays for the `init` container. Normally, you don't need to change anything here:
![Screenshot 3 - init definition](https://files.serv.sh/frameworks/mongodb-replicaset/images/screenshots/mongodb-screenshot_3.png)

## Start with Marathon

Example app definition (as long as the package is not yet in the Universe):

```javascript
{
    "id": "/mongodb-replicaset",
    "cmd": "export LD_LIBRARY_PATH=$MESOS_SANDBOX/libmesos-bundle/lib && export MESOS_NATIVE_JAVA_LIBRARY=$(ls $MESOS_SANDBOX/libmesos-bundle/lib/libmesos-*.so) && export PATH=$(ls -d $MESOS_SANDBOX/jre*/bin):$PATH && env && ./scheduler/bin/mongodb-replicaset ./scheduler/svc.yml",
    "env": {
        "SERVICE_NAME": "mongodb-replicaset",
        "MONGODB_MEM": "2048",
        "MONGODB_CPUS": "0.5",
        "MONGODB_DISK": "1000",
        "MONGODB_COUNT": "3",
        "MONGODB_PORT": "27017",
        "MONGODB_REPLSET": "rs",
        "MONGODB_CONSTRAINT": "[[&#39;hostname&#39;, &#39;UNIQUE&#39;]]",
        "INIT_MEM": "256",
        "INIT_CPUS": "0.1",
        "INIT_DISK": "0",
        "INIT_COUNT": "1",
        "INIT_ADD_DELAY": "10",
        "INIT_INITIATE_DELAY": "10",
        "CONFIG_TEMPLATE_PATH": "scheduler",
        "LD_LIBRARY_PATH": "/opt/mesosphere/lib",
        "EXECUTOR_URI": "https://files.serv.sh/frameworks/mongodb-replicaset/0.1.0/executor.zip",
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
            "uri": "https://files.serv.sh/frameworks/mongodb-replicaset/0.1.0/scheduler.zip"
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

## Service discovery

As the framework uses VIPs, you can use a simple MongoDB connection string to connect to the running Replica Set from inside the DC/OS cluster:

```bash
mongodb://mongodb.mongodb-replicaset.l4lb.thisdcos.directory:27017/?replicaSet={mongodb.replicaSetName}
```

It will then use Minuteman under the hood to loadbalance the requests to individual MongoDB instances.
 
## Roadmap

 - [ ] Enable authorization
 - [ ] Enable scaling after the Replica Set has started