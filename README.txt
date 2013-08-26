Juffrou is a maven project. The sources are organized in a typical maven2 structure

To upload files to sourceforge
> mvn wagon:upload

To release without deploying the site
> mvn release:clean release:prepare release:perform -Dgoals=deploy

To deploy the site
> mvn site site:deploy

Before deploying, artifacts or site, create a shell on sourceforge with the command
> ssh cemartins,juffrou@shell.sourceforge.net create

Snapshot and release deployment are made with sonatype and you will need a GPG client installed on your command line path. 
For more information, please refer to http://www.gnupg.org/.
 