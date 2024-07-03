package org.acme.order;

import io.github.microcks.testcontainers.MicrocksContainersEnsemble;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.Network;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class ContainersConfiguration {

   private static Network network = Network.newNetwork();

   @Bean
   @ServiceConnection
   MicrocksContainersEnsemble microcksEnsemble() {
      MicrocksContainersEnsemble ensemble = new MicrocksContainersEnsemble(network, DockerImageName.parse("quay.io/microcks/microcks-uber:1.9.0"))
              .withPostman()             // We need this to do contract-testing with Postman collection
              .withAsyncFeature()        // We need this for async mocking and contract-testing
              .withAccessToHost(true);   // We need this to access our webapp while it runs

      ensemble.start(); // Start the ensemble

      return ensemble;
   }
}
