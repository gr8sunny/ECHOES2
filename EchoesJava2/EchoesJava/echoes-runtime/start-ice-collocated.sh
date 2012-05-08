#!/bin/bash
cd $ECHOES_RUNTIME

java -jar $ICE_HOME/lib/IceGridGUI.jar --Ice.Config=config/icegridnode.cfg &
$ICE_HOME/bin/icegridnode --Ice.Config=config/icegridnode-collocated.cfg 

export ts=`date +%m%d%Y_%H%M`
mkdir logs/$ts
mv *.out logs/$ts
mv bin/rendering-engine/*.out logs/$ts

echo "Log Files successfully moved to logs/$ts/"
echo "*******************************************************"
