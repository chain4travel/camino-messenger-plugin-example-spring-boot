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
    var response = pingService.sendPing("Ping pong", "0x1bba6d75f329022349799d78d87fe9d79fa4c36e");
    System.out.println(response);
  }
}
