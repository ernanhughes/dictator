FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 800 -h 800 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 800x800 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxxhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 600 -h 600 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 600x600 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 420 -h 420 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 420x420 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 290 -h 290 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 290x290 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-hdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 220 -h 220 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 220x220 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-mdpi\"


xcopy /e /y /f *.svg "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\svg\"


