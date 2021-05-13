@ECHO on
SETLOCAL

CALL :DELETE todo-jar-with-dependencies.jar
move /Y "update\*" "."
@RD /S /Q "update"
(goto) 2>nul & del "%~f0"
exit 0

:RETRY_DELETE
sleep 1
:DELETE
del %~1
IF EXIST %~1 GOTO :RETRY_DELETE
exit /B 0