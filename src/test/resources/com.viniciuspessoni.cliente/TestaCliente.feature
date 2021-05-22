Feature: Check the client microservice

  Scenario: when we register a new client it has to be present in the results
    Given I have a client to register "Vinny" with age "32" and id "10101"
    When I "POST" this client to the service
    Then I can see the client in the results list
    And the response HTTP code should be "201"