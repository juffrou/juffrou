# to release without deploying the site
mvn release:clean release:prepare release:perform -Dgoals=deploy