# SwinGifts - Let's swing gifts between us!

This application aims to manage gifts wishlists between friends and to be able to designate one friend to offer a gift to for a specific event, a sort of "circle of gift".

# How to install SwinGifts

## Using Docker
```bash
docker run -it --rm -v `pwd`:/workspace -v ~/.m2:/.m2 -w /workspace maven:3-jdk-8-alpine bash -c "adduser -D -g '' -u $UID user && ln -nfs /.m2 /home/user/ && su user -c 'mvn package'"
docker run -it --rm -p 8080:8080 -p 9092:9092 -v `pwd`:/workspace -v `pwd`/docker-data:/var/local/swingifts tomcat:8.5-alpine sh -c "cp /workspace/target/*.war \$CATALINA_HOME/webapps/ROOT.war && rm -rf \$CATALINA_HOME/webapps/ROOT && catalina.sh run"
```

When [this bug](https://github.com/docker/docker/issues/24660) will be fixed, one should better use this command to run SwinGifts in order for Tomcat not to run as root:
```bash
docker run -it --rm -p 8080:8080 -p 9092:9092 -v `pwd`:/workspace tomcat:8.5-alpine sh -c "adduser -D -g '' -u $UID user && chown -R user \$CATALINA_HOME && cp /workspace/target/*.war \$CATALINA_HOME/webapps/ROOT.war && su user -c 'catalina.sh run'"
```

# TODOs

 * Translate all the application thanks to [angular-translate](https://github.com/angular-translate/angular-translate);
 * Allow the admin of an event to make a user subscribe for the circle gift;