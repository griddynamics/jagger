echo off
REM deleting all processes with specified name
for /f "tokens=1" %%i in ('jps -m ^| find "AgentStarter"') do ( taskkill /F /PID %%i )
