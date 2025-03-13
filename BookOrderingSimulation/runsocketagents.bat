@echo off
cd %~dp0
echo Starting JADE with socket-enabled agents...
java -cp bin;lib\jade-4.5.0.jar jade.Boot -gui seller:fipaacl.SocketSellerAgent sellerkqml:KMQL.SocketSellerAgentKQML
pause 