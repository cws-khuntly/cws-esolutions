#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  add_ns_ui_helper.sh
#         USAGE:  ./add_cname_ui_helper.sh
#   DESCRIPTION:  Helper interface for add_record_ui. Pluggable, can be modified
#                 or copied for all allowed record types.
#
#       OPTIONS:  ---
#  REQUIREMENTS:  ---
#          BUGS:  ---
#         NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com>
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#      REVISION:  ---
#==============================================================================

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

[ -z "${PLUGIN_ROOT_DIR}" ] && exit 0;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

METHOD_NAME="${CNAME}#startup";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

trap "print '$(sed -e '/^ *#/d;s/#.*//' ${SYSTEM_MESSAGES} | awk -F "=" '/\<system.trap.signals\>/{print $2}' | sed -e 's/^ *//g;s/ *$//g' -e "s/%SIGNAL%/Ctrl-C/")'; sleep "${MESSAGE_DELAY}"; reset; clear; continue " 1 2 3

#===  FUNCTION  ===============================================================
#          NAME:  add_ip_info
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#==============================================================================
function add_zone_ui_helper
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain NS record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read NS_TARGET;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NS_TARGET->${NS_TARGET}";

        case ${NS_TARGET} in
            [Xx]|[Qq]|[Cc])
                ## user has chosen to cancel
                unset NS_TARGET;
                unset NS_ALIAS;
                unset RECORD_TYPE;
                unset ADD_SUBDOMAINS;

                CANCEL_REQ=${_TRUE};

                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                reset; clear;
                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                if [ -z "${NS_TARGET}" ]
                then
                    ## ip addr provided is blank
                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${NS_TARGET} was blank. Cannot continue.";
                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                    unset NS_TARGET;

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

                    THIS_CNAME="${CNAME}";
                    unset METHOD_NAME;
                    unset CNAME;

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                    ## validate the input
                    ${PLUGIN_ROOT_DIR}/lib/validators/validateRecordData.sh target ns ${NS_TARGET};
                    typeset -i RET_CODE=${?};

                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                    CNAME="${THIS_CNAME}";
                    local METHOD_NAME="${THIS_CNAME}#${0}";

                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                    if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                    then
                        if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                        then
                            ## we got a warning on validation - we arent failing, but we do want to inform
                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while updating the requested zone. Please try again.";
                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                            sleep "${MESSAGE_DELAY}";
                        fi

                        unset RET_CODE;
                        unset RETURN_CODE;

                        ## successful validation
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${NS_TARGET}";
                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${NS_ALIAS},${NS_TARGET} -s";

                        ## our provided address is valid.
                        ## make a call out to add_records
                        ## to process the request

                        ## temp unset
                        unset METHOD_NAME;
                        unset CNAME;

                        . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${NS_TARGET} -r;
                        typeset -i RET_CODE=${?};

                        ## reset vars
                        local METHOD_NAME="${CNAME}#${0}";
                        CNAME="$(basename "${0}")";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                        if [ ${RET_CODE} -eq 0 ]
                        then
                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset NS_ALIAS;
                            unset NS_TARGET;
                            unset RECORD_TYPE;

                            ## record was successfully added. ask if we should add more
                            ADD_COMPLETE=${_TRUE};
                            reset; clear; break;
                        else
                            ## an "ERROR" occurred
                            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                            unset RET_CODE;
                            unset RETURN_CODE;
                            unset NS_TARGET;
                            unset NS_ALIAS;

                            sleep "${MESSAGE_DELAY}"; reset; clear; break;
                        fi
                    else
                        ## target failed validation
                        ## show an "ERROR"
                        reset; clear;
                        print "$(grep target.provided.not.valid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/${RECORD_TYPE}/" -e "s/%TARGET%/${NS_TARGET}/")\n";

                        unset NS_TARGET;
                        unset RET_CODE;
                        unset RETURN_CODE;

                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                    fi
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  add_subdomain_ui_helper
#   DESCRIPTION:  Processes requests to add additional record types to a zone
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function add_subdomain_ui_helper
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Requesting subdomain NS record information..";

    trap "print '$(grep system.trap.signals "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%SIGNAL%/Ctrl-C/")'; sleep ${MESSAGE_DELAY}; reset; clear; continue " 1 2 3

    while true
    do
        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
        then
            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

            break;
        fi

        reset; clear;

        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
        print "\t$(grep add.record.alias ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE} | tr "[a-z]" "[A-Z]")/")";
        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        read NS_ALIAS;

        reset; clear;
        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NS_ALIAS->${NS_ALIAS}";

        case ${NS_ALIAS} in
            [Xx]|[Qq]|[Cc])
                ## user chose to cancel this request
                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Subdomain record addition for ${RECORD_TYPE} canceled.";
                unset NS_ALIAS;
                unset RECORD_TYPE;
                unset ADD_SUBDOMAINS;
                CANCEL_REQ=${_TRUE};

                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)";

                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                ;;
            *)
                if [ -z "${NS_ALIAS}" ]
                then
                    ## alias provided was blank
                    unset NS_ALIAS;

                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "A target requested was blank. Cannot continue.";
                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)";

                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                else
                    while true
                    do
                        if [[ ! -z "${ADD_RECORDS}" || ! -z "${ADD_SUBDOMAINS}" || ! -z "${CANCEL_REQ}" || ! -z "${ADD_COMPLETE}" ]]
                        then
                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Received request to break out. ADD_RECORDS->${ADD_RECORDS}, CANCEL_REQ->${CANCEL_REQ}.";

                            break;
                        fi

                        reset; clear;

                        print "\t\t\t$(grep plugin.application.name ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                        print "\t$(grep add.record.target ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2 | sed -e "s/%RECORD_TYPE%/$(echo ${RECORD_TYPE}| tr "[a-z]" "[A-Z]")/")";
                        print "\t$(grep system.option.cancel "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        read NS_TARGET;

                        reset; clear;
                        print "$(grep system.pending.message "${SYSTEM_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";

                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "NS_TARGET->${NS_TARGET}";

                        case ${NS_TARGET} in
                            [Xx]|[Qq]|[Cc])
                                ## user has chosen to cancel
                                unset NS_TARGET;
                                unset NS_ALIAS;
                                unset RECORD_TYPE;
                                unset ADD_SUBDOMAINS;

                                CANCEL_REQ=${_TRUE};

                                [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Configuration for ${ZONE_NAME}, record type ${RECORD_TYPE} canceled.";
                                reset; clear;
                                print "$(grep system.request.canceled ${PLUGIN_SYSTEM_MESSAGES} | grep -v "#" | cut -d "=" -f 2)\n";
                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                ;;
                            *)
                                if [ -z "${NS_TARGET}" ]
                                then
                                    ## ip addr provided is blank
                                    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${NS_TARGET} was blank. Cannot continue.";
                                    print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                    unset NS_TARGET;

                                    sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                else
                                    ## validate target
                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Validating target information..";

                                    THIS_CNAME="${CNAME}";
                                    unset METHOD_NAME;
                                    unset CNAME;

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

                                    ## validate the input
                                    ${PLUGIN_ROOT_DIR}/lib/validators/validateRecordData.sh target ns ${NS_TARGET};
                                    typeset -i RET_CODE=${?};

                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
                                    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;

                                    CNAME="${THIS_CNAME}";
                                    local METHOD_NAME="${THIS_CNAME}#${0}";

                                    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

                                    if [ ${RET_CODE} -eq 0 ] || [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                                    then
                                        if [ ${RET_CODE} -eq 63 ] || [ ${RET_CODE} -eq 64 ]
                                        then
                                            ## we got a warning on validation - we arent failing, but we do want to inform
                                            ${LOGGER} "WARN" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while updating the requested zone. Please try again.";
                                            print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                            sleep "${MESSAGE_DELAY}";
                                        fi

                                        unset RET_CODE;
                                        unset RETURN_CODE;

                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address record provided is ${NS_TARGET}";
                                        [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Executing add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${NS_ALIAS},${NS_TARGET} -s";

                                        ## our provided address is valid.
                                        ## make a call out to add_records
                                        ## to process the request
                                        if [ "${ADD_EXISTING_RECORD}" = "${_TRUE}" ]
                                        then
                                            ## we're adding a new record to an existing zone.
                                            ## call out the appropriate runner
                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Adding new record type to zone..";

                                            ## temp unset
                                            unset METHOD_NAME;
                                            unset CNAME;
                                            execute runner here

                                            local METHOD_NAME="${CNAME}#${0}";
                                            CNAME="$(basename "${0}")";

                                            check retcode here
                                        else
                                            . ${PLUGIN_ROOT_DIR}/lib/helpers/data/add_${RECORD_TYPE}_record.sh -b ${BIZ_UNIT} -p ${SITE_PRJCODE} -z "${SITE_HOSTNAME}" -i ${IUSER_AUDIT} -c ${CHG_CTRL} -t ${RECORD_TYPE} -a ${NS_ALIAS},${NS_TARGET} -s;
                                            typeset -i RET_CODE=${?};

                                            ## reset vars
                                            local METHOD_NAME="${CNAME}#${0}";
                                            CNAME="$(basename "${0}")";

                                            [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Processing complete. Return code -> ${RET_CODE}";

                                            if [ ${RET_CODE} -eq 0 ]
                                            then
                                                unset RET_CODE;
                                                unset RETURN_CODE;
                                                unset NS_ALIAS;
                                                unset NS_TARGET;
                                                unset RECORD_TYPE;

                                                ## record was successfully added. ask if we should add more
                                                ADD_COMPLETE=${_TRUE};
                                                reset; clear; break;
                                            else
                                                ## an "ERROR" occurred
                                                ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "An "ERROR" occurred while adding ${RECORD_TYPE} to ${SITE_HOSTNAME}. Return code from add_${RECORD_TYPE}_record.sh -> ${RET_CODE}";
                                                print "$(grep ${RET_CODE} "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                                unset RET_CODE;
                                                unset RETURN_CODE;
                                                unset NS_TARGET;
                                                unset NS_ALIAS;

                                                sleep "${MESSAGE_DELAY}"; reset; clear; break;
                                            fi
                                        fi
                                    else
                                        ## ip addr provided is blank
                                        ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Address ${NS_TARGET} was blank. Cannot continue.";
                                        print "$(grep selection.invalid "${ERROR_MESSAGES}" | grep -v "#" | cut -d "=" -f 2)\n";
                                        unset NS_TARGET;
                                        unset RET_CODE;
                                        unset RETURN_CODE;

                                        sleep "${MESSAGE_DELAY}"; reset; clear; continue;
                                    fi
                                fi
                                ;;
                        esac
                    done
                fi
                ;;
        esac
    done

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 0;
}

#===  FUNCTION  ===============================================================
#          NAME:  usage
#   DESCRIPTION:  Provide information on the function usage of this application
#    PARAMETERS:  None
#          NAME:  usage
#==============================================================================
function usage
{
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;
    [ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

    print "${CNAME} - Add "AUDIT" indicators and other flags to the failover zone file";
    print "Usage: ${CNAME} [ zone | subdomain ]";
    print "  -h|-?   Show this help";

    [ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

    return 3;
}

[ ${#} -eq 0 ] && usage;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

[ "${1}" = "zone" ] && add_zone_ui_helper || add_subdomain_ui_helper;

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RETURN_CODE -> ${RETURN_CODE}";
[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} -> exit";

unset SCRIPT_ABSOLUTE_PATH;
unset SCRIPT_ROOT;
unset THIS_CNAME;
unset RET_CODE;
unset CNAME;
unset METHOD_NAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

return ${RETURN_CODE};
