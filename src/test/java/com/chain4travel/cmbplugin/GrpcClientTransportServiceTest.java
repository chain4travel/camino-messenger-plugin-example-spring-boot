package com.chain4travel.cmbplugin;

import com.chain4travel.cmbplugin.grpc.client.TransportSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GrpcClientTransportSearchServiceTest {

  @Autowired private TransportSearchService TransportSearchService;

  @Test
  void testTransportSearch() {
    var response = TransportSearchService.sendTransportSearchRequest("Ping pong", "0x1234");
    System.out.println(response);
  }
}
