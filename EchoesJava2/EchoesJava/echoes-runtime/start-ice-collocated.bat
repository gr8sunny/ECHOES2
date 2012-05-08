%ECHOES_RUNTIME:~0,2%
cd %ECHOES_RUNTIME%
start "vision-system" bin\vision-system\cameraRecorder.exe
%ICE_HOME%\bin\icegridnode.exe --Ice.Config=config/icegridnode-collocated.cfg

@echo off
TASKKILL /F /IM "cameraRecorder.exe"
For /f "tokens=1-4 delims=/ " %%a in ('date /t') do (set mydate=%%a%%b%%c)
For /f "tokens=1-2 delims=/:" %%a in ('time /t') do (set mytime=%%a%%b)
mkdir logs\%mydate%_%mytime%

copy *.out logs\%mydate%_%mytime%
del *.out
copy *.avi logs\%mydate%_%mytime%
del *.avi
copy *.txt logs\%mydate%_%mytime%
del *.txt
copy bin\rendering-engine\*.out logs\%mydate%_%mytime%
del bin\rendering-engine\*.out
copy bin\head-tracker\*.out logs\%mydate%_%mytime%
del bin\head-tracker\*.out
copy bin\vision-system\*.out logs\%mydate%_%mytime%
del bin\vision-system\*.out

echo "Log Files successfully moved to logs/%mydate%_%mytime%/"
echo "*******************************************************"
pause