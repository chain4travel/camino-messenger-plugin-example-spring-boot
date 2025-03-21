package com.chain4travel.cmbplugin;

import com.chain4travel.cmbplugin.grpc.client.PingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GrpcClientPingServiceTest {

  @Autowired private PingService pingService;

  @Test
  void testSendPing() {
    var response = pingService.sendPing("Ping pong", "CM_ACCOUNT_ADDRESS");
    System.out.println(response);
  }
}
