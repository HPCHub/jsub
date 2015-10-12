SOURCE="${BASH_SOURCE[0]}"
DIR="$( dirname "$SOURCE" )"
while [ -h "$SOURCE" ]
do 
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE"
  DIR="$( cd -P "$( dirname "$SOURCE"  )" && pwd )"
done
SCRIPTS_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
JSUB_JAR="$SCRIPTS_DIR/../jsub-cli.jar";
java -jar $JSUB_JAR $@