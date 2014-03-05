echo off
set arg1=%1
set arg2=%2
set arg3=%3


FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w %arg1% -h %arg2% --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent %arg1%x%arg2% *.png
FOR %%x IN (*.png) DO pngout.exe %%x

deltree %arg3%
mkdir %arg3%
move *.png %arg3% 