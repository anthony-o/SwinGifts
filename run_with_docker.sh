#!/bin/bash

set -e

usage() {
    cat <<EOF
$0 [-h | --help] [[-b | --base-dir] <path>] [[-n | --container-name] <name>]
  [-h | --help]                    Print this usage help
  [-b | --base-dir] <path>         Sets the base directory where this script will its data. Defaults to /opt/SwinGifts
  [-n | --container-name] <name>   Sets the Docker container name that will be used to run SwinGifts in Tomcat
  [-r | --run-tomcat-as-root]      Runs Tomcat as root if you're on an old platform with the following aufs bug https://github.com/docker/docker/issues/24660
  [-U | --run-as-default-user]     Do not try to run the Docker processes using a specific user ID. Typically required on Windows.
  [-p | --use-proxy] <url>         Sets the proxy url that will be used by npm & Maven while building. For example: http://myproxy.mycompagny.com:3129
EOF
    #' comment used to trick bugged IntelliJ syntax coloring
    exit $1
}

KNOWN_ARG=true
while [ "$KNOWN_ARG" = true ]; do
    KNOWN_ARG=true
    if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
        usage 0
    elif [ "$1" = "-b" ] || [ "$1" = "--base-dir" ]; then
        BASE_DIR=$(readlink -f "$2")
        shift
        shift
    elif [ "$1" = "-n" ] || [ "$1" = "--container-name" ]; then
        CONTAINER_NAME=$2
        shift
        shift
    elif [ "$1" = "-U" ] || [ "$1" = "--run-as-default-user" ]; then
        RUN_AS_DEFAULT_USER=true
        shift
    elif [ "$1" = "-r" ] || [ "$1" = "--run-tomcat-as-root" ]; then
        RUN_TOMCAT_AS_ROOT=true
        shift
    elif [ "$1" = "-p" ] || [ "$1" = "--use-proxy" ]; then
        PROXY_DOCKER_ENV="-e http_proxy=$2 -e HTTP_PROXY=$2 -e https_proxy=$2 -e HTTPS_PROXY=$2"
        PROXY_URL=$2
        PROXY_PROTOCOL=`echo $2 | sed -e's,^\(.*\)://.*:.*,\1,g'`
        PROXY_HOST=`echo $2 | sed -e's,^.*://\(.*\):.*,\1,g'`
        PROXY_PORT=`echo $2 | sed -e's,^.*://.*:\(.*\),\1,g'`
        shift
        shift
    elif [ "$#" != "0" ]; then
        echo "Unknown argument: $1" >&2
        usage 1
    else
        KNOWN_ARG=false
    fi
done

set -x

BASE_DIR="${BASE_DIR:-/opt/SwinGifts}"
SRC_DIR="$(readlink -f `dirname $0`)"

VAR_DIR="$BASE_DIR/var"
BACKUPS_DIR="$VAR_DIR/backups"
CACHE_DIR="$VAR_DIR/cache"
CONTAINER_NAME=${CONTAINER_NAME:-SwinGifts}
# Use current date thanks to http://stackoverflow.com/a/1401495/535203
BACKUP_DIR="$BACKUPS_DIR/$(date +%Y%m%d-%H%M%S)"

cd "$SRC_DIR"

# Handling the bower vendor parts
rm -rf src/main/webapp/vendor
mkdir -p "$CACHE_DIR/apk" "$CACHE_DIR/bower" "$CACHE_DIR/npm"
if [ -z "$RUN_AS_DEFAULT_USER" ]; then
    docker run -it --rm $PROXY_DOCKER_ENV -v "$CACHE_DIR/apk:/etc/apk/cache" -v "$CACHE_DIR/bower:/bower-cache" -v "$CACHE_DIR/npm:/.npm" -v "$SRC_DIR:/workspace" -w /workspace node:6-alpine sh -c "if [ '$UID' == '1000' ] ; then USER=node ; else adduser -D -g '' -u $UID user && USER=user ; fi && apk --update add git && ln -nfs /.npm /home/\$USER/.npm && su \$USER -c 'mkdir ~/.cache' && ln -nfs /bower-cache /home/\$USER/.cache/bower && su \$USER -c 'npm install'"
