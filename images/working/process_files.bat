set arg1=%1
set arg2=%2
set arg3=%3
set arg4=%4


FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w %arg1% -h %arg2% --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent %arg1%x%arg2% *.png
REM mogrify *.png -draw "text 40,40 '%arg1%x%arg2%'" -fill darkred *.png
REM mogrify *.png -draw "text 40,60 '%arg3%'" -fill darkred *.png
FOR %%x IN (*.png) DO pngquant.exe %%x --force --ext .png
FOR %%x IN (*.png) DO pngout.exe %%x

mkdir "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\"%arg3%
move *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\"%arg3% 

copy /Y *.svg "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\svg"
 