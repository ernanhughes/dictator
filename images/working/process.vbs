Dim shell: Set shell = CreateObject("WScript.Shell")
Dim fso: Set fso = CreateObject("Scripting.FileSystemObject")
Dim folder: Set folder = fso.GetFolder(fso.GetFolder("."))
WScript.Echo "Working folder: " & folder

 
Call processFiles(folder)

Function processFiles(folder)
	imageFolderPath = fso.GetParentFolderName(folder) & "\svg"
	Dim imageFolder: Set imageFolder = fso.getFolder(imageFolderPath)
	WScript.Echo "Image Folder: " & imageFolder
	Dim files: Set files = imageFolder.Files
	startTime = Now()
	For each fileItem In files
		strExtension = fso.getExtensionName(fileItem)
		If InStr(UCase(fileItem.Name), ".SVG") Then 
			minuteCount = DateDiff("n", fileItem.DateLastModified, startTime) 	
			If minuteCount < 10 Then 
				WScript.Echo minuteCount & "-->"  & fileItem
				WScript.Echo folder
				fso.CopyFile fileItem, folder & "\", True
				
				Dim curFile: Set curFile = fso.getFile(folder & "\" & fileItem.Name) 
				processFile(curFile)
				Call curFile.delete()
 			End If
		End IF
	Next
End Function

Function processFile(file) 
	processType = getProcessType(file)
	if (processType = "NORMAL") Then
		Dim sizes: Set sizes = getIconSizes()
		For Each elem in sizes
			Call process(elem, elem, sizes.Item(elem))
		Next 
	ElseIF (processType = "HALF") Then
		Dim halfSizes: Set halfSizes = getHalfIconSizes()
		For Each elem in halfSizes
			Call process(elem, elem, halfSizes.Item(elem))
		Next 
	ElseIF (processType = "QUARTER") Then
		Dim quarterSizes: Set quarterSizes = getQuarterIconSizes()
		For Each elem in quarterSizes
			Call process(elem, elem, quarterSizes.Item(elem))
		Next 
	ElseIF (processType = "MENU") Then
		Dim menuSizes: Set menuSizes = getMenuIconSizes()
		For Each elem in menuSizes
			Call process(elem, elem, menuSizes.Item(elem))
		Next 
	End If
End Function

Function process(width, height, directory)
	strCommand = "process_files.bat " & width & ", " & height & ", " & directory
    Wscript.Echo(strCommand)
	shell.run strCommand, 0, True
End Function

Function getProcessType(fileItem) 
	processType = "NORMAL"
	strExtension = fso.getExtensionName(fileItem)
	If InStr(UCase(fileItem.Name), ".SVG") Then 	
		Wscript.Echo("Normal...." & fileItem.Name)
		Wscript.Echo(fileItem.Name)
	End IF
	If InStr(UCase(fileItem.Name), "_HALF.SVG") Then 	
		processType = "HALF"
		Wscript.Echo("Half size file...." & fileItem.Name)
	End IF
	If InStr(UCase(fileItem.Name), "_HALF_SELECTED.SVG") Then 	
		processType = "HALF"
		Wscript.Echo("Half size file...." & fileItem.Name)
	End IF
	If InStr(UCase(fileItem.Name), "_QUARTER.SVG") Then 	
		processType = "QUARTER"
		Wscript.Echo("QUARTER size file...." & fileItem.Name)
	End IF
	If InStr(UCase(fileItem.Name), "_QUARTER_SELECTED.SVG") Then 	
		processType = "QUARTER"
		Wscript.Echo("QUARTER size file...." & fileItem.Name)
	End IF
	If InStr(UCase(fileItem.Name), "IC_MENU_") Then 	
		processType = "MENU"
		Wscript.Echo("MENU size file...." & fileItem.Name)
	End IF
	getProcessType = processType
End Function

Function getFullIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	startSize = 320
	Dim mapping: Set mapping = scaleRatios()
	For Each elem in mapping
		WScript.Echo "Calculation: " & startSize * mapping.Item(elem) & "  " & elem	
		Call process(startSize * mapping.Item(elem), startSize * mapping.Item(elem), elem)
	Next 
	Set getFullIconSizes = list
End Function


Function getHalfIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	startSize = 140
	Dim mapping: Set mapping = scaleRatios()
	For Each elem in mapping
		WScript.Echo "Calculation: " & startSize * mapping.Item(elem) & "  " & elem	
		Call process(startSize * mapping.Item(elem), startSize * mapping.Item(elem), elem)
	Next 
	Set getHalfIconSizes = list
End Function

Function getQuarterIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	startSize = 75
	Dim mapping: Set mapping = scaleRatios()
	For Each elem in mapping
		WScript.Echo "Calculation: " & startSize * mapping.Item(elem) & "  " & elem	
		Call process(startSize * mapping.Item(elem), startSize * mapping.Item(elem), elem)
	Next 
	Set getQuarterIconSizes = list
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

Function getMenuIconSizes()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	list.Add "96", "drawable-xxhdpi"
	list.Add "72", "drawable-xxhdpi"
	list.Add "48", "drawable-xhdpi"
	list.Add "36", "drawable-hdpi"
	list.Add "24", "drawable-mdpi"
	Set getMenuIconSizes = list
End Function



Function scaleRatios()
	Dim list: Set list = CreateObject("Scripting.Dictionary")
	list.Add    "drawable-sw360dp-mdpi"   , 1.125
	list.Add    "drawable-sw360dp-hdpi"   , 1.6875
	list.Add    "drawable-sw360dp-xhdpi"   , 2.25
	list.Add    "drawable-sw360dp-xxhdpi"   , 3.375
	list.Add    "drawable-sw480dp-mdpi"   , 1.5
	list.Add    "drawable-sw480dp-hdpi"   , 2.25
	list.Add    "drawable-sw480dp-xhdpi"   , 3
	list.Add    "drawable-sw480dp-xxhdpi"   , 4.5
	list.Add    "drawable-sw600dp-mdpi"   , 1.875
	list.Add    "drawable-sw600dp-hdpi"   , 2.8125
	list.Add    "drawable-sw600dp-xhdpi"   , 3.75	
	list.Add    "drawable-sw600dp-xxhdpi"   , 5.625
	list.Add    "drawable-sw720dp-mdpi"   , 2.25
	list.Add    "drawable-sw720dp-hdpi"   , 3.375
	list.Add    "drawable-sw720dp-xhdpi"   , 4.5
	list.Add    "drawable-sw720dp-xxhdpi"   , 6.75
	Set scaleRatios = list
End Function


	