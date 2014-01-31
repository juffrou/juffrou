Juffrou
=======
_Java Utilities Framework For the Rest Of Us_


---

Please see the [juffrou site](http://cemartins.github.io/juffrou) for detailed documentation

---

Juffrou is a maven project. The sources are organized in a typical maven2 structure

To release without deploying the site
> mvn release:clean release:prepare release:perform -Dgoals=deploy

To deploy the site
> mvn site site:deploy

To deploy the site from behind a corporate proxy add the parameters
> mvn -Dhttps.proxyHost=\<proxy_host> -Dhttps.proxyPort=\<proxy_port> -Dhttps.proxyUsername=\<proxy_username> -Dhttps.proxyPassword=\<proxy_password> site-deploy 

Snapshot and release deployment are made with sonatype and you will need a GPG client installed on your command line path. 
For more information, please refer to http://www.gnupg.org/.
