package main

import (
	"fmt"
	"github.com/mesosphere/dcos-commons/cli"
	"gopkg.in/alecthomas/kingpin.v2"
	"log"
	"strings"
)

func main() {
	app, err := cli.NewApp("0.1.0", "serv.sh", "Provides CLI integration for the mongodb-replicaset package")
	if err != nil {
		log.Fatalf(err.Error())
	}

	cli.HandleCommonArgs(app, "mongodb-replicaset", "MongoDb Replica Set DC/OS CLI Module", []string{"foo", "bar"})
	handleExampleSection(app)

	// Omit modname:
	kingpin.MustParse(app.Parse(cli.GetArguments()))
}
