Feature: I want to test the metadata endpoint

  Scenario Outline: metadata GET request negative tests caused by <value> as a subject
    Given I have an invalid subject as <value>
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code <statusCode>
    Examples:
      | value | statusCode |
      |       | 404        |
      | 123   | 404        |

  Scenario Outline: metadata property GET request negative tests caused by <value> as a subject
    Given I have an invalid subject as <value>
    And  I have the valid property name as <property>
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code <statusCode>
    Examples:
      | value | property | statusCode |
      |       | url      | 404        |
      | 123   | name     | 404        |

  Scenario Outline: metadata property GET request negative tests caused by <value> as a property
    Given I have a valid subject
    And I have an invalid property as <name>
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code <statusCode>
    Examples:
      | name  | statusCode |
      |       | 404        |
      | names | 404        |
      | £$%^^ | 404        |

  Scenario: Successful metadata GET request test
    Given I have a valid subject
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code 200
    And I can see the subject is matched
    And I can see all properties include sequenceNumber
    And I can see all properties include value
    And I can see all properties include signatures
    And I can see all signature are valid in all properties
    And I can see all publicKey are valid in all properties

  Scenario Outline: Successful metadata property GET request test
    Given I have a valid subject
    And I have the <name> property
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code 200
    And I can see the valid sequenceNumber
    And I can see the valid value
    And I can see the valid signatures
    Examples:
      | name        |
      | url         |
      | name        |
      | ticker      |
      | logo        |
      | description |

  Scenario Outline: Successful metadata property GET request call for the property which has a single value
    Given I have a valid subject
    And I have the <name> property
    When I call http://metadata-server-mock.herokuapp.com/metadata/
    Then I can see the status code 200
    And <name> attribute has value
    Examples:
      | name    |
      | subject |
      | policy  |

  Scenario Outline: query POST request negative tests caused by <invalid> in <attribute>
    Given I have <invalid> data in <attribute>
    When I call http://metadata-server-mock.herokuapp.com/metadata/query
    Then I can see the status code <statusCode>
    Examples:
      | invalid     | attribute  | statusCode |
      |             | subjects   | 400        |
      | 123,456,789 | properties | 400        |

  Scenario: Successful query POST request
    Given I have the valid subjects data as 789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f16861707079636f696e, 789ef8ae89617f34c07f7f6a12e4d65146f958c0bc15a97b4ff169f1, 94d4cdbcffb09ebd4780d94f932a657dc4852530fa8013df66c72d4c676f6f64636f696e
    And I have the valid properties data
    When I call http://metadata-server-mock.herokuapp.com/metadata/query
    Then  I can see the status code 201
    When I repeatedly call http://metadata-server-mock.herokuapp.com/metadata/
    Then  I can see the status code 200
    And I can see the matched data with above subjects and their properties
