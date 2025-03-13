@echo off
cd %~dp0
echo Current directory: %CD%
echo Starting JADE with socket-enabled agents...
echo Make sure no other process is using ports 5555 and 5556
echo Checking if ports are available...
netstat -ano | findstr :5555
netstat -ano | findstr :5556
echo.
echo Starting JADE agents...
echo Note: You should see two agents starting:
echo 1. FIPA-ACL agent on port 5555
echo 2. KQML agent on port 5556
echo.
java -cp bin;lib\jade-4.5.0.jar jade.Boot -gui -agents "seller:fipaacl.SocketSellerAgent;sellerkqml:KMQL.SocketSellerAgentKQML"
pause 