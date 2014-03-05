 ' some nofificaton filter constants...
    Const FILE_NOTIFY_CHANGE_CREATION = &H40
    Const FILE_NOTIFY_CHANGE_LAST_WRITE = &H10  
    
    ' instantiate the file system watcher...
    Set oFSW = WScript.CreateObject("wshFileSystemWatcher.ucFSW", "oFSW_")

    ' set file system watcher parameters...
    oFSW.Path = "D:\Users\Ernan\AndroidStudioProjects\DictatorProject\images\working"  ' directory to watch
	oFSW.IncSubDir = False  ' don't include subdirectories
    oFSW.NotifyFlags = FILE_NOTIFY_CHANGE_CREATION  ' new or updated files
    oFSW.WaitInterval = 200  ' ms

    ' start file system watcher running...
    oFSW.EnableRaisingEvents = True
	