%ECHOES_RUNTIME:~0,2%
cd %ECHOES_RUNTIME%
start %ICE_HOME%\bin\IceGridGUI.jar --Ice.Config=config/icegridnode.cfg
%ICE_HOME%\bin\icegridnode.exe --Ice.Config=config/icegridnode-collocated.cfg

@echo off
For /f "tokens=1-4 delims=/ " %%a in ('date /t') do (set mydate=%%a%%b%%c)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
mkdir logs\%mydate%_%mytime%

copy *.out logs\%mydate%_%mytime%
del *.out
copy bin\rendering-engine\*.out logs\%mydate%_%mytime%
del bin\rendering-engine\*.out
copy bin\head-tracker\*.out logs\%mydate%_%mytime%
del bin\head-tracker\*.out
copy bin\vision-system\*.out logs\%mydate%_%mytime%
del bin\vision-system\*.out

echo "Log Files successfully moved to logs/%mydate%_%mytime%/"
echo "*******************************************************"
pause