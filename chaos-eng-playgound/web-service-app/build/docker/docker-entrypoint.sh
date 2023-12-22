#!/bin/bash
set -eu

exec_dir=/opt/web-service-app/lib/
cd $exec_dir

JAVA_OPT_ARGS=()
if [ ! -z "$MIN_HEAP_SIZE" ]; then
	JAVA_OPT_ARGS+=( $MIN_HEAP_SIZE )
fi

if [ ! -z "$MAX_HEAP_SIZE" ]; then
	JAVA_OPT_ARGS+=( $MAX_HEAP_SIZE )
fi

if [ ! -z "$PRINT_GC" ]; then
	JAVA_OPT_ARGS+=( $PRINT_GC )
fi

exec java -XX:+PrintFlagsFinal -XX:+UseG1GC -XX:+UseStringDeduplication ${JAVA_OPT_ARGS[@]+"${JAVA_OPT_ARGS[@]}"} -Dlogging.config=/opt/web-service-app/lib/logback-rolling.xml -Djava.net.preferIPv6Addresses=${HORIZON_PREFER_IPV6} -jar web-service-app.jar $@
