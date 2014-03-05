FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 48 -h 48 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 48x48 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-mdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 72 -h 72 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 72x72 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-hdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 96 -h 96 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 96x96 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 142 -h 142 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 142x142 *.png
mogrify *.png -stroke pink -strokewidth 1  -fill none -draw "circle 71,71 113,113 " *.png
mogrify *.png -stroke pink -strokewidth 1  -fill none -draw "rectangle 11,11 131,131 " *.png
mogrify *.png -stroke lightblue -strokewidth 1  -fill none -draw "line 0,71 142,71 " *.png
mogrify *.png -stroke lightblue -strokewidth 1  -fill none -draw "line 71,0 71,142 " *.png
mogrify *.png -stroke SlateGray1 -strokewidth 1  -fill none -draw "line 0,22 142,22 " *.png
mogrify *.png -stroke SlateGray1 -strokewidth 1  -fill none -draw "line 22,0 22,142 " *.png
mogrify *.png -stroke SlateGray1 -strokewidth 1  -fill none -draw "line 0,120 142,120 " *.png
mogrify *.png -stroke SlateGray1 -strokewidth 1  -fill none -draw "line 120,0 120,142 " *.png
mogrify *.png -stroke DarkSeaGreen1 -strokewidth 1  -fill none -draw "line 0,0 142,142 " *.png
mogrify *.png -stroke DarkSeaGreen1 -strokewidth 1  -fill none -draw "line 142,0 0,142 " *.png
mogrify *.png -stroke DarkSeaGreen1 -strokewidth 1  -fill none -draw "line 0,44 142,44 " *.png
mogrify *.png -stroke DarkSeaGreen1 -strokewidth 1  -fill none -draw "line 44,0 44,142 " *.png
mogrify *.png -stroke pink -strokewidth 1  -fill none -draw "line 0,98 142,98 " *.png
mogrify *.png -stroke pink -strokewidth 1  -fill none -draw "line 98,0 98,142 " *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 192 -h 192 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 192x192 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxxhdpi\"

xcopy /e /y /f *.svg "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\svg\"

del *.png
del *.svg




