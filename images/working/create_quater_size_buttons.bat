FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 200 -h 200 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 200x200 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xhdpi\"

xcopy /e /y /f *.svg "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\svg\"