else
    docker run -it --rm $PROXY_DOCKER_ENV -v "$CACHE_DIR/apk:/etc/apk/cache" -v "$CACHE_DIR/bower:/bower-cache" -v "$CACHE_DIR/npm:/.npm" -v "$SRC_DIR:/workspace" -w /workspace node:6-alpine sh -c "apk --update add git && ln -nfs /.npm ~/.npm && mkdir ~/.cache && ln -nfs /bower-cache ~/.cache/bower && npm install --unsafe-perm"
fi

# Compiling the war
if [ -f "$SRC_DIR/target/*.war" ]; then
    # First backup this version
    mkdir -p "$BACKUP_DIR"
    cp -a "$SRC_DIR/target/*.war" "$BACKUP_DIR/"
fi
if [ -n "$PROXY_URL" ]; then
    CREATE_MVN_PROXY_SETTINGS="echo '<settings><proxies><proxy><id>default</id><active>true</active><protocol>$PROXY_PROTOCOL</protocol><host>$PROXY_HOST</host><port>$PROXY_PORT</port></proxy></proxies></settings>' > ~/.m2/settings.xml ;"
fi
mkdir -p "$CACHE_DIR/m2"
if [ -z "$RUN_AS_DEFAULT_USER" ]; then
    docker run -it --rm $PROXY_DOCKER_ENV -v "$SRC_DIR:/workspace" -v "$CACHE_DIR/m2:/.m2" -w /workspace maven:3-jdk-8-alpine bash -c "adduser -D -g '' -u $UID user && ln -nfs /.m2 /home/user/ && su user -c '$CREATE_MVN_PROXY_SETTINGS mvn clean && mvn package'"
else
    docker run -it --rm $PROXY_DOCKER_ENV -v "$SRC_DIR:/workspace" -v "$CACHE_DIR/m2:/root/.m2" -w /workspace maven:3-jdk-8-alpine bash -c "$CREATE_MVN_PROXY_SETTINGS mvn clean && mvn package"
fi

if docker ps | egrep " $CONTAINER_NAME\$" >/dev/null ; then
    # The container exists, we must ask him to shutdown properly
    docker stop --time 20 $CONTAINER_NAME
fi
if docker ps -a | egrep " $CONTAINER_NAME\$" >/dev/null ; then
    # Now we can remove it in order to restart it
    docker rm -f $CONTAINER_NAME
fi

# Launch Tomcat
if [ -d "$VAR_DIR/db" ]; then
    # First backup the Database
    mkdir -p "$BACKUP_DIR"
    cp -ar "$VAR_DIR/db" "$BACKUP_DIR/db"
else
    mkdir -p "$VAR_DIR/db"
fi

if [ -z "$RUN_AS_DEFAULT_USER" ]; then
    TOMCAT_RUN_CMD="catalina.sh run"
    if [ "$RUN_TOMCAT_AS_ROOT" != "true" ]; then
        TOMCAT_RUN_CMD="su user -c '$TOMCAT_RUN_CMD'"
    fi
    docker run -itd -p 8080:8080 -p 9092:9092 --name "$CONTAINER_NAME" --restart "always" -v "$VAR_DIR/db:/var/local/swingifts" -v "$SRC_DIR:/workspace:ro" tomcat:8.5-alpine sh -c "if ! id -u user ; then adduser -D -g '' -u $UID user && chown -R user:user \$CATALINA_HOME /var/local/swingifts && rm -rf \$CATALINA_HOME/webapps/* && cp /workspace/target/*.war \$CATALINA_HOME/webapps/ROOT.war ; fi && $TOMCAT_RUN_CMD"
else
    docker run -itd -p 8080:8080 -p 9092:9092 --name "$CONTAINER_NAME" --restart "always" -v "$VAR_DIR/db:/var/local/swingifts" -v "$SRC_DIR:/workspace:ro" tomcat:8.5-alpine sh -c "rm -rf \$CATALINA_HOME/webapps/* && cp /workspace/target/*.war \$CATALINA_HOME/webapps/ROOT.war && catalina.sh run"
fi