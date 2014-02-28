FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 144 -h 144 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 144x144 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxxhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 96 -h 96 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 600x600 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xxhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 64 -h 64 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 64x64 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-xhdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 48 -h 48 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 48x48 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-hdpi\"

FOR %%x IN (*.svg) DO "C:\Program Files\Inkscape-0.48\inkscape.exe" %%x -z -e -w 32 -h 32 --export-dpi=1200 --export-area-drawing --export-png="%%~nx.png"
mogrify *.png -background none -gravity Center -extent 32x32 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\drawable-mdpi\"


xcopy /e /y /f *.svg "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\svg\"


