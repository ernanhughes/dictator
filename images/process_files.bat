set arg1=%1
set arg2=%2
set arg3=%3
set arg4=%4

"C:\Program Files\Inkscape-0.48\inkscape.exe" %arg1%.svg -z -e -w %arg2% -h %arg3% --export-dpi=1200 --export-area-drawing --export-png=%arg1%.png
mogrify *.png -background none -gravity Center -extent %arg2%x%arg3% *.png

mkdir "D:\Users\Ernan\AndroidStudioProjects\Dictator\app\src\main\res\"%arg4%
move *.png "D:\Users\Ernan\AndroidStudioProjects\Dictator\app\src\main\res\"%arg4% 

 