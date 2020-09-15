import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

fun main(args: Array<String>) {

    val rootPathName = "src/test/resources"
    moveFiles(rootPathName)
}

/**
 * Move files according to their filetype to the given target folder.
 * If the target folder already exists no files are moved.
 */
private fun moveFiles(
    rootPathName: String,
    fileTypeToTargetFolderMapping: Map<String, String> = mapOf("jpg" to "rgb", "tif" to "ms")
) {
    /**
     * Remember folders successfully created for a file
     * Key is a concatenation of <folder name>.<file type>
     */
    val successfullyCreatedFolderForFileTypes = mutableMapOf<String, Boolean>()

    File(rootPathName)
        .walkTopDown()
        .onEnter { !fileTypeToTargetFolderMapping.values.contains(it.name) } // Visit only non target folders
        .forEach { file ->
            val fileType = file.extension.toLowerCase()
            if (fileTypeToTargetFolderMapping.containsKey(fileType)) { // do only for file types having a target folder configured
                fileTypeToTargetFolderMapping[fileType]?.let {
                    val folderType = file.parentFile.absolutePath + "." + file.extension.toLowerCase()
                    val targetFolder = file.parentFile.resolve(it)
                    if (!targetFolder.exists()) {
                        successfullyCreatedFolderForFileTypes.putIfAbsent(
                            folderType,
                            targetFolder.mkdir()
                        )
                    }
                    if (successfullyCreatedFolderForFileTypes[folderType] == true) {
                        Files.move(
                            file.toPath(),
                            file.parentFile.toPath().resolve(it).resolve(file.name),
                            StandardCopyOption.ATOMIC_MOVE
                        )
                    }
                }
            }
        }
}



