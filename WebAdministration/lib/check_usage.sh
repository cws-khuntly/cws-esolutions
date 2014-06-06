#!/usr/bin/env ksh
#==============================================================================
#
#           FILE:  check_usage.sh
#          USAGE:  ./check_usage.sh [-v] [-b] [-f] [-t] [-p] [-h] [-?]
#    DESCRIPTION:  Checks for the existance of a file that indicates if
#                  the application is already being used.
#
#        OPTIONS:  ---
#   REQUIREMENTS:  ---
#           BUGS:  ---
#          NOTES:  ---
#        AUTHOR:  Kevin Huntly <kmhuntly@gmail.com
#       COMPANY:  CaspersBox Web Services
#       VERSION:  1.0
#       CREATED:  ---
#       REVISION:  ---
#==============================================================================

[[ ! -z "${TRACE}" && "${TRACE}" = "TRUE" ]] && set -x;

## Application constants
CNAME="$(basename "${0}")";
SCRIPT_ABSOLUTE_PATH="$(cd "${0%/*}" 2>/dev/null; echo "${PWD}"/"${0##*/}")";
SCRIPT_ROOT="$(dirname "${SCRIPT_ABSOLUTE_PATH}")";

#===  FUNCTION  ===============================================================
#          NAME:  check_usage
#   DESCRIPTION:  Checks to see if this application is already in use, if so,
#                 locks out new users
#    PARAMETERS:  None
#       RETURNS:  0 for positive result, >1 for non-positive
#==============================================================================
function check_usage
{
    [[ ! -z "${TRACE}" && "${TRACE}" = "${_TRUE}" ]] && set -x;
    local METHOD_NAME="${CNAME}#${0}";

    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";
    [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Checking if the application is already in use..";

    ## update_in_progress is the name of the file
    ## we touch when a DNS change option is selected
    ## if the application is called to simply retrieve
    ## information, no file is created
    if [ -f ${APP_ROOT}/${APP_FLAG} ]
    then
        ## the file exists, check if our requestor is the same as the file's contents
        if [ $(grep -c ${IUSER_AUDIT} ${APP_ROOT}/${APP_FLAG}) -gt 1 ]
        then
            ${LOGGER} "ERROR" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Application already running. Terminating";
            IN_USE=${_TRUE};
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=5;
        else
            [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";
            RETURN_CODE=0;
        fi
    else
        ## file doesn't exist, create it and continue forward
        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "First time use - lets keep going";

        print "Application started by $(${IUSER_AUDIT} | awk '{print $1}') on $(date +"%m-%d-%Y") at $(date +"%H:%M:%S")" >> ${APP_ROOT}/${APP_FLAG};

        [[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> exit";

        RETURN_CODE=0;
    fi
}

METHOD_NAME="${CNAME}#startup";

[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${CNAME} starting up.. Process ID ${$}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "Provided arguments: ${@}";
[[ ! -z "${VERBOSE}" && "${VERBOSE}" = "${_TRUE}" ]] && ${LOGGER} "DEBUG" "${METHOD_NAME}" "${CNAME}" "${LINENO}" "${METHOD_NAME} -> enter";

check_usage;

return ${RETURN_CODE};
