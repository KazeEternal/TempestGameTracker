@echo off
setlocal enabledelayedexpansion

cls

set items

for %%f in (Schema\*.fbs) do (
  set /p val=<%%f
  echo "%%f"
  call ..\flatbuffers\Debug\flatc.exe -o Android\\app\\src\\main\\java --java "%%f"
)

pause