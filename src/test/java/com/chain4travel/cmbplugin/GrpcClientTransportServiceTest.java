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
    String jsonString =
        """
             {
               \"header\":{
                  \"baseHeader\":{

                  }
               },
               \"searchParameters\":{
                  \"currency\":{
                     \"isoCurrency\":6
                  }
               },
               \"queries\":[
                  {
                     \"travellers\":[
                        {
                           \"type\":\"TRAVELLER_TYPE_ADULT\",
                           \"birthdate\":{
                              \"year\":1980,
                              \"month\":1,
                              \"day\":1
                           },
                           \"nationality\":\"COUNTRY_DE\"
                        },
                        {
                           \"travellerId\":1,
                           \"type\":\"TRAVELLER_TYPE_ADULT\",
                           \"birthdate\":{
                              \"year\":1980,
                              \"month\":1,
                              \"day\":2
                           },
                           \"nationality\":\"COUNTRY_IT\"
                        }
                     ],
                     \"trips\":[
                        {
                           \"departure\":{
                              \"date\":{
                                 \"year\":2024,
                                 \"month\":5,
                                 \"day\":15
                              },
                              \"location\":{
                                 \"locationCodes\":{
                                    \"codes\":[
                                       {
                                          \"code\":\"BCN\",
                                          \"type\":\"LOCATION_CODE_TYPE_IATA_CODE\"
                                       }
                                    ]
                                 }
                              }
                           },
                           \"arrival\":{
                              \"date\":{
                                 \"year\":2024,
                                 \"month\":5,
                                 \"day\":15
                              },
                              \"location\":{
                                 \"locationCodes\":{
                                    \"codes\":[
                                       {
                                          \"code\":\"LIS\",
                                          \"type\":\"LOCATION_CODE_TYPE_IATA_CODE\"
                                       }
                                    ]
                                 }
                              }
                           }
                        }
                     ]
                  }
               ]
            }
               """;

    var response = TransportSearchService.sendTransportSearchRequest(jsonString, "CM");
    System.out.println(response);
  }
}
