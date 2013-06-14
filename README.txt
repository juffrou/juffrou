# Before deploying, uploading files or web site, create a shell on sourceforge with the command
ssh cemartins,juffrou@shell.sourceforge.net create

# to upload files to sourceforge
mvn wagon:upload

# to release without deploying the site
mvn release:clean release:prepare release:perform -Dgoals=deploy
# to upload files to sourceforge

# to deploy the site
mvn site site:deploy