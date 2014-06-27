#!/usr/bin/env ksh
#==============================================================================
#
#          FILE:  constants.sh
#         USAGE:  ./constants.sh
#   DESCRIPTION:  Sets and unsets system variables
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

[[ "$(uname)" = CYGWIN_NT* ]] && [ -z "$(set | grep CYGWIN)" ] && typeset -rx CYGWIN="error_start=C:/cygwin/bin/gdb.exe -nw %1 %2";

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

## Application constants
CNAME="${THIS_CNAME}";
THIS_CNAME="${CNAME}";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo -n "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";
typeset -r PLUGIN_NAME="DNSAdministration";

## load application-wide constants if not already done
if [ -z "${APP_ROOT}" ]
then
    case $(pwd) in
        *monitors*|*executors*|*sys*|*bin*) . $(pwd)/../../../constants.sh ;;
        *home*|*lib*) . ${SCRIPT_ROOT}/../../../constants.sh ;;
        *) . ${SCRIPT_ROOT}/../../constants.sh ;;
    esac
fi

unset METHOD_NAME;
unset CNAME;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;

## validate the input
${APP_ROOT}/${LIB_DIRECTORY}/validateSecurityAccess.sh -a;
typeset -i RET_CODE=${?};

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set -x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set -x;

CNAME="${THIS_CNAME}";
local METHOD_NAME="${CNAME}#${0}";

[ ! -z "${ENABLE_DEBUG}" ] && [ "${ENABLE_DEBUG}" = "${_TRUE}" ] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "RET_CODE -> ${RET_CODE}";

if [ -z "${RET_CODE}" ] || [ ${RET_CODE} -ne 0 ]
then
    ${LOGGER} "AUDIT" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security violation found while executing ${CNAME} by ${IUSER_AUDIT} on host ${SYSTEM_HOSTNAME}";
    ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Security configuration blocks execution. Please verify security configuration.";

    echo -n "Security configuration does not allow the requested action.";

    return ${RET_CODE};
fi

unset RET_CODE;

###############################################################################
#       check if is running on a eSupport DR Node, exit if it is not.
###############################################################################
typeset -r ES_LIB="/opt/esupport/lib";
typeset -r ECOMSERVER_MODULE="${ES_LIB}/runsOnEcomServer.mod";

if [ -s ${ECOMSERVER_MODULE} ]
then
    . ${ECOMSERVER_MODULE};

    runsOnEcomServer "ED";
fi

typeset -rx PLUGIN_ROOT_DIR=${PLUGIN_DIR}/${PLUGIN_NAME};
typeset -rx PLUGIN_CONF_BASE=${PLUGIN_ROOT_DIR}/${ETC_DIRECTORY};

case ${EXPORT_ENVIRONMENT} in
    [Ss][Tt][Gg]|[Ss][Tt][Aa][Gg][Ee]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/stg/ ;;
    [Uu][Aa][Tt]|[Qq][Aa]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/qa/ ;;
    [Ii][Ss][Tt]|[Dd][Ee][Vv]|[Dd][Ee][Vv][Ee][Ll][Oo][Pp][Mm][Ee][Nn][Tt]) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/dev/ ;;
    *) PLUGIN_CONF_ROOT=${PLUGIN_CONF_BASE}/ ;; ## default to production
esac

[[ -z "${PLUGIN_CONF_ROOT}" || ! -s ${PLUGIN_CONF_ROOT}/plugin.properties ]] && return 1;

typeset -rx PLUGIN_LOADED=true;
typeset -rx PLUGIN_CONFIG=${PLUGIN_CONF_ROOT}/plugin.properties;

[ -f ${PLUGIN_CONFIG} ] && . ${PLUGIN_CONFIG};

typeset -i AUTHORIZATION_COUNT; AUTHORIZATION_COUNT=0;

[[ ! -z "${PLUGIN_TMP_DIRECTORY}" && ! -d ${PLUGIN_TMP_DIRECTORY} ]] && mkdir ${PLUGIN_TMP_DIRECTORY} > /dev/null 2>&1;
[[ ! -z "${PLUGIN_DATA_DIRECTORY}" && ! -d ${PLUGIN_DATA_DIRECTORY} ]] && mkdir ${PLUGIN_DATA_DIRECTORY} > /dev/null 2>&1;
[[ ! -z "${PLUGIN_WORK_DIRECTORY}" && ! -d ${PLUGIN_WORK_DIRECTORY} ]] && mkdir ${PLUGIN_WORK_DIRECTORY} > /dev/null 2>&1;
[[ ! -z "${PLUGIN_MAILSTORE_DIR}" && ! -d ${PLUGIN_MAILSTORE_DIR} ]] && mkdir ${PLUGIN_MAILSTORE_DIR} > /dev/null 2>&1;
[[ ! -z "${PLUGIN_BACKUP_DIR}" && ! -d ${PLUGIN_BACKUP_DIR} ]] && mkdir ${PLUGIN_BACKUP_DIR} > /dev/null 2>&1;

## common aliases
[ -s ${PLUGIN_LIB_DIRECTORY}/aliases ] && . ${PLUGIN_LIB_DIRECTORY}/aliases;
[ -s ${PLUGIN_LIB_DIRECTORY}/functions ] && . ${PLUGIN_LIB_DIRECTORY}/functions;

[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "true" ] && set +x;
[ ! -z "${ENABLE_TRACE}" ] && [ "${ENABLE_TRACE}" = "${_TRUE}" ] && set +x;
