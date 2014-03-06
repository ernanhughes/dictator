for /D %subdir in (D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\*) DO FOR %%x IN (*.png) DO pngquant.exe %%x --force --ext .png
for /D %subdir in (D:\Users\Ernan\AndroidStudioProjects\DictatorProject\Dictator\src\main\res\*) DO FOR %%x IN (*.png) DO pngout.exe %%x
 