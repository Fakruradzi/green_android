#!/bin/bash
set -e

# ----- Help
help_message() {
  cat <<- _EOF_
  Update Gradle Dependency Verification checksums

  Usage: $SCRIPT_NAME [-h|--help] [-d|--docker] [-a|--add] [-al]

  Options:
    -h, --help        Display this help message and exit
    -d, --docker      Run in Docker container
    -o, --overwrite   Add new checksums
    -b, --build       Run build task to get more dependencies (takes longer)
    -t, --test        Run test task to get more dependencies (takes longer)

_EOF_
  exit 0
}

# ----- Vars
GRADLE="local"
OVERWRITE=false
TASK="help"

# --- Argument handling
# https://stackoverflow.com/questions/192249/how-do-i-parse-command-line-arguments-in-bash
POSITIONAL=()
while [[ $# -gt 0 ]]
do
key="$1"

case $key in
    -h | --help)
      help_message ;;
    -d | --docker)
      GRADLE="docker"
      shift ;;
    -b | --build)
      TASK="assembleDevelopmentRelease"
      shift ;;
    -t | --test)
      TASK="test"
      shift ;;
    -o | --overwrite)
      OVERWRITE=true
      shift ;;
    *)    # unknown option
    POSITIONAL+=("$1") # save it in an array for later
    shift # past argument
    ;;
esac
done

set -- "${POSITIONAL[@]:-}" # restore positional parameters

# --- Execution
if [[ $OVERWRITE = true ]]; then
  # remove all checksums
  # TODO: Fix sed in linux
  sed -i '' -e '/<components>/,/<\/components>/d' gradle/verification-metadata.xml
fi

# Run locally or in Docker
if [[ $GRADLE == "local" ]]; then
    ./gradlew --write-verification-metadata sha256 "${TASK}"
else
  echo "Using Docker"
  # Deprecated: Use `docker run -it -v $PWD:/ga greenaddress/android -u` instead
  # docker run --rm -v $PWD:/ga --entrypoint /bin/sh greenaddress/android@sha256:6c319f48b63af1107aa3d144fb9ea4ad909648bf6d0c538fa4724690bb64edc6 "-c" "cd /ga && ./gradlew --write-verification-metadata sha256 ${TASK}"
  docker run --rm -it -v $PWD:/ga greenaddress/android -u --write-verification-metadata sha256 ${TASK}
fi
