#!/bin/bash
set -e
### constant
WORKDIR=/app
OK="\033[32m [OK] \033[0m"
ERROR="\033[31m [ERROR] \033[0m"

gen_config() {
    # suppervisor
    echo -e " - configurating: supervisor"
    cp $WORKDIR/conf/supervisord.conf /etc
    chmod 644 /etc/supervisord.conf
    # permission
    echo -e " - configurating: permission"
    mkdir /data/logs -p
    chown -R worker:worker /app /data
    rm -rf /app/src
}

init() {
    echo -e "*** STEP: generate config file..."
    gen_config
}

## run command
# check for the expected command
if [ "$1" = 'supervisord' ]; then
    # run command
    init
    # use gosu to drop to a non-root user
    echo -e "*** STEP: running supervisor..."
    exec gosu worker "$@"
fi

# else default to run whatever the user wanted like "bash"
exec "$@"

