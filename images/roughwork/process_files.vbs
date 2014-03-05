Dim shell: Set shell = CreateObject("WScript.Shell")
Dim fso: Set fso = CreateObject("Scripting.FileSystemObject")
Dim folder: Set folder = fso.GetFolder(Wscript.Arguments.Item(0))
Dim files: Set files = folder.Files

Wscript.Echo fso.GetParentFolderName(wscript.ScriptFullName) 
For each fileItem In files
	strExtension = fso.getExtensionName(fileItem)
	If InStr(UCase(fileItem.Name), ".SVG") Then 	
		Wscript.Echo(fileItem.Name)
		fileName = fso.getBaseName(fileItem)
	End IF
Next

Dim sizes: Set sizes = getIconSizes()
For Each elem in sizes
	Call process(elem, elem, sizes.Item(elem))
Next 

Function process(width, height, directory)
	strCommand = "process_files.bat " & width & ", " & height & ", " & directory
    Wscript.Echo(strCommand)
	shell.run strCommand, 1, True
End Function

Function getIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	list.Add "192", "drawable-xxhdpi"
	list.Add "142", "drawable-xxhdpi"
	list.Add "96", "drawable-xhdpi"
	list.Add "72", "drawable-hdpi"
	list.Add "48", "drawable-mdpi"
	Set getIconSizes = list
End Function

Function getOddIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	list.Add "192", "drawable-xxhdpi"
	list.Add "142", "drawable-xxhdpi"
	list.Add "96", "drawable-xhdpi"
	list.Add "72", "drawable-hdpi"
	list.Add "48", "drawable-mdpi"
	Set getIconSizes = list
End Function


Function scaleRatios()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	list.Add 1.125, "drawable-sw360dp-mdpi"
	list.Add 1.6875, "drawable-sw360dp-hdpi"
	list.Add 2.25, "drawable-sw360dp-xhdpi"
	list.Add 3.375, "drawable-sw360dp-xxhdpi"
	list.Add 1.5, "drawable-sw480dp-mdpi"
	list.Add 2.25, "drawable-sw480dp-hdpi"
	list.Add 3.0, "drawable-sw480dp-xhdpi"
	list.Add 4.5, "drawable-sw480dp-xxhdpi"
	list.Add 1.875, "drawable-sw600dp-mdpi"
	list.Add 2.8125, "drawable-sw600dp-hdpi"
	list.Add 3.75, "drawable-sw600dp-xhdpi"
	list.Add 5.625, "drawable-sw600dp-xxhdpi"
	list.Add 2.25, "drawable-sw720dp-mdpi"
	list.Add 3.375, "drawable-sw720dp-hdpi"
	list.Add 4.5, "drawable-sw720dp-xhdpi"
	list.Add 6.75, "drawable-sw720dp-xxhdpi"
End Function


	