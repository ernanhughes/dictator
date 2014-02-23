"C:\Program Files\Inkscape-0.48\inkscape.exe" ic_launcher.svg -z -e -w 512 -h 512 --export-dpi=1200 --export-area-drawing --export-png="ic_launcher-web.png"
mogrify *.png -background none -gravity Center -extent 512x512 *.png
FOR %%x IN (*.png) DO pngout.exe %%x
xcopy /e /y /f *.png "D:\Users\Ernan\AndroidStudioProjects\DirectorProject\Director\src\main\"
