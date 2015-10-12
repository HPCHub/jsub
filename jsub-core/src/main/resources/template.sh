SCRIPT_NAME=$(basename "$BASH_SOURCE");
echo "=> Start $SCRIPT_NAME";


#### var-section #####
#input list as standalone variables
${INPUT_PROPERTIES}

#input and clear lists as associative arrays
#see http://www.linuxjournal.com/content/bash-associative-arrays for usage
${CLEAR_ARRAY}
${MAP_ARRAY}
######################
#
#
#
#### exec-section ####
IS_FIRST=${IS_FIRST}
IS_SKIPED=${IS_SKIPED}
if $IS_SKIPED; then
    for FROM in ${!MAP_ARRAY[@]}; do
        if $IS_FIRST; then
            cp --verbose "$FROM" "${MAP_ARRAY[$FROM]}";
        else
            mv --verbose "$FROM" "${MAP_ARRAY[$FROM]}";
        fi
    done
else
${SCRIPT_INSTANCE}
fi
######################


CLEAR_INPUT_ARRAY=${CLEAR_INPUT_ARRAY}
if $CLEAR_INPUT_ARRAY; then
    for CLEAR_FILE in ${CLEAR_ARRAY[@]}; do
         rm --verbose "$CLEAR_FILE"
    done
fi


echo "uptime: $SECONDS seconds";
echo "=> Stop $SCRIPT_NAME";
