@echo off
 
::默认PID，无需修改
set "PID=999999"

::记录当前目录，无需修改
set "CURRENT_PATH=%cd%"
 
::指定jre_home
set "JRE_HOME_CUSTOM=%CURRENT_PATH%\java"
 
::指定程序包名
set "JARNAME=swak-tools-1.0.0_final.jar"

::指定程序启动日志名
set "LOG_FILE=out.log"

::指定GC日志文件名
md "%CURRENT_PATH%\logs\jvm"
set "TIMES=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%"
set "GC_FILE=logs\jvm\gc-%TIMES%.log"
set "OOM_FILE=logs\jvm\oom-%TIMES%.dump"

::流程控制
if "%1"=="start" (
  call:START
) else (
  if "%1"=="stop" ( 
    call:STOP 
  ) else ( 
    if "%1"=="restart" (
	  call:RESTART 
	) else ( 
	  call:DEFAULT 
	)
  )
)
goto:eof
 
 
::启动jar包
:START
echo function "start" starting...
start /b %JRE_HOME_CUSTOM%\bin\javaw.exe -Dfile.encoding=UTF-8 -Xms1024m -Xmx1024m -Xmn600m -server -XX:+PrintGCDetails -Xloggc:%GC_FILE% -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=%OOM_FILE% -jar %JARNAME% > %LOG_FILE%
echo == service start success
call:sleep5
goto:eof
 
 
::停止java程序运行
:STOP
echo function "stop" starting...
call:findPid
call:shutdown
echo == service stop success
goto:eof
 
 
::重启jar包
:RESTART
echo function "restart" starting...
call:STOP
call:sleep2
call:START
echo == service restart success
goto:eof
 
 
::执行默认方法--重启jar包
:DEFAULT
echo Now choose default item : restart
call:STOP
call:sleep2
call:START
echo == service restart success
goto:eof
 
 
::找到端口对应程序的pid
:findPid
echo function "findPid" start.
for /f "tokens=2" %%i in ('TASKLIST  /FI "IMAGENAME eq javaw.exe"') do (
   set "PID=%%i"
)
if "%PID%"=="999999" ( echo pid not find, skip stop . ) else ( echo pid is %PID%. )
goto:eof
 
 
::杀死pid对应的程序
:shutdown
if not "%PID%"=="999999" ( taskkill /f /pid %PID% )
goto:eof
 
::延时5秒
:sleep5
ping 127.0.0.1 -n 5 >nul
goto:eof
 
 
::延时2秒
:sleep2
ping 127.0.0.1 -n 2 >nul
goto:eof