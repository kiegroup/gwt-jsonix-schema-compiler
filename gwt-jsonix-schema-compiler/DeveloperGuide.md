Developer Guide
===============


Test
----
**src/it** folder contains example projects used for ~~integration tests~~. For such tests, the *maven-invoker-plugin* is used; invoke it with:

    mvn clean verify
    
At the end of execution, built artifacts are put inside **target/its** folder, while reports are written inside **target/invoker-reports**.


**src/test** folder contains ~~unit tests~~. For such tests, the *maven-surefire-plugin* is used; invoke it with:

    mvn clean test
    




