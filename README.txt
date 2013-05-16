# Before deploying, uploading files or web site, create a shell on sourceforge with the command
ssh cemartins,juffrou@shell.sourceforge.net create
# to release without deploying the site
mvn release:clean release:prepare release:perform -Dgoals=deploy