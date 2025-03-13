@echo off
cd %~dp0
java -cp bin;lib\jade-4.5.0.jar jade.Boot -gui Seller:fipaacl.SellerAgent;Buyer:fipaacl.BuyerAgent
pause 