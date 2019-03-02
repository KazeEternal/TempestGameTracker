@echo off

echo Run Sharp Make

call ..\Sharpmake\Bin\Sharpmake.Application.exe /sources(@"SharpMake/TempestDesktopSolutions.sharpmake.cs") %*

pause