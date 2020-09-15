import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

fun main(args: Array<String>) {

    val rootPathName = "src/test/resources"
    moveFiles(collectFoldersWithFiles(rootPathName))
}

private fun moveFiles(
    foldersWithFiles: MutableMap<File, MutableList<File>>,
    folderToFileExtensionMapping: Map<String, List<String>> = mapOf(
        "rgb" to listOf("jpg"), "ms" to listOf("tif")
    )
) {

    val fileExtensionToFolderNameMapping = reverseMapping(folderToFileExtensionMapping)

    foldersWithFiles.forEach { folderWithFiles ->
        // Create folders
        folderToFileExtensionMapping.keys.forEach {
            folderWithFiles.key.resolve(it).mkdir()
        }

        // Move files
        folderWithFiles.value.forEach { file ->
            val folderName = fileExtensionToFolderNameMapping[file.extension.toLowerCase()]
            folderName?.let {
                Files.move(
                    file.toPath(), folderWithFiles.key.toPath().resolve(folderName).resolve(file.name),
                    StandardCopyOption.ATOMIC_MOVE
                )
            }
        }
    }
}

private fun reverseMapping(folderToFileExtensionMapping: Map<String, List<String>>): MutableMap<String, String> {
    val fileExtensionToFolderMapping = mutableMapOf<String, String>()
    folderToFileExtensionMapping.entries.forEach { entry ->
        entry.value.forEach { fileExtension ->
            fileExtensionToFolderMapping[fileExtension] = entry.key
        }
    }
    return fileExtensionToFolderMapping
}

private fun collectFoldersWithFiles(
    rootPathName: String,
    supportedFileExtensions: Set<String> = setOf("tif", "jpg"),
    ignoredFolders: Set<String> = setOf("rgb", "ms")
): MutableMap<File, MutableList<File>> {
    val directories = mutableMapOf<File, MutableList<File>>()
    val rootPath = File(rootPathName)
    var hasFiles = false
    rootPath.walkBottomUp()
        .onEnter { file -> hasFiles = false; true }
        .filter { file -> supportedFileExtensions.contains(file.extension.toLowerCase()) }
        .filter { file -> !(file.isDirectory && ignoredFolders.contains(file.name.toLowerCase())) }
        .forEach { file ->
            if (hasFiles || file.isFile) {
                println(file)
                println("""directory ${file.parent} has files""")
                directories.getOrPut(file.parentFile, { mutableListOf() }).add(file)
            }
        }
    return directories
}

