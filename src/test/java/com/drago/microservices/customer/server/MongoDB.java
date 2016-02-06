package com.drago.microservices.customer.server;


import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

import java.io.IOException;

/**
 * Starts and stop mongo db database.
 */
public class MongoDB {

    private MongodExecutable mongodExecutable;

    public MongoDB(final int port) {

        MongodStarter mongodStarter = MongodStarter.getDefaultInstance();
        IMongodConfig mongodConfig;
        try {
            mongodConfig = new MongodConfigBuilder()
                    .version(Version.Main.PRODUCTION)
                    .net(new Net(port, Network.localhostIsIPv6()))
                    .build();
            mongodExecutable = mongodStarter.prepare(mongodConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        mongodExecutable.start();
    }

    public void stop() {
        mongodExecutable.stop();
    }
}
